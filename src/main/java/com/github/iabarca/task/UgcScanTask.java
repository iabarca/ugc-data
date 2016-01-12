
package com.github.iabarca.task;

import com.github.iabarca.ss.SizzlingApi;
import com.github.iabarca.ss.SizzlingCache;
import com.github.iabarca.ss.SizzlingMatch;
import com.github.iabarca.ss.SizzlingStats;
import com.github.iabarca.ss.SizzlingStats.PlayerStats;
import com.github.iabarca.ugc.UgcMatch;
import com.github.iabarca.ugc.UgcMember;
import com.github.iabarca.ugc.UgcRoster;
import com.github.iabarca.ugc.UgcTeam;
import com.github.iabarca.ugc.UgcApi;
import com.github.iabarca.ugc.UgcCache;
import com.github.iabarca.ugc.UgcWeekResults;
import com.github.iabarca.ui.Presenter;
import com.github.iabarca.util.Connector.ConnectorException;
import com.github.iabarca.util.Pair;
import com.github.iabarca.util.Utils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;

import joptsimple.OptionSet;

import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

public class UgcScanTask extends SwingWorker<Void, Void> {

    private static final Logger log = Logger.getLogger("stats");

    private Presenter presenter;
    private UgcApi ugc;
    private SizzlingApi sizz;
    private UgcCache leagueCache;
    private SizzlingCache statsCache;
    private Map<Integer, Map<Integer, UgcWeekResults>> allResults;
    private OptionSet options;

