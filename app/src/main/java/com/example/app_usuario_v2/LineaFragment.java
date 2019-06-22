package com.example.app_usuario_v2;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.app_usuario_v2.model.Agencia;
import com.example.app_usuario_v2.model.LineaTrasporte;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


public class LineaFragment extends Fragment {


    public LineaFragment() {
        // Required empty public constructor
    }


    private DatabaseReference myDatabase;

    private List<Agencia> AgenciaList = new ArrayList<>();
    private List<LineaTrasporte> TrasporteList = new ArrayList<>();

    private RecyclerView recyclerLineas;
    private adaptadorLineas adaptador;

    //elementos de fragmentos
    private FragmentManager fm;
    private FragmentTransaction ft;

    //declarar circulo de cargando
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_linea, container, false);
        myDatabase = FirebaseDatabase.getInstance().getReference();

        //iniciar progressDialig
        progressDialog = new ProgressDialog(getContext());

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        octenerLineasTrasporte();

        recyclerLineas = v.findViewById(R.id.list_recycler);
        recyclerLineas.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new adaptadorLineas(TrasporteList);

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sacar id del objeto seleccionado
                int id = TrasporteList.get(recyclerLineas.getChildAdapterPosition(v)).getIdLinea();
                String linea = TrasporteList.get(recyclerLineas.getChildAdapterPosition(v)).getNombreLinea();
                mapFragment mapFragment = new mapFragment();

             //   Toast.makeText(getContext(),"recorrido "+linea , Toast.LENGTH_SHORT).show();

                //enviar parametros
                Bundle bundle = new Bundle();
                bundle.putInt("ID", id);
                bundle.putString("linea", linea);
                mapFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.frag_linea, mapFragment).commit();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            }
        });

        recyclerLineas.setAdapter(adaptador);
        return v;
    }

    private void octenerLineasTrasporte() {

        progressDialog.setMessage("cargando lista...");
        progressDialog.show();

        myDatabase.child("lineaTrasporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TrasporteList.removeAll(TrasporteList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LineaTrasporte linea = snapshot.getValue(LineaTrasporte.class);

                    /*
                    for (Agencia a : AgenciaList) {
                        if (a.getIdAgencia().equals(linea.getIdAgencia())) {
                            linea.setIdAgencia(a.getNombreAgencia() + "");
                        }
                    }*/

                    if (linea.getIdAgencia().equals("A1")) {
                        linea.setIdAgencia("Taxutal");
                    } else if (linea.getIdAgencia().equals("A2")) {
                        linea.setIdAgencia("Sotratal");
                    } else {
                        linea.setIdAgencia("Abate Molina");
                    }


                    TrasporteList.add(linea);

                }
                adaptador.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        progressDialog.dismiss();
    }





    /*
    private void octenerAgencia() {
        myDatabase.child("agencia").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Agencia agencia = snapshot.getValue(Agencia.class);
                    AgenciaList.add(agencia);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


}
