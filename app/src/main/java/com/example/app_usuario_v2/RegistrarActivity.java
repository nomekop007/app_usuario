package com.example.app_usuario_v2;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RegistrarActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbar;

    private TextInputLayout nombre;
    private TextInputLayout usuario;
    private TextInputLayout correo;
    private TextInputLayout direccion;
    private TextInputLayout contraseña;
    private TextInputLayout confContraseña;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        nombre = findViewById(R.id.txt_nombreCompleto);
        usuario = findViewById(R.id.txt_usuario);
        correo = findViewById(R.id.txt_correo);
        direccion = findViewById(R.id.txt_direccion);
        contraseña = findViewById(R.id.txt_contraseña);
        confContraseña = findViewById(R.id.txt_confContraseña);

        ConfigurarToolbar();


    }

    public void ConfigurarToolbar() {
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Registrar");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void crearUsuario(View view) {


        //validar todos los campos
        if (!validarNombre() | !validarUsuario() | !validarGmail() | !validarDireccion() | !validarContraseña()){
        return;
        }



            //verifica que son iguales las contraseñas
        if (!contraseña.getEditText().getText().toString().trim().equals(confContraseña.getEditText().getText().toString().trim())){

            contraseña.getEditText().setText("");
            Toast.makeText(RegistrarActivity.this, "contraseña inconsistente!", Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(RegistrarActivity.this, "Registrado", Toast.LENGTH_SHORT).show();
            //operacion


        }



    }


    private boolean validarNombre(){
        String Tnombre = nombre.getEditText().getText().toString().trim();
        if (Tnombre.isEmpty()) {
            nombre.setError("Campo Vacio");
            return false;
        }else {
            nombre.setError(null);
            return true;
        }

    }
    private boolean validarUsuario(){
        String Tusuario = usuario.getEditText().getText().toString().trim();

        if (Tusuario.isEmpty()) {
            usuario.setError("Campo Vacio");
            return false;
        }else {
            usuario.setError(null);
            return true;
        }
    }
    private boolean validarGmail(){

        String Tcorreo = correo.getEditText().getText().toString().trim();
        if (Tcorreo.isEmpty()) {
            correo.setError("Campo Vacio");
        return false;
        }else {
            correo.setError(null);
            return true;
        }
    }
    private boolean validarDireccion(){
        String Tdireccion = direccion.getEditText().getText().toString().trim();
        if (Tdireccion.isEmpty()) {
            direccion.setError("Campo Vacio");
        return false;
        }else {
            direccion.setError(null);
            return true;
        }
    }
    private boolean validarContraseña(){

        String Tcontraseña = contraseña.getEditText().getText().toString().trim();
        if (Tcontraseña.isEmpty()) {
            contraseña.setError("Campo Vacio");
            return false;
        }else {
            contraseña.setError(null);
            return true;
        }
    }

}
