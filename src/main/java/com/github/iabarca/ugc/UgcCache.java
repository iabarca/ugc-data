
package com.github.iabarca.ugc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UgcCache {

    private static final Logger log = Logger.getLogger("stats");

    private Map<Long, UgcTeam> teams = new LinkedHashMap<>();
    private Map<Integer, Map<Integer, UgcWeekResults>> matches = new LinkedHashMap<>();

    private transient File saveFile;
    private transient Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public Map<Long, UgcTeam> getTeams() {
        return teams;
    }

    public Map<Integer, Map<Integer, UgcWeekResults>> getMatches() {
        return matches;
    }

    public void save() {
        try {
            com.google.common.io.Files.write(gson.toJson(this), saveFile, Charset.defaultCharset());
            log.info("[UGC League] Saving match data and " + teams.size() + " teams");
        } catch (IOException e) {
            log.log(Level.INFO, "", e);
        }
    }

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

}
