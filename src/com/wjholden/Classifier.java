package com.wjholden;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Classifier implements Runnable {
    private final BlockingQueue<String> queue;
    private final List<List<String>> clusters = new ArrayList<>();
    private final double threshold;
    private final List<String> db;

    public Classifier(double threshold, BlockingQueue<String> queue, List<String> db) {
        this.queue = queue;
        this.threshold = threshold;
        this.db = db;
    }

    @Override
    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
            try {
                String s = queue.take();
                classify(s);
                db.add(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
                keepGoing = false;
            }
        }
    }

    private void classify(String s) {
        final List<Double> distances = clusters.stream().map(cluster -> distance(cluster, s)).collect(Collectors.toList());
        final double min_distance = distances.isEmpty() ? 1 : Collections.min(distances);

        assert(Double.isFinite(min_distance));
        assert(!Double.isNaN(min_distance));
        assert(0 <= min_distance);
        assert(min_distance <= 1);

        if (min_distance < threshold) {
            final int index = distances.indexOf(min_distance);
            clusters.get(index).add(s);
        } else {
            List<String> c = new ArrayList<>();
            c.add(s);
            clusters.add(c);
        }
    }

    public static double distance(List<String> events, String s) {
        // Randomly select 1 + log_2(|events|) from the |events| set.
        // Compute the normalized editing distance from s to each of those elements.
        // If the result is not empty, return the minimum distance.
        // Otherwise return 1, which is interpreted as infinite distance
        // (completely different inputs).
        return DoubleStream.generate(Math::random).
                limit((long) (1 + Math.log(events.size()) / Math.log(2))).
                map(d -> editingDistanceNormalized(s, events.get((int) (d * events.size())))).
                min().
                orElse(1.0);
    }

    public static double editingDistance(String s1, String s2) {
        final int[][] d = new int[s1.length() + 1][s2.length() + 1];
        IntStream.range(0, s1.length() + 1).forEach(i -> d[i][0] = i);
        IntStream.range(0, s2.length() + 1).forEach(j -> d[0][j] = j);

        for (int row = 0 ; row < s1.length() ; row++) {
            for (int col = 0 ; col < s2.length() ; col++) {
                if (s1.charAt(row) == s2.charAt(col)) {
                    d[row + 1][col + 1] = d[row][col];
                } else {
                    d[row + 1][col + 1] = 1 + Math.min(d[row][col],
                            Math.min(d[row + 1][col], d[row][col + 1]));
                }
            }
        }

        return d[s1.length()][s2.length()];
    }

    public static double editingDistanceNormalized(String s1, String s2) {
        final int max = Math.max(s1.length(), s2.length());
        assert(max > 0);
        return editingDistance(s1, s2) / max;
    }

    public List<String> menu() {
        return clusters.stream().map(cluster -> "[" + cluster.size() + " events] " + cluster.get(0)).
                collect(Collectors.toList());
    }

    public int clusterCount() {
        return clusters.size();
    }
}
