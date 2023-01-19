package com.example.sorting_tome;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        //format timestamp to DD/MM/YYYY
        String date = DateFormat.format("DD/MM/YYYY", cal).toString();

        return date;
    }
}
