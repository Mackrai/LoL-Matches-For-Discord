import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends ListenerAdapter {
    private final LolService lolService;
    private final ScheduledExecutorService scheduler;
    private final Properties config;

    public Bot(Properties config, LolService lolService, ScheduledExecutorService scheduler) {
        this.config = config;
        this.lolService = lolService;
        this.scheduler = scheduler;

        // TODO get channel
        // runScheduler(channel);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        String msgRaw = event.getMessage().getContentRaw();
        String regex = "!update\\s*(([a-zA-Z0-9]*\\s*)*)";

        if (msgRaw.matches(regex)) {
            Matcher matcher = Pattern.compile(regex).matcher(msgRaw);
            matcher.matches();
            lolService.sendLastGameInfo(matcher.group(1), channel, true);
        }
    }

    private void runScheduler(MessageChannel channel) {
        Runnable sendLastGameInfoRunnable =
            () -> lolService.sendLastGameInfo(config.getProperty("default-summoner-name"), channel, false);

        scheduler.scheduleAtFixedRate(sendLastGameInfoRunnable, 0, 5, TimeUnit.MINUTES);
    }
}
