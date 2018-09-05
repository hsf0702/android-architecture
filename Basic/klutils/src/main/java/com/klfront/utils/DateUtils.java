package com.klfront.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lujinlong on 2015/11/24.
 */
public class DateUtils {
    public static String FormatDate(long time) {
        Date date = new Date(time);
        return FormatDate(date);
    }

    public static String FormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String FormatDate(long time, String format) {
        Date date = new Date(time);
        return FormatDate(date, format);
    }

    public static String FormatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取格式化的当前时间
     * @param format
     * @return
     */
    public static String GetCurrentTime(String format) {
        return FormatDate(System.currentTimeMillis(), format);
    }

    /**
     * 获取相对当前时间按小时延后的时间
     * @param format
     * @param offHour
     * @return
     */
    public static String GetTimeAfterHours(String format, int offHour) {
        Date date = new Date();
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, offHour);
        Date result =  ca.getTime();
        return FormatDate(result, format);
    }

    /**
     * 获取按天数偏移的历史时间
     * @param format 时间格式
     * @param offday 倒回的天数
     * @return
     */
    public static String GetHistoryTime(String format,int offday) {
        Date date = new Date();
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_YEAR, - offday);
        Date result =  ca.getTime();
        return FormatDate(result, format);
    }


    public static long GetTimeByString(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static Date GetDateByString(String time,String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String getTimeDisplay(String time, String format) {
        long timevalue = GetTimeByString(time, format);
        Date date = new Date(timevalue);
        Date today = new Date();
        if (date.getYear() == today.getYear()) {
            if (date.getMonth() == today.getMonth()) {
                if (date.getDate() >= today.getDate() - 7) {
                    if (date.getDate() == today.getDate()) {
                        return FormatDate(date, "HH:mm");
                    } else if (date.getDate() == today.getDate() - 1) {
                        return "昨天";
                    } else {
                        int day = date.getDay();
                        switch (day) {
                            case 0:
                                return "周日";
                            case 1:
                                return "周一";
                            case 2:
                                return "周二";
                            case 3:
                                return "周三";
                            case 4:
                                return "周四";
                            case 5:
                                return "周五";
                            case 6:
                                return "周六";
                        }
                    }
                }
                return FormatDate(date, "MM-dd");
            }
            return FormatDate(date, "MM-dd");
        }
        return FormatDate(date, "yyyy-MM-dd");
    }


    public static long getDistanceDay(long time1, long time2) {
        long day = 0;
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time1 = GetTimeByString(str1,"yyyy-MM-dd HH:mm:ss");
        long time2 = GetTimeByString(str2,"yyyy-MM-dd HH:mm:ss");
        return getDistanceTime(time1,time2);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param time1 时间参数 1
     * @param time2 时间参数 2
     * @return String 返回值为：小时xx分xx秒
     */
    public static String getDistanceTime(long time1, long time2) {
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        return formatTime(diff);
    }

    /**
     * 根据时间毫秒数返回显示文本
     * @param ms time by unit of milliSecond
     * @return
     */
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms- day * dd)/hh;
        long minute =  (ms - day * dd - hour * hh) / mi;
        long second =  (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        if(day>0)
        {
            return strDay+"天 "+strHour + ":" + strMinute + ":" + strSecond;
        }
        else
        {
            if(hour > 0){
                return strHour + ":" +strMinute + ":" + strSecond;
            } else {
                return strMinute + ":" + strSecond;
            }
        }
    }
}
