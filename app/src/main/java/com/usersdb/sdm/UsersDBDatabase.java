package com.usersdb.sdm;

import android.content.Context;
import net.sqlcipher.database.*;
import android.provider.BaseColumns;


public final class UsersDBDatabase {
    private UsersDBDatabase() {
    }

    // Tabla "usuarios" y columnas que la forman
    public static class TablaUsuarios implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_LARGEPICTURE = "largepicture";
        public static final String COLUMN_NAME_REGISTERED = "registered";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    // Tabla "usuariosSesion" y columnas que la forman
    public static class TablaUsuariosSesion implements BaseColumns {
        public static final String TABLE_NAME = "usuariosSesion";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";


    // Query para la creacion de la tabla "usuarios"
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TablaUsuarios.TABLE_NAME + " (" +
                    TablaUsuarios._ID + " INTEGER PRIMARY KEY," +
                    TablaUsuarios.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_LARGEPICTURE + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_REGISTERED + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                    TablaUsuarios.COLUMN_NAME_PASSWORD + TEXT_TYPE + " )";

    // Query para la creacion de la tabla "usuariosSesion"
    private static final String SQL_CREATE_ENTRIES_SESION =
            "CREATE TABLE " + TablaUsuariosSesion.TABLE_NAME + " (" +
                    TablaUsuariosSesion._ID + " INTEGER PRIMARY KEY," +
                    TablaUsuariosSesion.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                    TablaUsuariosSesion.COLUMN_NAME_PASSWORD + TEXT_TYPE + " )";


    // Query para la eliminacion de la tabla "usuarios"
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TablaUsuarios.TABLE_NAME;

    // Query para la eliminacion de la tabla "usuariosSesion"
    private static final String SQL_DELETE_ENTRIES_SESION =
            "DROP TABLE IF EXISTS " + TablaUsuariosSesion.TABLE_NAME;



    // Clase para la creacion de la base de datos
    public static class UsersDBDatabaseHelper extends SQLiteOpenHelper {
        // Aumentar la version si se modifica la estructura de la BBDD
        public static final int DATABASE_VERSION = 5;
        public static final String DATABASE_NAME = "usuariosDB.db";

        public UsersDBDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES_SESION);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_ENTRIES_SESION);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
