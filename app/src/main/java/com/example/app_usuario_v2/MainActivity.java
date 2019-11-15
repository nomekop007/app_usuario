package com.example.app_usuario_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


public class MainActivity extends AppCompatActivity  {




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
                        progressDialog.setMessage("cerrando sesion");
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









    private void goLoginInSreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(MainActivity.this, "sesion cerrada!", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    private void cerrarSesion() {
        goLoginInSreen();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
    }
}