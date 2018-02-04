package com.practica_1.seguridaddismov.practica_1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class insertar_usuarios extends AppCompatActivity {

    Button insertarUsuarios;
    EditText txtNacionalidad;
    CheckBox txtHombre;
    CheckBox txtMujer;
    EditText txtNumeroUsuarios;
    EditText txtFechaRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_usuarios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        insertarUsuarios = (Button)findViewById(R.id.InsertarUsuarios);

        txtNacionalidad = (EditText)findViewById(R.id.editNacionalidad);
        txtHombre = (CheckBox) findViewById(R.id.radioHombre);
        txtMujer = (CheckBox) findViewById(R.id.radioMujer);
        txtNumeroUsuarios = (EditText)findViewById(R.id.editNumeroUsuarios);
        txtFechaRegistro = (EditText)findViewById(R.id.editFechaRegistro);

        insertarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nacionalidad = txtNacionalidad.getText().toString();
                String hombre = txtHombre.getText().toString();
                String mujer = txtMujer.getText().toString();
                String numeroUsuarios = txtNumeroUsuarios.getText().toString();
                String fechaRegistro = txtFechaRegistro.getText().toString();

                StringBuilder API_URL_BUILD = "https://randomuser.me/api/";

                try {
                    if(nacionalidad!=null){ API_URL_BUILD.append("?nat="+nacionalidad.toLowerCase());}

                    if(hombre!=null){ API_URL_BUILD.append("?gender=male");}

                    if(mujer!=null){ API_URL_BUILD.append("?gender=female");}

                    if(numeroUsuarios!=null){ API_URL_BUILD.append("?results="+numeroUsuarios);}




                    String API_URL = API_URL_BUILD.toString();
                    URL url = new URL(API_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    finally{
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }
        });

    }


}
