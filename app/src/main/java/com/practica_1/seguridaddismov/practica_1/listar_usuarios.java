package com.practica_1.seguridaddismov.practica_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

public class listar_usuarios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_usuarios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Se preparan las variables necesarias para leer de la BBDD
        FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Se define una proyeccion para especificar las columnas que se van a usar de la query
        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LARGEPICTURE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED,
                FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD
        };

        // El resultado de la query se devuelve en un objeto Cursor
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,        // The table to query
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
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            itemGender = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER));
            itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION));
            itemPicture = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE));
            itemLargePicture = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LARGEPICTURE));
            itemRegister = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED));
            itemUsername = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME));
            itemPassword = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD));

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}