package api.lol;

import org.json.JSONObject;

public class Summoner {
    public String accountId;
    public String id;
    public String puuid;
    public String name;

    public Summoner(JSONObject fromJson) {
        accountId = fromJson.getString("accountId");
        id = fromJson.getString("id");
        puuid = fromJson.getString("puuid");
        name = fromJson.getString("name");
    }
}
