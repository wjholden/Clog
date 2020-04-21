package com.wjholden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Main {

    private final static List<String> db = new ArrayList<>();

    private final static Page database = new Page("Database", "Database",
            new Page[0],
            () -> db);
    private final static Page doubles = new Page("Doubles (Random 1000)", "Doubles",
            new Page[] { database },
            () -> DoubleStream.generate(Math::random).limit(1000).boxed().map(Object::toString).collect(Collectors.toList()));
    private final static Page integers = new Page("Integers (Random 100)", "Integers",
            new Page[] { doubles, database },
            () -> IntStream.generate(() -> (int) (1000 * Math.random())).limit(100).boxed().map(Object::toString).collect(Collectors.toList()));
    private final static Page empty = new Page("Empty", "Empty",
            new Page[0],
            Collections::emptyList);
    private final static Page mainPage = new Page("Main Menu", "Main",
            new Page[] { doubles, integers, database, empty },
            () -> List.of("Hello", "World"));

    public static void main(String[] args) {
        new Thread(new Syslog(514, db)).start();
        new Menu(23, mainPage);
    }
}
