package com.example.alan8.organizzeclone.activity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.alan8.organizzeclone.R;
import com.example.alan8.organizzeclone.activity.adapter.AdapterSaldoAnual;
import com.example.alan8.organizzeclone.activity.config.ConfiguracaoFirebase;
import com.example.alan8.organizzeclone.activity.helper.Base64Custom;
import com.example.alan8.organizzeclone.activity.model.Movimentacao;
import com.example.alan8.organizzeclone.activity.model.SaldoAnual;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SaldoAnualActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterSaldoAnual adapter;
    private List<SaldoAnual> listSaldoAnual = new ArrayList<>();
    private FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference myRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener valueEventListener;
    private DatabaseReference saldoAnualRef;


    private Double receitaMensalTotal=0.00, despesaMensalTotal=0.00, valorMensalTotal=0.00,
    valorAnualTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_anual);
        getSupportActionBar().setTitle("Saldo Anual");

        recyclerView = findViewById(R.id.recyclerView);

        /*
        SaldoAnual saldoAnual = new SaldoAnual();
        saldoAnual.setSaldo(900.00);
        saldoAnual.setAno("2018");

        SaldoAnual saldoAnual2 = new SaldoAnual();
        saldoAnual2.setSaldo(-900.00);
        saldoAnual2.setAno("2019");

        listSaldoAnual.add(saldoAnual);
        listSaldoAnual.add(saldoAnual2);
        */


        //COnfigura adaptador
        adapter = new AdapterSaldoAnual(listSaldoAnual, this);

        //configura recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


    }

    public void recuperaSaldoAnual(){

        String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        saldoAnualRef = myRef.child("movimentacao")
                        .child(currentUser);

        valueEventListener = saldoAnualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ano: dataSnapshot.getChildren()){
                    //Log.i("informacao", "chave "+ano.getKey().toString());

                     for (DataSnapshot mesAno: ano.getChildren()){
                         //Log.i("informacao", "chave "+mesAno.getKey().toString());
                         for (DataSnapshot dados: mesAno.getChildren()){

                             Movimentacao movimentacao = dados.getValue(Movimentacao.class);

                             //Log.i("informacao", "atributo "+movimentacao.getTipo().toString());

                             if (movimentacao.getTipo() == "d" || movimentacao.getTipo().equals("d")){
                                 despesaMensalTotal += movimentacao.getValor();
                             }

                             if (movimentacao.getTipo() == "r" || movimentacao.getTipo().equals("r")){
                                 receitaMensalTotal += movimentacao.getValor();
                             }
                         }

                    }


                    valorAnualTotal = receitaMensalTotal - despesaMensalTotal;
                    SaldoAnual saldoAnual = new SaldoAnual();
                    saldoAnual.setAno(ano.getKey().toString());
                    saldoAnual.setSaldo(valorAnualTotal);
                    listSaldoAnual.add(saldoAnual);

                    adapter.notifyDataSetChanged();

                    despesaMensalTotal = 0.00;
                    receitaMensalTotal = 0.00;
                    valorAnualTotal = 0.00;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        recuperaSaldoAnual();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saldoAnualRef.removeEventListener(valueEventListener);
    }
}
