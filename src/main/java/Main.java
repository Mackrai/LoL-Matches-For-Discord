import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    final private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws LoginException {
        Properties config = new Properties();
        String configFileName = "app.config";
        try (FileInputStream stream = new FileInputStream(configFileName)) {
            config.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String riotToken    = config.getProperty("riot-token");
        String discordToken = config.getProperty("discord-token");

        LolService lolService = new LolService(HttpClient.newHttpClient(), riotToken);

        JDABuilder.createLight(discordToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
            .addEventListeners(new Bot(config, lolService, scheduler))
            .setActivity(Activity.playing("Looking through recent matches"))
            .build();
    }
}
