package com.example.alan8.organizzeclone.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alan8.organizzeclone.R;
import com.example.alan8.organizzeclone.activity.model.SaldoAnual;

import java.util.List;

public class AdapterSaldoAnual extends RecyclerView.Adapter<AdapterSaldoAnual.MyViewHolder> {
    List<SaldoAnual> saldoAnuais;
    Context context;

    public AdapterSaldoAnual(List<SaldoAnual> saldoAnuais, Context contexto) {
        this.saldoAnuais = saldoAnuais;
        this.context = contexto;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_saldo_anuidade, parent, false);
        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SaldoAnual saldoAnual = saldoAnuais.get(position);

        holder.textSaldo.setText("Saldo");
        holder.ano.setText(saldoAnual.getAno());
        String valor = String.valueOf(saldoAnual.getSaldo());
        holder.valor.setText(valor);
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorPrimayReceita));

        if (saldoAnual.getSaldo() < 0){
            holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.valor.setText(valor);
        }



    }

    @Override
    public int getItemCount() {
        return saldoAnuais.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ano, textSaldo, valor;

         public MyViewHolder(View itemView) {
             super(itemView);

             ano = itemView.findViewById(R.id.textAdapterTitulo);
             valor = itemView.findViewById(R.id.textAdapterValor);
             textSaldo = itemView.findViewById(R.id.textAdapterCategoria);
         }
     }
}
