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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app_usuario_v2.model.Usuario;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity  {


    public static int TIPO_LOGEO = 0;



    //declarar objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

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

        //extraer tipo de logeo
        Intent i = getIntent();
        TIPO_LOGEO = Integer.parseInt(i.getStringExtra("TIPO_LOGEO"));

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
                        bundle.putInt("TIPO_LOGEO", TIPO_LOGEO);
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
        firebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginInSreen();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
    }
}