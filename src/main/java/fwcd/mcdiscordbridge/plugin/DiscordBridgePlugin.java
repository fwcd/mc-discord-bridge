package fwcd.mcdiscordbridge.plugin;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChatForwardingListener;
import fwcd.mcdiscordbridge.plugin.listener.DiscordPresenceUpdatingListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBridgePlugin extends JavaPlugin {
    private static final String BOT_TOKEN_CONFIG_KEY = "botToken";

    @Override
    public void onEnable() {
        getLogger().info("Starting Discord bridge...");
        
        getConfig().addDefault(BOT_TOKEN_CONFIG_KEY, "");
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        try {
            TextChannelRegistry subscribedChannels = new TextChannelRegistry();
            DiscordBridgeBot bot = new DiscordBridgeBot("+", subscribedChannels);
            JDA jda = JDABuilder.createDefault(getConfig().getString(BOT_TOKEN_CONFIG_KEY))
                .addEventListeners(bot)
                .build();
            
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new DiscordChatForwardingListener(jda, subscribedChannels), this);
            manager.registerEvents(new DiscordPresenceUpdatingListener(jda), this);
        } catch (Exception e) {
            getLogger().warning("Could not start Discord bridge: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
