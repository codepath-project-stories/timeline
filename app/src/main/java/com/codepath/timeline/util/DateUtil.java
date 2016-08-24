package com.codepath.timeline.util;

import android.util.Log;

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
}
