package com.example.alan8.organizzeclone.activity.activity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan8.organizzeclone.R;
import com.example.alan8.organizzeclone.activity.adapter.AdapterMovimentacao;
import com.example.alan8.organizzeclone.activity.config.ConfiguracaoFirebase;
import com.example.alan8.organizzeclone.activity.helper.Adaptador;
import com.example.alan8.organizzeclone.activity.helper.Base64Custom;
import com.example.alan8.organizzeclone.activity.helper.DataCustom;
import com.example.alan8.organizzeclone.activity.model.Movimentacao;
import com.example.alan8.organizzeclone.activity.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference myRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private Double despesaTotal = 0.00, receitaTotal = 0.00, resumoUsuario = 0.00;
    private DatabaseReference refUser;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerMovimentacaoes;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> listMovimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef;
    private String mesAno;
    private String ano;
    //private String nomeUsuario;
    private Double receitaMensalTotal=0.00, despesaMensalTotal=0.00, valorMensalTotal=0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerViewMovimentacao);
        configuraCalendario();
        swipe();

        adapterMovimentacao = new AdapterMovimentacao(listMovimentacoes, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);




        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Log.i("swipe", "item foi arrastado");
                excluiMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluiMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Excluir movimentação");
        alertDialog.setMessage("Você tem certeza que deseja excluir este item?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = listMovimentacoes.get(position);

                String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
                movimentacaoRef = myRef.child("movimentacao")
                        .child(currentUser)
                        .child(ano)
                        .child(mesAno);

                movimentacaoRef.child(movimentacao.getKeyItemMovimentacao()).removeValue();
                adapterMovimentacao.notifyItemRemoved( position);
                atualizarSaldoGeral();
                recuperarMovimentacoes();
            }
        });

        alertDialog.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this,
                        "Cancelado",
                        Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void atualizarSaldoGeral(){

        String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        refUser = myRef.child("usuarios").child(currentUser);


        if (movimentacao.getTipo().equals("r")){
            receitaTotal -= movimentacao.getValor();
            refUser.child("receitaTotal").setValue(receitaTotal);

        }

        if (movimentacao.getTipo().equals("d")){
            despesaTotal -= movimentacao.getValor();
            refUser.child("despesaTotal").setValue(despesaTotal);

        }

    }

    public void recuperarMovimentacoes(){

        valorMensalTotal = 0.00;
        despesaMensalTotal = 0.00;
        receitaMensalTotal = 0.00;
        textoSaldo.setText("Carregando...");

        Log.i("valorDespesa", "valor despesa fora do for"+despesaMensalTotal);
        Log.i("valorDespesa", "valor receita fora do for "+receitaMensalTotal);
        //Log.i("conferindo", "recuperaMovimentacoes carregado");

        String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        movimentacaoRef = myRef.child("movimentacao")
                .child(currentUser)
                .child(ano)
                .child(mesAno);

        valueEventListenerMovimentacaoes = movimentacaoRef.
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listMovimentacoes.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){


                        Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                        movimentacao.setKeyItemMovimentacao(dados.getKey());

                        if (movimentacao.getTipo() == "d" || movimentacao.getTipo().equals("d")){
                            despesaMensalTotal += movimentacao.getValor();
                            Log.i("valorDespesa",
                                    "valor despesa dentro do for "+despesaMensalTotal);
                        }

                        if (movimentacao.getTipo() == "r" || movimentacao.getTipo().equals("r")){
                            receitaMensalTotal += movimentacao.getValor();
                            Log.i("valorDespesa",
                                    "valor receita dentro do for"+receitaMensalTotal);
                        }


                        listMovimentacoes.add(movimentacao);


                }


                valorMensalTotal = receitaMensalTotal - despesaMensalTotal;

                textoSaldo.setTextColor(getResources().getColor(android.R.color.white));
                if(valorMensalTotal < 0) {
                    textoSaldo.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(valorMensalTotal);
                textoSaldo.setText("R$ "+resultadoFormatado);

                valorMensalTotal = 0.00;
                despesaMensalTotal = 0.00;
                receitaMensalTotal = 0.00;

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void recuperaNomeUsuario(){
         //textoSaudacao.setText("Carregando...");
        String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        refUser = myRef.child("usuarios").child(currentUser);
                //.child(DataCustom.retornaAno(DataCustom.dataAtual()));

        valueEventListenerUser = refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
                despesaTotal = usuario.getDespesaTotal();
                textoSaudacao.setText("Olá, "+usuario.getNome());
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /*public void recuperarResumo(){
        String currentUser = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
        refUser = myRef.child("usuarios").child(currentUser);

        valueEventListenerUser = refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;


                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);

                textoSaudacao.setText("Olá, "+usuario.getNome());
                textoSaldo.setText("R$ "+resultadoFormatado);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
               mAuth.signOut();
               startActivity(new Intent(this, MainActivity.class));
               finish();
            break;
            case R.id.saldo_anual:
                startActivity(new Intent(this, SaldoAnualActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void addDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }



    public void configuraCalendario(){

        final CalendarDay dataAtual = calendarView.getCurrentDate();
        String mes = String.format("%02d", (dataAtual.getMonth()+1));
        mesAno = String.valueOf(mes + "" + dataAtual.getYear());
        ano = String.valueOf(dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mes = String.format("%02d", (date.getMonth()+1));
                mesAno = String.valueOf(mes + "" + date.getYear());
                ano = String.valueOf(date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacaoes);
                recuperarMovimentacoes();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaNomeUsuario();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        refUser.removeEventListener(valueEventListenerUser);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacaoes);
    }
}
