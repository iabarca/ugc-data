
package com.github.iabarca.ss;

import com.github.iabarca.logs.LogsTfStats.PlayerStats;
import com.github.iabarca.stats.IPlayerStats;
import com.github.iabarca.stats.Stats;
import com.github.iabarca.util.Utils;

import org.joda.time.LocalDateTime;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SizzlingStats implements Stats {

    public static class Chat {
        private String steamid;
        private boolean isTeam;
        private int time;
        private String message;
        private String _id;
    }

    public static class PlayerStats implements IPlayerStats {
        private String name;
        private String steamid;
        private int team;
        private String _id;
        private List<Integer> medpicks;
        private List<Integer> ubersdropped;
        private List<Integer> healsreceived;
        private List<Integer> points;
        private List<Integer> bonuspoints;
        private List<Integer> resupplypoints;
        private List<Integer> crits;
        private List<Integer> damagedone;
        private List<Integer> teleports;
        private List<Integer> invulns;
        private List<Integer> healpoints;
        private List<Integer> backstabs;
        private List<Integer> headshots;
        private List<Integer> buildingsdestroyed;
        private List<Integer> buildingsbuilt;
        private List<Integer> revenge;
        private List<Integer> dominations;
        private List<Integer> suicides;
        private List<Integer> defenses;
        private List<Integer> captures;
        private List<Integer> deaths;
        private List<Integer> killassists;
        private List<Integer> kills;
        private List<Integer> playedclasses;
        private List<Integer> mostplayedclass;
        private String avatar;
        private String numericid;
        private String country;

        public int getTeam() {
            return team;
        }

        public int getDamagedoneSum() {
            int sum = 0;
            for (Integer i : damagedone) {
                if (i != null) {
                    sum += i;
                }
            }
            return sum;
        }

        public int getKillsSum() {
            int sum = 0;
            for (Integer i : kills) {
                if (i != null) {
                    sum += i;
                }
            }
            return sum;
        }

    }

    private int __v;
    private int _id;
    private String bluCountry;
    private String bluname;
    private String created;
    private String hostip;
    private String hostname;
    private int hostport;
    private String ip;
    private String map;
    private int matchDuration;
    private String pluginVersion;
    private String redCountry;
    private String redname;
    private int round;
    private String updated;
    private int viewCount;
    private boolean isLive;
    private List<Chat> chats;
    private Map<String, PlayerStats> players;
    private List<Integer> roundduration;
    private List<Integer> teamfirstcap;
    private List<Integer> bluscore;
    private List<Integer> redscore;

    private transient Map<String, IPlayerStats> playersStats;

    public int getId() {
        return _id;
    }

    public boolean isLive() {
        return isLive;
    }

    @Override
    public Map<String, IPlayerStats> getPlayers() {
        if (playersStats == null) {
            playersStats = new LinkedHashMap<>();
            for (Entry<String, PlayerStats> e : players.entrySet()) {
                playersStats.put(e.getKey(), e.getValue());
            }
        }
        return playersStats;
    }

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public String toString() {
        return _id + ";" + map + ";" + round + ";" + sum(roundduration) + ";" + damage(2) + ";"
                + frags(2) + ";" + damage(3) + ";" + frags(3) + ";" + sum(redscore) + ";"
                + sum(bluscore) + ";" + string(bluscore, '|') + ";" + string(roundduration, '|');
    }

    private int damage(int i) {
        int sum = 0;
        for (PlayerStats player : players.values()) {
            if (player.getTeam() == i) {
                sum += player.getDamagedoneSum();
            }
        }
        return sum;
    }

    private int frags(int i) {
        int sum = 0;
        for (PlayerStats player : players.values()) {
            if (player.getTeam() == i) {
                sum += player.getKillsSum();
            }
        }
        return sum;
    }

    private int sum(List<Integer> list) {
        int sum = 0;
        for (Integer i : list) {
            if (i != null) {
                sum += i;
            }
        }
        return sum;
    }

    private String string(List<Integer> list, char del) {
        Iterator<Integer> it = list.iterator();
        if (!it.hasNext())
            return "";

        StringBuilder sb = new StringBuilder();
        for (;;) {
            Integer e = it.next();
            sb.append(e);
            if (!it.hasNext())
                return sb.toString();
            sb.append(del);
        }
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public LocalDateTime getCreatedDate() {
        return Utils.parseDate(created, SizzlingApi.DATE_FORMAT);
    }

    public LocalDateTime getUpdatedDate() {
        return Utils.parseDate(updated, SizzlingApi.DATE_FORMAT);
    }

}
