package com.practica_1.seguridaddismov.practica_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;


public class insertar_usuarios extends AppCompatActivity {

    Button insertarUsuarios;
    EditText txtNacionalidad;
    CheckBox txtHombre;
    CheckBox txtMujer;
    EditText txtNumeroUsuarios;
    EditText txtFechaRegistro;
    StringBuilder API_URL_BUILD = new StringBuilder();
    String API_URL="";
    Context c = this;

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
            public void onClick (View v) {
                String nacionalidad = txtNacionalidad.getText().toString();
                String numeroUsuarios = txtNumeroUsuarios.getText().toString();
                String fechaRegistro = txtFechaRegistro.getText().toString();

                API_URL_BUILD.append("https://randomuser.me/api/?inc=name,registered,gender,picture,location,login");

                try {
                    if(!nacionalidad.equals("")) {
                         API_URL_BUILD.append("&nat="+nacionalidad.toUpperCase());
                    }
                    if(txtHombre.isChecked() && !txtMujer.isChecked()) {
                            API_URL_BUILD.append("&gender=male");
                    }
                    if(!txtHombre.isChecked() && txtMujer.isChecked()) {
                            API_URL_BUILD.append("&gender=female");
                    }
                    if(!numeroUsuarios.equals("")) {
                            API_URL_BUILD.append("&results=" + numeroUsuarios);
                    }
                    if(!fechaRegistro.equals("")) {
                            API_URL_BUILD.append("&registered=" + fechaRegistro);
                    }

                    API_URL = API_URL_BUILD.toString();
                    Log.d("PRUEBA7.1", "URL BUENA: "+API_URL);


                    /*Resultado de URL*/
                    String resultadoURL = new obtenerUsuarios().execute().get();
                    Log.d("PRUEBA7", "Lista de usuarios: "+resultadoURL);

                    /*Array con todos los usuarios devueltos.*/
                    JSONObject objectJson = new JSONObject(resultadoURL);
                    JSONArray listaResultado = objectJson.getJSONArray("results");
                    Log.d("PRUEBA7", "Lista de usuarios2: "+listaResultado);

                    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(c);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    for(int i=0; i<listaResultado.length(); i++){
                        JSONObject aux = listaResultado.getJSONObject(i);

                        String gender = aux.getString("gender");
                        String name = aux.getString("name");
                        String location = aux.getString("location");
                        String login = aux.getString("login");
                        String registered = aux.getString("registered");
                        String picture = aux.getString("picture");

                        Usuario usr = new Usuario(name, gender, location, picture, registered, login);

                        ContentValues values = new ContentValues();
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER, gender);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION, location);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE, picture);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED, registered);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOGIN, login);

                        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                    }
                    JSONObject aux = listaResultado.getJSONObject(0);
                    Log.d("PRUEBA8", "Usuario 0: "+aux);

                    String gender = aux.getString("gender");
                    String name = aux.getString("name");
                    String location = aux.getString("location");
                    String login = aux.getString("login");
                    String registered = aux.getString("registered");
                    String picture = aux.getString("picture");

                    Log.d("PRUEBA9","Genero de usuario 0: "+gender);
                    Log.d("PRUEBA9","Nombre de usuario 0: "+name);
                    Log.d("PRUEBA9","Location de usuario 0: "+location);

                    //TAREAS FUTURAS: Parsear el json, guardarlo en la BBDD y recuperarlo



                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                   // return null;
                }
            }
        });

    }

    //Esto es necesario porque sino da error por ejecutar un proceso en backgroud en el thread principal
    class obtenerUsuarios extends  AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(false);
                    BufferedReader bR = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sB = new StringBuilder();
                    String line;
                    while ((line = bR.readLine()) != null) {
                        sB.append(line).append("\n");
                    }
                    bR.close();
                    urlConnection.disconnect();
                    return sB.toString();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
        }
    }
}
