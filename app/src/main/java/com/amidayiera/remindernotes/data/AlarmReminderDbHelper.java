package com.amidayiera.remindernotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//where we create our table
//SQLiteOpenHelper: helper class to manage database creation and version management

//  SQLiteDatabase: creates databases that are private to your application and provider
public class AlarmReminderDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "remindernotes.db";
    private static final int DATABASE_VERSION = 1;

    public AlarmReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
//    called when db is created for the first time
    public void onCreate(SQLiteDatabase db) {
//        create a string that contains the SQL statement to create the reminder table
        String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + AlarmReminderContract.ReminderNotesEntry.TABLE_NAME + " ("
                + AlarmReminderContract.ReminderNotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AlarmReminderContract.ReminderNotesEntry.KEY_TITLE + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_DATE  + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_TIME  + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT  + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO  + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE  + " TEXT NOT NULL, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_NOTES  + " TEXT, "
                + AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE  + " TEXT NOT NULL" + ");";

//        Execute SQL statement
        db.execSQL(SQL_CREATE_REMINDER_TABLE);
    }

    @Override
//    called when db needs to be upgraded
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
