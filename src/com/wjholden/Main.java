package com.wjholden;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {
    private final List<String> db = new ArrayList<>();
    private final BlockingQueue<String> queue = new LinkedBlockingDeque<>();
    private final Classifier classifier = new Classifier(.20, queue, db);

    public Main() {
        final Page database = new Page("Database", new Page[0], () -> db);
        final Page clusters = new Page("Clusters", new Page[0], classifier::menu);
        final Page mainPage = new Page("Main Menu", new Page[] { database, clusters },
                () -> List.of("Logs: " + db.size(), "Clusters: " + classifier.clusterCount()),
                false, false, true);

        new Thread(classifier).start();
        new Thread(new Syslog(514, queue)).start();
        new Menu(23, mainPage);
    }

    public static void main(String[] args) {
        new Main();
    }

}
