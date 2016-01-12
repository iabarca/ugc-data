
package com.github.iabarca.ugc;

import com.github.iabarca.util.Utils;

import org.joda.time.LocalDateTime;

public class UgcMember {

    private String name;
    private String type;
    private String dateAdded;
    private String dateUpdated;
    private String steamId32;
    private long steamId64;

    public UgcMember(String name, String type, String dateAdded, String dateUpdated, String steamId32,
            long steamId64) {
        this.name = name;
        this.type = type;
        this.dateAdded = dateAdded;
        this.dateUpdated = dateUpdated;
        this.steamId32 = "STEAM_" + steamId32;
        this.steamId64 = steamId64;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public LocalDateTime getAddedDateTime() {
        return Utils.parseDate(dateAdded, "MMM, dd yyyy HH:mm:ss");
    }

    public LocalDateTime getUpdatedDateTime() {
        return Utils.parseDate(dateUpdated, "MMM, dd yyyy HH:mm:ss");
    }

    public String getSteamId32() {
        return steamId32;
    }

    public long getSteamId64() {
        return steamId64;
    }

    @Override
    public String toString() {
        return name + " [" + steamId32.replace("STEAM_", "") + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((steamId32 == null) ? 0 : steamId32.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UgcMember other = (UgcMember) obj;
        if (steamId32 == null) {
            if (other.steamId32 != null)
                return false;
        } else if (!steamId32.equals(other.steamId32))
            return false;
        return true;
    }

}
