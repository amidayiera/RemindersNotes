package com.amidayiera.remindernotes;
//  this activity is for a new reminder or updating a particular reminder

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.amidayiera.remindernotes.data.AlarmReminderContract;
import com.amidayiera.remindernotes.reminder.AlarmScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Objects;

public class AddReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VEHICLE_LOADER = 0;


    private EditText mTitleText, mAddNotes ;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Switch mRepeatSwitch;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mNotes;
    private String mActive;

    private Uri mCurrentReminderUri;
    private boolean mVehicleHasChanged = false;

    // Values for orientation change.
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_NOTES = "notes_key";
    private static final String KEY_ACTIVE = "active_key";


    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener mTouchListener = (view, motionEvent) -> {
        mVehicleHasChanged = true;
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();

        if (mCurrentReminderUri == null) {
            setTitle("Add Reminder Details");
//            setTitle(getString(R.string.editor_activity_title_new_reminder));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a reminder that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Reminder");
//            setTitle(getString(R.string.editor_activity_title_new_reminder));
//            trigger loader when current reminder uri is not known
//            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER,null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);

            getSupportLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
        }



        // Initialize Views
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mTitleText =  findViewById(R.id.reminder_title);
        mDateText =  findViewById(R.id.select_date);
        mTimeText =  findViewById(R.id.select_time);
        mRepeatText =  findViewById(R.id.repeat);
        mRepeatNoText =  findViewById(R.id.repeat_every);
        mRepeatTypeText =  findViewById(R.id.repetition_intervals);
        mRepeatSwitch =  findViewById(R.id.repeat_switch);
        mAddNotes =  findViewById(R.id.add_notes);
        mFAB1 =  findViewById(R.id.starred1);
        mFAB2 =  findViewById(R.id.starred2);

        // Initialize default values
        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
//      plus one because it starts from 0
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });
//        Set up Reminder Notes section EditText
        mAddNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNotes = s.toString().trim();
                mAddNotes.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), mRepeatNo, getString(R.string.text_space), mRepeatType, getString(R.string.text_s)));
//        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            String savedNotes = savedInstanceState.getString(KEY_NOTES);
            mAddNotes.setText(savedNotes);
            mNotes = savedNotes;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup up active buttons
        assert mActive != null;
        if (mActive.equals("false")) {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);

        } else if (mActive.equals("true")) {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void setSupportActionBar(Toolbar mToolbar) {
    }
    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_NOTES, mAddNotes.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    // On clicking Time picker
    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(
                this,
                null,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
//        tpd.setThemeDark(false);
//        if you're calling it from a support fragment
        tpd.show();
    }

    // On clicking Date picker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(
                this,
                null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();
    }

//    Obtain time from time picker
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

//    Obtain date from date picker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    // On clicking the active button
    public void selectFab1(View v) {
        mFAB1 = findViewById(R.id.starred1);
        mFAB1.setVisibility(View.GONE);
        mFAB2 = findViewById(R.id.starred2);
        mFAB2.setVisibility(View.VISIBLE);
        mActive = "true";
    }

    // On clicking the inactive button
    public void selectFab2(View v) {
        mFAB2 = findViewById(R.id.starred2);
        mFAB2.setVisibility(View.GONE);
        mFAB1 = findViewById(R.id.starred1);
        mFAB1.setVisibility(View.VISIBLE);
        mActive = "false";
    }
    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), mRepeatNo, getString(R.string.text_space), mRepeatType, getString(R.string.text_s)));
