package dev.fwcd.mcdiscordbridge.plugin;

import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.BOT_COMMAND_PREFIX;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.BOT_PRESENCE_ENABLED;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.BOT_TOKEN;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.FORWARD_ADVANCEMENT;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.FORWARD_CHAT;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.FORWARD_DEATH;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.FORWARD_JOIN_LEAVE;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.FORWARD_WEB_CHAT;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.SUBSCRIBED_CHANNELS;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.WEBHOOK_ENABLED;
import static dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeConfigKey.WEBHOOK_URL;

import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import club.minnced.discord.webhook.WebhookClient;
import dev.fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import dev.fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordChannelAdvancementForwarder;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordChannelChatForwarder;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordChannelDeathMessageForwarder;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordChannelJoinLeaveMessageForwarder;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordPresenceUpdater;
import dev.fwcd.mcdiscordbridge.plugin.listener.DiscordWebhookChatForwarder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBridgePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DiscordBridgeLogger.set(getLogger());
        DiscordBridgeLogger.get().info("Starting Discord bridge...");
        
        FileConfiguration config = getConfig();
        config.addDefault(BOT_TOKEN, "");
        config.addDefault(BOT_COMMAND_PREFIX, "+");
        config.addDefault(BOT_PRESENCE_ENABLED, true);
        config.addDefault(WEBHOOK_URL, "");
        config.addDefault(WEBHOOK_ENABLED, false);
        config.addDefault(FORWARD_CHAT, true);
        config.addDefault(FORWARD_JOIN_LEAVE, true);
        config.addDefault(FORWARD_DEATH, true);
        config.addDefault(FORWARD_ADVANCEMENT, false); // disabled for now
        config.addDefault(FORWARD_WEB_CHAT, true);
        config.addDefault(SUBSCRIBED_CHANNELS, Collections.emptyList());
        config.options().copyDefaults(true);
        saveConfig();
        
        try {
            List<String> initialSubscribedChannels = config.getStringList(SUBSCRIBED_CHANNELS);
            TextChannelRegistry subscribedChannels = new TextChannelRegistry(initialSubscribedChannels, chIds -> {
                config.set(SUBSCRIBED_CHANNELS, chIds);
                saveConfig();
            });

            DiscordBridgeBot bot = new DiscordBridgeBot(config.getString(BOT_COMMAND_PREFIX), subscribedChannels);
            JDA jda = JDABuilder.createDefault(config.getString(BOT_TOKEN))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(bot)
                .build();
            
            PluginManager manager = getServer().getPluginManager();

            if (config.getBoolean(BOT_PRESENCE_ENABLED)) {
                manager.registerEvents(new DiscordPresenceUpdater(jda), this);
            }
            if (config.getBoolean(FORWARD_DEATH)) {
                manager.registerEvents(new DiscordChannelDeathMessageForwarder(jda, subscribedChannels), this);
            }
            if (config.getBoolean(FORWARD_JOIN_LEAVE)) {
                manager.registerEvents(new DiscordChannelJoinLeaveMessageForwarder(jda, subscribedChannels), this);
            }
            if (config.getBoolean(FORWARD_ADVANCEMENT)) {
                manager.registerEvents(new DiscordChannelAdvancementForwarder(jda, subscribedChannels), this);
            }
            
            WebhookClient webhookClient = null;

            if (config.getBoolean(WEBHOOK_ENABLED)) {
                String webhookUrl = config.getString(WEBHOOK_URL);
                webhookClient = WebhookClient.withUrl(webhookUrl);
                manager.registerEvents(new DiscordWebhookChatForwarder(webhookClient), this);
            } else {
                manager.registerEvents(new DiscordChannelChatForwarder(jda, subscribedChannels), this);
            }
            
            String dynmapPluginName = "dynmap";
            boolean dynmapAvailable = manager.getPlugin(dynmapPluginName) != null && manager.isPluginEnabled(dynmapPluginName);

            if (dynmapAvailable && config.getBoolean(FORWARD_WEB_CHAT)) {
                DiscordBridgeLogger.get().info("Enabling Dynmap web chat integration...");

                if (config.getBoolean(WEBHOOK_ENABLED)) {
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
