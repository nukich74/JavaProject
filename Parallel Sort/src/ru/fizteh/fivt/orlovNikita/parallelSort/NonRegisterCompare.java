package ru.fizteh.fivt.orlovNikita.parallelSort;


import java.util.Comparator;

public class NonRegisterCompare implements Comparator<String> {
    public int compare(String ob1, String ob2) {
        return ob1.compareToIgnoreCase(ob2);
    }
}
