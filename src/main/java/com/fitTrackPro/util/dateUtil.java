package com.fitTrackPro.util;


import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Date Utility Class
 * Provides date formatting methods
 */
public class dateUtil {
    
    private static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    private static final String DISPLAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    
    /**
     * Formats a Date for display
     */
    public static String formatDisplayDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Formats a Timestamp for display
     */
    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_TIME_FORMAT);
        return sdf.format(timestamp);
    }
    
    /**
     * Gets current date
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
    
    /**
     * Checks if a date is expired
     */
    public static boolean isExpired(Date expiryDate) {
        if (expiryDate == null) {
            return true;
        }
        Date today = getCurrentDate();
        return expiryDate.before(today);
    }
}