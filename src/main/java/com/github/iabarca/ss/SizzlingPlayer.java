
package com.github.iabarca.ss;

import java.util.List;

public class SizzlingPlayer {

    public static class PreviousName {
        private String _id;
        private int frequency;
    }

    private int __v;
    private String _id;
    private String avatar;
    private String country;
    private String name;
    private String numericid;
    private String updated;
    private List<SizzlingMatch> matches;
    private int count;

    public String getId() {
        return _id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getNumericid() {
        return numericid;
    }

    public String getUpdated() {
        return updated;
    }

    public List<SizzlingMatch> getMatches() {
        return matches;
    }

    public int getCount() {
        return count;
    }
    
    @Override
    public String toString() {
        return name + " (" + numericid + ") [" + _id + "] with " + count + " matches";
    }

}
