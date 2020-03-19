package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.JDA;

public class DiscordChannelDeathMessageForwarder implements Listener {
    private final JDA jda;
    private final TextChannelRegistry subscribedChannels;
    
    public DiscordChannelDeathMessageForwarder(JDA jda, TextChannelRegistry subscribedChannels) {
        this.jda = jda;
        this.subscribedChannels = subscribedChannels;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        subscribedChannels.broadcastMessage(event.getDeathMessage(), jda);
    }
}
