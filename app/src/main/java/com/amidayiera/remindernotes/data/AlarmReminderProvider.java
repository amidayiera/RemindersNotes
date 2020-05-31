package com.amidayiera.remindernotes.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//  helps to assess the data from the database
//  ContentProvider : manages access to central repositroy of data.
//  One of your classes implements a subclass ContentProvider,
//  which is the interface between your provider and other applications.
//  Although content providers are meant to make data available to other applications,
//  you may of course have activities in your application that allow the user to
//  query and modify the data managed by your provider.

public class AlarmReminderProvider extends ContentProvider {

    public static final String LOG_TAG = AlarmReminderProvider.class.getSimpleName();

    //    reminder for a single and multiple reminders
    private static final int REMINDER = 100;

    private static final int REMINDER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//create the URI matcher
    static {
    /*
     * The calls to addURI() go here, for all of the content URI patterns that the provider
     * should recognize.
     */

    sUriMatcher.addURI(AlarmReminderContract.CONTENT_AUTHORITY, AlarmReminderContract.PATH_VEHICLE, REMINDER);

    sUriMatcher.addURI(AlarmReminderContract.CONTENT_AUTHORITY, AlarmReminderContract.PATH_VEHICLE + "/#", REMINDER_ID);
    }

    private AlarmReminderDbHelper mDBHelper;
    @Override
//    initialize provider. Provider is not created until ContentResolver object tries to create it
    public boolean onCreate() {
        mDBHelper = new AlarmReminderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
//    retrieves data from provider. Uses arguments to select table to query, rows and columns to
//    return and the sort order of result. Returns data as Cursor object
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

//          This cursor will hold the result of the query
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                cursor = database.query(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case REMINDER_ID:
                selection = AlarmReminderContract.ReminderNotesEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
//    Return the MIME type corresponding to a content URI.
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return AlarmReminderContract.ReminderNotesEntry.CONTENT_LIST_TYPE;
            case REMINDER_ID:
                return AlarmReminderContract.ReminderNotesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI "+ uri + " with match " + match);
        }

    }

    @Nullable
    @Override
//    insert a new row into the provider.  Use the arguments to select the destination table and to
//    get the column values to use. Return a content URI for the newly-inserted row
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case REMINDER:
                return insertReminder(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is ont supported for " + uri);
        }
    }

    private Uri insertReminder(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        long id = database.insert(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
//    Delete rows from your provider. Use the arguments to select the table and the rows to delete.
//    Return the number of rows deleted.
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                rowsDeleted = database.delete(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER_ID:
                selection = AlarmReminderContract.ReminderNotesEntry._ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowsDeleted = database.delete(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
//    Update existing rows in your provider. Use the arguments to select the table and rows to
//    update and to get the updated column values. Return the number of rows updated.
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case REMINDER:
                return updateReminder(uri, contentValues, selection, selectionArgs);
            case REMINDER_ID:
                selection = AlarmReminderContract.ReminderNotesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateReminder(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is ont supported for " + uri);
        }
    }

    private int updateReminder(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(AlarmReminderContract.ReminderNotesEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
