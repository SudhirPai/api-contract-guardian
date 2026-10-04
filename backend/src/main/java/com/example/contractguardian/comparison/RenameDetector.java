package com.example.contractguardian.comparison;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RenameDetector {
    public String canonical(String name) {
        return String.join("", name.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().trim().split("\\s+"));
    }

    public int confidence(String oldPath, String newPath) {
        String a = canonical(last(oldPath)), b = canonical(last(newPath));
        if (a.equals(b)) return 98;
        int d = distance(a, b), max = Math.max(a.length(), b.length());
        return max == 0 ? 0 : Math.max(0, 100 - (d * 100 / max));
    }

    private String last(String p) {
        int i = Math.max(p.lastIndexOf('.'), p.lastIndexOf(']'));
        return p.substring(i + 1).replace("[]", "");
    }

    private int distance(String a, String b) {
        int[] d = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) d[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int prev = d[0]++;
            for (int j = 1; j <= b.length(); j++) {
                int temp = d[j];
                d[j] = Math.min(Math.min(d[j] + 1, d[j - 1] + 1), prev + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
                prev = temp;
            }
        }
        return d[b.length()];
    }
}