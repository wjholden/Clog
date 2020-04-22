package com.wjholden;

import java.time.Instant;
import java.util.Objects;

public class LogEvent implements Comparable<LogEvent> {
    final protected String text;
    final protected Instant time;

    public LogEvent(String text, Instant time) {
        this.text = text;
        this.time = time;
    }

    @Override
    public int compareTo(LogEvent o) {
        return (time.compareTo(o.time) << 8) | text.compareTo(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        LogEvent logEvent = (LogEvent) o;
        return text.equals(logEvent.text) &&
                time.equals(logEvent.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, time);
    }
}
