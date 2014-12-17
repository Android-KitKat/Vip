package com.it5z.vip.util;

import java.text.DateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2014/12/12.
 */
public class DateUtil {
    private static final Pattern timePattern = Pattern.compile("(?i)([0-9]+d)?([0-9]+h)?([0-9]+m)?([0-9]+s)?([0-9]+ms)?");

    private DateUtil() {}

    public static long toTimeValue(String value) {
        Matcher matcher = timePattern.matcher(value);
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid value");
        long millis = 0L;
        millis += toMillis(matcher.group(1), TimeUnit.DAYS);
        millis += toMillis(matcher.group(2), TimeUnit.HOURS);
        millis += toMillis(matcher.group(3), TimeUnit.MINUTES);
        millis += toMillis(matcher.group(4), TimeUnit.SECONDS);
        millis += toMillis(matcher.group(5), TimeUnit.MILLISECONDS);
        return millis;
    }

    public static String toTimeString(long value) {
        if(value == 0) return "现在";
        long days = value / (1000 * 60 * 60 * 24);
        long hours = (value % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (value % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (value % (1000 * 60)) / 1000;
        long milliseconds = value % 1000;
        long[] times = {days, hours, minutes, seconds, milliseconds};
        String[] names = {"日", "小时", "分钟", "秒", "毫秒"};
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < times.length; i ++) {
            if(times[i] != 0) {
                sb.append(times[i]).append(names[i]);
            }
        }
        return String.valueOf(sb);
    }

    public static String toDateString(long value) {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA).format(value);
    }

    private static long toMillis(String value, TimeUnit unit) {
        if ((value != null) && (!value.isEmpty())) {
            Matcher matcher = Pattern.compile("[0-9]+").matcher(value);
            if (!matcher.find())
                return 0L;
            return unit.toMillis(Integer.parseInt(matcher.group()));
        }
        return 0L;
    }
}
