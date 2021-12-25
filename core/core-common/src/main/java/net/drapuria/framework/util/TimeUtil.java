package net.drapuria.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    public static long parseTime(String timeformat) {
        Pattern pattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeformat);

        long years = 0;
        long month = 0;
        long weeks = 0;
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        boolean found = false;

        while (matcher.find()) {
            if (matcher.group() != null && !(matcher.group().isEmpty())) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    if (matcher.group(i) != null && !(matcher.group(i).isEmpty())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    if (matcher.group(1) != null && !(matcher.group(1).isEmpty())) {
                        years = Long.parseLong(matcher.group(1));
                    }
                    if (matcher.group(2) != null && !(matcher.group(2).isEmpty())) {
                        month = Long.parseLong(matcher.group(2));
                    }
                    if (matcher.group(3) != null && !(matcher.group(3).isEmpty())) {
                        weeks = Long.parseLong(matcher.group(3));
                    }
                    if (matcher.group(4) != null && !(matcher.group(4).isEmpty())) {
                        days = Long.parseLong(matcher.group(4));
                    }
                    if (matcher.group(5) != null && !(matcher.group(5).isEmpty())) {
                        hours = Long.parseLong(matcher.group(5));
                    }
                    if (matcher.group(6) != null && !(matcher.group(6).isEmpty())) {
                        minutes = Long.parseLong(matcher.group(6));
                    }
                    if (matcher.group(7) != null && !(matcher.group(7).isEmpty())) {
                        seconds = Long.parseLong(matcher.group(7));
                    }
                }
            }
        }

        if (!(found)) {
            return -1;
        }

        long millis = 0;

        if (years > 0) {
            millis += years * (1000L * 60L * 60L * 24L * 7L * 31L * 12L);
        }

        if (month > 0) {
            millis += month * (1000L * 60L * 60L * 24L * 7L * 31L);
        }

        if (weeks > 0) {
            millis += weeks * (1000L * 60L * 60L * 24L * 7L);
        }

        if (days > 0) {
            millis += days * (1000L * 60L * 60L * 24L);
        }

        if (hours > 0) {
            millis += hours * (1000L * 60L * 60L);
        }

        if (minutes > 0) {
            millis += minutes * (1000L * 60L);
        }

        if (seconds > 0) {
            millis += seconds * 1000L;
        }

        return millis;
    }

    public static String timeToString(long time, boolean shorten) {
        String msg = "";
        long seconds = time / 1000;

        if (seconds >= (30 * 60 * 60 * 24)) {
            long months = seconds / (30 * 60 * 60 * 24);
            msg = msg + months + (shorten ? "mo " : (months == 1 ? " Mon, " : " Mon, "));
            seconds %= (30 * 60 * 60 * 24);
        }

        if (seconds >= (60 * 60 * 24)) {
            long days = seconds / (60 * 60 * 24);
            msg = msg + days + (shorten ? "d " : (days == 1 ? " Tag, " : " Tage, "));
            seconds %= (60 * 60 * 24);
        }

        if (seconds >= (60 * 60)) {
            long hours = seconds / (60 * 60);
            msg = msg + hours + (shorten ? "h " : (hours == 1 ? " Std, " : " Std, "));
            seconds %= (60 * 60);
        }

        if (seconds >= 60) {
            long minutes = seconds / 60;
            msg = msg + minutes + (shorten ? "m " : (minutes == 1 ? " Min, " : " Min, "));
            seconds %= 60;
        }

        if (seconds > 0) {
            msg = msg + seconds + (shorten ? "s " : (seconds == 1 ? " Sek, " : " Sek, "));
        }

        if (!(msg.isEmpty())) {
            msg = msg.substring(0, msg.length() - (shorten ? 1 : 2));
        } else {
            msg = shorten ? "0s" : "0 Sek";
        }

        return msg;
    }

    public static String timeToStringApproximately(long time, boolean shorten) {
        long seconds = time / 1000;

        if (seconds >= (30 * 60 * 60 * 24)) {
            long months = seconds / (30 * 60 * 60 * 24);
            return months + (shorten ? "mo" : (months == 1 ? " Mon" : " Mon"));
        }

        if (seconds >= (60 * 60 * 24)) {
            long days = seconds / (60 * 60 * 24);
            return days + (shorten ? "d" : (days == 1 ? " Tag" : " Tage"));
        }

        if (seconds >= (60 * 60)) {
            long hours = seconds / (60 * 60);
            return hours + (shorten ? "h" : (hours == 1 ? " Std" : " Std"));
        }

        if (seconds >= 60) {
            long minutes = seconds / 60;
            return minutes + (shorten ? "m" : (minutes == 1 ? " Min" : " Min"));
        }

        if (seconds > 0) {
            return seconds + (shorten ? "s" : (seconds == 1 ? " Sek" : " Sek"));
        }

        return shorten ? "0s" : "0 Sek";
    }

}
