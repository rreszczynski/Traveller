package com.example.podroznik_s14983;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsShowLocationsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_LOCATIONS = "com.example.podroznik_s14983.EXTRA_LOCATIONS";

    private GoogleMap mMap;
    private Marker marker;
    private List<MyLocation> locations;

    ImageButton btn_exitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_show_locations);
        setTitle("Mapa moich miejsc");

        Intent intent = getIntent();
        locations = (List<MyLocation>) intent.getSerializableExtra(EXTRA_LOCATIONS);

        btn_exitMap = (ImageButton) findViewById(R.id.imageButton_mapShowLocationsClose);

        //po kliknięciu w "zamknij" na mapie
        btn_exitMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MyLocation location : locations) {
            LatLng coords = new LatLng(location.getLat(), location.getLon());
            marker = mMap.addMarker(new MarkerOptions().position(coords));
            marker.setTitle(location.getName());

            mMap.addCircle(new CircleOptions()
                    .center(coords)
                    .radius(location.getCircle())
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF)
                    .strokeWidth(1)
            );

            marker.showInfoWindow();
            builder.include(coords);
        }

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 10));
    }
}
