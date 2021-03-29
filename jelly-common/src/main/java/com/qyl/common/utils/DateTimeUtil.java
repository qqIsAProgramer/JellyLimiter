package com.qyl.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: qyl
 * @Date: 2021/3/22 10:45
 */
public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String now() {
        return LocalDateTime.now().format(formatter);
    }

    public static String toString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(formatter);
    }

    public static LocalDateTime parse(String dateTime) {
        return LocalDateTime.parse(dateTime, formatter);
    }
}
