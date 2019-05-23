package com.example.app_usuario_v2;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_usuario_v2.model.LineaTrasporte;

import java.util.List;

public class adaptadorLineas extends RecyclerView.Adapter<adaptadorLineas.lineaviewholder> implements View.OnClickListener {


    List<LineaTrasporte> lineas;

    //elementos de fragmentos
    private FragmentManager fm;
    private FragmentTransaction ft;
    private mapFragment mapFragment;
    private LineaFragment lineaFragment;

    //evento onClick
    private View.OnClickListener Listener;


    public adaptadorLineas(List<LineaTrasporte> lineas) {
        this.lineas = lineas;
    }

    @NonNull
    @Override
    public lineaviewholder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linea_card, viewGroup, false);
         v.setOnClickListener(this);
        return new lineaviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptadorLineas.lineaviewholder lineaviewholder, int i) {
        LineaTrasporte lineaTrasporte = lineas.get(i);

        lineaviewholder.itemID.setText(lineaTrasporte.getIdLinea() + "");
        lineaviewholder.itemLinea.setText(lineaTrasporte.getNombreLinea());
        lineaviewholder.itemAgencia.setText(lineaTrasporte.getIdAgencia() + "");


    }


    @Override
    public int getItemCount() {
        return lineas.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.Listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (Listener != null) {
            Listener.onClick(v);
        }
    }

    public static class lineaviewholder extends RecyclerView.ViewHolder {

        private RelativeLayout itemContainer;
        private TextView itemLinea, itemAgencia, itemID;

        public lineaviewholder(@NonNull View itemView) {
            super(itemView);
            itemID = itemView.findViewById(R.id.item_id);
            itemLinea = itemView.findViewById(R.id.item_linea);
            itemAgencia = itemView.findViewById(R.id.item_agencia);
            itemContainer = itemView.findViewById(R.id.item_container);

        }
    }
}
