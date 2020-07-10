package de.zekro.hcrevive.util;

public class TimeUtil {

    public static String getFormattedTimeSpan(int totalSeconds) {
        int hours = (int) Math.floor(totalSeconds / 60F / 60F);
        int minutes = (int) Math.floor(totalSeconds % 60 / 60F);
        int seconds = totalSeconds % 60 % 60;

        StringBuilder res = new StringBuilder();

        if (hours > 0)
            res.append(String.format("%d hours,", hours));

        if (minutes > 0)
            res.append(String.format("%d minutes,", minutes));

        if (seconds > 0)
            res.append(String.format("%d seconds,", seconds));

        return res.toString().substring(0, res.length()-2);
    }
}
