
package com.github.iabarca.ss;

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
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SizzlingApi implements StatsApi {

    private static class SizzMatchIterator implements Iterator<StatsMatch> {

        private SizzlingApi api;
        private Queue<SizzlingMatch> cache = new ArrayDeque<>();
        private String id;
        private int skip = 0;

        public SizzMatchIterator(SizzlingApi api, String id32) {
            this.api = api;
            this.id = id32;
            loadMatches();
        }

        @Override
        public boolean hasNext() {
            if (cache.peek() != null) {
                return true;
            }
            List<SizzlingMatch> list = loadMatches();
            return (list != null && !list.isEmpty());
        }

        private List<SizzlingMatch> loadMatches() {
            try {
                List<SizzlingMatch> list = api.getMatches(id, Math.max(1, skip));
                skip += list.size();
                log.info("Loaded " + list.size() + " matches: " + list);
                cache.addAll(list);
                return list;
            } catch (ConnectorException e) {
                log.log(Level.INFO, "", e);
            }
            return null;
        }

        @Override
        public SizzlingMatch next() {
            return cache.poll();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private static final Logger log = Logger.getLogger("stats");

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String MATCHES = "http://sizzlingstats.com/api/matches";
    public static final String PLAYER_LAST_MATCHES = "http://sizzlingstats.com/api/player/{id64}";
    public static final String PLAYER_MATCHES = "http://sizzlingstats.com/api/player/{id32}/matches?currentmatch={match}&skip={skip}";

    public static final String STATS = "http://sizzlingstats.com/api/stats/{id}";

    private Connector conn = new Connector();
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private SizzlingCache cache;

    public SizzlingApi(Options options) {
        conn.setMaxAttempts(options.valueOf(options.getMaxAttempts()));
        conn.setTimeout(options.valueOf(options.getTimeout()));
        conn.setRequestDelay(options.valueOf(options.getRequestDelay()));

        File cacheFile = options.valueOf(options.getSizzCacheFile());
        try {
            cache = gson.fromJson(new FileReader(cacheFile), SizzlingCache.class);
            log.info("[Sizzling] Loaded " + cache.getStatsSize() + " stats");
        } catch (JsonSyntaxException | JsonIOException e) {
            log.log(Level.INFO, "[Sizzling] Default to new cache", e);
            cache = new SizzlingCache();
        } catch (FileNotFoundException e) {
            log.info("[Sizzling] No stats cache found, default to new file");
            cache = new SizzlingCache();
        }
        cache.setSaveFile(cacheFile);
        cache.setSaveInterval(options.valueOf(options.getSaveInterval()));
    }

    @Override
    public Iterable<StatsMatch> getAllMatchesBySteamId(String id32) {
        final String id = id32;
        return new Iterable<StatsMatch>() {

            @Override
            public Iterator<StatsMatch> iterator() {
                return new SizzMatchIterator(SizzlingApi.this, id);
            }
        };
    }

    private List<SizzlingMatch> getLastMatches(long id64) throws ConnectorException {
        String url = PLAYER_LAST_MATCHES.replace("{id64}", id64 + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonSizzPlayer.class).getMatches();
        }
        return null;
    }

    private int getLastMatchId(long id64) throws ConnectorException {
        String url = PLAYER_LAST_MATCHES.replace("{id64}", id64 + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonSizzPlayer.class).getMatches().get(0).getId();
        }
        return 0;
    }

    @Override
    public int getLatestStatsId() throws ConnectorException {
        String json = conn.getJson(MATCHES);
        return gson.fromJson(json, JsonSizzMatches.class).getMatches().get(0).getId();
    }

    private List<SizzlingMatch> getMatches(String id32, int skip) throws ConnectorException {
        String url = PLAYER_MATCHES.replace("{id32}", id32)
                .replace("{match}", Integer.MAX_VALUE + "").replace("{skip}", skip + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonSizzPlayer.class).getMatches();
        }
        return null;
    }

    private SizzlingPlayer getPlayer(long id64) throws ConnectorException {
        String url = PLAYER_LAST_MATCHES.replace("{id64}", id64 + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonSizzPlayer.class).getPlayer();
        }
        return null;
    }

    private SizzlingStats getStats(int id) throws ConnectorException {
        String url = STATS.replace("{id}", id + "");
        String json = conn.getJson(url);
        if (json != null) {
            return gson.fromJson(json, JsonSizzStats.class).getStats();
        }
        return null;
    }

    @Override
    public Stats getStatsById(int id) {
        if (!cache.hasStatsById(id) && !cache.isInvalid(id)) {
            try {
                SizzlingStats stats = getStats(id);
                if (stats != null && !stats.isLive()) {
                    cache.putStats((long) id, stats);
                    cache.save(false);
                    log.info("[Sizzling] (#" + id + ") Stats stored in cache");
                    return stats;
                } else {
                    log.info("[Sizzling] (#" + id + ") Skipping ive match");
                }
            } catch (ConnectorException e) {
                cache.addInvalid(id);
                log.info("[Sizzling] (#" + id + ") Invalid match. Error code " + e.getCode());
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
