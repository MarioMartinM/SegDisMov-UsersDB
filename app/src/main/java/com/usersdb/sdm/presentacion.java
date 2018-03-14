package com.usersdb.sdm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.usersdb.sdm.R;

public class presentacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Se crea un Thread para que a los 3s se pase a la pagina principal
        Thread reloj = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent abrirPaginaAut = new Intent("android.intent.action.AUT");
                    startActivity(abrirPaginaAut);
                }
            }
        };

        reloj.start();
    }


    protected void onPause() {
        super.onPause();
        finish();
    }
}
