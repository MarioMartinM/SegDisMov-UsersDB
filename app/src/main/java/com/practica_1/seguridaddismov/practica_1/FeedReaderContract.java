package com.practica_1.seguridaddismov.practica_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.view.View;

/**
 * Created by a0330659 on 07/02/2018.
 */

public final class FeedReaderContract {
    private FeedReaderContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_REGISTERED = "registered";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_REGISTERED + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "usuariosDB.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }



}
