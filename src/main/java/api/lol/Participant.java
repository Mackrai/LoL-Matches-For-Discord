package api.lol;

import org.json.JSONObject;

public class Participant {
    String championName;

    public Participant(JSONObject fromJson) {
        championName = fromJson.getString("championName");
    }
}
