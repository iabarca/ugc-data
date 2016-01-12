
package com.github.iabarca.ui;

import static java.util.Arrays.asList;

import com.github.iabarca.util.Utils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.util.List;

public class Options {

    private OptionSpec<Void> league;
    private OptionSpec<String> stats;
    private OptionSpec<Integer> start;
    private OptionSpec<Integer> stop;
    private OptionSpec<Integer> step;
    private OptionSpec<Integer> count;
    private OptionSpec<File> output;
    private OptionSpec<Integer> minPlayers;
    private OptionSpec<Integer> maxPlayers;
    private OptionSpec<String> maps;
    private OptionSpec<Void> exportNewOnly;
    private OptionSpec<Integer> season;
    private OptionSpec<Integer> weeks;
    private OptionSpec<Integer> playerLogScanDepth;
    private OptionSpec<Integer> minTeamRosterMatch;
    private OptionSpec<File> leagueCacheFile;
    private OptionSpec<Void> deleteWeekResults;
    private OptionSpec<Integer> scanGracePeriod;
    private OptionSpec<String> sources;
    private OptionSpec<Integer> saveInterval;
    private OptionSpec<Integer> requestDelay;
    private OptionSpec<Integer> maxAttempts;
    private OptionSpec<Integer> timeout;
    private OptionSpec<File> sizzCacheFile;
    private OptionSpec<File> logsTfCacheFile;
    private OptionParser parser;
    private OptionSet optionSet;

    public Options() {
        parser = new OptionParser();
        createOptions();
    }

    public OptionParser getParser() {
        return parser;
    }

    private void createOptions() {
        // Tasks
        league = parser.acceptsAll(asList("league", "l"),
                "Execute league official match stats search");
        stats = parser.acceptsAll(asList("stats", "s"), "Tasks to execute with stats data")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',')
                .defaultsTo("get", "export");

        // Get parameters
        start = parser.accepts("start", "First stats id to get").withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        stop = parser.accepts("stop", "Last stats id to get").withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        step = parser.accepts("step", "Step for each stats request").withRequiredArg()
                .ofType(Integer.class).defaultsTo(-1);
        count = parser.accepts("count", "Expected number of stats to get").withRequiredArg()
                .ofType(Integer.class).defaultsTo(100);

