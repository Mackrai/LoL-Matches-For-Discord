package api.lol;

import org.json.JSONObject;

public class Match {
    public MatchInfo matchInfo;

    public Match(JSONObject fromJson) {
        matchInfo = new MatchInfo(fromJson.getJSONObject("info"));
    }
}


