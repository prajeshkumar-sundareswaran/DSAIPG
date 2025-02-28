package com.phasmidsoftware.dsaipg.adt.FibonacciHeap;

import java.util.Comparator;

/**
 * An adapter class so that the Fibonacci heap has a similar interface to PriorityQueue.
 * @param <K>
 */
public class FibonacciHeapAdapter<K> {
    private final FibonacciHeap<K> heap;

    /**
     *
     * @param comparator
     */
    public FibonacciHeapAdapter(Comparator<K> comparator) {
        heap = new FibonacciHeap<>(comparator);
    }

    /**
     *
     * @param key
     */
    public void insert(K key) {
        heap.insert(key);
    }

    /**
     *
     * @return
     */
    public K extractMin() {
        return heap.extractMin();
    }

    /**
     *
     * @return
     */
    public int size() {
        return heap.size();
    }
}
