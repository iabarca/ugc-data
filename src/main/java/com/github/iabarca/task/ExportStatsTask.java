
package com.github.iabarca.task;

import com.github.iabarca.stats.Stats;
import com.github.iabarca.stats.StatsApi;
import com.github.iabarca.ui.Options;
import com.github.iabarca.ui.Presenter;
import com.google.common.base.Predicate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

public class ExportStatsTask extends SwingWorker<Void, String> {

    private static final Logger log = Logger.getLogger("stats");

    private Presenter presenter;
    private Options options;
    private Path path;
    private Set<Long> idFilter;
    private StatsApi api;

    public ExportStatsTask(Presenter presenter, StatsApi api) {
        this.presenter = presenter;
        this.options = presenter.getOptions();
        this.path = options.valueOf(options.getOutput()).toPath();
        this.api = api;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            exportStats();
        } catch (Exception e) {
            log.log(Level.INFO, "", e);
        }
        return null;
    }

    private void exportStats() {
        writeHeader();
        api.applyOnStats(new Predicate<Stats>() {

            @Override
            public boolean apply(Stats input) {
                if (presenter.accept(input)
                        && (idFilter == null || idFilter.contains(input.getId()))) {
                    publish(input.toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void writeHeader() {
        try {
            com.google.common.io.Files.write("'ID;Map;Rounds;Duration;DamageRED;"
                    + "FragsRED;DamageBLU;FragsBLU;ScoreRED;ScoreBLU;"
                    + "ScoresBLU;RoundTimes\r\n", path.toFile(), Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.INFO, "", e);
        }
    }

    @Override
    protected void process(List<String> chunks) {
        try {
            Files.write(path, chunks, Charset.defaultCharset(), StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.log(Level.INFO, "", e);
        }
    }

    public void filterById(Set<Long> set) {
        this.idFilter = set;
    }

}
