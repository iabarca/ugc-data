
package com.github.iabarca.logs;

import com.github.iabarca.stats.Stats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogsTfCache {

    private static final Logger log = Logger.getLogger("stats");

    private Map<Long, LogsTfStats> stats = new LinkedHashMap<>();

    private transient int lastStatsCount;
    private transient Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private transient File saveFile;
    private transient int saveInterval;

    public Map<Long, LogsTfStats> getStats() {
        return stats;
    }

    public void save(boolean force) {
        if (force) {
            save();
        } else {
            if (lastStatsCount == 0) {
                lastStatsCount = stats.size();
            }
            if (stats.size() >= saveInterval + lastStatsCount) {
                save();
            }
        }
    }

    public void save() {
        lastStatsCount = stats.size();
        try {
            com.google.common.io.Files.write(gson.toJson(this), saveFile, Charset.defaultCharset());
            log.info("[LogsTF] Saving " + lastStatsCount + " stats from matches");
        } catch (IOException e) {
            log.log(Level.INFO, "", e);
        }
    }

    public void setSaveFile(File file) {
        this.saveFile = file;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public int getStatsSize() {
        return stats.size();
    }

    public boolean hasStatsById(int id) {
        return stats.containsKey(id);
    }

    public LogsTfStats putStats(long id, LogsTfStats value) {
        return stats.put(id, value);
    }

    public Stats getStatsById(int id) {
        return stats.get(id);
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

}
