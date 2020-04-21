package com.wjholden;

import java.util.List;
import java.util.function.Supplier;

public class Page {
    public final String name, description;
    private final Supplier<List<String>> reportFunction;
    private int offset = 0;
    private int more = 0;
    private static final String title = "Page.java";
    private final Page[] children;
    private static final int WIDTH = 80;
    private static final boolean showColumnNumbers = false;
    private List<String> report;

    public Page(String name, String description, Page[] children, Supplier<List<String>> reportFunction) {
        this.reportFunction = reportFunction;
        this.name = name;
        this.description = description;
        this.children = children;
        generateReport();
    }

    private void generateReport() {
        this.report = reportFunction.get();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (showColumnNumbers) {
            for (int i = 1; i <= 80; i++) {
                str.append(i >= 10 ? i / 10 : " ");
            }
            str.append("\r\n");
            for (int i = 1; i <= 80; i++) {
                str.append(i % 10);
            }
            str.append("\r\n");
        }

        str.append(center(title, WIDTH).toUpperCase()).append("\r\n");
        str.append(center(name, WIDTH).toUpperCase()).append("\r\n");
        for (int i = 0 ; i < 15 ; i++) {
            final int position = offset + i;
            final int number_width = 1 + (report.isEmpty() ? 0 : (int) Math.log10(report.size()));
            str.append(String.format("%" + number_width + "d: ", position + 1));
            if (position < report.size()) {
                str.append(String.format("%-" + (WIDTH - 3 - number_width) + "s", report.get(position)));
            }
            str.append("\r\n");
        }

        str.append("\r\n");

        for (int i = 1 ; i <= 4 ; i++) {
            if (children.length >= i + more) {
                str.append(i).append("-").append(leftAlignFixed(children[more + i - 1].description, 12).toUpperCase());
            } else {
                str.append(i).append("-").append(leftAlignFixed("", 12));
            }
        }

        str.append("5-");
        if (children.length > 5) {
            str.append("MORE");
        } else if (children.length == 5) {
            str.append(leftAlignFixed(children[4].description, 12).toUpperCase());
        }
        str.append("\r\n");

        // common parameters to all pages
        str.append("6-REFRESH     7-PAGE_UP     8-PAGE_DOWN   9-BACK        0-EXIT        ");

        return str.toString();
    }

    public Page command(char c) throws InterruptedException {
        if (c == '5' && children.length > 5) {
            more = (more + 4 < children.length) ? more + 4 : 0;
            return this;
        }

        if ('1' <= c && c <= '5') {
            int index = more + c - '1';
            assert(0 <= index);
            if (index < children.length) {
                return children[index];
            }
        }

        switch(c) {
            case '6': generateReport(); break;
            case '7': offset = Math.max(0, offset - 10); break;
            case '8': offset += 10; break;
            case '9': return null;
            case '0': throw new InterruptedException();
        }

        return this;
    }

    private static String leftAlignFixed(String string, int width) {
        assert(width > 0);

        return String.format("%-" + width + "." + width + "s", string);
    }

    private static String center(String string, int width) {
        assert(width > 0);

        return String.format("%" + (string.length() + (width - string.length())/2) + "s", string);
    }
}
