package com.practica_1.seguridaddismov.practica_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class insertar_usuarios extends AppCompatActivity {
    // Variables globales para la insercion de usuarios
    Button insertarUsuarios;
    EditText txtNacionalidad;
    CheckBox txtHombre;
    CheckBox txtMujer;
    EditText txtNumeroUsuarios;
    EditText txtFechaRegistro;
    StringBuilder API_URL_BUILD;
    String API_URL = "";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_usuarios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Se cargan los elementos de la actividad en las variables globales
        insertarUsuarios = (Button)findViewById(R.id.InsertarUsuarios);
        txtNacionalidad = (EditText)findViewById(R.id.editNacionalidad);
        txtHombre = (CheckBox) findViewById(R.id.radioHombre);
        txtMujer = (CheckBox) findViewById(R.id.radioMujer);
        txtNumeroUsuarios = (EditText)findViewById(R.id.editNumeroUsuarios);
        txtFechaRegistro = (EditText)findViewById(R.id.editFechaRegistro);

        // Esta es la funcion que se realiza cada vez que se pulse el boton "Insertar usuarios"
        insertarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String nacionalidad = txtNacionalidad.getText().toString();
                String numeroUsuarios = txtNumeroUsuarios.getText().toString();
                String fechaRegistro = txtFechaRegistro.getText().toString();

                try {
                    // Lo primero que se hace es crear la URL para acceder a la API de RandomUser según los campos marcados por el usuario
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
                    Log.d("PRUEBA1", "URL BUENA: "+API_URL);


                    // Resultado de la llamada a la URL de la API y organización en un JSONArray
                    String resultadoURL = new obtenerUsuarios().execute().get();
                    JSONObject objectJson = new JSONObject(resultadoURL);
                    JSONArray listaResultado = objectJson.getJSONArray("results");
                    Log.d("PRUEBA", "Lista de usuarios: "+resultadoURL);
                    Log.d("PRUEBA", "Lista de usuarios en array: "+listaResultado);

                    // Se preparan las variables para insertar en la BBDD
                    FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(context);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    // Se crea un contador para los usuarios que finalmente se incluyan en la BBDD
                    int usersAdded = 0;
                    for(int i=0; i<listaResultado.length(); i++){
                        // Se recorre el JSONArray y se convierte en JSONObject cada elemento del mismo
                        JSONObject aux = listaResultado.getJSONObject(i);

                        // Se obtiene la fecha de registro del usuario y se convierte en Date
                        String registered = aux.getString("registered");
                        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date registeredDate = formatter1.parse(registered);

                        // Se convierte en Date la fecha indicada por el usuario en la aplicacion
                        Date specifiedDate = new Date();
                        if (!fechaRegistro.equals("")){
                            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                            specifiedDate = formatter2.parse(txtFechaRegistro.getText().toString());
                        }

                        // Si la fecha indicada por el usuario esta vacia o es posterior a la del usuario actual, se añade a la BBDD el usuario
                        if (registeredDate.before(specifiedDate) || fechaRegistro.equals("")){
                            // Se obtiene M o F para indicar el genero del usuario
                            String gender = aux.getString("gender");
                            if (gender.equals("male")){
                                gender = "M";
                            }
                            else {
                                gender = "F";
                            }

                            // Se obtiene unicamente la fecha en formato dd/MM/yyyy de lo obtenido del usuario
                            registered = registered.split("\\s+")[0];
                            SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
                            Date auxDate = formatter3.parse(registered);
                            formatter3.applyPattern("dd/MM/yyyy");
                            registered = formatter3.format(auxDate);


                            // Se obtiene el nombre completo
                            JSONObject parsedName = aux.getJSONObject("name");
                            String titleName = parsedName.getString("title");
                            String titleNameCap = titleName.substring(0, 1).toUpperCase() + titleName.substring(1);
                            String firstName = parsedName.getString("first");
                            String firstNameCap = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                            String lastName = parsedName.getString("last");
                            String lastNameCap = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                            String fullName = titleNameCap+" "+firstNameCap+" "+lastNameCap;

                            // Se obtiene la calle del usuario
                            JSONObject parsedLocation = aux.getJSONObject("location");
                            String location = parsedLocation.getString("street");

                            // Se obtienen el usuario y la contraseña del usuario
                            JSONObject parsedLogin = aux.getJSONObject("login");
                            String username = parsedLogin.getString("username");
                            String password = parsedLogin.getString("password");

                            // Se obtiene la imagen mediana del usuario
                            JSONObject parsedPicture = aux.getJSONObject("picture");
                            String picture = parsedPicture.getString("medium");


                            // Se prepara un ContentValues con los datos para realizar la insercion en la BBDD
                            ContentValues values = new ContentValues();
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, fullName);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER, gender);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION, location);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE, picture);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED, registered);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME, username);
                            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD, password);

                            // Se inserta el nuevo usuario en la BBDD y se aumenta el contador de usuarios
                            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                            usersAdded++;
                        }
                    }

                    // Una vez se han recorrido todos los usuarios, se muestra el numero de usuarios que finalmente se han añadido
                    Toast toast = Toast.makeText(context, "Se han añadido "+usersAdded+" usuarios.", Toast.LENGTH_LONG);
                    toast.show();




                    /*// Se preparan las variables necesarias para leer de la BBDD
                    SQLiteDatabase db2 = mDbHelper.getReadableDatabase();

                    // Se define una proyeccion para especificar las columnas que se van a usar de la query
                    String[] projection = {
                            FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME,
                            FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD
                    };

                    // El resultado de la query se devuelve en un objeto Cursor
                    Cursor cursor = db2.query(
                            FeedReaderContract.FeedEntry.TABLE_NAME,        // The table to query
                            projection,                                     // The columns to return
                            null,                                  // The columns for the WHERE clause
                            null,                               // The values for the WHERE clause
                            null,                                    // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
                    );


                    cursor.moveToFirst();
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
                    String itemGender = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER));
                    String itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION));
                    String itemPicture = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE));
                    String itemUsername = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME));
                    String itemPassword = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD));
                    Log.d("PRUEBA", "Informacion primer usuario:"+
                                                "\n- Nombre: "+itemName+
                                                "\n- Gender: "+itemGender+
                                                "\n- Location: "+itemLocation+
                                                "\n- Picture: "+itemPicture+
                                                "\n- Username: "+itemUsername+
                                                "\n- Password: "+itemPassword
                    );*/

                    // Finalmente se regresa a la pantalla principal
                    Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                    startActivity(paginaPrincipal);
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        });

    }



    // Esta clase se ha creado para que no de error al intentar ejecutar un proceso en backgroud en el thread principal
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
