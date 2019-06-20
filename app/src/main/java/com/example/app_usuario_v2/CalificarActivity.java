package com.example.app_usuario_v2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app_usuario_v2.model.Calificacion;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class CalificarActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView patente, nombre, txtcalificar;
    private EditText editReclamo;
    private Button btnCalificar, btnModifcar;
    ImageView img;
    private String IDTrasporte = "";
    private Trasporte trasporte;
    private Calificacion calificacion;

    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    //hacer referencia a la base de datos de firebase


    //llamar a Usuario
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        ratingBar = findViewById(R.id.ratingBar);
        patente = findViewById(R.id.t2_patente);
        nombre = findViewById(R.id.t2_nombre);
        btnCalificar = findViewById(R.id.btn_calificar);
        btnModifcar = findViewById(R.id.btn_modificarCalificacion);
        txtcalificar = findViewById(R.id.txt_calificacion);
        editReclamo = findViewById(R.id.edit_reclamo);
        img = findViewById(R.id.iconos2);

        //mostrar descripcion
        Intent i = getIntent();
        IDTrasporte = i.getStringExtra("idTrasporte");

        //sin calificar
        btnModifcar.setVisibility(View.INVISIBLE);
        txtcalificar.setVisibility(View.INVISIBLE);


        ConfiguracionToolbar();
        BuscarMostrarTrasporte();
        ConfigDeCalificacion();
        actualizarCalificacion();
    }


    private void ConfigDeCalificacion() {


        myDatabase.child("calificacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Calificacion cal = snapshot.getValue(Calificacion.class);

                    if (cal.getIdTrasporte().equals(trasporte.getIdTrasporte())
                            && cal.getIdUsuario().equals(user.getUid())) {

                        ratingBar.setRating(cal.getCalificacion());
                        calificacion = cal;

                        ratingBar.setEnabled(false);
                        btnCalificar.setVisibility(View.INVISIBLE);
                        btnModifcar.setVisibility(View.VISIBLE);
                        txtcalificar.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void ConfiguracionToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calificacion y Reclamos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void actualizarCalificacion() {

        myDatabase.child("calificacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cantidad = 0;
                float suma = 0;
                float calificacionfinal;


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Calificacion cal = snapshot.getValue(Calificacion.class);

                    if (cal.getIdTrasporte().equals(trasporte.getIdTrasporte())) {
                        cantidad++;
                        suma = suma + cal.getCalificacion();
                    }
                }

                DecimalFormat format = new DecimalFormat("#.##");
                calificacionfinal = suma / cantidad;

                format.format(calificacionfinal);

                //actualiza la calificacion del trasporte
                if (suma==0){
                    trasporte.setCalificacion(0);
                }else {
                    trasporte.setCalificacion(calificacionfinal);
                }
                try {
                    myDatabase.child("trasporte").child(trasporte.getIdTrasporte()).setValue(trasporte);
                } catch (Exception x) {
                    Log.e("error", "en modificacion a 0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void BuscarMostrarTrasporte() {

        myDatabase.child("trasporte").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Trasporte tr = snapshot.getValue(Trasporte.class);

                    if (tr.getIdTrasporte().equals(IDTrasporte)) {
                        trasporte = tr;

                        patente.setText(tr.getPatente());
                        nombre.setText(tr.getNombreConductor());

                        Glide.with(getApplicationContext())
                                .load(trasporte.getFotoConductorUrl())
                                .fitCenter()
                                .centerCrop()
                                .circleCrop()
                                .error(R.drawable.error)
                                .placeholder(R.drawable.cargando)
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(img);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }




    public void CalificarConductor(View view) {


        float calificacionUsuario = ratingBar.getRating();

        if (calificacionUsuario > 0) {


            Calificacion calificacion = new Calificacion();

            calificacion.setIdCalificacion(UUID.randomUUID().toString());
            calificacion.setCalificacion(calificacionUsuario);
            calificacion.setIdUsuario(user.getUid());
            calificacion.setIdTrasporte(trasporte.getIdTrasporte());
            //crear calificacion
            myDatabase.child("calificacion").child(calificacion.getIdCalificacion()).setValue(calificacion);


            Toast.makeText(getApplicationContext(), "calificado con "
                    + ratingBar.getRating() + " starts al conductor " + trasporte.getNombreConductor(), Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(getApplicationContext(), "ingrese calificacion!", Toast.LENGTH_LONG).show();
        }


    }

    public void RestaurarCalificacion(View view) {

        //borra calificacion
        myDatabase.child("calificacion").child(calificacion.getIdCalificacion()).setValue(null);

        ratingBar.setEnabled(true);
        ratingBar.setRating(0);
        btnModifcar.setVisibility(View.INVISIBLE);
        txtcalificar.setVisibility(View.INVISIBLE);
        btnCalificar.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "mi calificacion restaurada!", Toast.LENGTH_LONG).show();

    }

    public void RealizarReclamo(View view) {

        String ElReclamo = editReclamo.getText().toString();

        if (!ElReclamo.isEmpty()) {

            Log.e("reclamo: ", ElReclamo);

            //extraer fecha y hora del sistema
            long date = System.currentTimeMillis();
            SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String fechaHora = d.format(date);
            Log.e("ms : ", fechaHora);


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
}
