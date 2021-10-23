package com.example.clubolimpus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.example.clubolimpus.data.ClubOlimpusContract.MemberEntry;



public class AddMemberActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText sportEditText;
    private Spinner genderSpinner;
    private int gender = MemberEntry.GENDER_UNKNOWN;

    private ArrayAdapter spinnerAdapter;
    private final static int EDIT_MEMBER_LOADER = 1234;
    Uri currentMemberUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();
        currentMemberUri = intent.getData();

        if (currentMemberUri == null){
            setTitle("Add member");
            invalidateOptionsMenu();
        }else {
            setTitle("Edit member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER,null,this);

        }

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        sportEditText = findViewById(R.id.sportEditText);
        genderSpinner = findViewById(R.id.genderSpinner);

        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.gender_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selectedGender)){
                    if (selectedGender.equals("Male")){
                        gender= MemberEntry.GENDER_MALE;
                    } else if (selectedGender.equals("Female")){
                        gender= MemberEntry.GENDER_FEMALE;
                    } else {gender= MemberEntry.GENDER_UNKNOWN;}
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = MemberEntry.GENDER_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentMemberUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_member:
                saveMember();
                return true;

            case R.id.delete_member:
                showDeleteDialog();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default: return true;
        }
    }
    private void saveMember(){
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)||TextUtils.isEmpty(lastName)||TextUtils.isEmpty(sport)){
            Toast.makeText(this, "Please, input data.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemberEntry.COLUMN_FIRST_NAME, firstName);
        contentValues.put(MemberEntry.COLUMN_LAST_NAME, lastName);
        contentValues.put(MemberEntry.COLUMN_SPORT, sport);
        contentValues.put(MemberEntry.COLUMN_GENDER,gender);

        if (currentMemberUri == null){

            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(MemberEntry.CONTENT_URI, contentValues);

            if(uri == null){
                Toast.makeText(this, "(((", Toast.LENGTH_SHORT).show();
            } else
                {Toast.makeText(this, ")))", Toast.LENGTH_SHORT).show();}

        }
        else{

            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues, null, null);
            if(rowsChanged == 0){
                Toast.makeText(this, "(((", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "update)))", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_FIRST_NAME,
                MemberEntry.COLUMN_LAST_NAME,
                MemberEntry.COLUMN_GENDER,
                MemberEntry.COLUMN_SPORT
        };

        return new CursorLoader(this,
                currentMemberUri,
                projection,
                null, null,null);

    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()){
            int firstNameColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_FIRST_NAME);
            int lastNameColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_LAST_NAME);
            int genderColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_GENDER);
            int sportColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_SPORT);

            String firstName = data.getString(firstNameColumnIndex);
            String lastName = data.getString(lastNameColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            String sport = data.getString(sportColumnIndex);

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            sportEditText.setText(sport);

            switch (gender){
                case MemberEntry.GENDER_UNKNOWN:
                    genderSpinner.setSelection(0);
                    break;
                case MemberEntry.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case MemberEntry.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;

            }
        }
    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {

    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete member?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember(){
        if(currentMemberUri != null){
            int rawsDeleted = getContentResolver().delete(currentMemberUri,null,null);
            if(rawsDeleted == 0){
                Toast.makeText(this, "delete(((", Toast.LENGTH_SHORT).show();
            }else {Toast.makeText(this, "delete)))", Toast.LENGTH_SHORT).show();}
        }
        finish();
    }
}