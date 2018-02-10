package com.practica_1.seguridaddismov.practica_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class perfil_usuario extends AppCompatActivity {
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Usuario user = (Usuario) getIntent().getSerializableExtra("Usuario");

        ImageView imagenPerfil = findViewById(R.id.imagenPerfil);
        try{
            URL url = new URL(user.getImagenPerfilGrande());
            Bitmap bmp = new obtenerImagen().execute(url).get();
            imagenPerfil.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView nombrePerfil = findViewById(R.id.nombrePerfil);
        nombrePerfil.setText(user.getNombre());
        TextView registroPerfil = findViewById(R.id.registroPerfil);
        registroPerfil.setText(user.getFechaRegistro());
        TextView generoPerfil = findViewById(R.id.generoPerfil);
        generoPerfil.setText(user.getGenero());
        TextView usuarioPerfil = findViewById(R.id.usuarioPerfil);
        usuarioPerfil.setText(user.getUsuario());
        TextView passwordPerfil = findViewById(R.id.passwordPerfil);
        passwordPerfil.setText(user.getContrasena());

        ImageButton mapa = findViewById(R.id.buttonMapsPerfil);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + user.getLocalizacion());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


        // Activamos el boton para eliminar el usuario
        FloatingActionButton eliminarPerfil = findViewById(R.id.botonEliminar);
        eliminarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " LIKE ?";
                String[] selectionArgs = { user.getNombre() };
                db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);

                Intent abrirListar = new Intent("android.intent.action.LISTAR");
                startActivity(abrirListar);
                Toast toast = Toast.makeText(context, "El usuario ha sido correctamente eliminado", Toast.LENGTH_LONG);
                toast.show();
            }
        });


        /*
        // Activamos los botones para cambiar los datos
        FloatingActionButton cambiarNombre = findViewById(R.id.botonEliminar);
        eliminarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedReaderContract.FeedReaderDbHelper mDbHelper = new FeedReaderContract.FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                ContentValues values = new ContentValues();
                values.put(FeedEntry.COLUMN_NAME_TITLE, title);

                String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
                String[] selectionArgs = { "MyTitle" };

                int count = db.update(
                        FeedReaderDbHelper.FeedEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
            }
        });*/
    }





    // Esta clase se ha creado para que no de error al intentar ejecutar un proceso en backgroud en el thread principal (Acceso a Internet)
    class obtenerImagen extends AsyncTask<URL, Integer, Bitmap> {
        protected Bitmap doInBackground(URL ... urls) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (Exception e){
                e.printStackTrace();
            }
            return bmp;
        }
    }
}
