package com.amidayiera.remindernotes.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

//  set up columns where we'll be saving data to in the SQLite db
public class AlarmReminderContract {
    private AlarmReminderContract() {}

//    name of package we're using
    public static final String CONTENT_AUTHORITY = "com.amidayiera.remindernotes";
//    points to content authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
//    path to table
    public static final String PATH_VEHICLE = "reminder-path";
//    inner class that implements BaseColumns
    public static final class ReminderNotesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE);
//        MIME type: standardized way to define that data type by giving it a unique name
//        helps ContentProvider that handles several diff types of data to keep things organized
//       ContentResolver class: provides application access to content model

//       CURSOR_DIR_BASE_TYPE: Uri containing a cursor of zero or more items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;
//       CURSOR_ITEM_BASE_TYPE : Uri containing a cursor of a single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;

        public final static String TABLE_NAME = "reminders";

        public static final String _ID = BaseColumns._ID;
        public static final String KEY_TITLE = "title";
        public static final String KEY_DATE = "date";
        public static final String KEY_TIME = "time";
        public static final String KEY_REPEAT = "repeat";
        public static final String KEY_REPEAT_NO = "repeat_no";
        public static final String KEY_REPEAT_TYPE = "repeat_type";
        public static final String KEY_NOTES = "notes";
        public static final String KEY_ACTIVE = "active";

    }
//    helper method to get the column string of the particular index - shortens the call
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString((cursor.getColumnIndex(columnName)));
    }
}

