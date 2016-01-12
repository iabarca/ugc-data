
package com.github.iabarca.stats;

import com.github.iabarca.util.Connector.ConnectorException;
import com.google.common.base.Predicate;

public interface StatsApi {

    int getLatestStatsId() throws ConnectorException;

    Stats getStatsById(int id);

    Iterable<StatsMatch> getAllMatchesBySteamId(String id);

    void cacheSave();

    void applyOnStats(Predicate<Stats> predicate);

}
