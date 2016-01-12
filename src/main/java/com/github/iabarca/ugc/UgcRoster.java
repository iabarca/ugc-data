
package com.github.iabarca.ugc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UgcRoster {

    private static final Logger log = Logger.getLogger("stats");

    private List<UgcMember> members = new ArrayList<>();

    public UgcRoster(JsonUgcResponse json) {
        if (json == null) {
            // TODO
            return;
        }
        if (json.getData() == null) {
            // TODO
            return;
        }
        for (List<Object> data : json.getData()) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) == null) {
                    data.set(i, "");
                }
            }
            UgcMember member = new UgcMember(
                    data.get(0).toString(),
                    data.get(1).toString(),
                    data.get(2).toString(),
                    data.get(3).toString(),
                    data.get(4).toString(),
                    ((Double) data.get(5)).longValue());
            log.info("Adding Player: " + member.toString());
            members.add(member);
        }
    }

    public List<UgcMember> getMembers() {
        return members;
    }

}
