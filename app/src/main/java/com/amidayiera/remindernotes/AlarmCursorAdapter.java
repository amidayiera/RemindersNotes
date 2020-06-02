package com.amidayiera.remindernotes;
//  where we get to bind data before
//  alarm scheduler - deals with the alarm manager

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amidayiera.remindernotes.data.AlarmReminderContract;

class AlarmCursorAdapter<TextDrawable, ColorGenerator> extends CursorAdapter {
//    create constructor matching super
    private TextView mTitleText, mDateAndTimeText, mRepeatInfoText, mNotesText;
    private ImageView mActiveImage , mThumbnailImage;
    private ColorGenerator mColorGenerator;
    private TextDrawable mDrawableBuilder;
    AlarmCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_reminders, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//      initialize views needed
        mTitleText = view.findViewById(R.id.recycle_title);
        mDateAndTimeText = view.findViewById(R.id.recycle_date_time);
        mRepeatInfoText = view.findViewById(R.id.recycle_repeat_info);
        mActiveImage = view.findViewById(R.id.active_image);
        mNotesText = view.findViewById(R.id.recycle_notes);
//        mThumbnailImage = view.findViewById(R.id.thumbnail_image);

        int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_NO);
        int repeatTypeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_REPEAT_TYPE);
        int notesColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_NOTES);
        int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.ReminderNotesEntry.KEY_ACTIVE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String notes = cursor.getString(notesColumnIndex);
        String active = cursor.getString(activeColumnIndex);

        String dateTime = date + " " + time;

        setReminderTitle(title);
        setReminderDateTime(dateTime);
        setReminderRepeatInfo(repeat, repeatNo, repeatType);
        setActiveImage(active);
        setReminderNotes(notes);



    }

    private void setReminderNotes(String notes) {
        mNotesText.setText(notes);
    }

    private void setActiveImage(String active) {
        if(active.equals("true")){
            mActiveImage.setImageResource(R.drawable.ic_notifications_on);
        }else if (active.equals("false")) {
            mActiveImage.setImageResource(R.drawable.ic_notifications_off);
        }
    }

    private void setReminderDateTime(String dateTime) {
        mDateAndTimeText.setText(dateTime);
    }

    private void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

//        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
//        mDrawableBuilder = TextDrawable.builder()
//                .buildRound(letter, color);
//        mThumbnailImage.setImageDrawable((Drawable) mDrawableBuilder);
    }

    private void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType){
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
        
    }


}
