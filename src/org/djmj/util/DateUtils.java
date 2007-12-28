/*
 * DateUtils.java
 *
 * Created on March 10, 2003, 11:53 PM
 */

package org.djmj.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author  Javier
 */
public class DateUtils 
{    
   // TODO: hardcoded format and locale, need to make this more flexible    
    private static DateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy", 
                                        new DateFormatSymbols(Locale.US));
    
    /** Creates a new instance of DateUtils */
    private DateUtils() 
    {
    }
    
    public static String dateToString(Date date)
    {
        try {
            return dateFormat.format(date);
        }
        catch (Exception e) {
            // TODO: deal with this exception
            e.printStackTrace(System.err);
            return "";
        }
    }  
    
    public static Date stringToDate(String date)
    {
        try {
            return dateFormat.parse(date);
        } 
        catch (Exception e) {
            // TODO: deal with this exception
            e.printStackTrace(System.err);
            return new Date();
        }
    }
}
