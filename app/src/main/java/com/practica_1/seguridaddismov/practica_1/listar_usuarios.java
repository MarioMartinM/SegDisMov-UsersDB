package com.practica_1.seguridaddismov.practica_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


        ArrayList<Usuario> usuarios = new ArrayList<>();
        String itemName, itemGender, itemLocation, itemPicture, itemLargePicture, itemRegister, itemUsername, itemPassword;
        for (int i = 0; i < cursor.getCount(); i++){
            if (i == 0){
                // Se accede al primer resultado devuelto
                cursor.moveToFirst();
            }
            else {
                cursor.moveToNext();
            }

            itemName = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
            itemGender = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GENDER));
            itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION));
            itemPicture = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PICTURE));
            itemLargePicture = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LARGEPICTURE));
            itemRegister = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_REGISTERED));
            itemUsername = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME));
            itemPassword = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSWORD));

            Usuario aux = new Usuario(itemName, itemGender, itemLocation, itemPicture, itemLargePicture, itemRegister, itemUsername, itemPassword);
            usuarios.add(aux);
        }

        cursor.close();
        ListView lv = findViewById(R.id.listaUsuarios);
        AdapterUser adapter = new AdapterUser(this, usuarios);
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
}