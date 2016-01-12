
package com.github.iabarca.ugc;

import com.github.iabarca.util.Utils;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.LinkedHashSet;
import java.util.Set;

public class UgcMatch {

    private int matchId;
    private int scheduleId;
    private String scheduleDate;
    private String mapName;
    private int homeClanId;
    private String homeTeam;
    private int visitingClanId;
    private int homeScore1;
    private int homeScore2;
    private int homeScore3;
    private String visitingTeam;
    private int visitingScore1;
    private int visitingScore2;
    private int visitingScore3;
    private int winnerClanId;
    private String winnerTeam;

    private Set<Integer> statsId;

    public UgcMatch(int matchId, int scheduleId, String scheduleDate, String mapName,
            int homeClanId,
            String homeTeam, int visitingClanId, int homeScore1, int homeScore2, int homeScore3,
            String visitingTeam, int visitingScore1, int visitingScore2, int visitingScore3,
            int winnerClanId, String winnerTeam) {
        this.matchId = matchId;
        this.scheduleId = scheduleId;
        this.scheduleDate = scheduleDate;
        this.mapName = mapName;
        this.homeClanId = homeClanId;
        this.homeTeam = homeTeam;
        this.visitingClanId = visitingClanId;
        this.homeScore1 = homeScore1;
        this.homeScore2 = homeScore2;
        this.homeScore3 = homeScore3;
        this.visitingTeam = visitingTeam;
        this.visitingScore1 = visitingScore1;
        this.visitingScore2 = visitingScore2;
        this.visitingScore3 = visitingScore3;
        this.winnerClanId = winnerClanId;
        this.winnerTeam = winnerTeam;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public LocalDateTime getScheduleDateTime() {
        return Utils.parseDate(scheduleDate, "MMM, dd yyyy HH:mm:ss");
    }

    public String getScheduleFormattedTime(String format) {
        return getScheduleDateTime().toString(
                DateTimeFormat.forPattern(format));
    }

    public String getMapName() {
        return mapName;
    }

    public int getHomeClanId() {
        return homeClanId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public int getVisitingClanId() {
        return visitingClanId;
    }

    public int getHomeScore1() {
        return homeScore1;
    }

    public int getHomeScore2() {
        return homeScore2;
    }

    public int getHomeScore3() {
        return homeScore3;
    }

    public String getVisitingTeam() {
        return visitingTeam;
    }

    public int getVisitingScore1() {
        return visitingScore1;
    }

    public int getVisitingScore2() {
        return visitingScore2;
    }

    public int getVisitingScore3() {
        return visitingScore3;
    }

    public int getWinnerClanId() {
        return winnerClanId;
    }

    public String getWinnerTeam() {
        return winnerTeam;
    }

    @Override
    public String toString() {
        return matchId + ";" + homeTeam + ";" + homeClanId + ";" + visitingTeam + ";"
                + visitingClanId + ";" + mapName + ";"
                + getScheduleFormattedTime("yyyy-MM-dd HH:mm:ss");
    }

    public void addStatsMatchId(int id) {
        getStatsId().add(id);
    }

    public Set<Integer> getStatsId() {
        if (statsId == null) {
            statsId = new LinkedHashSet<Integer>();
        }
        return statsId;
    }

}
