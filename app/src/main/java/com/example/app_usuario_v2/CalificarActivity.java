package com.example.app_usuario_v2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_usuario_v2.model.Trasporte;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalificarActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView descripcion;


    private String TrasporteDescripcion="";

    private Trasporte trasporte;

    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    //hacer referencia a la base de datos de firebase




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);


        ConfiguracionToolbar();
        ConfigurarRatingBar();
        BuscarTrasporte();
    }

    private void ConfigurarRatingBar() {
        ratingBar = findViewById(R.id.ratingBar);
        descripcion = findViewById(R.id.txt_Descripcion);


        //mostrar descripcion
        Intent i = getIntent();
        String descr = i.getStringExtra("descripcion");
        TrasporteDescripcion = descr;
        descripcion.setText(descr);


    }

    private void ConfiguracionToolbar(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calificacion y Reclamos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void BuscarTrasporte() {

        myDatabase.child("trasporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                      Trasporte  tr = snapshot.getValue(Trasporte.class);


                 //si no es -1 significa que lo encontro
                    String patente = tr.getPatente();
                    int resultado = TrasporteDescripcion.indexOf(patente);
                 if (resultado != -1){

                     //guarda el trasporte encontrado
                    trasporte = tr;
                     Log.e("funciono ",trasporte.getNombreConductor());
                 }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    //boton de flecha atras de toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void Calificar(View view) {


        Toast.makeText(getApplicationContext(), "calificado con "
                +ratingBar.getRating()+" starts al conductor "+trasporte.getNombreConductor(), Toast.LENGTH_LONG).show();
    }
}
