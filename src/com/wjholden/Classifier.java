package com.wjholden;

import java.util.Collection;
import java.util.Queue;

public class Classifier implements Runnable {
    private final Queue<String> queue;

    public Classifier(Queue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {

        }
    }
}
