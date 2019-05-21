package com.example.app_usuario_v2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    RecyclerView recyclerLineas;
    List<LineaTrasporte> TrasporteList;

    adaptadorLineas adaptador;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_linea, container, false);
        TrasporteList = new ArrayList<>();
        octenerLineasTrasporte();


        recyclerLineas = v.findViewById(R.id.list_recycler);
        recyclerLineas.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new adaptadorLineas(TrasporteList);
        recyclerLineas.setAdapter(adaptador);





        return v;


    }

    private void octenerLineasTrasporte() {
        myDatabase = FirebaseDatabase.getInstance().getReference();

        myDatabase.child("lineaTrasporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TrasporteList.removeAll(TrasporteList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LineaTrasporte linea = snapshot.getValue(LineaTrasporte.class);
                    TrasporteList.add(linea);
                }
                adaptador.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
