package ru.fizteh.fivt.orlovNikita.parallelSort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            OptAnalizer optAnalizer = new OptAnalizer(args);
            CopyOnWriteArrayList<String> sortList = new CopyOnWriteArrayList<String>();
            String stream = optAnalizer.getNextStream();
            if (stream == null) {
                Scanner in = new Scanner(new BufferedInputStream(System.in));
                while (in.hasNextLine()) {
                    sortList.add(in.nextLine());
                }
                in.close();
            } else
                for (; stream != null; stream = optAnalizer.getNextStream()) {
                    Scanner in = new Scanner(new File(stream));
                    while (in.hasNextLine()) {
                        sortList.add(in.nextLine());
                    }
                }
            int threadNum = optAnalizer.getThreadCount();
            if (threadNum == -1) {
                threadNum = sortList.size() * sortList.size();
            }
            Thread sort;
            if (optAnalizer.hasKey('i')) {
                (sort = new Thread(new Sorter(sortList, 0, sortList.size(), threadNum, new NonRegisterCompare()))).start();
            } else {
                (sort = new Thread(new Sorter(sortList, 0, sortList.size(), threadNum, new LexCompare()))).start();
            }
            sort.join();
            PrintWriter writer;
            if (optAnalizer.getOutputStream() == null) {
                writer = new PrintWriter(new BufferedOutputStream(System.out));
            } else {
                writer = new PrintWriter(new File(optAnalizer.getOutputStream()));
            }

            if (optAnalizer.hasKey('u')) {
                TreeSet<String> set = new TreeSet<String>();
                for (String s : sortList) {
                    if (!set.contains(s)) {
                        writer.println(s);
                        set.add(s);
                    }
                }
            } else {
                for (String s : sortList) {
                    writer.println(s);
                }
            }
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}

