package fwcd.mcdiscordbridge.plugin;

import javax.security.auth.login.LoginException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fwcd.mcdiscordbridge.bot.DiscordBridgeBot;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordBridgePlugin extends JavaPlugin {
    private final FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info("Starting Discord bridge...");
        
        try {
            DiscordBridgeBot bot = new DiscordBridgeBot("+");
            JDABuilder.createDefault(config.getString("botToken"))
                .addEventListeners(bot)
                .build();
        } catch (LoginException e) {
            getLogger().warning("Could not start Discord bot: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(new DiscordForwardingChatListener(), this);
    }
}
