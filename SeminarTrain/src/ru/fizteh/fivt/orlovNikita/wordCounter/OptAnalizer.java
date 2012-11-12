package ru.fizteh.fivt.orlovNikita.wordCounter;


import java.util.HashSet;

public class OptAnalizer {
    private HashSet<String> longNamesList;
    private HashSet<Character> keyList;

    public OptAnalizer() {
        longNamesList = new HashSet<String>();
        keyList = new HashSet<Character>();
    }

    private void RunAnalize(String current) {
        if (current.matches("\\-[A-Za-z]*")) {
            for (int i = 1; i < current.length(); i++) {
                keyList.add(current.charAt(i));
            }
        } else {
            longNamesList.add(current);
        }
    }

    public void processArgs(String[] args) {
        for (String s : args) {
            RunAnalize(s);
        }
        if (!keyList.contains('l') && !keyList.contains('w')) {
            keyList.add('w');
        }
    }

    public HashSet<String> getLongNamesList() {
        return longNamesList;
    }

    public HashSet<Character> getKeyList() {
        return keyList;
    }

}
