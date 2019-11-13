package com.example.app_usuario_v2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class LoginActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;



    //declarar circulo de cargando
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SolicitudDePermisoGPS();
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


    private void goMainScreen(int tipo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("TIPO_LOGEO", tipo + "");
        startActivity(intent);
        progressDialog.dismiss();
    }


}
