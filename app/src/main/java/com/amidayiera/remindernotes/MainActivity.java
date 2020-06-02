package com.amidayiera.remindernotes;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;




import com.amidayiera.remindernotes.data.AlarmReminderContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


//  1. CursorAdapter fits between a Cursor(data source) and the ListView (visual Representation)
//  2. define adapter to describe process of projecting cursor's data into a view by overriding the
//  newView and bindView methods - done in the AlarmCursorAdapter class
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    AlarmCursorAdapter mCursorAdapter;
    ListView reminderListView;

    private static final int VEHICLE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        getSupportLoaderManager().initLoader(VEHICLE_LOADER, null, this);
        reminderListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        reminderListView.setEmptyView(emptyView);

        mCursorAdapter = new AlarmCursorAdapter(this, R.layout.activity_reminders, null, null, null, 0) ;
        reminderListView.setAdapter(mCursorAdapter);

        reminderListView.setOnItemClickListener((adapterView, view, position, id) -> {

            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);

            Uri currentVehicleUri = ContentUris.withAppendedId(AlarmReminderContract.ReminderNotesEntry.CONTENT_URI, id);

            // Set the URI on the data field of the intent
            intent.setData(currentVehicleUri);

            startActivity(intent);

        });


        FloatingActionButton mAddReminderButton = findViewById(R.id.fab);

        mAddReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddReminderActivity.class);
            startActivity(intent);
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmReminderContract.ReminderNotesEntry._ID,
                AlarmReminderContract.ReminderNotesEntry.KEY_TITLE,
                AlarmReminderContract.ReminderNotesEntry.KEY_DATE,
                AlarmReminderContract.ReminderNotesEntry.KEY_TIME,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE,
                AlarmReminderContract.ReminderNotesEntry.KEY_NOTES,
                AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE

        };

        return new CursorLoader(this,   // Parent activity context
                AlarmReminderContract.ReminderNotesEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
