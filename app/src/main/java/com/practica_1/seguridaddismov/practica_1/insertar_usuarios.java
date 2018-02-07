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
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;


public class insertar_usuarios extends AppCompatActivity {

    Button insertarUsuarios;
    EditText txtNacionalidad;
    CheckBox txtHombre;
    CheckBox txtMujer;
    EditText txtNumeroUsuarios;
    EditText txtFechaRegistro;
    StringBuilder API_URL_BUILD;
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

                try {
                    API_URL_BUILD = new StringBuilder("https://randomuser.me/api/?inc=name,registered,gender,picture,location,login");
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

                    int usersAdded = 0;
                    for(int i=0; i<listaResultado.length(); i++){
                        JSONObject aux = listaResultado.getJSONObject(i);

                        String registered = aux.getString("registered");
                        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date registeredDate = formatter1.parse(registered);

                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                        Date specifiedDate = formatter2.parse(txtFechaRegistro.getText().toString());

                        if (registeredDate.before(specifiedDate) || fechaRegistro.equals("")){
                            String gender = aux.getString("gender");
                            if (gender.equals("male")){
                                gender = "M";
                            }
                            else {
                                gender = "F";
                            }

                            JSONObject parsedName = aux.getJSONObject("name");
                            String titleName = parsedName.getString("title");
                            String titleNameCap = titleName.substring(0, 1).toUpperCase() + titleName.substring(1);
                            String firstName = parsedName.getString("first");
                            String firstNameCap = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                            String lastName = parsedName.getString("last");
                            String lastNameCap = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                            String fullName = titleNameCap+". "+firstNameCap+" "+lastNameCap;

                            JSONObject parsedLocation = aux.getJSONObject("location");
                            String location = parsedLocation.getString("street");

                            JSONObject parsedLogin = aux.getJSONObject("login");
                            String username = parsedLogin.getString("username");
                            String password = parsedLogin.getString("password");

                            JSONObject parsedPicture = aux.getJSONObject("picture");
                            String picture = parsedPicture.getString("medium");

                            //Usuario usr = new Usuario(fullName, gender, location, picture, registered, login);

                            ContentValues values = new ContentValues();
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, fullName);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER, gender);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION, location);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE, picture);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED, registered);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME, username);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD, password);

                            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                            usersAdded++;
                        }
                    }

                    //TOAST CON USERSADDED
                    Toast toast = Toast.makeText(c, "Se han añadido "+usersAdded+" usuarios.", Toast.LENGTH_LONG);
                    toast.show();
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
