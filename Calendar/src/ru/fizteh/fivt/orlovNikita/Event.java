package ru.fizteh.fivt.orlovNikita;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Event {

    public static ArgsAnalizer argsAnalizer;

    public static String buildOutputCalendar(Calendar calendar) {
        String res = "  " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " +
                calendar.get(Calendar.YEAR) + '\n' +
                (argsAnalizer.getIsWeekNumber() ? "   " : "") + "Mo Tu We Th Fr Sa Su" + '\n';

        if (argsAnalizer.getIsWeekNumber()) {
            res += String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) - 1);
            if (String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) - 1).length() == 1) {
                res += "  ";
            } else {
                res += " ";
            }
        }

        for (int i = 1; i < calendar.get(Calendar.DAY_OF_WEEK) - 1; i++) {
            res += "   ";
        }
        int currMonth = calendar.get(Calendar.MONTH);
        int i = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        while (currMonth == calendar.get((Calendar.MONTH))) {
            String temp = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            if (temp.length() == 1) {
                temp += "  ";
            } else {
                temp += " ";
            }
            res += temp;
            i++;
            calendar.add(Calendar.DATE, 1);
            if (i % 7 == 1) {
                res += "\n";
                if (argsAnalizer.getIsWeekNumber()) {
                    res += String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) - 1);
                    if (String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) - 1).length() == 1) {
                        res += "  ";
                    } else {
                        res += " ";
                    }
                }
            }
        }


        return res;
    }


    public static void main(String[] args) {
        try {
            argsAnalizer = new ArgsAnalizer(args);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(new Date());
            int a = calendar.getFirstDayOfWeek();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            if (argsAnalizer.getYear() != null) {
                calendar.set(Calendar.YEAR, Integer.valueOf(argsAnalizer.getYear()));
            }
            if (argsAnalizer.getMonth() != null) {
                calendar.set(Calendar.MONTH, Integer.valueOf(argsAnalizer.getMonth()) - 1);
            }
            System.out.println(buildOutputCalendar(calendar));
            if (argsAnalizer.getTimeZone() != null) {
                calendar.setTimeZone(TimeZone.getTimeZone(argsAnalizer.getTimeZone()));
                System.out.println("NOW: ");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                String s = simpleDateFormat.format(calendar.getTime());
                System.out.print(s + '\n');
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
