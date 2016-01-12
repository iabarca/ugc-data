
package com.github.iabarca.logs;

import com.github.iabarca.stats.IPlayerStats;
import com.github.iabarca.stats.Stats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LogsTfStats implements Stats {

    public static class TeamScore {
        private int score;
        private int kills;
        private int deaths;
        private int dmg;
        private int charges;
        private int drops;
        private int firstcaps;
        private int caps;
    }

    public static class WeaponStats {
        private int kills;
        private int dmg;
        private double avg_dmg;
        private int shots;
        private int hits;
    }

    public static class ClassStats {
        private String type;
        private int kills;
        private int assists;
        private int deaths;
        private int dmg;
        private Map<String, WeaponStats> weapon;
        private int total_time;
    }

    public static class MedicStats {
        private int advantages_lost;
        private int biggest_advantage_lost;
        private int deaths_with_95_99_uber;
        private int deaths_within_20s_after_uber;
        private double avg_time_before_healing;
        private double avg_time_to_build;
        private double avg_time_before_using;
        private double avg_uber_length;
    }

    public static class PlayerStats implements IPlayerStats {
        private String team;
        private List<ClassStats> class_stats;
        private int kills;
        private int deaths;
        private int assists;
        private int suicides;
        private String kapd;
        private String kpd;
        private int dmg;
        private int dmg_real;
        private int dt;
        private int dt_real;
        private int hr;
        private int lks;
        private int as;
        private int dapd;
        private int dapm;
        private int ubers;
        private Map<String, Integer> ubertypes;
        private int drops;
        private int medkits;
        private int medkits_hp;
        private int backstabs;
        private int headshots;
        private int headshots_hit;
        private int sentries;
        private int heal;
        private int cpc;
        private int ic;
        private MedicStats medicstats;
    }

    public static class TeamRoundStats {
        private int score;
        private int kills;
        private int dmg;
        private int ubers;
    }

    public static class Event {
        private String type;
        private int time;
        private String team;
        private int point;
        private String medigun;
        private String steamid;
        private String killer;
    }

    public static class PlayerRoundStats {
        private int kills;
        private int dmg;
    }

    public static class RoundStats {
        private long start_time;
        private String winner;
        private Map<String, TeamRoundStats> team;
        private List<Event> events;
        private Map<String, PlayerRoundStats> players;
        private String firstcap;
        private int length;
    }

    public static class ClassCounter {
        private int demoman;
        private int scout;
        private int spy;
        private int engineer;
        private int pyro;
        private int soldier;
        private int sniper;
        private int heavyweapons;
        private int medic;
    }

    public static class Chat {
        private String steamid;
        private String name;
        private String msg;
    }

    public static class ServerInfo {
        private String map;
        private boolean supplemental;
        private int total_length;
        private boolean hasRealDamage;
        private boolean hasWeaponDamage;
        private boolean hasAccuracy;
        private boolean hasHP;
        private boolean hasHP_real;
        private boolean hasHS;
        private boolean hasHS_hit;
        private boolean hasBS;
        private boolean hasCP;
        private boolean hasDT;
        private boolean hasAS;
        private boolean hasHR;
        private boolean hasIntel;
        private List<Object> notifications;
    }

    public static class KillStreak {
        private String steamid;
        private int streak;
        private int time;
    }

    private int version;
    private Map<String, TeamScore> teams;
    private int length;
    private Map<String, PlayerStats> players;
    private Map<String, String> names;
    private List<RoundStats> rounds;
    private Map<String, Map<String, Integer>> healspread;
    private Map<String, ClassCounter> classkills;
    private Map<String, ClassCounter> classdeaths;
    private Map<String, ClassCounter> classkillassists;
    private List<Chat> chat;
    private ServerInfo info;
    private List<KillStreak> killstreaks;
    private int id;
    private transient Map<String, IPlayerStats> playersStats;

    @Override
    public boolean isLive() {
        // no live match support
        return false;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getMap() {
        if (info != null) {
            if (info.map != null) {
                return info.map;
            }
        }
        return null;
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

}
