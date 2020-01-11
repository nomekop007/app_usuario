package com.example.app_usuario_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity  {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //declarar elementos de drawer
    private DrawerLayout drawerLayout;
    private android.support.v7.widget.Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private View header;

    //elementos de fragmentos
    private FragmentManager fm;
    private FragmentTransaction ft;


    //declarar circulo de cargando
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //iniciar progressDialig
        progressDialog = new ProgressDialog(this);
        configuracionDrawer();
        cargarPerfil();
    }

    private void cargarPerfil() {



            //informacion del usuario que se muestra en el drawer header
            ((TextView) header.findViewById(R.id.nombreUsuario)).setText(user.getDisplayName());
            ((TextView) header.findViewById(R.id.correoElectronico)).setText(user.getEmail());

            //cargar imagen
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .error(R.drawable.error)
                    .placeholder(R.drawable.cargando)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) header.findViewById(R.id.circulo));

    }


    // se inicializa y configura las opciones del drawer
    public void configuracionDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation);
        header = ((NavigationView) findViewById(R.id.navigation)).getHeaderView(0);

        toolbar.setTitle("Mapa");
        //conf de los fragmentos
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        //carga primeramente el mapa
        mapFragment mapFragment = new mapFragment();
        fm.beginTransaction().replace(R.id.mainActivity, mapFragment).commit();


        //configuracion del comportamiento del drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.cerrar:
                     //cuadro de espera
                        progressDialog.setMessage("cerrando sesion...");
                        progressDialog.show();
                        cerrarSesion();

                        break;
                    case R.id.mapa:
                        //carga nuevamente el mapa
                        toolbar.setTitle("Mapa");
                        mapFragment mapFragment = new mapFragment();
                        fm.beginTransaction().replace(R.id.mainActivity, mapFragment).commit();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        break;
                    case R.id.lineas:
                        toolbar.setTitle("Lineas de Trasportes");
                        LineaFragment lineaFragment = new LineaFragment();
                        fm.beginTransaction().replace(R.id.mainActivity, lineaFragment).commit();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        break;
                    case R.id.perfil:
                        toolbar.setTitle("Mi Perfil");
                        PerfilFragment perfilFragment = new PerfilFragment();

                        //enviar parametros
                        Bundle bundle = new Bundle();
                        bundle.putInt("TIPO_LOGEO", 0);
                        perfilFragment.setArguments(bundle);

                        fm.beginTransaction().replace(R.id.mainActivity, perfilFragment).commit();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }




    private void cerrarSesion() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
    }
}