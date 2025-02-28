package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.Supplier;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.adt.FibonacciHeap.*;

public class PriorityQueueBenchmark {
    // Heap capacity and number of operations as per requirements.
    static int M = 4095;
    static int INSERT_COUNT = 16000;
    static int REMOVE_COUNT = 4000;
    static Random random = new Random();

    public static void main(String[] args) {
        Map<String, List<Double>> results = new HashMap<>();
        results.put("Basic Binary Heap", new ArrayList<>());
        results.put("Binary Heap with Floyd", new ArrayList<>());
        results.put("4-ary Heap", new ArrayList<>());
        results.put("4-ary Heap with Floyd", new ArrayList<>());
        results.put("Fibonacci Heap", new ArrayList<>());

        benchmarkPriorityQueue("Basic Binary Heap", false, false, results);
        benchmarkPriorityQueue("Binary Heap with Floyd", true, false, results);
        benchmarkPriorityQueue("4-ary Heap", false, true, results);
        benchmarkPriorityQueue("4-ary Heap with Floyd", true, true, results);
        benchmarkFibonacciHeap("Fibonacci Heap", results);

        for (Map.Entry<String, List<Double>> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * Benchmarks one of the PriorityQueue implementations.
     */
    private static void benchmarkPriorityQueue(String description, boolean useFloyd, boolean isFourAry,
                                               Map<String, List<Double>> results) {
        // Supplier creates a new PriorityQueue for each run.
        Supplier<PriorityQueue<Integer>> supplier = () -> {
            PriorityQueue<Integer> pq = new PriorityQueue<>(M, true, Comparator.comparing(Integer::intValue), useFloyd);
            pq.setFourAry(isFourAry);
            return pq;
        };

        Benchmark_Timer<PriorityQueue<Integer>> timer =
                new Benchmark_Timer<>(description, PriorityQueueBenchmark::runPriorityQueueOps);

        double avgTime = timer.runFromSupplier(supplier, 100);
        System.out.printf("%s: Average time = %.2f ms%n", description, avgTime);
        results.get(description).add(avgTime);
    }

    /**
     * Performs operations (insertions then removals) on the PriorityQueue.
     */
    private static void runPriorityQueueOps(PriorityQueue<Integer> pq) {
        int maxSpilled = Integer.MIN_VALUE;
        int count = 0;
        for (int i = 0; i < INSERT_COUNT; i++) {
            int value = random.nextInt(10000);
            if (pq.size() < M) {
                pq.give(value);
                if (random.nextBoolean() && count++ < REMOVE_COUNT) {
                    try {
                        pq.take();
                    } catch (PQException e) {
                        System.err.println("Error removing element: " + e.getMessage());
                    }
                }
            } else {
                maxSpilled = Math.max(maxSpilled, value);
            }
        }
        System.out.printf("Max spilled element: %d%n", maxSpilled);
    }

    /**
     * Benchmarks the Fibonacci heap implementation.
     */
    private static void benchmarkFibonacciHeap(String description, Map<String, List<Double>> results) {
        Supplier<FibonacciHeapAdapter<Integer>> supplier =
                () -> new FibonacciHeapAdapter<>(Comparator.comparingInt(Integer::intValue));

        Benchmark_Timer<FibonacciHeapAdapter<Integer>> timer =
                new Benchmark_Timer<>(description, PriorityQueueBenchmark::runFibonacciHeapOps);

        double avgTime = timer.runFromSupplier(supplier, 100);
        System.out.printf("%s: Average time = %.2f ms%n", description, avgTime);
        results.get(description).add(avgTime);
    }

    /**
     * Performs the operations on the Fibonacci heap.
     */
    private static void runFibonacciHeapOps(FibonacciHeapAdapter<Integer> heap) {
        int maxSpilled = Integer.MIN_VALUE;
        int count = 0;
        for (int i = 0; i < INSERT_COUNT; i++) {
            int value = random.nextInt(10000);
            if (heap.size() < M) {
                heap.insert(value);
                if (random.nextBoolean() && count++ < REMOVE_COUNT) {
                    Integer extracted = heap.extractMin();
                    if (extracted == null) break;
                }
            } else {
                maxSpilled = Math.max(maxSpilled, value);
            }
        }
        System.out.printf("Max spilled element (Fibonacci): %d%n", maxSpilled);
    }
}
