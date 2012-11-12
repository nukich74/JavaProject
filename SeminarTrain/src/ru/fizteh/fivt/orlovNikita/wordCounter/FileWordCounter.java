package ru.fizteh.fivt.orlovNikita.wordCounter;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileWordCounter {
    private String fileName;
    private TreeMap<String, Long> wordsTable;
    private TreeMap<String, Long> linesTable;
    private long allWordsCount;
    private long allLinesCount;
    private HashSet<Character> keySet;

    public FileWordCounter(String filename, HashSet<Character> keySet) {
        fileName = filename;
        this.keySet = keySet;
        wordsTable = new TreeMap<String, Long>();
        linesTable = new TreeMap<String, Long>();
        allWordsCount = 0;
        allLinesCount = 0;
        countStatistics();
    }

    private void countLines(HashSet<Character> keySet, Scanner in) {
        String temp;
        while (in.hasNextLine()) {
            temp = in.nextLine();
            if (keySet.contains('U')) {
                temp = temp.toLowerCase();
            }
            allLinesCount++;
            linesTable.put(temp, (linesTable.containsKey(temp)) ? (linesTable.get(temp) + 1) : (long) 1);
        }
    }

    private void countWords(HashSet<Character> keySet, Scanner in) {
        while (in.hasNextLine()) {
            String temp = in.nextLine();
            ArrayList<String> wordsArray = getWordsArray(temp);

            if (keySet.contains('U')) {
                for (int i = 0; i < wordsArray.size(); i++) {
                    wordsArray.set(i, wordsArray.get(i).toLowerCase());
                }
            }
            allWordsCount += wordsArray.size();
            for (String word : wordsArray) {
                wordsTable.put(word, wordsTable.containsKey(word) ? wordsTable.get(word) + 1 : (long) 1);
            }
        }
    }

    private ArrayList<String> getWordsArray(String s) {
        Pattern pattern = Pattern.compile("[(A-Z)|(a-z)|(а-я)|(А-Я)][(A-Z)|(a-z)|(а-я)|(А-Я)]*");
        Matcher matcher = pattern.matcher(s);
        ArrayList<String> array = new ArrayList<String>();
        while (matcher.find()) {
            array.add(matcher.group());
        }
        return array;
    }

    private void countStatistics() {
        try {
            Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(new File(fileName))));
            if (keySet.contains('l')) {
                countLines(keySet, in);
            }
            if (keySet.contains('w')) {
                countWords(keySet, in);
            }
            in.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File <".concat(fileName.concat("> was not found")));
        }
    }

    public long getAllWordsCount() {
        return allWordsCount;
    }


    public long getAllLinesCount() {
        return allLinesCount;
    }

    public TreeMap<String, Long> getUniqueLinesTable() {
        return linesTable;
    }

    public TreeMap<String, Long> getUniqueWordsTable() {
        return wordsTable;
    }

    public String getFileName() {
        return fileName;
    }
}
