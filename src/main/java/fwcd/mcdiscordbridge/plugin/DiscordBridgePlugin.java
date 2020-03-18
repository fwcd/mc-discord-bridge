package fwcd.mcdiscordbridge.plugin;

import javax.security.auth.login.LoginException;

import org.bukkit.plugin.java.JavaPlugin;

import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
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
            DiscordBridgeBot bot = new DiscordBridgeBot("+");
            JDABuilder.createDefault(getConfig().getString(BOT_TOKEN_CONFIG_KEY))
                .addEventListeners(bot)
                .build();
        } catch (LoginException e) {
            getLogger().warning("Could not start Discord bot: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(new DiscordForwardingChatListener(), this);
    }
}
