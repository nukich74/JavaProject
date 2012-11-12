package ru.fizteh.fivt.orlovNikita.parallelSort;


import java.util.Comparator;

public class LexCompare implements Comparator<String> {
    public int compare(String obj1, String obj2) {
        return obj1.compareTo(obj2);
    }
}