//            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, (dialog, item) -> {

            mRepeatType = items[item];
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), mRepeatNo, getString(R.string.text_space), mRepeatType, getString(R.string.text_s)));
//                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // On clicking repeat interval button
    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                (dialog, whichButton) -> {

                    if (input.getText().toString().length() == 0) {
                        mRepeatNo = Integer.toString(1);
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), mRepeatNo, getString(R.string.text_space), mRepeatType, getString(R.string.text_s)));
//                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                    }
                    else {
                        mRepeatNo = input.getText().toString().trim();
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), mRepeatNo, getString(R.string.text_space), mRepeatType, getString(R.string.text_s)));
//                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                    }
                });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // do nothing
        });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new reminder, hide the "Delete" menu item.
        if (mCurrentReminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save_reminder:
                if (mTitleText.getText().toString().length() == 0){
                    mTitleText.setError("Reminder Title cannot be blank!");
                }

                else {
                    saveReminder();
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_discard_reminder:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the reminder hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mVehicleHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        (dialogInterface, i) -> {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveReminder() {
           /*   if (mCurrentReminderUri == null ) {
            // Since no fields were modified, we can return early without creating a new reminder.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
            */
        ContentValues values = new ContentValues();

        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_TITLE, mTitle);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_DATE, mDate);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_TIME, mTime);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT, mRepeat);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO, mRepeatNo);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE, mRepeatType);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_NOTES, mNotes);
        values.put(AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE, mActive);


        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        long selectedTimestamp =  mCalendar.getTimeInMillis();

        // Check repeat type
        switch (mRepeatType) {
            case "Minute":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                break;
            case "Hour":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                break;
            case "Day":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                break;
            case "Week":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                break;
            case "Month":
                mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                break;
        }

        if (mCurrentReminderUri == null) {
            // This is a NEW reminder, so insert a new reminder into the provider,
            // returning the content URI for the new reminder.
            Uri newUri = getContentResolver().insert(AlarmReminderContract.ReminderNotesEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_saving_reminder),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.reminder_saved),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentReminderUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_updating_reminder),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.reminder_updated),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Create a new notification
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                new AlarmScheduler().setRepeatAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new AlarmScheduler().setAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri);
            }

            Toast.makeText(this, "Alarm time is " + selectedTimestamp,
                    Toast.LENGTH_LONG).show();
        }

        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), R.string.saved,
                Toast.LENGTH_SHORT).show();

    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_discard_changes);
            builder.setPositiveButton(R.string.dialog_discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.dialog_keep_editing, (dialog, id) -> {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the reminder.
                if (dialog != null) {
                    dialog.dismiss();
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_this_reminder);
        builder.setPositiveButton(R.string.dialog_delete, (dialog, id) -> {
            // User clicked the "Delete" button, so delete the reminder.
            deleteReminder();
        });
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, id) -> {
            // User clicked the "Cancel" button, so dismiss the dialog
            // and continue editing the reminder.
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteReminder() {
        // Only perform the delete if this is an existing reminder.
        if (mCurrentReminderUri != null) {
            // Call the ContentResolver to delete the reminder at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentreminderUri
            // content URI already identifies the reminder that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReminderUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.error_deleting_reminder),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.reminder_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @NonNull
    @Override
//    assist us with editing
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                AlarmReminderContract.ReminderNotesEntry._ID,
                AlarmReminderContract.ReminderNotesEntry.KEY_TITLE,
                AlarmReminderContract.ReminderNotesEntry.KEY_DATE,
                AlarmReminderContract.ReminderNotesEntry.KEY_TIME,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO,
                AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE,
                AlarmReminderContract.ReminderNotesEntry.KEY_NOTES,
                AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentReminderUri,         // Query the content URI for the current reminder
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_DATE);
            int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_TIME);
            int repeatColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT);
            int repeatNoColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO);
            int repeatTypeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE);
            int notesColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_NOTES);
            int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String repeat = cursor.getString(repeatColumnIndex);
            String repeatNo = cursor.getString(repeatNoColumnIndex);
            String repeatType = cursor.getString(repeatTypeColumnIndex);
            String notes =cursor.getString(notesColumnIndex);
            String active = cursor.getString(activeColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleText.setText(title);
            mDateText.setText(date);
            mTimeText.setText(time);
            mRepeatNoText.setText(repeatNo);
            mRepeatTypeText.setText(repeatType);
            mRepeatText.setText(String.format("%s%s%s%s%s", getString(R.string.text_every), repeatNo, getString(R.string.text_space), repeatType, getString(R.string.text_s)));
            mAddNotes.setText(notes);
            // Setup up active buttons
            // Setup repeat switch
            if (repeat.equals("false")) {
                mRepeatSwitch.setChecked(false);
                mRepeatText.setText(R.string.repeat_off);

            } else if (repeat.equals("true")) {
                mRepeatSwitch.setChecked(true);
            }

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

  
}
