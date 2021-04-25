package fwcd.mcdiscordbridge.plugin;

import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import club.minnced.discord.webhook.WebhookClient;
import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChannelChatForwarder;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChannelDeathMessageForwarder;
import fwcd.mcdiscordbridge.plugin.listener.DiscordChannelJoinLeaveMessageForwarder;
import fwcd.mcdiscordbridge.plugin.listener.DiscordPresenceUpdater;
import fwcd.mcdiscordbridge.plugin.listener.DiscordWebhookChatForwarder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBridgePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DiscordBridgeLogger.set(getLogger());
        DiscordBridgeLogger.get().info("Starting Discord bridge...");
        
        FileConfiguration config = getConfig();
        config.addDefault(DiscordBridgeConfigKey.BOT_TOKEN, "");
        config.addDefault(DiscordBridgeConfigKey.BOT_COMMAND_PREFIX, "+");
        config.addDefault(DiscordBridgeConfigKey.WEBHOOK_URL, "");
        config.addDefault(DiscordBridgeConfigKey.WEBHOOK_ENABLED, false);
        config.addDefault(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS, Collections.emptyList());
        config.options().copyDefaults(true);
        saveConfig();
        
        try {
            List<String> initialSubscribedChannels = config.getStringList(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS);
            TextChannelRegistry subscribedChannels = new TextChannelRegistry(initialSubscribedChannels, chIds -> {
                config.set(DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS, chIds);
                saveConfig();
            });

            DiscordBridgeBot bot = new DiscordBridgeBot(config.getString(DiscordBridgeConfigKey.BOT_COMMAND_PREFIX), subscribedChannels);
            JDA jda = JDABuilder.createDefault(config.getString(DiscordBridgeConfigKey.BOT_TOKEN))
                .addEventListeners(bot)
                .build();
            
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new DiscordPresenceUpdater(jda), this);
            manager.registerEvents(new DiscordChannelDeathMessageForwarder(jda, subscribedChannels), this);
            manager.registerEvents(new DiscordChannelJoinLeaveMessageForwarder(jda, subscribedChannels), this);
            
            boolean webhookEnabled = config.getBoolean(DiscordBridgeConfigKey.WEBHOOK_ENABLED);
            WebhookClient webhookClient = null;
            if (webhookEnabled) {
                String webhookUrl = config.getString(DiscordBridgeConfigKey.WEBHOOK_URL);
                webhookClient = WebhookClient.withUrl(webhookUrl);
                manager.registerEvents(new DiscordWebhookChatForwarder(webhookClient), this);
            } else {
                manager.registerEvents(new DiscordChannelChatForwarder(jda, subscribedChannels), this);
            }
            
            String dynmapPluginName = "dynmap";
            boolean dynmapAvailable = manager.getPlugin(dynmapPluginName) != null && manager.isPluginEnabled(dynmapPluginName);
            if (dynmapAvailable) {
                DiscordBridgeLogger.get().info("Enabling Dynmap web chat integration...");

                if (webhookEnabled) {
                    manager.registerEvents((Listener) Class.forName("fwcd.mcdiscordbridge.plugin.listener.DiscordWebhookWebChatForwarder")
                        .getConstructor(WebhookClient.class)
                        .newInstance(webhookClient), this);
                } else {
                    manager.registerEvents((Listener) Class.forName("fwcd.mcdiscordbridge.plugin.listener.DiscordChannelWebChatForwarder")
                        .getConstructor(TextChannelRegistry.class, JDA.class)
                        .newInstance(subscribedChannels, jda), this);
                }
            } else {
                DiscordBridgeLogger.get().info("Skipping Dynmap integration");
            }
        } catch (Exception e) {
            DiscordBridgeLogger.get().warning("Could not start Discord bridge: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
