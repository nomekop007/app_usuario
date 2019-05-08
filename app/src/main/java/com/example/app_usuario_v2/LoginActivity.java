package com.example.app_usuario_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {




    EditText t_correo, t_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t_correo = findViewById(R.id.txt_correo);
        t_pass = findViewById(R.id.txt_pass);


    }



    public void iniciar(View view) {
        String correo = t_correo.getText().toString();
        String pass = t_pass.getText().toString();

        if (correo.isEmpty()) {
            t_correo.setError("Campo Vacio");
            t_correo.requestFocus();

        } else if (pass.isEmpty()) {
            t_pass.setError("Campo Vacio");
            t_pass.requestFocus();
        } else {


        }
    }

    public void registrar(View view) {
    }
}