        // Export parameters
        output = parser.accepts("output", "File to export CSV stats data").withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("stats.dump-" + Utils.now("MMddHHmm") + ".csv"));
        minPlayers = parser.accepts("min-players", "Include stats with at least this player count")
                .withRequiredArg().ofType(Integer.class).defaultsTo(12);
        maxPlayers = parser.accepts("max-players", "Include stats with at most this player count")
                .withRequiredArg().ofType(Integer.class).defaultsTo(Integer.MAX_VALUE);
        maps = parser.accepts("maps", "Include stats with a map that matches this regex")
                .withRequiredArg().ofType(String.class).defaultsTo("^.*$");
        exportNewOnly = parser.acceptsAll(asList("new", "export-new-only"),
                "Only export stats obtained in this run of 'get' task");

        // League scan parameters
        season = parser.accepts("season", "Extract data from this league season").withRequiredArg()
                .ofType(Integer.class).defaultsTo(12);
        weeks = parser.accepts("weeks", "Extract data from these weeks").withRequiredArg()
                .ofType(Integer.class).withValuesSeparatedBy(',').defaultsTo(1, 2);
        playerLogScanDepth = parser.accepts("scan-depth",
                "How many matches to look for roster matches in each player's history")
                .withRequiredArg().ofType(Integer.class).defaultsTo(100);
        minTeamRosterMatch = parser
                .accepts("min-roster-match", "How many roster matches to look for in each match")
                .withRequiredArg().ofType(Integer.class).defaultsTo(7);
        leagueCacheFile = parser.accepts("league-cache", "UGC League cache file location")
                .withRequiredArg()
                .ofType(File.class).defaultsTo(new File("ugc-cache.json"));
        deleteWeekResults = parser.acceptsAll(
                asList("renew", "renew-results", "delete-week-results"),
                "Instructs the league cache to delete previously stored week results");
        scanGracePeriod = parser.accepts("grace-period",
                "For how many days after schedule date should we look for logs")
                .withRequiredArg().ofType(Integer.class).defaultsTo(10);

        // Cache parameters
        sources = parser.accepts("sources", "List of stats data sources to use").withRequiredArg()
                .ofType(String.class).withValuesSeparatedBy(',').defaultsTo("ss", "logstf");
        sizzCacheFile = parser.accepts("sizz-cache", "SizzlingStats cache file location")
                .withRequiredArg()
                .ofType(File.class).defaultsTo(new File("sizzling-cache.json"));
        logsTfCacheFile = parser.accepts("logstf-cache", "SizzlingStats cache file location")
                .withRequiredArg()
                .ofType(File.class).defaultsTo(new File("logstf-cache.json"));
        saveInterval = parser
                .accepts("save-interval", "Dump cache to disk after this amount of stats collected")
                .withRequiredArg().ofType(Integer.class).defaultsTo(50);

        // Connector parameters
        requestDelay = parser.accepts("request-delay", "Delay for each JSON request in ms")
                .withRequiredArg()
                .ofType(Integer.class).defaultsTo(100);
        maxAttempts = parser.accepts("max-attempts", "Maximum retries for each request")
                .withRequiredArg()
                .ofType(Integer.class).defaultsTo(3);
        timeout = parser.accepts("timeout", "Timeout for each request in ms").withRequiredArg()
                .ofType(Integer.class).defaultsTo(5000);

        parser.acceptsAll(asList("help", "?"), "This help").forHelp();
    }

    public OptionSpec<Void> getLeague() {
        return league;
    }

    public OptionSpec<String> getStats() {
        return stats;
    }

    public OptionSpec<Integer> getStart() {
        return start;
    }

    public OptionSpec<Integer> getStop() {
        return stop;
    }

    public OptionSpec<Integer> getStep() {
        return step;
    }

    public OptionSpec<Integer> getCount() {
        return count;
    }

    public OptionSpec<File> getOutput() {
        return output;
    }

    public OptionSpec<Integer> getMinPlayers() {
        return minPlayers;
    }

    public OptionSpec<Integer> getMaxPlayers() {
        return maxPlayers;
    }

    public OptionSpec<String> getMaps() {
        return maps;
    }

    public OptionSpec<Void> getExportNewOnly() {
        return exportNewOnly;
    }

    public OptionSpec<Integer> getSeason() {
        return season;
    }

    public OptionSpec<Integer> getWeeks() {
        return weeks;
    }

    public OptionSpec<Integer> getPlayerLogScanDepth() {
        return playerLogScanDepth;
    }

    public OptionSpec<Integer> getMinTeamRosterMatch() {
        return minTeamRosterMatch;
    }

    public OptionSpec<File> getLeagueCacheFile() {
        return leagueCacheFile;
    }

    public OptionSpec<Void> getDeleteWeekResults() {
        return deleteWeekResults;
    }

    public OptionSpec<Integer> getScanGracePeriod() {
        return scanGracePeriod;
    }

    public OptionSpec<String> getSources() {
        return sources;
    }

    public OptionSpec<Integer> getSaveInterval() {
        return saveInterval;
    }

    public OptionSpec<Integer> getRequestDelay() {
        return requestDelay;
    }

    public OptionSpec<Integer> getMaxAttempts() {
        return maxAttempts;
    }

    public OptionSpec<Integer> getTimeout() {
        return timeout;
    }

    public OptionSpec<File> getSizzCacheFile() {
        return sizzCacheFile;
    }

    public OptionSpec<File> getLogsTfCacheFile() {
        return logsTfCacheFile;
    }

    public OptionSet parse(String[] args) {
        optionSet = parser.parse(args);
        return optionSet;
    }

    public boolean has(OptionSpec<?> option) {
        return optionSet.has(option);
    }

    public boolean hasArgument(OptionSpec<?> option) {
        return optionSet.hasArgument(option);
    }

    public <T> T valueOf(OptionSpec<T> option) {
        return optionSet.valueOf(option);
    }

    public <T> List<T> valuesOf(OptionSpec<T> option) {
        return optionSet.valuesOf(option);
    }

}
