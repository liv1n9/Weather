package com.example.weather.model.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SimpleDate {
    private String dayOfWeek;
    private String dayOfMonth;

    public SimpleDate(String timezone, Long timeStamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTime(new Date(timeStamp));
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        dayOfWeek = sdf.format(new Date(timeStamp));
        dayOfMonth = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }
}
