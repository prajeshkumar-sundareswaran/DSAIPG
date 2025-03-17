/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import scala.tools.nsc.doc.html.HtmlTags;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
        int size = 500000;
        while(size <= 2000000) {
            int thread = 2;
            System.out.println("Size of the ARRAY " +size);
            while (thread < 128) {
                ForkJoinPool pool = new ForkJoinPool(thread);
                System.out.println("Degree of parallelism: " + pool.getParallelism());
                Random random = new Random();
                int[] array = new int[size];
                ArrayList<Long> timeList = new ArrayList<>();
                ArrayList<Integer> cutoffs = new ArrayList<>();
                ParSort.cutoff = size/4+10000;
                for (int j = 1; j < 25; j++) {
                    // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    long time;
                    long startTime = System.currentTimeMillis();
                    for (int t = 0; t < 10; t++) {
                        for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                        ParSort.sort(array, 0, array.length, pool);
                    }
                    long endTime = System.currentTimeMillis();
                    time = (endTime - startTime);
                    timeList.add(time);
                    cutoffs.add(ParSort.cutoff);
                    System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t10times Time:" + time + "ms");
                    ParSort.cutoff = ParSort.cutoff + 10000 ;
                }
                try {
                    String outputFile = "./src/Assignment5/result"+size+"_"+thread+".csv";
                    FileOutputStream fis = new FileOutputStream(outputFile);
                    OutputStreamWriter isr = new OutputStreamWriter(fis);
                    BufferedWriter bw = new BufferedWriter(isr);
                    int j = 0;
                    for (long i : timeList) {
                        String content = cutoffs.get(j) + "," + (double) i / 10 + "\n";
                        j++;
                        bw.write(content);
                        bw.flush();
                    }
                    bw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                thread = thread * 2;
            }
            size = size*2;
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}