package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.JDA;

public class DiscordChannelAdvancementForwarder implements Listener {
    private final JDA jda;
    private final TextChannelRegistry subscribedChannels;

    public DiscordChannelAdvancementForwarder(JDA jda, TextChannelRegistry subscribedChannels) {
        this.jda = jda;
        this.subscribedChannels = subscribedChannels;
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        // TODO: Figure out a way to get the display name instead
        String advancement = event.getAdvancement().getKey().getKey();
        subscribedChannels.broadcastMessage(event.getPlayer().getName() + " has made the advancement [" + advancement + "]", jda);
    }
}
