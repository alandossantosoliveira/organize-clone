package com.example.alan8.organizzeclone.activity.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alan8.organizzeclone.R;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutModel = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_recyclerview, parent, false
        );
        return new MyViewHolder(layoutModel);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textoValor.setText("500");
        holder.textoDescricao.setText("salario mensal");
        holder.textoCategoria.setText("salario");
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView textoCategoria, textoDescricao, textoValor;

        public MyViewHolder(View itemView) {
            super(itemView);
            textoCategoria = itemView.findViewById(R.id.textCategoria);
            textoDescricao = itemView.findViewById(R.id.textDescricao);
            textoValor = itemView.findViewById(R.id.textValor);
        }
    }

}
