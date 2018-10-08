package com.qlh.sdk.myview.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者：QLH on 2016/12/7 17:24
 * 描述：日期处理
 */
public class DateUtil {

    private final static String MONTH_FORMAT = "yyyy-MM";
    private final static String DAY_FORMAT = "yyyy-MM-dd";
    private final static String HOUR_FORMAT = "yyyy-MM-dd HH";
    private final static String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    private final static String SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String MILL_SECOND_FORMAT = "yyyy-MM-dd HH:mm:ss:SSSS";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * yyyy-MM-dd HH:mm:ss:SSSS  毫秒
     *
     * @return String
     */
    public static String formatDateToMillSecond(Date time){
        SimpleDateFormat format = new SimpleDateFormat(MILL_SECOND_FORMAT);
        return format.format(time);

    }

    /**
     * yyyy-MM-dd HH:mm:ss 秒
     *
     * @return String
     */
   public static String formatDateToSecond(Date time){
       SimpleDateFormat format = new SimpleDateFormat(SECOND_FORMAT);
       return format.format(time);

}

/**
 * yyyy-MM-dd HH:mm 分
 *
 * @return String
 */
public static String formatDateToMinutes(Date time){
    SimpleDateFormat format = new SimpleDateFormat(MINUTE_FORMAT);
    return format.format(time);

}


    /**
     * yyyy-MM-dd HH:mm:ss 时
     *
     * @return String
     */
    public static String formatDateToHour(Date time){
        SimpleDateFormat format = new SimpleDateFormat(HOUR_FORMAT);
        return format.format(time);

    }
    /**
     * yyyy-MM-dd  天
     *
     * @return String
     */
    public static String formatDateToDay(Date time){
        SimpleDateFormat format = new SimpleDateFormat(DAY_FORMAT);
        return format.format(time);

    }
    /**
     * yyyy-MM   月
     *
     * @return String
     */
    public static String formatDateToMonth(Date time){
        SimpleDateFormat format = new SimpleDateFormat(MONTH_FORMAT);
        return format.format(time);

    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        String currentDate = formatDateToDay(cal.getTime());
        return currentDate;
    }

    /**
     *
     *  得到与当前日期偏移量为X的日期。
     *@param offset 天
     * @return String
     */
    public static String getOffsetDate(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, offset);
        String currentDate = formatDateToDay(cal.getTime());
        return currentDate;
    }
    /**
     * 将字符串时间如：2017-12-26格式化成long型
     * **/

    public static Long StringToDate(String date){
        SimpleDateFormat format = new SimpleDateFormat(DAY_FORMAT);
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d.getTime();
    }

}
