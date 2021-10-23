package com.example.clubolimpus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.clubolimpus.data.ClubOlimpusContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.clubolimpus.data.ClubOlimpusContract.*;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MEMBER_LOADER = 123;
    MemberCursorAdapter memberCursorAdapter;

    ListView dataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataListView = findViewById(R.id.dataListView);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(addIntent);

            }
        });
        memberCursorAdapter = new MemberCursorAdapter(this,null);
        dataListView.setAdapter(memberCursorAdapter);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent editIntent = new Intent(MainActivity.this, AddMemberActivity.class);
                Uri currentMemberUri = ContentUris.withAppendedId(MemberEntry.CONTENT_URI, id);
                editIntent.setData(currentMemberUri);
                startActivity(editIntent);
            }
        });

        getSupportLoaderManager().initLoader(MEMBER_LOADER,null,this);
    }




    @Override
    public Loader<Cursor> onCreateLoader(int id,  Bundle args) {

        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_FIRST_NAME,
                MemberEntry.COLUMN_LAST_NAME,

                MemberEntry.COLUMN_SPORT
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                MemberEntry.CONTENT_URI,
                projection,
                null, null,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {
        memberCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {
        memberCursorAdapter.swapCursor(null);
    }
}