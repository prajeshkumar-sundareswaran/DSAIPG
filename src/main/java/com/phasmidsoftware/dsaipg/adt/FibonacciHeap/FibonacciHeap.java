package com.phasmidsoftware.dsaipg.adt.FibonacciHeap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A Fibonacci Heap implementation.
 *
 * @param <K> the type of keys stored in the heap
 */
public class FibonacciHeap<K> {
    private Node<K> min;
    private int n;
    private final Comparator<K> comparator;

    /**
     * Inner class representing a node in the Fibonacci heap.
     */
    private static class Node<K> {
        K key;
        int degree;
        Node<K> parent;
        Node<K> child;
        Node<K> left;
        Node<K> right;
        boolean mark;

        Node(K key) {
            this.key = key;
            this.degree = 0;
            this.mark = false;
            // Initialize as a circular doubly-linked list.
            this.left = this;
            this.right = this;
        }
    }

    /**
     * Constructs an empty Fibonacci heap with the given comparator.
     *
     * @param comparator the comparator to determine key ordering
     */
    public FibonacciHeap(Comparator<K> comparator) {
        this.comparator = comparator;
        this.min = null;
        this.n = 0;
    }

    /**
     * Inserts a new key into the Fibonacci heap.
     *
     * @param key the key to insert
     */
    public void insert(K key) {
        Node<K> node = new Node<>(key);
        if (min == null) {
            min = node;
        } else {
            // Insert node into the root list.
            node.left = min;
            node.right = min.right;
            min.right.left = node;
            min.right = node;
            if (comparator.compare(key, min.key) < 0) {
                min = node;
            }
        }
        n++;
    }

    /**
     * Extracts and returns the minimum key from the heap.
     *
     * @return the minimum key, or null if the heap is empty
     */
    public K extractMin() {
        if (min == null) return null;
        Node<K> z = min;
        if (z.child != null) {
            // Add each child of z to the root list.
            List<Node<K>> children = new ArrayList<>();
            Node<K> x = z.child;
            do {
                children.add(x);
                x = x.right;
            } while (x != z.child);

            for (Node<K> child : children) {
                // Remove child from its sibling list.
                child.left.right = child.right;
                child.right.left = child.left;
                // Add child to the root list.
                child.left = min;
                child.right = min.right;
                min.right.left = child;
                min.right = child;
                child.parent = null;
            }
        }
        // Remove z from the root list.
        z.left.right = z.right;
        z.right.left = z.left;
        if (z == z.right) {
            min = null;
        } else {
            min = z.right;
            consolidate();
        }
        n--;
        return z.key;
    }

    /**
     * Consolidates the heap by merging trees of equal degree.
     */
    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(n) / Math.log(2))) + 1;
        List<Node<K>> A = new ArrayList<>(Collections.nCopies(arraySize, null));

        // Build a list of root nodes.
        List<Node<K>> rootList = new ArrayList<>();
        Node<K> x = min;
        if (x != null) {
            do {
                rootList.add(x);
                x = x.right;
            } while (x != min);
        }

        for (Node<K> w : rootList) {
            x = w;
            int d = x.degree;
            while (d < A.size() && A.get(d) != null) {
                Node<K> y = A.get(d);
                if (comparator.compare(x.key, y.key) > 0) {
                    // Swap x and y.
                    Node<K> temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                A.set(d, null);
                d++;
                if (d >= A.size()) {
                    // Expand A if needed.
                    for (int i = A.size(); i <= d; i++) {
                        A.add(null);
                    }
                }
            }
            A.set(d, x);
        }
        min = null;
        for (Node<K> node : A) {
            if (node != null) {
                if (min == null || comparator.compare(node.key, min.key) < 0) {
                    min = node;
                }
            }
        }
    }

    /**
     * Links two trees of equal degree by making node y a child of node x.
     *
     * @param y the node to become a child
     * @param x the node that becomes the parent
     */
    private void link(Node<K> y, Node<K> x) {
        // Remove y from the root list.
        y.left.right = y.right;
        y.right.left = y.left;
        // Make y a child of x.
        if (x.child == null) {
            x.child = y;
            y.left = y;
            y.right = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right.left = y;
            x.child.right = y;
        }
        y.parent = x;
        x.degree++;
        y.mark = false;
    }

    /**
     * Returns the number of nodes in the heap.
     *
     * @return the heap size
     */
    public int size() {
        return n;
    }
}