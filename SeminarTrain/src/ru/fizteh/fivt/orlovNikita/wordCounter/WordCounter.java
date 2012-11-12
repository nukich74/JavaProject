package ru.fizteh.fivt.orlovNikita.wordCounter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class WordCounter {
    /*baar@1c.ru*/

    private static ArrayList<FileWordCounter> fileWordCounter;

    private static void resultProcessor(HashSet<Character> keySet, char workChar) {
        if (keySet.contains('a')) {
            if (keySet.contains('U') || keySet.contains('u')) {
                TreeMap<String, Long> tree = new TreeMap<String, Long>();
                for (FileWordCounter fwd : fileWordCounter) {
                    String s = fwd.getFileName();
                    TreeMap<String, Long> t = (workChar == 'w') ? fwd.getUniqueWordsTable() : fwd.getUniqueLinesTable();
                    for (Map.Entry<String, Long> vertex : t.entrySet()) {
                        tree.put(vertex.getKey(), tree.containsKey(vertex.getKey()) ?
                                tree.get(vertex.getKey()) + vertex.getValue() : vertex.getValue());
                    }
                }
                for (Map.Entry<String, Long> entry : tree.entrySet()) {
                    String key = entry.getKey();
                    Long value = entry.getValue();
                    System.out.printf("%s : %d\n", key, value);
                }
            } else {
                long sum = 0;
                for (FileWordCounter fwd : fileWordCounter) {
                    sum += ((workChar == 'w') ? (fwd.getAllWordsCount()) : (fwd.getAllLinesCount()));
                }
                System.out.println(sum);
            }
        } else {
            if (keySet.contains('U') || keySet.contains('u')) {
                for (FileWordCounter fwd : fileWordCounter) {
                    System.out.println(fwd.getFileName() + " :\n");
                    TreeMap<String, Long> tree = ((workChar == 'w') ? fwd.getUniqueWordsTable() : fwd.getUniqueLinesTable());
                    for (Map.Entry<String, Long> entry : tree.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                }
            } else {
                for (FileWordCounter fwd : fileWordCounter) {
                    System.out.println(fwd.getFileName() + " : " + (workChar == 'w' ? fwd.getAllWordsCount() :
                            fwd.getAllLinesCount()) + "\n");
                }
            }
        }
    }

    private static void launchAnalizer(String[] args) throws Exception {
        OptAnalizer analizer = new OptAnalizer();
        analizer.processArgs(args);

        if (!checkParams(analizer.getKeyList())) {
            throw new RuntimeException("Bad sequence of keys\n");
        }

        fileWordCounter = new ArrayList<FileWordCounter>();
        HashSet<String> fileArray = analizer.getLongNamesList();
        for (String fileName : fileArray) {
            fileWordCounter.add(new FileWordCounter(fileName, analizer.getKeyList()));
        }

        if (analizer.getKeyList().contains('w')) {
            resultProcessor(analizer.getKeyList(), 'w');
        }
        if (analizer.getKeyList().contains('l')) {
            resultProcessor(analizer.getKeyList(), 'l');
        }
    }

    private static boolean checkParams(HashSet<Character> keySet) {
        return keySet.contains('U') && keySet.contains('u') ? false : true;
    }


    public static void main(String[] args) {
        try {
            launchAnalizer(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}