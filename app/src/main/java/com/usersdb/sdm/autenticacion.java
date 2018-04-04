package com.usersdb.sdm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import net.sqlcipher.database.*;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class autenticacion extends AppCompatActivity {
    Context context = this;
    EditText txtUsuario;
    EditText txtPassword;
    Button iniciarSesion;
    Button registrarse;
    public static final String MisPreferencias = "MyPrefs";
    public static final String Usuario = "key_user";
    public static final String Password = "key_pass";
    public static final String SQL_Password = "key_sql";
    SharedPreferences sprefs;
    String key_string;

    public static final String SALT = "usersDB";
    public static final String ALIAS = "keystore";
    public KeyStore keyStore;
    public static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        // Se mira si en el SharedPreferences hay un valor para la contraseña de cifrado de SQLite. Si no es así, se genera
        if (!sprefs.contains(SQL_Password)) {

            try {
                loadKeyStore();
                generateNewKeyPair(ALIAS, context);
                PublicKey publicKey = loadPublicKey(ALIAS);
                key_string = publicKeyToString(publicKey);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = sprefs.edit();
            editor.putString(SQL_Password, key_string);
            editor.apply();
        } else {
            key_string = sprefs.getString(SQL_Password, "");
        }


        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se obtienen los valores del cuestionario
                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();

                // Si algun campo no está rellenado, se le indica al usuario
                if (usuario.equals("") || password.equals("")) {
                    if (usuario.equals("")) {
                        txtUsuario.setError("Introduzca un nombre de usuario");
                    }
                    if (password.equals("")) {
                        txtPassword.setError("Introduzca una contraseña");
                    }
                } else {
                    // Se preparan las variables para leer de la BD
                    SQLiteDatabase.loadLibs(context);
                    UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                    SQLiteDatabase db = mDbHelper.getReadableDatabase(key_string);

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
                        String user_sprefs = sprefs.getString(Usuario, "");
                        String pass_sprefs = sprefs.getString(Password, "");

                        if (password.equals(pass_sprefs) && usuario.equals(user_sprefs)) {
                            Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                            startActivity(paginaPrincipal);
                            Toast toast = Toast.makeText(context, "¡Bienvenido/a, " + usuario + "!", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            String password_hashed = getSHA512(password, SALT);

                            // se comprueba si la contraseña de la BBDD coincide con la insertada por el usuario
                            cursor.moveToFirst();
                            String passwordDB = cursor.getString(cursor.getColumnIndexOrThrow(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD));

                            // Si no coinciden, se le indica al usuario que la contraseña no es correcta
                            if (!passwordDB.equals(password_hashed)) {
                                Toast toast = Toast.makeText(context, "La contraseña introducida no es correcta para el usuario indicado", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            // Si coinciden, se guardan los datos de usuarios en las preferencias (si no son los mismos) y se inicia sesión (se pasa al menú principal)
                            else {
                                SharedPreferences.Editor editor = sprefs.edit();
                                editor.putString(Usuario, usuario);
                                editor.putString(Password, password_hashed);
                                editor.apply();

                                Intent paginaPrincipal = new Intent("android.intent.action.INICIO");
                                startActivity(paginaPrincipal);
                                Toast toast = Toast.makeText(context, "¡Bienvenido/a, " + usuario + "!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                    cursor.close();
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
                if (usuario.equals("") || password.equals("")) {
                    if (usuario.equals("")) {
                        txtUsuario.setError("Introduzca un nombre de usuario");
                    }
                    if (password.equals("")) {
                        txtPassword.setError("Introduzca una contraseña");
                    }
                } else {
                    // Se preparan las variables para leer de la BD
                    SQLiteDatabase.loadLibs(context);
                    UsersDBDatabase.UsersDBDatabaseHelper mDbHelper = new UsersDBDatabase.UsersDBDatabaseHelper(context);
                    SQLiteDatabase db = mDbHelper.getReadableDatabase(key_string);

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
                        SQLiteDatabase.loadLibs(context);
                        String password_hashed = getSHA512(password, SALT);
                        SQLiteDatabase db2 = mDbHelper.getWritableDatabase(key_string);

                        ContentValues values = new ContentValues();
                        values.put(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_USERNAME, usuario);
                        values.put(UsersDBDatabase.TablaUsuariosSesion.COLUMN_NAME_PASSWORD, password_hashed);
                        db2.insert(UsersDBDatabase.TablaUsuariosSesion.TABLE_NAME, null, values);

                        Toast toast = Toast.makeText(context, "Se ha añadido el nuevo usuario a la BBDD", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    cursor.close();
                }
            }
        });
    }


    private String getSHA512(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


    protected void onPause() {
        super.onPause();
        finish();
    }




    public void loadKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void generateNewKeyPair(String alias, Context context) throws Exception {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        // expires 1 year from today
        end.add(Calendar.YEAR, 1);

        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                                    .setAlias(alias)
                                    .setSubject(new X500Principal("CN=" + alias))
                                    .setSerialNumber(BigInteger.TEN)
                                    .setStartDate(start.getTime())
                                    .setEndDate(end.getTime())
                                    .build();

        // use the Android keystore
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA",ANDROID_KEYSTORE);
        gen.initialize(spec);
        // generates the keypair
        gen.generateKeyPair();
    }


    public PublicKey loadPublicKey(String alias) throws Exception {
        if (!keyStore.isKeyEntry(alias)) {
            Log.e("TAG", "Could not find key alias: " + alias);
            return null;
        }

        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.e("TAG", " alias: " + alias + " is not a PrivateKey");
            return null;
        }
        Certificate cert = keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();

        return publicKey;
    }


    public String publicKeyToString(PublicKey publicKey){
        byte[] pubKey = publicKey.getEncoded();
        String pubKeyString = Base64.encodeToString(pubKey,  Base64.NO_WRAP);
        return pubKeyString;
    }
}