package com.example.app_usuario_v2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {



    public static int TIPO_LOGEO = 0;
    // 1 es logeo normal
    // 2 es logeo con google
    // 3 es logeo con facebook

    //librerias del cliente de google
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;

    //declarar objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    //declarar circulo de cargando
    private ProgressDialog progressDialog;


    public static final int SIGN_IN_CODE = 777;

    EditText t_correo, t_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //iniciar progressDialig
        progressDialog = new ProgressDialog(this);

        logeoConGmail();

    }

            //logearse con cuenta creada
    public void iniciar(View view) {
        t_correo = findViewById(R.id.txt_correo);
        t_pass = findViewById(R.id.txt_pass);

        String correo = t_correo.getText().toString();
        String pass = t_pass.getText().toString();

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

            firebaseAuth.signInWithEmailAndPassword(correo,pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Bienvenido "+user.getEmail(), Toast.LENGTH_SHORT).show();
                                //metodo para redireccionar al main

                                TIPO_LOGEO=1;

                                 goMainScreen(TIPO_LOGEO);
                            }else {
                                Toast.makeText(LoginActivity.this, "nombre o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                             t_pass.setText("");
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


    // inicio de logeo con cuenta gmail
    public void logeoConGmail() {


        signInButton = findViewById(R.id.btn_gmail);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cuadro de espera
                progressDialog.setMessage("iniciando Sesion con Google...");
                progressDialog.show();

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

        if (requestCode == 777) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }

    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            TIPO_LOGEO = 2;
            goMainScreen(TIPO_LOGEO);
        } else {

            Toast.makeText(this, "no se puede iniciar sesion", Toast.LENGTH_LONG).show();

        }
        progressDialog.dismiss();
    }
    private void goMainScreen(int tipo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("TIPO_LOGEO",tipo+"");
        startActivity(intent);

    }
    // fin de logeo
}
