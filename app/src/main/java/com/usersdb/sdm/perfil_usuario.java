package com.usersdb.sdm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import net.sqlcipher.database.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.usersdb.sdm.R;

import java.net.URL;

public class perfil_usuario extends AppCompatActivity {
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Cuando se accede a esta actividad, se pasa un usuario como parametro, asi que se obtiene
        final Usuario user = (Usuario) getIntent().getSerializableExtra("Usuario");

        // Se carga la imagne de perfil grande del usuario
        ImageView imagenPerfil = findViewById(R.id.imagenPerfil);
        try{
            URL url = new URL(user.getImagenPerfilGrande());
            Bitmap bmp = new obtenerImagen().execute(url).get();
            imagenPerfil.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Se carga el nombre, fecha de registro, genero, usuario y contraseña del usuario
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


        // Se asigna en un boton la calle del usuario
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

        // Se activa el boton para eliminar el usuario de la BBDD
        ImageButton eliminarPerfil = findViewById(R.id.eliminarUsuario);
        eliminarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase.loadLibs(context);
                UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String selection = UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME + " LIKE ?";
                String[] selectionArgs = { user.getNombre() };
                db.delete(UsersDBDatabase.TablaUsuarios.TABLE_NAME, selection, selectionArgs);

                Intent abrirListar = new Intent("android.intent.action.LISTAR");
                startActivity(abrirListar);

                Toast toast = Toast.makeText(context, "El usuario ha sido correctamente eliminado", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });

        // Se activan el boton para editar el nombre de usuario del usuario actual
        ImageButton editarUsuario = findViewById(R.id.editarUsuario);
        editarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new editarUsuarioDialog();

                Bundle args = new Bundle();
                args.putString("name_user", user.getNombre());
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "editarUsuario");
            }
        });

        // Se activan el boton para editar la contraseña del usuario actual
        ImageButton editarPassword = findViewById(R.id.editarPassword);
        editarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new editarPasswordDialog();

                Bundle args = new Bundle();
                args.putString("name_user", user.getNombre());
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "editarPassword");
            }
        });

        // Se activan el boton para editar la localizacion (calle) del usuario actual
        ImageButton editarLocalizacion = findViewById(R.id.editarLocalizacion);
        editarLocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new editarLocalizacionDialog();

                Bundle args = new Bundle();
                args.putString("name_user", user.getNombre());
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "editarLocalizacion");
            }
        });
    }


    // Clase para mostrar el dialogo correspondiente al pulsar el boton para editar el nombre de usuario y modificarlo con el nuevo valor introducido
    public static class editarUsuarioDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.modal_editar_usuario, null))
                    .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SQLiteDatabase.loadLibs(getActivity());
                            UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(getActivity());
                            SQLiteDatabase db = mDbHelper.getReadableDatabase();

                            ContentValues values = new ContentValues();
                            EditText editText = getDialog().findViewById(R.id.username);
                            values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_USERNAME, editText.getText().toString());

                            String selection = UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME + " LIKE ?";
                            String[] selectionArgs = { getArguments().getString("name_user") };

                            int count = db.update(
                                    UsersDBDatabase.TablaUsuarios.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);

                            Intent abrirPrincipal = new Intent("android.intent.action.INICIO");
                            abrirPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(abrirPrincipal);
                            Intent abrirLista = new Intent("android.intent.action.LISTAR");
                            startActivity(abrirLista);
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Cerrar el modal sin hacer cambios
                        }
                    });
            return builder.create();
        }
    }


    // Clase para mostrar el dialogo correspondiente al pulsar el boton para editar la contraseña y modificarlo con el nuevo valor introducido
    public static class editarPasswordDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.modal_editar_password, null))
                    .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SQLiteDatabase.loadLibs(getActivity());
                            UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(getActivity());
                            SQLiteDatabase db = mDbHelper.getReadableDatabase();

                            ContentValues values = new ContentValues();
                            EditText editText = getDialog().findViewById(R.id.password);
                            values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_PASSWORD, editText.getText().toString());

                            String selection = UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME + " LIKE ?";
                            String[] selectionArgs = { getArguments().getString("name_user") };

                            int count = db.update(
                                    UsersDBDatabase.TablaUsuarios.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);

                            Intent abrirPrincipal = new Intent("android.intent.action.INICIO");
                            abrirPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(abrirPrincipal);
                            Intent abrirLista = new Intent("android.intent.action.LISTAR");
                            startActivity(abrirLista);
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Cerrar el modal sin hacer cambios
                        }
                    });
            return builder.create();
        }
    }


    // Clase para mostrar el dialogo correspondiente al pulsar el boton para editar la localizacion (calle) y modificarlo con el nuevo valor introducido
    public static class editarLocalizacionDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.modal_editar_localizacion, null))
                    .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SQLiteDatabase.loadLibs(getActivity());
                            UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(getActivity());
                            SQLiteDatabase db = mDbHelper.getReadableDatabase();

                            ContentValues values = new ContentValues();
                            EditText editText = getDialog().findViewById(R.id.location);
                            values.put(UsersDBDatabase.TablaUsuarios.COLUMN_NAME_LOCATION, editText.getText().toString());

                            String selection = UsersDBDatabase.TablaUsuarios.COLUMN_NAME_NAME + " LIKE ?";
                            String[] selectionArgs = { getArguments().getString("name_user") };

                            int count = db.update(
                                    UsersDBDatabase.TablaUsuarios.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);

                            Intent abrirPrincipal = new Intent("android.intent.action.INICIO");
                            abrirPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(abrirPrincipal);
                            Intent abrirLista = new Intent("android.intent.action.LISTAR");
                            startActivity(abrirLista);
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Cerrar el modal sin hacer cambios
                        }
                    });
            return builder.create();
        }
    }


    // Esta clase se crea para descargar la imagen de perfil grande del usuario.
    // La conexion a Internet no puede hacerse desde el Thread principal, por eso se usa AsyncTask
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se asigna el menu de navegacion a la actividad actual
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cuando se seleccione une opcion se pasara a la actividad correspondiente
        int id = item.getItemId();
        if (id == R.id.listarMenu) {
            Intent abrirListar = new Intent("android.intent.action.LISTAR");
            startActivity(abrirListar);
            return true;
        }
        if (id == R.id.insertarMenu) {
            Intent abrirInsertar = new Intent("android.intent.action.INSERTAR");
            startActivity(abrirInsertar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
