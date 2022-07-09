import api.lol.Match;
import api.lol.Summoner;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

final public class LolService {
    private final HttpClient httpClient;
    private final String riotToken;

    private String lastMatchId = "";

    public LolService(HttpClient httpClient, String riotToken) {
        this.httpClient = httpClient;
        this.riotToken = riotToken;
    }

    public void sendLastGameInfo(String summonerName, MessageChannel channel, Boolean calledManually) {
        try {
            //Get summoner by name
            Summoner summoner = getSummoner(summonerName);

            //Get recent matches ids by summoner puuid
            ArrayList<String> matchesIds = getMatchesIds(summoner.puuid);

            String maybeNewMatch = matchesIds.get(0);
            //If there were new matches played recently
            if (!maybeNewMatch.equals(lastMatchId) || calledManually) {
                this.lastMatchId = maybeNewMatch;

                //Get last match by match id
                Match lastMatch = getMatchById(matchesIds.get(0));

                //Build outgoing message
                String outMessage =
                    lastMatch.matchInfo.gameMode + "\n" +
                    convertTime(lastMatch.matchInfo.gameCreation) + "\n";

                //Send message to channel
                channel.sendMessage(outMessage).queue();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Summoner getSummoner(String summonerName) throws IOException, InterruptedException {
        HttpResponse<String> summonerResponse =
            httpClient.send(
                requestWithRiotToken(getSummonerByNameUri(summonerName)),
                HttpResponse.BodyHandlers.ofString()
            );
        return new Summoner(new JSONObject(summonerResponse.body()));
    }

    private ArrayList<String> getMatchesIds(String puuid) throws IOException, InterruptedException {
        HttpResponse<String> matchesResponse =
            httpClient.send(
                requestWithRiotToken(getMatchesIdsByPuuidUri(puuid, 1)),
                HttpResponse.BodyHandlers.ofString()
            );
        ArrayList<String> matchesIds = new ArrayList<String>();
        JSONArray matches = new JSONArray(matchesResponse.body());
        for (int i=0; i<matches.length(); i++) {
            matchesIds.add(matches.getString(i));
        }
        return matchesIds;
    }

    private Match getMatchById(String matchId) throws IOException, InterruptedException {
        HttpResponse<String> matchResponse =
            httpClient.send(
                requestWithRiotToken(getMatchUri(matchId)),
                HttpResponse.BodyHandlers.ofString()
            );
        return new Match(new JSONObject(matchResponse.body()));
    }

    private HttpRequest requestWithRiotToken(String uri) {
        return
            HttpRequest
                .newBuilder()
                .uri(URI.create(uri))
                .setHeader("X-Riot-Token", riotToken).build();
    }

    private String getSummonerByNameUri(String summonerName) {
        return "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName.replace(" ", "%20");
    }

    private String getMatchesIdsByPuuidUri(String puuid, Integer count) {
        return "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=" + count;
    }

    private String getMatchUri(String matchId) {
        return "https://europe.api.riotgames.com/lol/match/v5/matches/" + matchId;
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return format.format(date);
    }
}
