package dev.fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev.fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.JDA;

/**
 * A listener that forwards Minecraft chat messages to
 * subscribed Discord text channels.
 */
public class DiscordChannelChatForwarder implements Listener {
    private final JDA jda;
    private final TextChannelRegistry subscribedChannels;
    
    public DiscordChannelChatForwarder(JDA jda, TextChannelRegistry subscribedChannels) {
        this.jda = jda;
        this.subscribedChannels = subscribedChannels;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        subscribedChannels.broadcastMessage("[" + event.getPlayer().getName() + "] " + event.getMessage(), jda);
    }
}
