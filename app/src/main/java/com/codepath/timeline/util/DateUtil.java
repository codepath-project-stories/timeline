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

  /*
    INPUT: 2016-08-22T19:22:54.695Z
    EXPECTED OUTPUT: 2016
 */
  public static String getYear(Date date) {
    return (String) android.text.format.DateFormat.format("yyyy", date);
  }

  /*
      INPUT: 2016-08-22T19:22:54.695Z
      EXPECTED OUTPUT: AUG 22
   */
  public static String getFormattedTimelineDate(Context context, Date date) {
    String formattedDate = new SimpleDateFormat("MMM dd").format(date.getTime());
    Log.d(TAG, "getFormattedTimelineDate: " + date);

    return formattedDate;
  }

  public static Date getCurrentDate() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }

  public static String getFormattedDate(Date date) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(TIMELINE_DB_DATE_FORMAT, Locale.ENGLISH);
    String formattedDate = dateFormatter.format(date);
    Log.d(TAG, "getFormattedDate: input=" + date.toString() + ";output=" + formattedDate);

    return formattedDate;
  }
}
