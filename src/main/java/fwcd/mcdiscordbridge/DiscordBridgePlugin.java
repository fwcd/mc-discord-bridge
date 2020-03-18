package fwcd.mcdiscordbridge;

import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBridgePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Starting Discord bridge...");
        getServer().getPluginManager().registerEvents(new DiscordForwardingChatListener(), this);
    }
}
