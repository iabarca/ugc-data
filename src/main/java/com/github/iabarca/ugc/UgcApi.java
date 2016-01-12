
package com.github.iabarca.ugc;

import com.github.iabarca.util.Connector;
import com.github.iabarca.util.Connector.ConnectorException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UgcApi {

    public static final String ROSTER = "http://www.ugcleague.com/team_roster_json.cfm?clan_id={id}";
    public static final String TEAM = "http://www.ugcleague.com/team_page_json.cfm?clan_id={id}";
    public static final String RESULTS = "http://www.ugcleague.com/results_tf2h_min_json.cfm?varweek={week}&varseason={season}";

    private Gson gson;
    private Connector conn;

    public UgcApi(Connector conn, Gson gson) {
        this.conn = conn;
        this.gson = gson;
    }

    private UgcRoster getRoster(int id) throws ConnectorException {
        String url = ROSTER.replace("{id}", id + "");
        String json = conn.getJson(url);
        if (json != null) {
            JsonUgcResponse parse = gson.fromJson(json, JsonUgcResponse.class);
            return new UgcRoster(parse);
        }
        return null;
    }

    private String cleanTeamJsonString(String s) {
        if (s == null) {
            return null;
        }
        return s.replace(") ", "").replace("onLoad( ", "");
    }

    public UgcTeam getTeam(int id) throws JsonSyntaxException, ConnectorException {
        UgcRoster roster = getRoster(id);
        if (roster != null) {
            String url = TEAM.replace("{id}", id + "");
            String json = cleanTeamJsonString(conn.getJson(url));
            if (json != null) {
                JsonUgcResponse parse = gson.fromJson(json, JsonUgcResponse.class);
                UgcTeam team = new UgcTeam(parse);
                team.setRoster(roster);
                return team;
            }
        }
        return null;
    }

    public UgcWeekResults getWeekResults(int season, int week) throws JsonSyntaxException,
            ConnectorException {
        String url = RESULTS.replace("{season}", season + "").replace("{week}", week + "");
        String json = conn.getJson(url);
        if (json != null) {
            return UgcWeekResults.fromJson(gson.fromJson(json, JsonUgcResponse.class));
        }
        return null;
    }

}
