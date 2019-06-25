package com.example.app_usuario_v2;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.app_usuario_v2.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {


    public PerfilFragment() {
        // Required empty public constructor
    }


    //elementos de fragmentos
    private FragmentManager fm;
    private FragmentTransaction ft;

    private ImageView img;
    private Button btn;
    private TextInputLayout nombre, correo, direccion, C_nueva, C_repetir;


    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    //hacer referencia a la base de datos de firebase


    //llamar a Usuario
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Usuario usuario;
    private int TipoLogeo;


    //declarar circulo de cargando
    private ProgressDialog progressDialog;

    private CheckBox opcion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        //iniciar progressDialig
        progressDialog = new ProgressDialog(getContext());

        img = v.findViewById(R.id.iconosPerfiluser);
        nombre = v.findViewById(R.id.txtPerfil_nombreCompleto);
        correo = v.findViewById(R.id.txtPerfil_correo);
        direccion = v.findViewById(R.id.txtPerfil_direccion);
        C_nueva = v.findViewById(R.id.txtPerfil_NuevaContraseña);
        C_repetir = v.findViewById(R.id.txtPerfil_RepetirContraseña);
        btn = v.findViewById(R.id.btPerfil_cambiar);
        opcion = v.findViewById(R.id.opcion);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        MostrarDatosUsuario();
        CheckboxOpcion();
        BotonGuardarCambios();
        return v;
    }

    public void CheckboxOpcion() {
        opcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opcion.isChecked()) {
                    nombre.setEnabled(false);
                    correo.setEnabled(false);
                    direccion.setEnabled(false);

                    C_nueva.setEnabled(true);
                    C_repetir.setEnabled(true);
                } else {
                    nombre.setEnabled(true);
                    correo.setEnabled(true);
                    direccion.setEnabled(true);

                    C_nueva.setEnabled(false);
                    C_repetir.setEnabled(false);
                }

            }
        });
    }


    public void BotonGuardarCambios() {
        //evento botton
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pregunta si es un logeo normal o no
                if (TipoLogeo == 1) {

                    if (!opcion.isChecked()) {
                        ActualizarDatos();
                    } else {
                        ActualizarContraseña();
                    }


                } else {
                    //logeo con cuenta de facebook o google


                    //validar todos los campos
                    if (!validarNombre() | !validarCorreo() | !validarDireccion()) {
                        return;
                    }


                    Usuario u = new Usuario();
                    u.setIdUsuario(user.getUid());
                    u.setNombreCompleto(user.getDisplayName());
                    u.setCorreoElectronico(user.getEmail());
                    u.setNombreUsuario(user.getDisplayName());
                    u.setDireccion(direccion.getEditText().getText().toString().trim());
                    u.setContraseña("*********");

                    //actualizar Usuario
                    myDatabase.child("usuario").child(u.getIdUsuario()).setValue(u);

                }
            }

        });

    }


    public void ActualizarDatos() {


        //validar todos los campos
        if (!validarNombre() | !validarCorreo() | !validarDireccion()) {
            return;
        }

        //cuadro de espera
        progressDialog.setMessage("Validando datos...");
        progressDialog.show();
        //actualizar Email
        user.updateEmail(correo.getEditText().getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            usuario.setCorreoElectronico(user.getEmail());
                            usuario.setNombreCompleto(nombre.getEditText().getText().toString().trim());
                            usuario.setDireccion(direccion.getEditText().getText().toString().trim());

                            //actualizar Usuario
                            myDatabase.child("usuario").child(usuario.getIdUsuario()).setValue(usuario);
                            Toast.makeText(getContext(), "datos actualizados!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        } else {
                            Toast.makeText(getContext(), "error en la operacion!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }

                    }
                });
    }

    public void ActualizarContraseña() {
        //si la caja de texto  estan vacia arroja mensaje en rojo
        if (!validarC_repetir() | !validarC_Nueva()) {
            return;
        }

        //pregunta si las dos contraseñas son iguales
        String T = C_nueva.getEditText().getText().toString().trim();
        String R = C_repetir.getEditText().getText().toString().trim();
        if (T.equals(R)) {

            //cuadro de espera
            progressDialog.setMessage("Validando contraseña...");
            progressDialog.show();

            user.updatePassword(C_nueva.getEditText().getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                C_nueva.getEditText().setText("");
                                C_repetir.getEditText().setText("");
                                Toast.makeText(getContext(), "contraseña Cambiada!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                return;
                            } else {
                                C_nueva.getEditText().setText("");
                                C_repetir.getEditText().setText("");

                                Toast.makeText(getContext(), "error al cambiar la contraseña!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                return;
                            }

                        }
                    });


        } else {
            Toast.makeText(getContext(), "las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            C_nueva.getEditText().setText("");
            C_repetir.getEditText().setText("");
            return;
        }
    }


    public void MostrarDatosUsuario() {
        //pregunta si llego una varible (siempre llega)
        if (getArguments() != null) {

            C_nueva.setEnabled(false);
            C_repetir.setEnabled(false);

            TipoLogeo = getArguments().getInt("TIPO_LOGEO");
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .fitCenter()
                    .centerCrop()
                    .circleCrop()
                    .error(R.drawable.user)
                    .placeholder(R.drawable.user)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);

            BuscarUsuario();
            if (TipoLogeo != 1) {
                opcion.setVisibility(View.INVISIBLE);
                nombre.getEditText().setText(user.getDisplayName());
                nombre.setEnabled(false);

                correo.getEditText().setText(user.getEmail());
                correo.setEnabled(false);

                C_nueva.getEditText().setText("**********");
                C_nueva.setEnabled(false);

                C_repetir.getEditText().setText("**********");
                C_repetir.setEnabled(false);

            }
        }
    }

    public void BuscarUsuario() {
        myDatabase.child("usuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usuario = snapshot.getValue(Usuario.class);

                    if (usuario.getIdUsuario().equals(user.getUid())) {

                        nombre.getEditText().setText(usuario.getNombreCompleto());
                        correo.getEditText().setText(usuario.getCorreoElectronico());
                        direccion.getEditText().setText(usuario.getDireccion());
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private boolean validarCorreo() {
        String T = correo.getEditText().getText().toString().trim();
        if (T.isEmpty()) {
            correo.setError("Campo Vacio");
            return false;
        } else {
            correo.setError(null);
            return true;
        }

    }

    private boolean validarDireccion() {
        String T = direccion.getEditText().getText().toString().trim();
        if (T.isEmpty()) {
            direccion.setError("Campo Vacio");
            return false;
        } else {
            direccion.setError(null);
            return true;
        }

    }

    private boolean validarC_repetir() {
        String T = C_repetir.getEditText().getText().toString().trim();
        if (T.isEmpty()) {
            C_repetir.setError("Campo Vacio");
            return false;
        } else {
            C_repetir.setError(null);
            return true;
        }

    }

    private boolean validarC_Nueva() {
        String T = C_nueva.getEditText().getText().toString().trim();
        if (T.isEmpty()) {
            C_nueva.setError("Campo Vacio");
            return false;
        } else {
            C_nueva.setError(null);
            return true;
        }

    }

}
