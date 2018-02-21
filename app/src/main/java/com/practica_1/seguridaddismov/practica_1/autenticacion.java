package com.practica_1.seguridaddismov.practica_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class autenticacion extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtPassword;
    Button iniciarSesion;
    public static final String MisPreferencias = "MyPrefs" ;
    public static final String Usuario = "key_user";
    public static final String Password = "key_pass";
    SharedPreferences sprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtUsuario = findViewById(R.id.editUsuarioAut);
        txtPassword = findViewById(R.id.editPasswordAut);
        iniciarSesion = findViewById(R.id.iniciarSesion);

        if (sprefs.contains(Usuario) && sprefs.contains(Password)) {
            txtUsuario.setText(sprefs.getString(Usuario, ""));
            txtPassword.setText(sprefs.getString(Password, ""));
        }



        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = txtUsuario.getText().toString();
                String passwword = txtPassword.getText().toString();
                SharedPreferences.Editor editor = sprefs.edit();
                editor.putString(Usuario, usuario);
                editor.putString(Password, passwword);
                editor.commit();


                Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                startActivity(paginaPrincipal);
            }
        });

    }

}
