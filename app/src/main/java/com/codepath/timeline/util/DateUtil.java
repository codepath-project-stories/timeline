package com.codepath.timeline.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import org.xml.sax.ErrorHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
    DB date is in the following format: 2016-08-22T19:22:54.695Z
 */
public class DateUtil {
  private static final String TAG = DateUtil.class.getSimpleName();
  private static final String TIMELINE_DB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private static long getDateMillis(String rawJsonDate) throws ParseException {
    long dateMillis = 0;

    SimpleDateFormat sf = new SimpleDateFormat(TIMELINE_DB_DATE_FORMAT, Locale.ENGLISH);
    sf.setLenient(true);
    dateMillis = sf.parse(rawJsonDate).getTime();
    return dateMillis;
  }

  private static Date getDate(String dateFromDB) throws ParseException {
    SimpleDateFormat parser = new SimpleDateFormat(TIMELINE_DB_DATE_FORMAT, Locale.US);
    Date date = parser.parse(dateFromDB);
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
  public static String getFormattedTimelineDate(Context context, String dateFromDB) {
    String date = "";
    try {
      long dateMillis = getDateMillis(dateFromDB);
      date = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE);
    } catch (ParseException e) {
      Log.d(TAG, "Exception from getFormattedDate()");
    }

    Log.d(TAG, "getFormattedDate: " + date);
    return date;
  }
}
