package com.example.app_usuario_v2;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);


        img = v.findViewById(R.id.iconosPerfiluser);
        nombre = v.findViewById(R.id.txtPerfil_nombreCompleto);
        correo = v.findViewById(R.id.txtPerfil_correo);
        direccion = v.findViewById(R.id.txtPerfil_direccion);
        C_nueva = v.findViewById(R.id.txtPerfil_NuevaContraseña);
        C_repetir = v.findViewById(R.id.txtPerfil_RepetirContraseña);
        btn = v.findViewById(R.id.btPerfil_cambiar);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        MostrarDatosUsuario();
        BotonGuardarCambios();
        return v;
    }

    public void BotonGuardarCambios() {
        //evento botton
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validar todos los campos
                if (!validarNombre() | !validarCorreo() | !validarDireccion()) {
                    return;
                }

                if (TipoLogeo == 1) {

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

                                    }
                                }
                            });


                    //actualizar Contraseña
                    String T = C_nueva.getEditText().getText().toString().trim();
                    String R = C_repetir.getEditText().getText().toString().trim();
                    if (!T.isEmpty()) {
                        //si la caja de texto repetir esta vacia arroja mensaje en rojo
                        if (!validarC_repetir()) {
                            return;
                        } else {
                            //pregunta si las dos contraseñas son iguales
                            if (T.equals(R)) {
                                user.updatePassword(C_nueva.getEditText().getText().toString().trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "contraseña Cambiada!", Toast.LENGTH_LONG).show();
                                                    C_nueva.getEditText().setText("");
                                                    C_repetir.getEditText().setText("");
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
                    }


                } else {

                    //logeo con cuenta de facebook o google
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

                Toast.makeText(getContext(), "cambios guardados!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void MostrarDatosUsuario() {
        //pregunta si llego una varible (siempre llega)
        if (getArguments() != null) {


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

}