    public UgcScanTask(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            scan();
        } catch (Exception e) {
            log.log(Level.INFO, "Exception while running UGC Scan Task", e);
        }
        return null;
    }

    private void scan() {
        ugc = new UgcApi(presenter.getNet(), presenter.getGson());
        sizz = new SizzlingApi(presenter.getNet(), presenter.getGson());
        leagueCache = presenter.getLeagueCache();
        statsCache = presenter.getStatsCache();
        allResults = leagueCache.getMatches();
        options = presenter.getOptions();
        int season = Math.max(1, (int) options.valueOf("season"));
        for (Object obj : options.valuesOf("weeks")) {
            int week = Math.max(0, (int) obj);
            Path path = Paths.get(Utils.now("MMddHHmm") + "-UGC-s" + season + "w" + week + ".csv");
            try {
                com.google.common.io.Files.write("MatchId;HomeTeam;HomeTeamId;VisitTeam;"
                        + "VisitTeamId;MapName;ScheduleDate;StatsId;Map;Rounds;Duration;"
                        + "DamageRED;FragsRED;DamageBLU;FragsBLU;ScoreRED;ScoreBLU;"
                        + "ScoresBLU;RoundTimes\r\n", path.toFile(), Charset.defaultCharset());
            } catch (IOException e) {
                log.log(Level.INFO, "", e);
            }
            try {
                getLeagueData(path, season, week);
            } catch (JsonSyntaxException | ConnectorException e) {
                log.log(Level.INFO, "Exception while retrieving season " + season + " week " + week
                        + " data", e);
            }
        }
    }

    private UgcTeam getTeam(long id) throws ConnectorException {
        UgcTeam team;
        if (!leagueCache.getTeams().containsKey(id)) {
            team = ugc.getTeam((int) id);
            leagueCache.getTeams().put(id, team);
            leagueCache.save();
        } else {
            team = leagueCache.getTeams().get(id);
        }
        return team;
    }

    private void getLeagueData(Path path, int season, int week) throws ConnectorException {
        UgcWeekResults weekResults = getWeekResults(season, week);
        leagueCache.save();
        for (UgcMatch match : weekResults.getMatches()) {
            if (!match.getStatsId().isEmpty()) {
                log.info("Already found stats for this match");
                for (Integer id : match.getStatsId()) {
                    write(path, new Pair<UgcMatch, SizzlingStats>(match, getStats(id)));
                }
                continue;
            }
            // Get the players of both teams
            try {
                UgcTeam home = getTeam(match.getHomeClanId());
                UgcTeam visit = getTeam(match.getVisitingClanId());
                if (home == null || visit == null) {
                    log.info("Skipping match id " + match.getMatchId());
                    continue;
                }
                UgcRoster homeRoster = home.getRoster();
                UgcRoster visitRoster = visit.getRoster();
                log.info("Finding stats for match " + match.getMatchId() + " - " + home.getTag()
                        + " vs " + visit.getTag() + " @ " + match.getMapName());
                scanStats(match, homeRoster, visitRoster, path);
            } catch (JsonSyntaxException | ConnectorException e) {
                log.log(Level.INFO, "Exception while retrieving team and stats data", e);
            }
        }
    }

    private void scanStats(UgcMatch match, UgcRoster homeRoster, UgcRoster visitRoster, Path path)
            throws ConnectorException {
        Iterable<UgcMember> players = Iterables.concat(homeRoster.getMembers(),
                visitRoster.getMembers());
        int depth = (int) options.valueOf("scan-depth");
        int minRosterMatch = (int) options.valueOf("min-roster-match");
        int saveInterval = (int) options.valueOf("save-interval");
        Set<Integer> scanned = new LinkedHashSet<>();
        for (UgcMember player : players) {
            log.info("Finding last matches for player: " + player);
            int count = 0;
            LocalDateTime scheduleDate = match.getScheduleDateTime();
            for (SizzlingMatch sizzMatch : sizz.getAllMatches(player.getSteamId32())) {
                int id = sizzMatch.getId();
                if (scanned.contains(id)) {
                    count++;
                    continue;
                }
                scanned.add(id);
                log.info("[#" + id + "] Retrieving match stats");
                if (count >= depth) {
                    log.info("[#" + id + "] Maximum depth reached");
                    break;
                }
                SizzlingStats stats = getStats(id);
                if (stats != null) {
                    if (stats.isLive() || stats.getMap().equals(match.getMapName())) {
                        continue;
                    }
                    statsCache.save(saveInterval);
                    LocalDateTime statsDate = stats.getCreatedDate();
                    if (statsDate.isBefore(scheduleDate)) {
                        log.info("[#" + id + "] Stats are older than schedule of match");
                        return;
                    }
                    if (!statsDate.isAfter(scheduleDate.plusDays((int) options
                            .valueOf("grace-period")))) {
                        int homeRosterMatched = 0;
                        int visitRosterMatched = 0;
                        Map<String, PlayerStats> participants = stats.getPlayers();
                        for (UgcMember toMatch : homeRoster.getMembers()) {
                            if (participants.containsKey(toMatch.getSteamId32())) {
                                ++homeRosterMatched;
                            }
                        }
                        for (UgcMember toMatch : visitRoster.getMembers()) {
                            if (participants.containsKey(toMatch.getSteamId32())) {
                                ++visitRosterMatched;
                            }
                        }
                        if (homeRosterMatched >= minRosterMatch
                                && visitRosterMatched >= minRosterMatch) {
                            log.info("[#" + id + "] Matched stats with league data ("
                                    + homeRosterMatched + "h/" + visitRosterMatched + "v)");
                            presenter.getLeagueCache().save();
                            write(path, new Pair<UgcMatch, SizzlingStats>(match, stats));
                        } else {
                            log.info("[#" + id + "] Not enough matched players ("
                                    + homeRosterMatched + "h/" + visitRosterMatched + "v)");
                        }
                    }
                }
                count++;
            }
        }
    }

    private UgcWeekResults getWeekResults(int season, int week) throws JsonSyntaxException,
            ConnectorException {
        Map<Integer, UgcWeekResults> seasonResults = getSeasonResults(season);
        UgcWeekResults results = null;
        if (options.has("renew")) {
            seasonResults.remove(week);
        }
        if (seasonResults.containsKey(week)) {
            results = seasonResults.get(week);
        } else {
            results = ugc.getWeekResults(season, week);
            if (results != null) {
                seasonResults.put(week, results);
            }
        }
        return results;
    }

    private Map<Integer, UgcWeekResults> getSeasonResults(int season) {
        Map<Integer, UgcWeekResults> map;
        if (allResults.containsKey(season)) {
            map = allResults.get(season);
        } else {
            map = new LinkedHashMap<Integer, UgcWeekResults>();
            allResults.put(season, map);
        }
        return map;
    }

    private SizzlingStats getStats(int id) throws ConnectorException {
        if (!statsCache.getStats().containsKey((long) id)
                && !statsCache.getInvalid().contains((long) id)) {
            SizzlingStats stats = sizz.getStats(id);
            if (stats != null) {
                statsCache.getStats().put((long) id, stats);
            }
            return stats;
        } else {
            return statsCache.getStats().get((long) id);
        }
    }

    private void write(Path path, Pair<UgcMatch, SizzlingStats> pair) {
        UgcMatch match = pair.getFirst();
        SizzlingStats stats = pair.getSecond();
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null");
        } else if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null");
        } else {
            log.info("Matched id: " + match.getMatchId() + " with stats id: " + stats.getId());
            match.addStatsMatchId(stats.getId());
            try {
                Files.write(path, Lists.newArrayList(match.toString() + ";" + stats.toString()),
                        Charset.defaultCharset(), StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.log(Level.INFO, "", e);
            }
        }
    }

    @Override
    protected void done() {
        presenter.getLeagueCache().save();
        presenter.getStatsCache().save();
    }
}
