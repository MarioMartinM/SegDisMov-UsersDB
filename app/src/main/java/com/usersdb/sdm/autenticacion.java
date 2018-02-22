package com.usersdb.sdm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.usersdb.sdm.sdm.R;

public class autenticacion extends AppCompatActivity {

    Context context = this;
    EditText txtUsuario;
    EditText txtPassword;
    Button iniciarSesion;
    Button registrarse;
    public static final String MisPreferencias = "MyPrefs";
    public static final String Usuario = "key_user";
    public static final String Password = "key_pass";
    SharedPreferences sprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtUsuario = findViewById(R.id.editUsuarioAut);
        txtPassword = findViewById(R.id.editPasswordAut);
        iniciarSesion = findViewById(R.id.iniciarSesion);
        registrarse = findViewById(R.id.registrarse);
        sprefs = getSharedPreferences(MisPreferencias, Context.MODE_PRIVATE);

        if (sprefs.contains(Usuario) && sprefs.contains(Password)) {
            txtUsuario.setText(sprefs.getString(Usuario, ""));
            txtPassword.setText(sprefs.getString(Password, ""));
        }


        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se obtienen los valores del cuestionario
                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();

                // Si algun campo no está rellenado, se le indica al usuario
                if (usuario.equals("") || password.equals("")){
                    if (usuario.equals("")){
                        txtUsuario.setError("Introduzca un nombre de usuario");
                    }
                    if (password.equals("")){
                        txtPassword.setError("Introduzca una contraseña");
                    }
                }
                else {
                    // Se preparan las variables para leer de la BD
                    UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();
                    String[] projection = {
                            UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME,
                            UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD
                    };
                    // Se indica la parte WHERE de la query y se mira si el usuario existe en la BD
                    String selection = UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME + " = ?";
                    String[] selectionArgs = {usuario};
                    Cursor cursor = db.query(
                            UsersDBDatabase.TablaUsuariosSesion.TABLE_NAME,        // The table to query
                            projection,                                           // The columns to return
                            selection,                                            // The columns for the WHERE clause
                            selectionArgs,                                       // The values for the WHERE clause
                            null,                                       // don't group the rows
                            null,                                        // don't filter by row groups
                            null                                        // The sort order
                    );


                    // Si no existe el usuario en la BBDD se le notifica al usuario
                    if (cursor.getCount() == 0) {
                        Toast toast = Toast.makeText(context, "El usuario indicado no existe en la BBDD", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    // Si existe el usuario
                    else if (cursor.getCount() == 1) {
                        // se comprueba si la contraseña de la BBDD coincide con la insertada por el usuario
                        cursor.moveToFirst();
                        String passwordDB = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD));

                        // Si no coinciden, se le indica al usuario que la contraseña no es correcta
                        if (!passwordDB.equals(password)){
                            Toast toast = Toast.makeText(context, "La contraseña introducida no es correcta para el usuario indicado", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        // Si coinciden, se guardan los datos de usuarios en las preferencias (si no son los mismos) y se inicia sesión (se pasa al menú principal)
                        else {
                            SharedPreferences.Editor editor = sprefs.edit();
                            editor.putString(Usuario, usuario);
                            editor.putString(Password, password);
                            editor.apply();

                            Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                            startActivity(paginaPrincipal);
                            Toast toast = Toast.makeText(context, "¡Bienvenido/a, "+usuario+"!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        });


        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se obtienen los valores del cuestionario
                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();

                // Si algun campo no está rellenado, se le indica al usuario
                if (usuario.equals("") || password.equals("")){
                    if (usuario.equals("")){
                        txtUsuario.setError("Introduzca un nombre de usuario");
                    }
                    if (password.equals("")){
                        txtPassword.setError("Introduzca una contraseña");
                    }
                }
                else {
                    // Se preparan las variables para leer de la BD
                    UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();
                    String[] projection = {
                            UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME,
                            UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD
                    };
                    // Se indica la parte WHERE de la query y se mira si el usuario existe en la BD
                    String selection = UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME + " = ?";
                    String[] selectionArgs = {usuario};
                    Cursor cursor = db.query(
                            UsersDBDatabase.TablaUsuariosSesion.TABLE_NAME,        // The table to query
                            projection,                                           // The columns to return
                            selection,                                            // The columns for the WHERE clause
                            selectionArgs,                                       // The values for the WHERE clause
                            null,                                       // don't group the rows
                            null,                                        // don't filter by row groups
                            null                                        // The sort order
                    );


                    // Si el usuario existe en la BD se indica al usuario
                    if (cursor.getCount() == 1) {
                        Toast toast = Toast.makeText(context, "Ya existe ese usuario en la BBDD", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    // Si no existe el usuario, se añade a la BD
                    else if (cursor.getCount() == 0) {
                        SQLiteDatabase db2 = mDbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME, usuario);
                        values.put(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD, password);
                        db2.insert(UsersDBDatabase.TablaUsuariosSesion.TABLE_NAME, null, values);

                        Toast toast = Toast.makeText(context, "Se ha añadido el nuevo usuario a la BBDD", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }
}
