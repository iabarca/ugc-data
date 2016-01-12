
package com.github.iabarca.task;

import com.github.iabarca.stats.Stats;
import com.github.iabarca.stats.StatsApi;
import com.github.iabarca.ui.Options;
import com.github.iabarca.ui.Presenter;
import com.github.iabarca.util.Connector.ConnectorException;

import joptsimple.OptionSet;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

public class GetStatsTask extends SwingWorker<Set<Long>, Void> {

    private static final Logger log = Logger.getLogger("stats");

    private Presenter presenter;
    private Options options;
    private Set<Long> collectedIds = new LinkedHashSet<>();
    private StatsApi api;

    public GetStatsTask(Presenter presenter, StatsApi api) {
        this.presenter = presenter;
        this.options = presenter.getOptions();
        this.api = api;
    }

    @Override
    protected Set<Long> doInBackground() throws Exception {
        try {
            getStats();
        } catch (Exception e) {
            log.log(Level.INFO, "", e);
        }
        return collectedIds;
    }

    private void getStats() throws ConnectorException {
        int id1 = Math.max(0, (int) options.valueOf("start"));
        int id2 = Math.max(0, (int) options.valueOf("stop"));
        int step = (int) options.valueOf("step");
        int count = Math.max(1, (int) options.valueOf("count"));

        int start, end;

        if (step == 0) {
            step = -1;
        }
        if (step < 0) {
            start = Math.max(id1, id2);
            end = Math.min(id1, id2);
        } else {
            start = Math.min(id1, id2);
            end = Math.max(id1, id2);
        }

        int id = (start == 0 && step < 0 ? api.getLatestStatsId() : start);
        int dumped = 0;
        int progress = 0;
        while (((step < 0 && id >= end) || (step > 0 && id <= end)) && dumped < count) {
            Stats stats = api.getStatsById(id);
            if (stats != null) {
                if (presenter.accept(stats, false)) {
                    collectedIds.add((long) id);
                    progress = (int) (100 * ((double) ++dumped / count));
                    log.info(progress + "% complete (" + dumped + "/" + count + ")");
                }
            }
            id = id + step;
        }
    }

    @Override
    protected void done() {
        api.cacheSave();
    }
}
