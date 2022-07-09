package api.lol;

import org.json.JSONObject;

public class MatchInfo {
    public String gameMode;
    public Long gameCreation;
    public Long gameDuration;

    public MatchInfo(JSONObject fromJson) {
        gameMode = fromJson.getString("gameMode");
        gameCreation = fromJson.getLong("gameCreation");
        gameDuration = fromJson.getLong("gameDuration");
    }
}
