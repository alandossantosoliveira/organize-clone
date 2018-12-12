package com.example.alan8.organizzeclone.activity.model;

import com.example.alan8.organizzeclone.activity.config.ConfiguracaoFirebase;
import com.example.alan8.organizzeclone.activity.helper.Base64Custom;
import com.example.alan8.organizzeclone.activity.helper.DataCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private Double valor;
    private String keyItemMovimentacao;

    public Movimentacao() {
    }

    public void salvar(String dataEscolhida){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        String dataMovimentacao = DataCustom.mesAno(dataEscolhida);
        String ano = DataCustom.retornaAno(dataEscolhida);

        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        reference.child("movimentacao")
                .child(idUsuario)
                .child(ano)
                .child(dataMovimentacao)
                .push()
                .setValue(this);
    }

    public String getKeyItemMovimentacao() {
        return keyItemMovimentacao;
    }

    public void setKeyItemMovimentacao(String keyItemMovimentacao) {
        this.keyItemMovimentacao = keyItemMovimentacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
