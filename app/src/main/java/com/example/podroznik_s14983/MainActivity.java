package com.example.podroznik_s14983;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

/*
    --- WAŻNE INFORMACJE ---

    Przykład z danymi - login przez email, login: projekt02@prm.pl, hasło: projekt02

    Program testowany na emulatorze w API 29
 */

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST = 1;
    private static final int LOCATIONS_LIST_REQUEST = 2;

    List<AuthUI.IdpConfig> providers; // Dostawcy tożsamości
    String uid; // identyfikator zalogowanego użytkownika

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init provider
        providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
        );


        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(R.style.MyTheme).setLogo(R.drawable.ic_logo).build(), LOGIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // po zalogowaniu
            if (resultCode == RESULT_OK) {
                //pobierz uid zalogowanego użytkownika
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Intent intent = new Intent(MainActivity.this, LocationsListActivity.class);
                intent.putExtra(LocationsListActivity.EXTRA_UID, uid);
                startActivityForResult(intent, LOCATIONS_LIST_REQUEST);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    // nie wracaj do poprzedniego ekranu po wcisnieciu "back" na telefonie
    @Override
    public void onBackPressed() {
    }
}
