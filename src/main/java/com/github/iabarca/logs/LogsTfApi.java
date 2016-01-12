
package com.github.iabarca.logs;

import com.github.iabarca.stats.Stats;
import com.github.iabarca.stats.StatsApi;
import com.github.iabarca.stats.StatsMatch;
import com.github.iabarca.ui.Options;
import com.github.iabarca.util.Connector;
import com.github.iabarca.util.Connector.ConnectorException;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogsTfApi implements StatsApi {

    private static final Logger log = Logger.getLogger("stats");

    public static final String MATCHES = "http://logs.tf/json_search?player={id32}";
    public static final String STATS = "http://logs.tf/json/{id}";

    private Connector conn = new Connector();
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private LogsTfCache cache;

    public LogsTfApi(Options options) {
        conn.setMaxAttempts(options.valueOf(options.getMaxAttempts()));
        conn.setTimeout(options.valueOf(options.getTimeout()));
        conn.setRequestDelay(options.valueOf(options.getRequestDelay()));

        File cacheFile = options.valueOf(options.getLogsTfCacheFile());
        try {
            cache = gson.fromJson(new FileReader(cacheFile), LogsTfCache.class);
            log.info("[LogsTF] Loaded " + cache.getStatsSize() + " stats");
        } catch (JsonSyntaxException | JsonIOException e) {
            log.log(Level.INFO, "[LogsTF] Default to new cache", e);
            cache = new LogsTfCache();
        } catch (FileNotFoundException e) {
            log.info("[LogsTF] No stats cache found, default to new file");
            cache = new LogsTfCache();
        }
        cache.setSaveFile(cacheFile);
    }

    private LogsTfStats getStats(int id) throws ConnectorException {
        String url = STATS.replace("{id}", id + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, LogsTfStats.class);
        }
        return null;
    }

    private List<LogsTfMatch> getMatches(String id32) throws ConnectorException {
        String url = MATCHES.replace("{id32}", id32);
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonLogsMatches.class).getMatches();
        }
        return null;
    }

    @Override
    public Iterable<StatsMatch> getAllMatchesBySteamId(String id32) {
        try {
            List<LogsTfMatch> matches = getMatches(id32);
            return new ArrayList<StatsMatch>(matches);
        } catch (ConnectorException e) {
            log.log(Level.INFO, "[Sizzling] Could not get matches from id " + id32, e);
            return new ArrayList<StatsMatch>();
        }
    }

    @Override
    public int getLatestStatsId() throws ConnectorException {
        throw new UnsupportedOperationException(
                "Could not get latest stats id. Please define --start parameter");
    }

    @Override
    public Stats getStatsById(int id) {
        if (!cache.hasStatsById(id)) {
            try {
                LogsTfStats stats = getStats(id);
                if (stats != null) {
                    stats.setId(id);
                    cache.putStats((long) id, stats);
                    cache.save(false);
                    log.info("[LogsTF] (#" + id + ") Stats stored in cache");
                    return stats;
                } else {
                    log.info("[LogsTF] (#" + id + ") No stats found");
                }
            } catch (ConnectorException e) {
                log.info("[LogsTF] (#" + id + ") Invalid match. Error code " + e.getCode());
            }
        } else {
            return cache.getStatsById(id);
        }
        return null;
    }

    @Override
    public void cacheSave() {
        cache.save();
    }

    @Override
    public void applyOnStats(Predicate<Stats> predicate) {
        // TODO Auto-generated method stub
        
    }

}
