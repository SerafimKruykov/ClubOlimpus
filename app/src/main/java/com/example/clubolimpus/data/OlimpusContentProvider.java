package com.example.clubolimpus.data;

import com.example.clubolimpus.data.ClubOlimpusContract.*;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import android.widget.Toast;


public class OlimpusContentProvider extends ContentProvider {

    private static final int members = 111;
    private static final int memberId = 222;

    OlimpusDbHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ClubOlimpusContract.AUTHORITY, ClubOlimpusContract.PATH_MEMBERS, members);
        uriMatcher.addURI(ClubOlimpusContract.AUTHORITY, ClubOlimpusContract.PATH_MEMBERS + "/#", memberId);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new OlimpusDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri,  String[] projection, String selection,  String[] selectionArgs,  String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = uriMatcher.match(uri);

        switch(match){
            case members:
                cursor = db.query(
                        MemberEntry.TABLE_NAME,
                        projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case memberId:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MemberEntry.TABLE_NAME,
                        projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert( Uri uri,  ContentValues values) {

        String firstName = values.getAsString(MemberEntry.COLUMN_FIRST_NAME);
        if (firstName == null) {
            throw new IllegalArgumentException("firstName((("+uri);
        }

        String lastName = values.getAsString(MemberEntry.COLUMN_LAST_NAME);
        if (lastName == null) {
            throw new IllegalArgumentException("lastName((("+uri);
        }

        Integer gender = values.getAsInteger(MemberEntry.COLUMN_GENDER);
        if(gender == null ||
                !(gender == MemberEntry.GENDER_UNKNOWN ||
                        gender == MemberEntry.GENDER_MALE ||
                        gender == MemberEntry.GENDER_FEMALE )){
            throw new IllegalArgumentException("gender((("+uri);
        }

        String sport = values.getAsString(MemberEntry.COLUMN_SPORT);
        if (sport == null) {
            throw new IllegalArgumentException("sport((("+uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        switch(match){
            case members:
               long id = db.insert(MemberEntry.TABLE_NAME, null, values);
               if(id == -1){
                   Log.e("insertMethod","Row not added"+uri);
                   return null;
               }

               getContext().getContentResolver().notifyChange(uri, null);
               return ContentUris.withAppendedId(uri,id);

               default:
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                return null;
        }

    }

    @Override
    public int update( Uri uri,  ContentValues values, String selection,  String[] selectionArgs) {

        if(values.containsKey(MemberEntry.COLUMN_FIRST_NAME)){
        String firstName = values.getAsString(MemberEntry.COLUMN_FIRST_NAME);
        if (firstName == null) {
            throw new IllegalArgumentException("firstName((("+uri);
        }}

        if(values.containsKey(MemberEntry.COLUMN_LAST_NAME)){
        String lastName = values.getAsString(MemberEntry.COLUMN_LAST_NAME);
        if (lastName == null) {
            throw new IllegalArgumentException("lastName((("+uri);
        }}

        if(values.containsKey(MemberEntry.COLUMN_GENDER)){
        Integer gender = values.getAsInteger(MemberEntry.COLUMN_GENDER);
        if(gender == null ||
                !(gender == MemberEntry.GENDER_UNKNOWN ||
                        gender == MemberEntry.GENDER_MALE ||
                        gender == MemberEntry.GENDER_FEMALE )){
            throw new IllegalArgumentException("gender((("+uri);
        }}

        if(values.containsKey(MemberEntry.COLUMN_SPORT)){
        String sport = values.getAsString(MemberEntry.COLUMN_SPORT);
        if (sport == null) {
            throw new IllegalArgumentException("sport((("+uri);
        }}

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch(match){
            case members:
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case memberId:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("update((("+uri);
        }
        if (rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch(match){
            case members:
                rowsDeleted = db.delete(MemberEntry.TABLE_NAME,  selection, selectionArgs);
                break;

            case memberId:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(MemberEntry.TABLE_NAME,  selection, selectionArgs);
                break;
            default:
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("delete((("+uri);
        }
        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }



    @Override
    public String getType( Uri uri) {

        int match = uriMatcher.match(uri);

        switch(match){
            case members:
                return MemberEntry.CONTENT_MULTIPLE_ITEM;

            case memberId:

                return MemberEntry.CONTENT_SINGLE_ITEM;

            default:
                Toast.makeText(getContext(), "Unknown URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("getType((("+uri);
        }


    }
}
