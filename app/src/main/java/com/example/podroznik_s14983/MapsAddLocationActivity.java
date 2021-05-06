package com.example.podroznik_s14983;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsAddLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_LAT = "com.example.podroznik_s14983.EXTRA_LAT";
    public static final String EXTRA_LON = "com.example.podroznik_s14983.EXTRA_LON";

    private GoogleMap mMap;
    private Marker marker;
    double pickedLatitude = 500;
    double pickedLongitude = 500;

    ImageButton btn_exitMap;
    ImageButton btn_saveLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_add_location);
        setTitle("Wybierz lokalizację");

        btn_exitMap = (ImageButton) findViewById(R.id.imageButton_mapPickClose);
        btn_saveLoc = (ImageButton) findViewById(R.id.imageButton_mapPickSave);

        //po kliknięciu w "zamknij" na mapie
        btn_exitMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //po kliknięciu w "zapisz" na mapie
        btn_saveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedLatitude != 500 && pickedLongitude != 500) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_LAT, pickedLatitude);
                    data.putExtra(EXTRA_LON, pickedLongitude);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Nie wybrano lokalizacji!", Toast.LENGTH_LONG);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ustaw mapę na warszawę
        LatLng warsaw = new LatLng(52.2297, 21.0122);
        marker = mMap.addMarker(new MarkerOptions().position(warsaw));
        marker.setVisible(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 10));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                pickedLatitude = point.latitude;
                pickedLongitude = point.longitude;
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(point));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
               // Toast.makeText(getApplicationContext(), pickedLatitude + ", " + pickedLongitude, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
