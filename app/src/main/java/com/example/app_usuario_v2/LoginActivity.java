package com.example.app_usuario_v2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.lang.reflect.Array;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    public static int TIPO_LOGEO = 0;
    // 1 es logeo normal
    // 2 es logeo con google
    // 3 es logeo con facebook

    //variables del cliente de google
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;

    //variables del cliente facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;


    //declarar objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    //declarar circulo de cargando
    private ProgressDialog progressDialog;


    public static final int SIGN_IN_CODE = 777;

    EditText t_correo;
    TextInputLayout t_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //cierra todas las sesion cada vez que se inicia la app
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();


        //iniciar progressDialig
        progressDialog = new ProgressDialog(this);

        logeoConGmail();
        logeoConFacebook();



            //escucha si es que se habre alguna instancia de logeo
        firebaseAuth = firebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    goMainScreen(TIPO_LOGEO);
                }
                progressDialog.dismiss();
            }
        };
    }

    //logearse con cuenta creada
    public void iniciar(View view) {
        t_correo = findViewById(R.id.txt_correo);
        t_pass = findViewById(R.id.txt_pass);

        String correo = t_correo.getText().toString();
        String pass = t_pass.getEditText().getText().toString().trim();

        if (correo.isEmpty()) {
            t_correo.setError("Campo Vacio");
            t_correo.requestFocus();

        } else if (pass.isEmpty()) {
            t_pass.setError("Campo Vacio");
            t_pass.requestFocus();
        } else {

            //cuadro de espera
            progressDialog.setMessage("Validando...");
            progressDialog.show();


            //inicializar el objeto firebaseAuth
            firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                //metodo para redireccionar al main

                                TIPO_LOGEO = 1;

                                goMainScreen(TIPO_LOGEO);
                            } else {
                                Toast.makeText(LoginActivity.this, "nombre o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                                t_pass.getEditText().setText("");
                                t_pass.setError(null);
                            }

                            progressDialog.dismiss();
                        }

                    });

        }
    }


    public void registrar(View view) {
        Intent intent = new Intent(this, RegistrarActivity.class);
        startActivity(intent);
    }




    public void logeoConFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.btn_facebook);
       // loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.setPermissions(Arrays.asList("email"));

        //se oprime botton de facebook
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                //cuadro de espera
                progressDialog.setMessage("iniciando Sesion con Facebook...");
                progressDialog.show();
                TIPO_LOGEO = 3;
                hardleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "se cancelo la operacion", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "ocurrio un error al ingresar", Toast.LENGTH_LONG).show();
            }
        });



    }

    private void hardleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());


        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "ocurrio un error de login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


//fin de logeo facebook








    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }










    // inicio de logeo con cuenta gmail
    public void logeoConGmail() {

        signInButton = findViewById(R.id.btn_gmail);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        //se oprime botton de google
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cuadro de espera
                progressDialog.setMessage("iniciando Sesion con Google...");
                progressDialog.show();
                TIPO_LOGEO = 2;

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResultGoogle(result);

        }

        //metodo de facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResultGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {

             firebaseAuthWithGoogle(result.getSignInAccount());

        } else {

            Toast.makeText(this, "Sesion cancelada", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "ocurrio un error de login", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }


    // fin de logeo google

    private void goMainScreen(int tipo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("TIPO_LOGEO", tipo + "");
        startActivity(intent);
        progressDialog.dismiss();
    }


}
