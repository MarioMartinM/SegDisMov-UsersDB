package com.usersdb.sdm;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import net.sqlcipher.database.*;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.usersdb.sdm.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
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

    public static final String MisPreferencias = "MyPrefs";
    public static final String SQL_Password = "key_sql";
    SharedPreferences sprefs;
    String key_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_usuarios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Se accede al SharedPreferences para almacenar en una variable global la contraseña de cifrado de la BD
        sprefs = getSharedPreferences(MisPreferencias, Context.MODE_PRIVATE);
        key_string = sprefs.getString(SQL_Password, "");

        // Se cargan los elementos de la actividad en las variables globales
        insertarUsuarios = findViewById(R.id.InsertarUsuarios);
        txtNacionalidad = findViewById(R.id.editNacionalidad);
        txtHombre = findViewById(R.id.radioHombre);
        txtMujer = findViewById(R.id.radioMujer);
        txtNumeroUsuarios = findViewById(R.id.editNumeroUsuarios);
        txtFechaRegistro = findViewById(R.id.editFechaRegistro);

        // Esta es la funcion que se realiza cada vez que se pulse el boton "Insertar usuarios"
        insertarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String nacionalidad = txtNacionalidad.getText().toString();
                String numeroUsuarios = txtNumeroUsuarios.getText().toString();
                String fechaRegistro = txtFechaRegistro.getText().toString();
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                int informacionInvalida = 0;

                // Validación del campo nacionalidad
                if (!nacionalidad.equals("")){
                    String [] nacionalidades = nacionalidad.split(",");
                    for (int i = 0; i < nacionalidades.length; i++){
                        if(!nacionalidades[i].equals("AU") && !nacionalidades[i].equals("BR") && !nacionalidades[i].equals("CA") && !nacionalidades[i].equals("CH")
                                && !nacionalidades[i].equals("DE") && !nacionalidades[i].equals("DK") && !nacionalidades[i].equals("ES") && !nacionalidades[i].equals("FI")
                                && !nacionalidades[i].equals("FR") && !nacionalidades[i].equals("GB") && !nacionalidades[i].equals("IE") && !nacionalidades[i].equals("IR")
                                && !nacionalidades[i].equals("NL") && !nacionalidades[i].equals("NZ") && !nacionalidades[i].equals("TR") && !nacionalidades[i].equals("US")){
                            txtNacionalidad.setError("- Introduce nacionalidades entre las disponibles\n- Si introduces varias nacionalidades, separáralas por una ',' únicamente");
                            informacionInvalida = 1;
                        }
                    }
                }

                // Validacion del campo numero de usuarios
                if(!numeroUsuarios.equals("")){
                    String regex = "[0-9]+";
                    if(!numeroUsuarios.matches(regex)){
                        txtNumeroUsuarios.setError("Introduce únicamente dígitos");
                        informacionInvalida = 1;
                    }
                }

                // Validación del campo fecha de registro
                Date specifiedDate = new Date();
                if (!fechaRegistro.equals("")){
                    try {
                        specifiedDate = formatter2.parse(txtFechaRegistro.getText().toString());
                    } catch (ParseException e) {
                       txtFechaRegistro.setError("Introduce un formato válido (dd/mm/yyyy)");
                        informacionInvalida = 1;
                    }
                }

                // Si los campos anteriores son correctos, se extraen los usuarios de la API
                if(informacionInvalida == 0){
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


                        // Se obtiene el resultado de la llamada a la URL de la API y se organiza en un JSONArray
                        String resultadoURL = new obtenerUsuarios().execute().get();
                        JSONObject objectJson = new JSONObject(resultadoURL);
                        JSONArray listaResultado = objectJson.getJSONArray("results");

                        // Se preparan las variables para insertar en la BBDD
                        SQLiteDatabase.loadLibs(context);
                        UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                        SQLiteDatabase db = mDbHelper.getWritableDatabase(key_string);

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
                            // MARIO Date specifiedDate = new Date();
                            if (!fechaRegistro.equals("")){
                                // MARIO  SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
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
                                String largePicture = parsedPicture.getString("large");


                                // Se prepara un ContentValues con los datos para realizar la insercion en la BBDD
                                ContentValues values = new ContentValues();
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME, fullName);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_GENDER, gender);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LOCATION, location);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PICTURE, picture);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LARGEPICTURE, largePicture);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_REGISTERED, registered);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_USERNAME, username);
                                values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PASSWORD, password);

                                // Se inserta el nuevo usuario en la BBDD y se aumenta el contador de usuarios
                                db.insert(UsersDBDatabase.TablaUsuarios.TABLE_NAME, null, values);
                                usersAdded++;
                            }
                        }

                        // Una vez se han recorrido todos los usuarios, se muestra el numero de usuarios que finalmente se han añadido
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context, "UsersDB_channel")
                                        .setSmallIcon(R.drawable.iconologo_transparente)
                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.iconologo))
                                        .setContentTitle("UsersDB")
                                        .setContentText("Se han insertado "+usersAdded+" nuevos usuarios")
                                        .setAutoCancel(true);
                        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(001, mBuilder.build());


                        //Toast toast = Toast.makeText(context, "Se han añadido "+usersAdded+" usuarios.", Toast.LENGTH_LONG);
                        //toast.show();

                        // Finalmente se regresa a la pantalla principal
                        Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                        paginaPrincipal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(paginaPrincipal);
                    }
                    catch(Exception e) {
                        Log.e("ERROR", e.getMessage(), e);
                    }
                }
            }
        });
    }


    // Esta clase se crea para acceder a la API de RandomUser.
    // La conexion a Internet no puede hacerse desde el Thread principal, por eso se usa AsyncTask
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se asigna el menu de navegacion a la actividad actual
        getMenuInflater().inflate(R.menu.menu, menu);

        // Se muestra deshabilitada la opcion "Insertar usuarios"
        menu.findItem(R.id.insertarMenu).setEnabled(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cuando se seleccione la opcion "Listar Usuarios" se pasara a la actividad correspondiente
        int id = item.getItemId();
        if (id == R.id.listarMenu) {
            Intent abrirListar = new Intent("android.intent.action.LISTAR");
            startActivity(abrirListar);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
