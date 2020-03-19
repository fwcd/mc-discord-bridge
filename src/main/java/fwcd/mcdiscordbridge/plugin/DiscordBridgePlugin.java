package fwcd.mcdiscordbridge.plugin;

import java.util.Collections;
import java.util.List;

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
            manager.registerEvents(new DiscordChannelJoinLeaveMessageForwarder(jda, subscribedChannels), this);
            
            boolean webhookEnabled = getConfig().getBoolean(DiscordBridgeConfigKey.WEBHOOK_ENABLED);
            WebhookClient webhookClient = null;
            if (webhookEnabled) {
                String webhookUrl = getConfig().getString(DiscordBridgeConfigKey.WEBHOOK_URL);
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
            getLogger().warning("Could not start Discord bridge: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
