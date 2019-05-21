package com.example.app_usuario_v2;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class adaptadorLineas extends RecyclerView.Adapter<adaptadorLineas.lineaviewholder> {


    List<LineaTrasporte> lineas;


    //elementos de fragmentos
    private FragmentManager fm;
    private FragmentTransaction ft;
    private mapFragment mf;
    private LineaFragment lf;

    public adaptadorLineas(List<LineaTrasporte> lineas) {
        this.lineas = lineas;
    }

    @NonNull
    @Override
    public lineaviewholder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
      View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linea_card,viewGroup,false);
        final lineaviewholder lineaviewholder = new lineaviewholder(v);

        //conf de los fragmentos

        mf = new mapFragment();
        lf = new LineaFragment();
        fm = lf.getFragmentManager();
        ft = fm.beginTransaction();

        //onclick
            lineaviewholder.itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ft.replace(R.id.frag_linea,mf).commit();


                    //extraer infromacion
                   Log.e("lineas : ", lineas.get(lineaviewholder.getAdapterPosition()).getIdLinea()+"");
                    Log.e("lineas : ", lineas.get(lineaviewholder.getAdapterPosition()).getNombreLinea()+"");
                    Log.e("lineas : ", lineas.get(lineaviewholder.getAdapterPosition()).getIdAgencia()+"");




                }
            });


            return  lineaviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull adaptadorLineas.lineaviewholder lineaviewholder, int i) {
        LineaTrasporte lineaTrasporte = lineas.get(i);

        lineaviewholder.itemID.setText(lineaTrasporte.getIdLinea()+"");
        lineaviewholder.itemLinea.setText(lineaTrasporte.getNombreLinea());
        lineaviewholder.itemAgencia.setText(lineaTrasporte.getIdAgencia()+"");


    }

    @Override
    public int getItemCount() {
        return lineas.size();
    }

    public static class lineaviewholder extends RecyclerView.ViewHolder{

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
