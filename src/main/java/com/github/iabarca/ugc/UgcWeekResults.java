
package com.github.iabarca.ugc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UgcWeekResults {

    private static final Logger log = Logger.getLogger("stats");

    public static UgcWeekResults fromJson(JsonUgcResponse json) {
        UgcWeekResults results = new UgcWeekResults();
        for (List<Object> data : json.getData()) {
            UgcMatch match = new UgcMatch(
                    ((Double) data.get(0)).intValue(),
                    ((Double) data.get(1)).intValue(),
                    (String) data.get(2),
                    (String) data.get(3),
                    ((Double) data.get(4)).intValue(),
                    data.get(5).toString(),
                    ((Double) data.get(6)).intValue(),
                    ((Double) data.get(7)).intValue(),
                    ((Double) data.get(8)).intValue(),
                    ((Double) data.get(9)).intValue(),
                    data.get(10).toString(),
                    ((Double) data.get(11)).intValue(),
                    ((Double) data.get(12)).intValue(),
                    ((Double) data.get(13)).intValue(),
                    ((Double) data.get(14)).intValue(),
                    data.get(15).toString());
            log.info("Adding match: " + match.toString());
            results.getMatches().add(match);
        }
        return results;
    }

    private List<UgcMatch> matches = new ArrayList<>();

    public List<UgcMatch> getMatches() {
        return matches;
    }

}
