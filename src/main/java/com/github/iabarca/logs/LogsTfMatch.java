
package com.github.iabarca.logs;

import com.github.iabarca.stats.StatsMatch;

import org.joda.time.LocalDateTime;

public class LogsTfMatch implements StatsMatch {

    private long date;
    private int id;
    private String title;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public LocalDateTime getCreatedDateTime() {
        return new LocalDateTime(date * 1000L);
    }

    @Override
    public String toString() {
        return "#" + id;
    }

}
