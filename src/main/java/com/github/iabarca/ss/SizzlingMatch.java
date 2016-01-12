
package com.github.iabarca.ss;

import com.github.iabarca.stats.StatsMatch;
import com.github.iabarca.util.Utils;

import org.joda.time.LocalDateTime;

public class SizzlingMatch implements StatsMatch {

    private int _id;
    private String bluCountry;
    private String bluname;
    private String created;
    private String hostname;
    private String redCountry;
    private String redname;

    public int getId() {
        return _id;
    }

    @Override
    public String toString() {
        return "#" + _id;
    }

    @Override
    public LocalDateTime getCreatedDateTime() {
        return Utils.parseDate(created, SizzlingApi.DATE_FORMAT);
    }

}
