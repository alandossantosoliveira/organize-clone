package com.example.alan8.organizzeclone.activity.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alan8.organizzeclone.R;
import com.example.alan8.organizzeclone.activity.config.ConfiguracaoFirebase;
import com.example.alan8.organizzeclone.activity.helper.Base64Custom;
import com.example.alan8.organizzeclone.activity.helper.DataCustom;
import com.example.alan8.organizzeclone.activity.model.Movimentacao;
import com.example.alan8.organizzeclone.activity.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {
    private EditText campoValor;
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotalRecuperada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);



        campoCategoria = findViewById(R.id.editCategoria);
        campoData = findViewById(R.id.editData);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        campoData.setText(DataCustom.dataAtual());

    }

    public void lancarReceita(View view){

        if (validarCampos()){

            Double valorDigitado = Double.parseDouble(campoValor.getText().toString());
            String dataDigitada = campoData.getText().toString();

            movimentacao = new Movimentacao();
            movimentacao.setValor(valorDigitado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setData(dataDigitada);
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setTipo("r");

            Double receitaAtualizada = valorDigitado + receitaTotalRecuperada;
            atualizaReceita(receitaAtualizada);
            movimentacao.salvar(dataDigitada);
            finish();

        }

    }

    public Boolean validarCampos(){
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if (!textoValor.isEmpty()){
            if (!textoData.isEmpty()){
                if (!textoCategoria.isEmpty()){
                    if (!textoDescricao.isEmpty()){
                        return true;
                    }else {
                        Toast.makeText(this, "Preencha a descrição",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Toast.makeText(this, "Preencha o campo Categoria.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(this, "Preencha a data",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(this, "Preencha o valor",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void recuperaReceitaTotal(){

        String ano = DataCustom.retornaAno(campoData.getText().toString());

        String currentUser = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference myRef = firebaseRef.child("usuarios").child(currentUser);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotalRecuperada = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void atualizaReceita(Double receita){


        String currentUser = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference myRef = firebaseRef.child("usuarios").child(currentUser);


        myRef.child("receitaTotal").setValue(receita);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaReceitaTotal();
    }
}
