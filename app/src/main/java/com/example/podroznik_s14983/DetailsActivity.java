package com.example.podroznik_s14983;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION = "com.example.podroznik_s14983.EXTRA_LOCATION";
    private MyLocation location;

    ImageButton btn_details_close;
    TextView tv_details_name;
    ImageView iv_details_picture;
    TextView tv_details_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Szczegóły");

        Intent intent = getIntent();
        location = (MyLocation) intent.getSerializableExtra(EXTRA_LOCATION);

        btn_details_close = (ImageButton) findViewById(R.id.imageButton_details_close);
        tv_details_name = (TextView) findViewById(R.id.textView_details_name);
        iv_details_picture =(ImageView) findViewById(R.id.imageView_details_picture);
        tv_details_note = (TextView) findViewById(R.id.textView_details_note);

        tv_details_name.setText(location.getName());
        tv_details_note.setText(location.getNote());

        Bitmap bitmap = MyLocation.decodeBitmapFromString(location.getEncodedPhoto());
        if(bitmap != null) {
            iv_details_picture.setImageBitmap(bitmap);
        }

        //po kliknięciu w "zamknij"
        btn_details_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
