package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * A listener that forwards Minecraft chat messages to
 * subscribed Discord text channels.
 */
public class DiscordChatForwardingListener implements Listener {
    private final JDA jda;
    private final TextChannelRegistry subscribedChannels;
    
    public DiscordChatForwardingListener(JDA jda, TextChannelRegistry subscribedChannels) {
        this.jda = jda;
        this.subscribedChannels = subscribedChannels;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        for (String channelId : subscribedChannels) {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage("[" + event.getPlayer().getName() + "] " + event.getMessage()).queue();
            }
        }
    }
}
