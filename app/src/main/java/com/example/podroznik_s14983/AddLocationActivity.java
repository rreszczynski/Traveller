package com.example.podroznik_s14983;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;

public class AddLocationActivity extends AppCompatActivity {

    public static final int CHOOSE_LOCATION_REQUEST = 1;
    public static final int TAKE_PHOTO_REQUEST = 2;

    public static final String EXTRA_ID = "com.example.podroznik_s14983.EXTRA_ID";
    public static final String EXTRA_NAME = "com.example.podroznik_s14983.EXTRA_NAME";
    public static final String EXTRA_LATITUDE = "com.example.podroznik_s14983.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.podroznik_s14983.EXTRA_LONGITUDE";
    public static final String EXTRA_CIRCLE = "com.example.podroznik_s14983.EXTRA_CIRCLE";
    public static final String EXTRA_NOTE = "com.example.podroznik_s14983.EXTRA_NOTE";
    public static final String EXTRA_PHOTO = "com.example.podroznik_s14983.EXTRA_IMAGE";

    FusedLocationProviderClient fusedLocationClient;

    Button btn_loc_current; // przycisk "bieżąca lokalizacja"
    Button btn_loc_getFromMap; // przycisk "lokalizacja z mapy"
    Button btn_addPhoto; // przycisk "dodaj zdjęcie"

    ImageButton btn_close; // przycisk "wyjdź"
    ImageButton btn_save; // przycisk "zapisz"

    EditText editText_name; // nazwa miejsca
    EditText editText_latitude; // dł. geogr. miejsca
    EditText editText_longitude; // szer. gogr. miejsca
    EditText editText_circle; // nazwa miejsca
    EditText editText_note; // nazwa miejsca

    int id; // id miejca - przy edycji
    String encodedImage; // zrobione zdjęcie


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btn_loc_current = (Button) findViewById(R.id.button_loc_current);
        btn_loc_getFromMap = (Button) findViewById(R.id.button_loc_getFromMap);
        btn_addPhoto = (Button) findViewById(R.id.button_addPhoto);

        btn_close = (ImageButton) findViewById(R.id.imageButton_close);
        btn_save = (ImageButton) findViewById(R.id.imageButton_save);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_latitude = (EditText) findViewById(R.id.editText_latitude);
        editText_longitude = (EditText) findViewById(R.id.editText_longitude);
        editText_circle = (EditText) findViewById(R.id.editText_circle);
        editText_note = (EditText) findViewById(R.id.editText_note);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edytuj miejsce");
            id = intent.getIntExtra(EXTRA_ID, -1);
            encodedImage = intent.getStringExtra(EXTRA_PHOTO);
            editText_name.setText(intent.getStringExtra(EXTRA_NAME));
            editText_latitude.setText(intent.getDoubleExtra(EXTRA_LATITUDE, 0)+"");
            editText_longitude.setText(intent.getDoubleExtra(EXTRA_LONGITUDE, 0)+"");
            editText_circle.setText(intent.getIntExtra(EXTRA_CIRCLE, 0)+"");
            editText_note.setText(intent.getStringExtra(EXTRA_NOTE));
        } else {
            setTitle("Dodaj nowe miejsce");
        }

        //po kliknięciu w przycisk "bieżąca lokalizacja" pobierz aktualne położenie i przypisz do textView
        btn_loc_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fusedLocationClient.getLastLocation().addOnSuccessListener(AddLocationActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            editText_latitude.setText(latitude + "");
                            editText_longitude.setText(longitude + "");
                        } else {
                            Toast.makeText(AddLocationActivity.this, "Ppbrana lokalizacja = null", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                fusedLocationClient.getLastLocation().addOnFailureListener(AddLocationActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddLocationActivity.this, "Nie udało się pobrać bieżącej lokalizacji :(", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // po kliknięciu w przycisk "lokalizacja z mapy" wyświetl mapę
        btn_loc_getFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddLocationActivity.this, MapsAddLocationActivity.class);
                startActivityForResult(intent, CHOOSE_LOCATION_REQUEST);
            }
        });

        // po kliknięciu w przycisk "dodaj zdjęcie"
        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
                }
            }
        });

        //po kliknięciu w "zamknij"
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //po kliknięciu w "zapisz"
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataCorrect()) {
                    Intent data = new Intent();
                    if(id != -1) {
                        data.putExtra(EXTRA_ID, id);
                    }

                    data.putExtra(EXTRA_PHOTO, encodedImage);
                    data.putExtra(EXTRA_NAME, editText_name.getText().toString());
                    data.putExtra(EXTRA_LATITUDE, Double.parseDouble(editText_latitude.getText().toString()));
                    data.putExtra(EXTRA_LONGITUDE, Double.parseDouble(editText_longitude.getText().toString()));
                    data.putExtra(EXTRA_CIRCLE, Integer.parseInt(editText_circle.getText().toString()));
                    data.putExtra(EXTRA_NOTE, editText_note.getText().toString());

                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // po wybraniu lokacji z mapy
        if (requestCode == CHOOSE_LOCATION_REQUEST && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra(MapsAddLocationActivity.EXTRA_LAT, 500);
            double longitude = data.getDoubleExtra(MapsAddLocationActivity.EXTRA_LON, 500);
            editText_latitude.setText(latitude + "");
            editText_longitude.setText(longitude + "");
        }

        // po zrobieniu zdjęcia
        else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Zdjęcie dodane.", Toast.LENGTH_SHORT).show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodedImage = MyLocation.encodeBitmapToString(imageBitmap);
        }
        else {
            Toast.makeText(this, "Anulowano.", Toast.LENGTH_SHORT).show();
        }
    }

    // sprawdza czy poprawnie wypelniono pola formularza
    private boolean isDataCorrect() {
        String name = editText_name.getText().toString();
        String latitude = editText_latitude.getText().toString();
        String longitude = editText_latitude.getText().toString();
        String circle = editText_latitude.getText().toString();

        if(name.trim().isEmpty() || latitude.trim().isEmpty() || longitude.trim().isEmpty() || circle.trim().isEmpty()) {
            Toast.makeText(AddLocationActivity.this, "Niepoprawnie wypełnione pola", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

