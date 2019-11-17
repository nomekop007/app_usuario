package com.example.app_usuario_v2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7117;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SolicitudDePermisoGPS();
        showsignOptions();

    }


    private void showsignOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                //iniciar proovedores
                                new AuthUI.IdpConfig.EmailBuilder().build(), //Email
                                new AuthUI.IdpConfig.FacebookBuilder().build(), //facebook
                                new AuthUI.IdpConfig.GoogleBuilder().build() // google
                        ))
                        .setLogo(R.drawable.logo2)
                        .setIsSmartLockEnabled(false,true)
                        .setTheme(R.style.temaLogin)
                        .build(), MY_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {

                goMainScreen();
            } else {
                try {
                    Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    finish();
                }

            }
        }
    }

    private void goMainScreen() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            finish();
        }
    }


    private void SolicitudDePermisoGPS() {
        //pregunta si el permiso no esta dado
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //si no esta dado , habre la ventana de pregunta
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;

        }
    }

    @Override
    public void onBackPressed() {
        isDestroyed();
    }
}
