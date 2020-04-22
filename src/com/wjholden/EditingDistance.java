package com.wjholden;

import java.util.stream.IntStream;

public class EditingDistance {
    public static float editingDistance(String s1, String s2) {
        final int d[][] = new int[s1.length() + 1][s2.length() + 1];
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
}
