package ru.fizteh.fivt.orlovNikita;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgsAnalizer {
    private String month;
    private String year;
    private String timeZone;
    private boolean isWeekNumber;

    ArgsAnalizer(String[] args) {
        String s = "";
        for (String aString : args) {
            s = s + " " + aString;
        }
        s += " ";
        Matcher matcher = Pattern.compile("\\-m [0-9]|(10)|(11)|(12)]").matcher(s);
        if (!matcher.find(0)) {
            month = null;
        } else
            this.month = (matcher.group().split(" "))[1];

        Matcher matcher1 = Pattern.compile("\\-y ([0-9]{4})").matcher(s);
        if (matcher1.find(0)) {
            this.year = (matcher1.group().split(" "))[1];
        } else
            this.year = null;

        if (s.matches(".*\\s\\-w\\s.*")) {
            this.isWeekNumber = true;
        }

        Matcher matcher2 = Pattern.compile("\\-t .*\\s").matcher(s);
        if (matcher2.find(0)) {
            this.timeZone = (matcher2.group().split(" "))[1];
        } else
            this.year = null;

    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public boolean getIsWeekNumber() {
        return this.isWeekNumber;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

}
