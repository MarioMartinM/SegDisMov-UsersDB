package com.usersdb.sdm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.usersdb.sdm.sdm.R;

public class pagina_principal extends AppCompatActivity {

    Button insertar;
    Button listar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Se activa el boton de "Insertar Usuarios" para que al hacer clic se pase a la actividad correspondiente
        insertar = findViewById(R.id.Insertar);
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirInsertar = new Intent("android.intent.action.INSERTAR");
                startActivity(abrirInsertar);
            }
        });

        // Se activa el boton de "Listar Usuarios" para que al hacer clic se pase a la actividad correspondiente
        listar = findViewById(R.id.Listar);
        listar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirListar = new Intent("android.intent.action.LISTAR");
                startActivity(abrirListar);
            }
        });
    }

}
