package ru.fizteh.fivt.orlovNikita.parallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Sorter<T extends Comparable<? super T>> implements Runnable {
    private Scanner in;
    private Comparator<T> comparator;
    private CopyOnWriteArrayList<T> arrayList;
    private ArrayList<T> tempArray;
    private int left;
    private int right;
    private int middle;
    private int size;
    private int threadNum;

    Sorter(CopyOnWriteArrayList<T> tArrayList, int tLeft, int tRight, int tThreadNum, Comparator<T> tComparator) {
        left = tLeft;
        right = tRight;
        threadNum = tThreadNum;
        middle = (left + right) / 2;
        size = right - left;
        tempArray = new ArrayList<T>();
        comparator = tComparator;
        arrayList = tArrayList;
    }


    public void run() {
 //       System.out.println("Sorting left=" + String.valueOf(left) + " right=" + String.valueOf(right) + " middle=" + String.valueOf((left + right) >>> 1) + "\n");
        if (left + 1< right) {
            Thread l = null, r = null;
            if (threadNum > 0) {
                threadNum--;
                Sorter<T> leftSort = new Sorter<T>(arrayList, left, middle, threadNum, comparator);
                (l = new Thread(leftSort)).start();
                leftSort.run();
            } else {
                Collections.sort(arrayList.subList(left, middle), comparator);
            }

            if (threadNum > 0) {
                threadNum--;
                Sorter<T> rightSort = new Sorter<T>(arrayList, middle, right, threadNum, comparator);
                (r = new Thread(rightSort)).start();
                rightSort.run();
            } else {
                Collections.sort(arrayList.subList(middle, right), comparator);
            }
            if (l != null) {
                try {
                    l.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (r != null) {
                try {
                    r.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
      //      System.out.println("Merging left=" + String.valueOf(left) + " right=" + String.valueOf(right) + " middle=" + String.valueOf((left + right) >>> 1) + "\n");
            int i = left, j = middle;
            for (int k = 0; k < size; k++) {
                if ((j >= right) || ((i < middle) && (arrayList.get(i).compareTo(arrayList.get(j))) < 0)) {
                    tempArray.add(arrayList.get(i++));
                } else {
                    tempArray.add(arrayList.get(j++));
                }
            }
            for (int p = left; p < right; p++) {
                arrayList.set(p, tempArray.get(p - left));
            }
        }
    }
}
