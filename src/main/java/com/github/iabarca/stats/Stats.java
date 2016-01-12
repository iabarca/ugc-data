
package com.github.iabarca.stats;

import java.util.Map;

public interface Stats {

    boolean isLive();

    int getId();

    String getMap();

    Map<String, IPlayerStats> getPlayers();

}
