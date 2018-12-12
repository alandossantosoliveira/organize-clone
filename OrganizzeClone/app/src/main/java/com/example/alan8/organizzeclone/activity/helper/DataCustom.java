package com.example.alan8.organizzeclone.activity.helper;

import java.text.SimpleDateFormat;

public class DataCustom {

    public static String dataAtual(){
        long data = System.currentTimeMillis();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);

        return dataString;
    }

    /*
    public static String dataMovimentacao(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMyyyy");
        String dataString = simpleDateFormat.format(data);

        return dataString;
    }*/

    public static String mesAno (String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAnoJoin = mes + ano;
        return mesAnoJoin;
    }

    public static String retornaAno(String data){
        String retornoData[] = data.split("/");
        String ano = retornoData[2];

        return ano;
    }

}
