package dev.fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeLogger;
import dev.fwcd.mcdiscordbridge.utils.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

/**
 * A listener for joins/quits that updates the activity.
 */
public class DiscordPresenceUpdater implements Listener {
    private final JDA jda;
    
    public DiscordPresenceUpdater(JDA jda) {
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        int playerCount = Bukkit.getOnlinePlayers().size();
        updateActivity(playerCount);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int playerCount = (int) Bukkit.getOnlinePlayers().stream().filter(it -> !it.equals(event.getPlayer())).count();
        updateActivity(playerCount);
    }
    
    private void updateActivity(int playerCount) {
        DiscordBridgeLogger.get().info("Updating Discord activity to player count " + playerCount);
        jda.getPresence().setActivity(Activity.playing(playerCount + " " + StringUtils.pluralize("player", playerCount) + " online"));
    }
}
