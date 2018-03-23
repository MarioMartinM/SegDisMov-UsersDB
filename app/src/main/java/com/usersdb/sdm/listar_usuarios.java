package com.usersdb.sdm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import net.sqlcipher.database.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.usersdb.sdm.R;

import java.util.ArrayList;

public class listar_usuarios extends AppCompatActivity {
    public static final String MisPreferencias = "MyPrefs";
    public static final String SQL_Password = "key_sql";
    SharedPreferences sprefs;
    String key_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_usuarios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Se accede al SharedPreferences para almacenar en una variable global la contraseña de cifrado de la BD
        sprefs = getSharedPreferences(MisPreferencias, Context.MODE_PRIVATE);
        key_string = sprefs.getString(SQL_Password, "");

        // Se preparan las variables necesarias para leer de la BBDD
        SQLiteDatabase.loadLibs(this);
        UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase(key_string);

        // Se define una proyeccion para especificar las columnas que se van a usar de la query
        String[] projection = {
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_GENDER,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LOCATION,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PICTURE,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LARGEPICTURE,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_REGISTERED,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_USERNAME,
                UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PASSWORD
        };

        // El resultado de la query se devuelve en un objeto Cursor
        Cursor cursor = db.query(
                UsersDBDatabase.TablaUsuarios.TABLE_NAME,        // The table to query
                projection,                                     // The columns to return
                null,                                  // The columns for the WHERE clause
                null,                               // The values for the WHERE clause
                null,                                    // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        // Se accede a los elementos obtenidos en el objeto Cursor
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String itemName, itemGender, itemLocation, itemPicture, itemLargePicture, itemRegister, itemUsername, itemPassword;
        for (int i = 0; i < cursor.getCount(); i++){
            // Si es la primera iteracion, se pasa al primer elemento del Cursor. Si no, se pasa al siguiente
            if (i == 0){
                cursor.moveToFirst();
            }
            else {
                cursor.moveToNext();
            }

            // Se obtienen todos los atributos del elemento actual (que es un Usuario)
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME));
            itemGender = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_GENDER));
            itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LOCATION));
            itemPicture = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PICTURE));
            itemLargePicture = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LARGEPICTURE));
            itemRegister = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_REGISTERED));
            itemUsername = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_USERNAME));
            itemPassword = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PASSWORD));

            // Se crea un usuario con los atributos anteriores y se añade a un ArrayList
            Usuario aux = new Usuario(itemName, itemGender, itemLocation, itemPicture, itemLargePicture, itemRegister, itemUsername, itemPassword);
            usuarios.add(aux);
        }
        cursor.close();


        // Se accede a la lista del XML y se le asigna el formato correspondiente
        ListView lv = findViewById(R.id.listaUsuarios);
        AdapterUser adapter = new AdapterUser(this, usuarios);

        // Ademas, cuando se clicke un elemento de la lista se pasara a la actividad del Perfil del usuario clickado
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Usuario item = (Usuario) adapter.getItemAtPosition(position);

                Intent abrirPerfil = new Intent("android.intent.action.PERFIL");
                abrirPerfil.putExtra("Usuario", item);
                startActivity(abrirPerfil);
            }
        });
        lv.setAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se asigna el menu de navegacion a la actividad actual
        getMenuInflater().inflate(R.menu.menu, menu);

        // Se muestra deshabilitada la opcion "Listar usuarios"
        menu.findItem(R.id.listarMenu).setEnabled(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cuando se seleccione la opcion "Insertar Usuarios" se pasara a la actividad correspondiente
        int id = item.getItemId();
        if (id == R.id.insertarMenu) {
            Intent abrirInsertar = new Intent("android.intent.action.INSERTAR");
            startActivity(abrirInsertar);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
        paginaPrincipal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(paginaPrincipal);
    }
}