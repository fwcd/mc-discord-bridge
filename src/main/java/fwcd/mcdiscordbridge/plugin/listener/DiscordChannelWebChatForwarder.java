package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.JDA;

public class DiscordChannelWebChatForwarder implements Listener {
    private final JDA jda;
    private final TextChannelRegistry subscribedChannels;
    
    public DiscordChannelWebChatForwarder(JDA jda, TextChannelRegistry subscribedChannels) {
        this.jda = jda;
        this.subscribedChannels = subscribedChannels;
    }
    
    @EventHandler
    public void onWebChat(DynmapWebChatEvent event) {
        subscribedChannels.broadcastMessage("[" + event.getName() + " via web] " + event.getMessage(), jda);
    }
}
