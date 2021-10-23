package com.example.clubolimpus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.clubolimpus.data.ClubOlimpusContract.MemberEntry;

public class OlimpusDbHelper extends SQLiteOpenHelper {

    public OlimpusDbHelper(Context context){
        super(context, ClubOlimpusContract.DATABASE_NAME, null, ClubOlimpusContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + MemberEntry.TABLE_NAME + "("
                + MemberEntry._ID + " INTEGER PRIMARY KEY,"
                + MemberEntry.COLUMN_FIRST_NAME + " TEXT,"
                + MemberEntry.COLUMN_LAST_NAME + " TEXT,"
                + MemberEntry.COLUMN_GENDER + " INTEGER NOT NULL,"
                + MemberEntry.COLUMN_SPORT + " TEXT" + ")";

        db.execSQL(CREATE_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ClubOlimpusContract.DATABASE_NAME);
        onCreate(db);
    }
}
