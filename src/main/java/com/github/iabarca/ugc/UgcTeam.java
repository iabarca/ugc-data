
package com.github.iabarca.ugc;

import java.util.List;

public class UgcTeam {

    private long id;
    private String tag;
    private String name;
    private String status;
    private String description;
    private String titles;
    private String steampage;
    private String irc;
    private String url;
    private String avatar;
    private String server1;
    private String server2;
    private String timezone;
    private int server1State;
    private int server2State;
    private String ladderShort;
    private String divisionName;

    private UgcRoster roster;

    public UgcTeam(JsonUgcResponse json) {
        for (List<Object> data : json.getData()) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) == null) {
                    data.set(i, "");
                }
            }
            this.id = ((Double) data.get(0)).longValue();
            this.tag = data.get(1).toString();
            this.name = data.get(2).toString();
            this.status = data.get(3).toString();
            this.description = data.get(4).toString();
            this.titles = data.get(5).toString();
            this.steampage = data.get(6).toString();
            this.irc = data.get(7).toString();
            this.url = data.get(8).toString();
            this.avatar = data.get(9).toString();
            this.server1 = data.get(10).toString();
            this.server2 = data.get(11).toString();
            this.timezone = data.get(12).toString();
            this.server1State = ((Double) data.get(13)).intValue();
            this.server2State = ((Double) data.get(14)).intValue();
            this.ladderShort = data.get(15).toString();
            this.divisionName = data.get(16).toString();
        }
    }

    public UgcRoster getRoster() {
        return roster;
    }

    public void setRoster(UgcRoster roster) {
        this.roster = roster;
    }

    public long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getTitles() {
        return titles;
    }

    public String getSteampage() {
        return steampage;
    }

    public String getIrc() {
        return irc;
    }

    public String getUrl() {
        return url;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getServer1() {
        return server1;
    }

    public String getServer2() {
        return server2;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getServer1State() {
        return server1State;
    }

    public int getServer2State() {
        return server2State;
    }

    public String getLadderShort() {
        return ladderShort;
    }

    public String getDivisionName() {
        return divisionName;
    }

}
