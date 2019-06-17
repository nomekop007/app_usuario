package com.example.app_usuario_v2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_usuario_v2.model.Reclamo;
import com.example.app_usuario_v2.model.Trasporte;
import com.example.app_usuario_v2.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class CalificarActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView descripcion;
    private EditText editReclamo;
    private String TrasporteDescripcion = "";
    private Trasporte trasporte;

    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    //hacer referencia a la base de datos de firebase


    //llamar al posible usuario
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);
        firebaseAuth = FirebaseAuth.getInstance();

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

    private void ConfiguracionToolbar() {
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

                    Trasporte tr = snapshot.getValue(Trasporte.class);


                    //si no es -1 significa que lo encontro
                    String patente = tr.getPatente();
                    int resultado = TrasporteDescripcion.indexOf(patente);
                    if (resultado != -1) {

                        //guarda el trasporte encontrado
                        trasporte = tr;
                        Log.e("funciono ", trasporte.getNombreConductor());
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

    public void CalificarConductor(View view) {


        float calificacionTotal = trasporte.getCalificacion();
        float calificacionUsuario = ratingBar.getRating();

       trasporte.setCalificacion((calificacionTotal+calificacionUsuario)/2);

       /*
        myDatabase.child("trasporte").child(trasporte.getIdTrasporte()).setValue(trasporte);
        */

        Toast.makeText(getApplicationContext(), "calificado con "
                +ratingBar.getRating()+" starts al conductor "+trasporte.getNombreConductor(), Toast.LENGTH_LONG).show();


        //pendiente

        finish();

    }

    public void RealizarReclamo(View view) {

        editReclamo = findViewById(R.id.edit_reclamo);
        String ElReclamo = editReclamo.getText().toString();

        if (!ElReclamo.isEmpty()) {

            Log.e("reclamo: ", ElReclamo);

            //extraer fecha y hora del sistema
            long date = System.currentTimeMillis();
            SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String fechaHora = d.format(date);
            Log.e("ms : ", fechaHora);

            //llamar a Usuario
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            Reclamo reclamo = new Reclamo();

            reclamo.setIdReclamo(UUID.randomUUID().toString());
            reclamo.setReclamo(ElReclamo);
            reclamo.setIdTrasporte(trasporte.getIdTrasporte());
            reclamo.setFechaHora(fechaHora);
            reclamo.setCorreoUsuario(user.getEmail());


            //crear reclamo
            myDatabase.child("reclamo").child(reclamo.getIdReclamo()).setValue(reclamo);

            Toast.makeText(getApplicationContext(), "Reclamo enviado!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "ingrese algun comentario", Toast.LENGTH_LONG).show();
        }

    }


}
