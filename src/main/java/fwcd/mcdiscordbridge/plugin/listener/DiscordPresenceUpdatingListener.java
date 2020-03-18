package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fwcd.mcdiscordbridge.plugin.DiscordBridgeLogger;
import fwcd.mcdiscordbridge.utils.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

/**
 * A listener for joins/quits that updates the activity.
 */
public class DiscordPresenceUpdatingListener implements Listener {
    private final JDA jda;
    
    public DiscordPresenceUpdatingListener(JDA jda) {
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateActivity();
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updateActivity();
    }
    
    private void updateActivity() {
        DiscordBridgeLogger.get().info("Updating Discord activity");
        int playerCount = Bukkit.getOnlinePlayers().size();
        jda.getPresence().setActivity(Activity.playing(playerCount + " " + StringUtils.pluralize("player", playerCount) + " online"));
    }
}
