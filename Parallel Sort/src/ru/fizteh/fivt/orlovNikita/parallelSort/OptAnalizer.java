package ru.fizteh.fivt.orlovNikita.parallelSort;


import java.util.ArrayList;
import java.util.HashSet;

public class OptAnalizer {
    private String arg;
    private HashSet<Character> keySet;
    private Integer threadCount;
    private String outputStream;
    private ArrayList<String> streamArray;
    private int indexStream;

    OptAnalizer(String[] args) {
        String fullString = "";
        threadCount = -1;
        for (String tString : args) {
            fullString += tString + " ";
        }
        arg = fullString;
        streamArray = new ArrayList<String>();
        keySet = new HashSet<Character>();
        /*  if (fullString.matches("((\\-i\\s)|(\\-u\\s)|(\\-iu\\s)|(\\-[iu]?[iu]?t\\s[0-9][0-9]*\\s)|(\\-[ui]?[ui]?o\\s.*))*(.*)") || fullString.equals("")) {
  System.out.println("Correct args");
} else {
  throw new RuntimeException("Args: Bad args sequence!");
}          */

        int i;
        for (i = 0; i < args.length; i++) {
            if (args[i].matches("\\-.*")) {
                for (int j = 1; j < args[i].length(); j++) {
                    keySet.add(args[i].charAt(j));
                }
                if (args[i].charAt(args[i].length() - 1) == 't') {
                    if (i + 1 < args.length) {
                        threadCount = Math.abs(Integer.valueOf(args[++i]));
                    } else {
                        throw new RuntimeException("Args: Bad args sequence!");
                    }
                }
                if (args[i].charAt(args[i].length() - 1) == 'o') {
                    if (i + 1 < args.length) {
                        outputStream = args[++i];
                    } else {
                        throw new RuntimeException("Args: Bad args sequence!");
                    }
                }
            } else {
                break;
            }
        }
        while (i < args.length) {
            streamArray.add(args[i]);
            i++;
        }
        indexStream = 0;
    }

    String getNextStream() {
        if (indexStream >= streamArray.size()) {
            return null;
        }
        return streamArray.get(indexStream++);
    }

    int getThreadCount() {
        return threadCount;
    }

    String getOutputStream() {
        return outputStream;
    }


    boolean hasKey(char c) {
        return keySet.contains(c);
    }
}