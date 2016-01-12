
package com.github.iabarca.ui;

import com.github.iabarca.logs.LogsTfApi;
import com.github.iabarca.logs.LogsTfCache;
import com.github.iabarca.ss.SizzlingApi;
import com.github.iabarca.ss.SizzlingCache;
import com.github.iabarca.stats.Stats;
import com.github.iabarca.stats.StatsApi;
import com.github.iabarca.task.ExportStatsTask;
import com.github.iabarca.task.GetStatsTask;
import com.github.iabarca.task.UgcScanTask;
import com.github.iabarca.ugc.UgcCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

public class Presenter {

    private static final Logger log = Logger.getLogger("stats");

    private Options options;
    private SizzlingCache statsCache;
    private UgcCache leagueCache;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private LogsTfCache logsTfCache;

    public Presenter(Options options) {
        this.options = options;
    }

    public void start() {
        if (options.has(options.getStats())) {
            ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime()
                    .availableProcessors());
            List<String> sources = options.valuesOf(options.getSources());
            List<String> tasks = options.valuesOf(options.getStats());
            try {
                if (sources.contains("ss")) {
                    exec.submit(launchStatsTasks(tasks, new SizzlingApi(options)));
                }
                if (sources.contains("logstf")) {
                    exec.submit(launchStatsTasks(tasks, new LogsTfApi(options)));
                }
            } catch (Exception e) {
                log.log(Level.INFO, "Exception when running stats tasks", e);
            } finally {
                exec.shutdown();
            }
        }
        if (options.has(options.getLeague())) {
            log.info("Executing UgcScanTask");
            SwingWorker<?, ?> worker = new UgcScanTask(this);
            worker.execute();
            try {
                worker.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable launchStatsTasks(final List<?> tasks, final StatsApi api) {
        return new Runnable() {

            @Override
            public void run() {
                Set<Long> newIds = null;
                if (tasks.contains("get")) {
                    log.info("Executing GetStatsTask");
                    SwingWorker<Set<Long>, ?> worker = new GetStatsTask(Presenter.this, api);
                    worker.execute();
                    try {
                        newIds = worker.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                if (tasks.contains("export")) {
                    log.info("Executing ExportStatsTask");
                    ExportStatsTask worker = new ExportStatsTask(Presenter.this, api);
                    if (newIds != null && options.has(options.getExportNewOnly())) {
                        worker.filterById(newIds);
                    }
                    worker.execute();
                    try {
                        worker.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public boolean accept(Stats stats) {
        int minPlayers = options.valueOf(options.getMinPlayers());
        int maxPlayers = options.valueOf(options.getMaxPlayers());
        int id = stats.getId();
        if (stats.getPlayers() != null) {
            int size = stats.getPlayers().size();
            if (size < minPlayers || size > maxPlayers) {
                log.info("[#" + id + "] Skipped. Only " + size + " players");
                return false;
            }
        } else {
            log.warning("[#" + id + "] Skipped. No player data found");
        }
        String map = stats.getMap();
        if (map == null || !map.matches(options.valueOf(options.getMaps()))) {
            log.info("[#" + id + "] Skipped. Not accepted map (" + map + ")");
            return false;
        }
        return true;
    }

    private SizzlingCache getSizzCache() {
        if (statsCache == null) {
            try {
                statsCache = gson.fromJson(new FileReader("sizzcache.json"), SizzlingCache.class);
                log.info("[Sizz] Loaded " + statsCache.getStatsSize() + " stats");
            } catch (JsonSyntaxException | JsonIOException e) {
                log.log(Level.INFO, "[Sizz] Default to new cache", e);
                statsCache = new SizzlingCache();
            } catch (FileNotFoundException e) {
                log.info("[Sizz] No stats cache found, default to new file");
                statsCache = new SizzlingCache();
            }
            statsCache.setSaveFile(new File("sizzcache.json"));
        }
        return statsCache;
    }

    private LogsTfCache getLogsTfCache() {
        if (logsTfCache == null) {
            try {
                logsTfCache = gson.fromJson(new FileReader("logstfcache.json"), LogsTfCache.class);
                log.info("[LogsTF] Loaded " + logsTfCache.getStats() + " stats");
            } catch (JsonSyntaxException | JsonIOException e) {
                log.log(Level.INFO, "[LogsTF] Default to new cache", e);
                logsTfCache = new LogsTfCache();
            } catch (FileNotFoundException e) {
                log.info("[LogsTF] No stats cache found, default to new file");
                logsTfCache = new LogsTfCache();
            }
            logsTfCache.setSaveFile(new File("logstfcache.json"));
        }
        return logsTfCache;
    }

    public Options getOptions() {
        return options;
    }

    private UgcCache getLeagueCache() {
        if (leagueCache == null) {
            try {
                leagueCache = gson.fromJson(
                        new FileReader(options.valueOf(options.getLeagueCacheFile())),
                        UgcCache.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                log.log(Level.INFO, "Default to new league cache", e);
                leagueCache = new UgcCache();
            } catch (FileNotFoundException e) {
                log.info("No league cache found, default to new file");
                leagueCache = new UgcCache();
            }
            leagueCache.setSaveFile(options.valueOf(options.getLeagueCacheFile()));
        }
        return leagueCache;
    }

}
