package com.fitaleks.walkwithme.utils;

import android.content.Context;
import android.text.format.Time;

import com.fitaleks.walkwithme.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by alexander on 23.06.16.
 */
public class DateUtils {
    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.
        final Time t = new Time();
        t.setToNow();
        final int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        final int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.date_today);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.date_yesterday);
        } else {
            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            return format.format(dateInMillis);
        }
    }

}
