package com.codepath.timeline.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
    DB date is in the following format: 2016-08-22T19:22:54.695Z
 */
public class DateUtil {
  private static final String TAG = DateUtil.class.getSimpleName();
  private static final String TIMELINE_DB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private static SimpleDateFormat dateFormatter = new SimpleDateFormat(TIMELINE_DB_DATE_FORMAT, Locale.ENGLISH);

  private static long getDateMillis(String rawJsonDate) throws ParseException {
    long dateMillis = 0;

    dateFormatter.setLenient(true);
    dateMillis = dateFormatter.parse(rawJsonDate).getTime();
    return dateMillis;
  }

  private static Date getDate(String dateFromDB) throws ParseException {
    Date date = dateFormatter.parse(dateFromDB);
    return date;
  }

  public static String getYear(String dateFromDB) {
    String year = "";

    try {
      Date date = getDate(dateFromDB);
      year = (String) android.text.format.DateFormat.format("yyyy", date);
    } catch (ParseException e) {
      Log.d(TAG, "Error extracting the `year` from raw string");
    }

    return year;
  }

  /*
      INPUT: 2016-08-22T19:22:54.695Z
      EXPECTED OUTPUT: AUG 22
   */
  public static String getFormattedTimelineDate(Context context, Date date) {
    long dateMillis = date.getTime();
    String formattedDate = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE);


    Log.d(TAG, "getFormattedDate: " + date);
    return formattedDate;
  }

  public static Date getCurrentDate() {
    Calendar cal = Calendar.getInstance();
    String formattedDate = dateFormatter.format(cal.getTime());
    Log.d(TAG, "getCurrentDate: " + formattedDate);

    Log.d(TAG, "getDate: " + cal.getTime());
    return cal.getTime();
  }
}
