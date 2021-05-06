package com.example.podroznik_s14983;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationsListActivity extends AppCompatActivity {

    public static final String EXTRA_UID = "com.example.podroznik_s14983.EXTRA_UID";
    public static final int LOGOUT_REQUEST = 1;
    public static final int  ADD_LOCATION_REQUEST = 2;
    public static final int  EDIT_LOCATION_REQUEST = 3;
    public static final int  SHOW_LOCATIONS_MAP_REQUEST = 4;
    public static final int  SHOW_DETAILS_REQUEST = 5;
    public static final int PERMISSIONS_REQUEST = 6;
    public static final int GEOFENCE_BR_REQUEST = 7;

    private ImageButton btn_logout; // przycisk "wyloguj"
    private ImageButton btn_add; // przycisk "dodaj lokalizację"
    private ImageButton btn_map; // przycisk "pokaż mapę lokalizacji"

    private String uid;
    List<MyLocation> locations;
    final LocationAdapter adapter = new LocationAdapter();;

    FirebaseDatabase database;
    DatabaseReference dbRef;

    FusedLocationProviderClient locationClient;
    GeofencingClient geofencingClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Location currrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        setTitle("Moje miejsca");

        // Sprawdź i ew. pytaj o uprawnienia
        String[] permissions = {
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST);
        }

        uid = getIntent().getStringExtra(EXTRA_UID);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users/"+uid);

        // update bieżącej lokalizacji
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);

        locationCallback =  new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                currrentLocation = result.getLastLocation();
                geoFenceAreasRegister();
            }
        };

        locationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        locations = new ArrayList<MyLocation>();

        //geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
       // geoFenceAreasRegister();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        //słuchacz zmian w bazie
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locations.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dss : snapshot.getChildren()) {
                        MyLocation location = dss.getValue(MyLocation.class);
                        locations.add(location);
                    }
                    adapter.setLocations(locations);
                    geoFenceAreasRegister();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // przycisk "wyloguj"
        btn_logout = findViewById(R.id.imageButton_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout
                AuthUI.getInstance().signOut(LocationsListActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(LocationsListActivity.this, MainActivity.class);
                                startActivityForResult(intent, LOGOUT_REQUEST);
                                // jeszcze będzie trzeba wyłączyć nasłuchiwanie lokalizacji po implementacji...
                                uid = null;
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LocationsListActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG);
                            }
                        });
            }
        });

        // przycisk "dodaj lokalizację"
        btn_add = findViewById(R.id.imageButton_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationsListActivity.this, AddLocationActivity.class);
                startActivityForResult(intent, ADD_LOCATION_REQUEST);
            }
        });

        // przycisk "pokaż mapę lokalizacji"
        btn_map = findViewById(R.id.imageButton_mapShowLocations);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationsListActivity.this, MapsShowLocationsActivity.class);
                intent.putExtra(MapsShowLocationsActivity.EXTRA_LOCATIONS, (Serializable) locations);

                startActivityForResult(intent, SHOW_LOCATIONS_MAP_REQUEST);
            }
        });

        // po krotkim kliknieciu na element listy - pokaż szczegóły miejsca
        adapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(MyLocation location) {
                Intent intent = new Intent(LocationsListActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_LOCATION, (Serializable) location);

                startActivityForResult(intent, SHOW_DETAILS_REQUEST);
            }
        });

        // po dlugim kliknieciu na elemencie listy - edytuj dane miejsca
        adapter.setOnItemLongClickListener(new LocationAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(MyLocation location) {
                Intent intent = new Intent(LocationsListActivity.this, AddLocationActivity.class);
                intent.putExtra(AddLocationActivity.EXTRA_ID, location.getId());
                intent.putExtra(AddLocationActivity.EXTRA_NAME, location.getName());
                intent.putExtra(AddLocationActivity.EXTRA_LATITUDE, location.getLat());
                intent.putExtra(AddLocationActivity.EXTRA_LONGITUDE, location.getLon());
                intent.putExtra(AddLocationActivity.EXTRA_CIRCLE, location.getCircle());
                intent.putExtra(AddLocationActivity.EXTRA_NOTE, location.getNote());
                intent.putExtra(AddLocationActivity.EXTRA_PHOTO, location.getEncodedPhoto());
                startActivityForResult(intent, EDIT_LOCATION_REQUEST);
            }
        });

    }

    // nie wracaj do poprzedniego ekranu po wcisnieciu "back" na telefonie
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // dodawanie nowego miejsca
        if (requestCode == ADD_LOCATION_REQUEST && resultCode == RESULT_OK) {
            int id = locations.size();
            String name = data.getStringExtra(AddLocationActivity.EXTRA_NAME);
            double latitude = data.getDoubleExtra(AddLocationActivity.EXTRA_LATITUDE, 500);
            double longitude = data.getDoubleExtra(AddLocationActivity.EXTRA_LONGITUDE, 500);
            int circle = data.getIntExtra(AddLocationActivity.EXTRA_CIRCLE, -1);
            String note = data.getStringExtra(AddLocationActivity.EXTRA_NOTE);
            String encodedPhoto = data.getStringExtra(AddLocationActivity.EXTRA_PHOTO);

            if(latitude  == 500 || longitude  == 500 || circle  == -1) {
                Toast.makeText(this, "Błąd przy dodawaniu miejsca", Toast.LENGTH_SHORT).show();
                return;
            }

            MyLocation location = new MyLocation(id, name, latitude, longitude, circle, note, encodedPhoto);
            locations.add(location);
            adapter.setLocations(locations);
            geoFenceAreasRegister();

            dbRef.child(""+id).setValue(location);
        }
        // edycja miejsca
        else if(requestCode == EDIT_LOCATION_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddLocationActivity.EXTRA_ID, -1);
            String name = data.getStringExtra(AddLocationActivity.EXTRA_NAME);
            double latitude = data.getDoubleExtra(AddLocationActivity.EXTRA_LATITUDE, 500);
            double longitude = data.getDoubleExtra(AddLocationActivity.EXTRA_LONGITUDE, 500);
            int circle = data.getIntExtra(AddLocationActivity.EXTRA_CIRCLE, -1);
            String note = data.getStringExtra(AddLocationActivity.EXTRA_NOTE);
            String encodedPhoto = data.getStringExtra(AddLocationActivity.EXTRA_PHOTO);

            if(id == -1 || latitude  == 500 || longitude  == 500 || circle  == -1) {
                Toast.makeText(this, "Błąd przy edycji miejsca", Toast.LENGTH_SHORT).show();
                return;
            }

            MyLocation location = new MyLocation(id, name, latitude, longitude, circle, note, encodedPhoto);
            for(MyLocation loc : locations) {
                if (loc.getId() == id) {
                    int index = locations.indexOf(loc);
                    locations.set(index, location);
                }
            }
            adapter.setLocations(locations);
            geoFenceAreasRegister();

            dbRef.child(""+id).setValue(location);
        }
        // pozostałe przypadki
        else {
           // Toast.makeText(this, "Anulowano.", Toast.LENGTH_SHORT).show();
        }
    }

    // metoda do sprawdzania uprawnień
    private static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // rejestracja obszarów do geofencingu
    private void geoFenceAreasRegister() {
        List<Geofence> myGeoFences = new ArrayList<Geofence>();

        GeofencingRequest.Builder geoReqBuild = new GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        if(!locations.isEmpty()) {
            for(MyLocation loc : locations) {
                String id = loc.getName();
                Geofence fence = new Geofence.Builder()
                        .setRequestId(id)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setCircularRegion(loc.getLat(), loc.getLon(), loc.getCircle())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build();
                myGeoFences.add(fence);
            }
            geoReqBuild.addGeofences(myGeoFences);

            GeofencingRequest geoReq = geoReqBuild.build();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, GEOFENCE_BR_REQUEST, new Intent (this, GeoFenceReceiver.class), 0);
            //geofencingClient.removeGeofences(pendingIntent);
            geofencingClient.addGeofences(geoReq, pendingIntent);
        }
    }
}
