package fwcd.mcdiscordbridge.plugin;

import java.util.Collections;
import java.util.List;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import club.minnced.discord.webhook.WebhookClient;
import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChannelChatForwarder;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChannelDeathMessageForwarder;
import fwcd.mcdiscordbridge.plugin.listener.DiscordPresenceUpdater;
import fwcd.mcdiscordbridge.plugin.listener.DiscordWebhookChatForwarder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBridgePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DiscordBridgeLogger.set(getLogger());

        DiscordBridgeLogger.get().info("Starting Discord bridge...");
        
        getConfig().addDefault(DiscordBridgeConfigKey.BOT_TOKEN, "");
        getConfig().addDefault(DiscordBridgeConfigKey.BOT_COMMAND_PREFIX, "+");
        getConfig().addDefault(DiscordBridgeConfigKey.WEBHOOK_URL, "");
        getConfig().addDefault(DiscordBridgeConfigKey.WEBHOOK_ENABLED, false);
        getConfig().addDefault(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS, Collections.emptyList());
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        try {
            List<String> initialSubscribedChannels = getConfig().getStringList(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS);
            TextChannelRegistry subscribedChannels = new TextChannelRegistry(initialSubscribedChannels, chIds -> {
                getConfig().set(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS, chIds);
                saveConfig();
            });

            DiscordBridgeBot bot = new DiscordBridgeBot(getConfig().getString(DiscordBridgeConfigKey.BOT_COMMAND_PREFIX), subscribedChannels);
            JDA jda = JDABuilder.createDefault(getConfig().getString(DiscordBridgeConfigKey.BOT_TOKEN))
                .addEventListeners(bot)
                .build();
            
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new DiscordPresenceUpdater(jda), this);
            manager.registerEvents(new DiscordChannelDeathMessageForwarder(jda, subscribedChannels), this);
            
            if (getConfig().getBoolean(DiscordBridgeConfigKey.WEBHOOK_ENABLED)) {
                String webhookUrl = getConfig().getString(DiscordBridgeConfigKey.WEBHOOK_URL);
                manager.registerEvents(new DiscordWebhookChatForwarder(WebhookClient.withUrl(webhookUrl)), this);
            } else {
                manager.registerEvents(new DiscordChannelChatForwarder(jda, subscribedChannels), this);
            }
        } catch (Exception e) {
            getLogger().warning("Could not start Discord bridge: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
