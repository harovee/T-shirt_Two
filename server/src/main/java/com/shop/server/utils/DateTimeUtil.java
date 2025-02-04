package com.shop.server.utils;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    public static Long convertStringToTimeStampSecond(String date) {
        return DateTimeUtil.convertDateToTimeStampSecond(DateTimeUtil.convertStringToDate(date));
    }

    public static String convertTimeStampSecondToStringTimeZone(Long timeStampSecond) {
        return DateTimeUtil.convertDateToString(DateTimeUtil.convertTimeStampSecondToString(timeStampSecond));
    }

    public static Date convertStringToDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return Date.from(zonedDateTime.toInstant());
    }

    public static Long convertDateToTimeStampSecond(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date convertTimeStampSecondToString(Long timeStampSecond) {
        if (timeStampSecond != null) {
            return Date.from(Instant.ofEpochSecond(timeStampSecond));
        }
        return null;
    }

    public static String convertDateToString(Date date) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date);
        }
        return null;
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static Long getCurrentTimeStampSecond() {
        return System.currentTimeMillis();
    }

    public static String convertDateToStringForExcel(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.convertStringToTimeStampSecond("2024-12-04T06:53:29.493Z"));
        System.out.println(DateTimeUtil.convertTimeStampSecondToString(1736755662935L / 1000L));
        System.out.println(DateTimeUtil.convertDateToStringForExcel(new Date()));
    }

}
