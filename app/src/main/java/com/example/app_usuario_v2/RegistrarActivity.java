package com.example.app_usuario_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.app_usuario_v2.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class RegistrarActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;

    private TextInputLayout nombre;
    private TextInputLayout usuario;
    private TextInputLayout correo;
    private TextInputLayout direccion;
    private TextInputLayout contraseña;
    private TextInputLayout confContraseña;


    //declarar objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    //hacer referencia a la base de datos de firebase
    DatabaseReference mydatabasereference = FirebaseDatabase.getInstance().getReference();


    //declarar circulo de cargando
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //inicializar el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //referenciar los TextInput
        nombre = findViewById(R.id.txt_nombreCompleto);
        usuario = findViewById(R.id.txt_usuario);
        correo = findViewById(R.id.txt_correo);
        direccion = findViewById(R.id.txt_direccion);
        contraseña = findViewById(R.id.txt_contraseña);
        confContraseña = findViewById(R.id.txt_confContraseña);


        //iniciar progressDialig
        progressDialog = new ProgressDialog(this);

        ConfigurarToolbar();


    }

    public void ConfigurarToolbar() {
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Registrar");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    //boton de flecha atras de toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void crearUsuario(View view) {


        //validar todos los campos
        if (!validarNombre() | !validarUsuario() | !validarGmail() | !validarDireccion() | !validarContraseña()) {
            return;
        }


        //verifica que son iguales las contraseñas
        if (!contraseña.getEditText().getText().toString().trim().equals(confContraseña.getEditText().getText().toString().trim())) {

            Toast.makeText(RegistrarActivity.this, "contraseña inconsistente!", Toast.LENGTH_SHORT).show();
            contraseña.getEditText().setText("");
            confContraseña.getEditText().setText("");
        } else {

            //cuadro de espera
            progressDialog.setMessage("Realizando Registro en linea...");
            progressDialog.show();

            String email = correo.getEditText().getText().toString().trim();
            final String pass = contraseña.getEditText().getText().toString().trim();

            //creando nuevo usuario con gmail y pass
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //si se registro exitosamente
                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                            //comprobante
                                Log.e("display name : ",user.getDisplayName()+"");
                                Log.e("email : ",user.getEmail()+"");
                                Log.e("providerid : ",user.getProviderId()+"");
                                Log.e("UID : ",user.getUid()+"");
                                Log.e("numero : ",user.getPhoneNumber()+"");


                                //crear objeto usuario para firebase

                                Usuario u = new Usuario();
                                u.setIdUsuario(user.getUid());
                                u.setNombreCompleto(nombre.getEditText().getText().toString().trim());
                                u.setNombreUsuario(usuario.getEditText().getText().toString().trim());
                                u.setDireccion(direccion.getEditText().getText().toString().trim());
                                u.setTipoCuenta("free");
                                u.setCorreoElectronico(user.getEmail());
                                //corregir seguridad a futuro
                                u.setContraseña(pass);

                                //crea el usuario en la base de datos de firebase realtime
                                mydatabasereference.child("usuario").child(user.getUid()).setValue(u);

                                     cerrar();

                           } else {

                                //si el usuario ya existe
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegistrarActivity.this, "Error el Usuario ya existe", Toast.LENGTH_SHORT).show();

                                } else {
                                    //en caso contrario
                                    Toast.makeText(RegistrarActivity.this, "Error de Registro!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });

        }


    }

    private void cerrar(){
        Intent intent = new Intent(this, LoginActivity.class);
        Toast.makeText(RegistrarActivity.this, "Registro exitoso! de Usuario : " + usuario.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
       onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean validarNombre() {
        String Tnombre = nombre.getEditText().getText().toString().trim();
        if (Tnombre.isEmpty()) {
            nombre.setError("Campo Vacio");
            return false;
        } else {
            nombre.setError(null);
            return true;
        }

    }

    private boolean validarUsuario() {
        String Tusuario = usuario.getEditText().getText().toString().trim();

        if (Tusuario.isEmpty()) {
            usuario.setError("Campo Vacio");
            return false;
        } else {
            usuario.setError(null);
            return true;
        }
    }

    private boolean validarGmail() {

        String Tcorreo = correo.getEditText().getText().toString().trim();
        if (Tcorreo.isEmpty()) {
            correo.setError("Campo Vacio");
            return false;
        } else {
            correo.setError(null);
            return true;
        }
    }

    private boolean validarDireccion() {
        String Tdireccion = direccion.getEditText().getText().toString().trim();
        if (Tdireccion.isEmpty()) {
            direccion.setError("Campo Vacio");
            return false;
        } else {
            direccion.setError(null);
            return true;
        }
    }

    private boolean validarContraseña() {

        String Tcontraseña = contraseña.getEditText().getText().toString().trim();
        if (Tcontraseña.isEmpty()) {
            contraseña.setError("Campo Vacio");
            return false;
        } else {
            contraseña.setError(null);
            return true;
        }
    }

}
