package com.example.app_usuario_v2;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.app_usuario_v2.model.LineaTrasporte;
import java.util.List;

public class adaptadorLineas extends RecyclerView.Adapter<adaptadorLineas.lineaviewholder> {


    List<LineaTrasporte> lineas;

    public adaptadorLineas(List<LineaTrasporte> lineas) {
        this.lineas = lineas;
    }

    @NonNull
    @Override
    public adaptadorLineas.lineaviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linea_card,viewGroup,false);
        lineaviewholder lineaviewholder = new lineaviewholder(v);
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


        private TextView itemLinea, itemAgencia, itemID;
        public lineaviewholder(@NonNull View itemView) {
            super(itemView);
            itemID = itemView.findViewById(R.id.item_id);
            itemLinea = itemView.findViewById(R.id.item_linea);
            itemAgencia = itemView.findViewById(R.id.item_agencia);

        }
    }
}
