package com.example.app_usuario_v2;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static int TIPO_LOGEO = 0;
    // 1 es logeo normal
    // 2 es logeo con google
    // 3 es logeo con facebook


    //clase de login gmail
    private GoogleApiClient googleApiClient;

    //hacer referencia a la base de datos de firebase
    DatabaseReference mydatabasereference = FirebaseDatabase.getInstance().getReference();

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //extraer tipo de logeo
        Intent i = getIntent();
        TIPO_LOGEO = Integer.parseInt(i.getStringExtra("TIPO_LOGEO"));

        //iniciar progressDialig
        progressDialog = new ProgressDialog(this);


        configuracionDrawer();

        if (TIPO_LOGEO == 2) {
            configuraciondeLoginGmail();
        } else if (TIPO_LOGEO == 3) {
            configuracionLoginFacebook();
        } else {
            configurardeLoginNormal();

        }


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
        fm.beginTransaction().replace(R.id.mainActivity,mapFragment).commit();


        //configuracion del comportamiento del drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.cerrar:
                        //cuadro de espera
                        progressDialog.setMessage("cerrando sesion");
                        progressDialog.show();

                        if (TIPO_LOGEO == 2) {
                            cerrarSesionGoogle();
                        } else if (TIPO_LOGEO == 3) {
                            cerrarSesionFacebook();
                        } else {
                            cerrarSesion();
                        }

                        break;
                    case R.id.mapa:
                        //carga nuevamente el mapa
                        toolbar.setTitle("Mapa");
                        mapFragment mapFragment = new mapFragment();
                        fm.beginTransaction().replace(R.id.mainActivity,mapFragment).commit();
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        break;
                    case  R.id.lineas:
                        toolbar.setTitle("Lineas de Trasportes");
                        LineaFragment lineaFragment = new LineaFragment();
                        fm.beginTransaction().replace(R.id.mainActivity,lineaFragment).commit();
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


    //metodo para logearse con cuenta normal
    public void configurardeLoginNormal() {
        //inicializar el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.e("display name : ", user.getDisplayName() + "");
        Log.e("email : ", user.getEmail() + "");
        Log.e("providerid : ", user.getProviderId() + "");
        Log.e("UID : ", user.getUid() + "");
        Log.e("numero : ", user.getPhoneNumber() + "");


        mydatabasereference.child("usuario").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario perfil = dataSnapshot.getValue(Usuario.class);

                //mostrar los datos del perfil en el header del drawer

                //informacion del usuario que se muestra en el drawer header
                ((TextView) header.findViewById(R.id.usuario)).setText(perfil.getNombreUsuario() + "");
                ((TextView) header.findViewById(R.id.nombre)).setText(perfil.getNombreCompleto() + "");
                ((TextView) header.findViewById(R.id.gmail)).setText(perfil.getCorreoElectronico() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }


    //metodo para logearse con cuenta facebook
    private void configuracionLoginFacebook() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //informacion del usuario que se muestra en el drawer header
            ((TextView) header.findViewById(R.id.nombre)).setText(user.getDisplayName());
            ((TextView) header.findViewById(R.id.gmail)).setText(user.getEmail());
            ((TextView) header.findViewById(R.id.code)).setText("Cuenta Facebook");

            //cargar imagen
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .error(R.drawable.error)
                    .placeholder(R.drawable.cargando)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) header.findViewById(R.id.imagenUser));

        } else {
            goLoginInSreen();
        }
    }

    private void cerrarSesionFacebook() {

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginInSreen();
        progressDialog.dismiss();
    }


    // metodos para logearse con cuenta gmail
    public void configuraciondeLoginGmail() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (TIPO_LOGEO == 2) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handSignInResult(result);

            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handSignInResult(googleSignInResult);
                    }
                });

            }
        }
    }

    //metodo donde se extrae al usuario logeado
    private void handSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            //informacion del usuario que se muestra en el drawer header
            ((TextView) header.findViewById(R.id.nombre)).setText(account.getDisplayName());
            ((TextView) header.findViewById(R.id.gmail)).setText(account.getEmail());
            ((TextView) header.findViewById(R.id.code)).setText("Cuenta Google");

            //cargar imagen
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .error(R.drawable.error)
                    .placeholder(R.drawable.cargando)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) header.findViewById(R.id.imagenUser));

        } else {
            goLoginInSreen();
        }

    }

    private void goLoginInSreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(MainActivity.this, "sesion cerrada!", Toast.LENGTH_SHORT).show();

        startActivity(intent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void cerrarSesionGoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ((status.isSuccess())) {

                    //agregado?
                    firebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    goLoginInSreen();
                } else {
                    Toast.makeText(getApplicationContext(), "no se pudo cerrar sesion", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }
    // fin de metodos

    private void cerrarSesion() {
        firebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginInSreen();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        if (TIPO_LOGEO == 2) {
            cerrarSesionGoogle();
        } else if (TIPO_LOGEO == 3) {
            cerrarSesionFacebook();
        } else {
            cerrarSesion();
        }

    }
}
