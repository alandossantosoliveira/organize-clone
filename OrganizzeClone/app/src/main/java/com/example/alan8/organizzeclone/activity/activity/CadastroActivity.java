package com.example.alan8.organizzeclone.activity.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alan8.organizzeclone.R;
import com.example.alan8.organizzeclone.activity.config.ConfiguracaoFirebase;
import com.example.alan8.organizzeclone.activity.helper.Base64Custom;
import com.example.alan8.organizzeclone.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private EditText campoNome, campoEmail, campoSenha;
    private Button btnCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");

        campoEmail = findViewById(R.id.editEmail);
        campoNome = findViewById(R.id.editNome);
        campoSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.buttonSalvar);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);//DEIXA PROGRESS BAR INVISIVEL

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textNome = campoNome.getText().toString();
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                //Validar se os campos foram preenchidos
                if (!textNome.isEmpty()){
                   if (!textEmail.isEmpty()){
                       if (! textSenha.isEmpty()){

                           usuario = new Usuario();
                           usuario.setNome(textNome);
                           usuario.setEmail(textEmail);
                           usuario.setSenha(textSenha);
                           cadastrarUsuario();
                           progressBar.setVisibility(View.VISIBLE);//DEIXA PROGRESS BAR visivel
                       }else{
                           Toast.makeText(CadastroActivity.this,
                                   "Preencha a senha.",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }else {
                       Toast.makeText(CadastroActivity.this,
                               "Preencha o email.",
                               Toast.LENGTH_SHORT).show();
                   }
                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha seu nome.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
           usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){
                  String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                  usuario.setIdUsuario(idUsuario);
                  usuario.salvar();
                  finish();

              }else {
                  String excessao ="";
                  try {
                      throw task.getException();
                  }catch(FirebaseAuthWeakPasswordException e){
                     excessao = "Digite uma senha mais fote!";
                  }catch(FirebaseAuthInvalidCredentialsException e){
                      excessao = "Por favor, digite um email valido!";
                  }catch(FirebaseAuthUserCollisionException e){
                      excessao = "Email já existe!";
                  }catch (Exception e){
                      excessao = "erro ao cadastrar usuário: "+e.getMessage();
                      e.printStackTrace();
                  }
                  Toast.makeText(CadastroActivity.this,
                          excessao,
                          Toast.LENGTH_SHORT).show();
                  progressBar.setVisibility(View.GONE);
              }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);//DEIXA PROGRESS BAR INVISIVEL
    }
}
