
package com.github.iabarca.ss;

import java.util.List;

public class JsonSizzMatches {

    public static class Match {

        private String bluCountry;
        private String redCountry;
        private String hostname;
        private String bluname;
        private String redname;
        private int _id;
        private boolean isLive;

        public int getId() {
            return _id;
        }

        public boolean isLive() {
            return isLive;
        }

        @Override
        public String toString() {
            return _id + ";" + isLive + ";" + hostname + ";" + bluCountry + ";" + bluname + ";"
                    + redCountry + ";" + redname;
        }

    }

    private List<Match> matches;

    public List<Match> getMatches() {
        return matches;
    }

}
