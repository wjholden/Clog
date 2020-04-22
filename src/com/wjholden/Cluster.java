package com.wjholden;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private final List<LogEvent> events;

    public Cluster(LogEvent e) {
        events = new ArrayList<>();
        events.add(e);
    }
}
