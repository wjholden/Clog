package com.wjholden;

import java.util.function.Supplier;

public class Page {
    public final String name, description;
    private final Supplier<Object[]> reportFunction;
    private int offset = 0;
    private int more = 0;
    private static final String title = "Page.java";
    private final Page[] children;
    private static final int WIDTH = 80;
    private static final boolean showColumnNumbers = false;
    private Object[] report;

    public Page(String name, String description, Page[] children, Supplier<Object[]> reportFunction) {
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
        String str = "";

        if (showColumnNumbers) {
            for (int i = 1; i <= 80; i++) {
                str += i >= 10 ? i / 10 : " ";
            }
            str += "\r\n";
            for (int i = 1; i <= 80; i++) {
                str += (i % 10);
            }
            str += "\r\n";
        }

        str += center(title, WIDTH).toUpperCase() + "\r\n";
        str += center(name, WIDTH).toUpperCase() + "\r\n";
        for (int i = 0 ; i < 15 ; i++) {
            final int position = offset + i;
            final int number_width = 1 + (report.length > 0 ? (int) Math.log10(report.length) : 0);
            str += String.format("%" + number_width + "d: ", position + 1);
            if (position < report.length) {
                str += String.format("%-" + (WIDTH - 3 - number_width) + "s", report[position]);
            }
            str += "\r\n";
        }

        str += "\r\n";

        for (int i = 1 ; i <= 4 ; i++) {
            if (children.length >= i + more) {
                str += i + "-" + leftAlignFixed(children[more+i-1].description, 12).toUpperCase();
            } else {
                str += i + "-" + leftAlignFixed("", 12);
            }
        }

        str += "5-";
        if (children.length > 5) {
            str += "MORE";
        } else if (children.length == 5) {
            str += leftAlignFixed(children[4].description, 12).toUpperCase();
        }
        str += "\r\n";

        // common parameters to all pages
        str += "6-REFRESH     7-PAGE_UP     8-PAGE_DOWN   9-BACK        0-EXIT        ";

        return str;
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

    private static final String leftAlignFixed(String string, int width) {
        assert(width > 0);

        return String.format("%-" + width + "." + width + "s", string);
    }

    private static final String center(String string, int width) {
        assert(width > 0);

        return String.format("%" + (string.length() + (width - string.length())/2) + "s", string);
    }
}
