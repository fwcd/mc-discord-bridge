package fwcd.mcdiscordbridge.plugin;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChatForwardingListener;
import fwcd.mcdiscordbridge.plugin.listener.DiscordPresenceUpdatingListener;
import fwcd.mcdiscordbridge.plugin.listener.DiscordWebhookForwardingListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBridgePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DiscordBridgeLogger.set(getLogger());

        DiscordBridgeLogger.get().info("Starting Discord bridge...");
        
        getConfig().addDefault(DiscordBridgeConfigKey.BOT_TOKEN, "");
        getConfig().addDefault(DiscordBridgeConfigKey.WEBHOOK_URL, "");
        getConfig().addDefault(DiscordBridgeConfigKey.WEBHOOK_ENABLED, false);
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        try {
            TextChannelRegistry subscribedChannels = new TextChannelRegistry();
            DiscordBridgeBot bot = new DiscordBridgeBot("+", subscribedChannels);
            JDA jda = JDABuilder.createDefault(getConfig().getString(DiscordBridgeConfigKey.BOT_TOKEN))
                .addEventListeners(bot)
                .build();
            
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new DiscordPresenceUpdatingListener(jda), this);
            
            if (getConfig().getBoolean(DiscordBridgeConfigKey.WEBHOOK_ENABLED)) {
                String webhookUrl = getConfig().getString(DiscordBridgeConfigKey.WEBHOOK_URL);
                manager.registerEvents(new DiscordWebhookForwardingListener(WebhookClient.withUrl(webhookUrl)), this);
            } else {
                manager.registerEvents(new DiscordChatForwardingListener(jda, subscribedChannels), this);
            }
        } catch (Exception e) {
            getLogger().warning("Could not start Discord bridge: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
