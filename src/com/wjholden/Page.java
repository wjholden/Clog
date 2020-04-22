package com.wjholden;

import java.util.List;
import java.util.function.Supplier;

public class Page {
    public final String name;
    private final Supplier<List<String>> reportFunction;
    private int offset = 0;
    private int more = 0;
    private static final String title = "Page.java";
    private final Page[] children;
    private static final int WIDTH = 80;
    private static final boolean showColumnNumbers = false;
    private List<String> report;
    protected boolean characterMode = true;
    private static final int lines = 15;

    // These three options were not in the original design. I need a way to read integers from the user.
    // This is then used to show full reports of a single entity.
    private boolean shortPage = false;
    private boolean inspectable = true;
    private boolean alwaysRefresh = false;

    public Page(String name, Page[] children, Supplier<List<String>> reportFunction) {
        this.reportFunction = reportFunction;
        this.name = name;
        this.children = children;
        generateReport();
    }

    public Page(String name, Page[] children, Supplier<List<String>> reportFunction,
                boolean shortPage, boolean inspectable, boolean alwaysRefresh) {
        this(name, children, reportFunction);
        this.shortPage = shortPage;
        this.inspectable = inspectable;
        this.alwaysRefresh = alwaysRefresh;
    }

    private void generateReport() {
        this.report = reportFunction.get();
    }

    @Override
    public String toString() {
        if (alwaysRefresh) {
            generateReport();
        }

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

        str.append(center(title).toUpperCase()).append("\r\n");
        str.append(center(name).toUpperCase()).append("\r\n");

        if (shortPage) {
            str.append(report.get(0));
        } else {
            for (int i = 0; i < lines; i++) {
                final int position = offset + i;
                final int number_width = 1 + (report.isEmpty() ? 0 : (int) Math.log10(report.size()));
                str.append(String.format("%" + number_width + "d: ", position + 1));
                if (position < report.size()) {
                    str.append(String.format("%-" + (WIDTH - 3 - number_width) + "." +
                            (WIDTH - 3 - number_width) + "s", report.get(position)));
                }
                str.append("\r\n");
            }
        }

        str.append("\r\n");

        if (characterMode) {
            for (int i = 1; i <= 4; i++) {
                if (children.length >= i + more) {
                    str.append(i).append("-").append(leftAlignFixed(children[more + i - 1].name, 12).toUpperCase());
                } else {
                    str.append(i).append("-").append(leftAlignFixed("", 12));
                }
            }

            str.append("5-");
            if (children.length > 5) {
                str.append("MORE");
            } else if (children.length == 5) {
                str.append(leftAlignFixed(children[4].name, 12).toUpperCase());
            }
            str.append("\r\n");

            // common parameters to all pages
            str.append("6-");
            if (inspectable) {
                str.append("INSPECT     ");
            } else {
                str.append("            ");
            }
            str.append("7-REFRESH     ");
            if (shortPage) {
                str.append("8-            9-            ");
            } else {
                str.append("8-PAGE_UP     9-PAGE_DOWN   ");
            }
            str.append("0-BACK/EXIT");
        } else {
            str.append("INSPECT: ");
        }

        return str.toString();
    }

    public Page command(String s) {
        characterMode = true; // always revert to character mode after reading a line
        try {
            int index = Integer.parseInt(s) - 1;
            return new Page("Inspect", new Page[0],
                    () -> List.of(this.report.get(index)), true, false, false);
        } catch (NumberFormatException ex) {
            return this;
        }
    }

    public Page command(char c) {
        if (c == '5' && children.length > 5) {
            more = (more + 4 < children.length) ? more + 4 : 0;
            return this;
        }

        if ('1' <= c && c <= '5') {
            int index = more + c - '1';
            assert(0 <= index);
            if (index < children.length) {
                children[index].generateReport();
                return children[index];
            }
        }

        switch(c) {
            case '6': characterMode = !inspectable; break;
            case '7': generateReport(); break;
            case '8': offset = Math.max(0, offset - 10); break;
            case '9': offset += 10; break;
            case '0': return null;
        }

        return this;
    }

    private static String leftAlignFixed(String string, int width) {
        assert(width > 0);
        return String.format("%-" + width + "." + width + "s", string);
    }

    private static String center(String string) {
        return String.format("%" + (string.length() + (WIDTH - string.length())/2) + "s", string);
    }
}
