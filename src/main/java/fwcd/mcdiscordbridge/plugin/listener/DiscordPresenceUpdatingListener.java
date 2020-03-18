package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fwcd.mcdiscordbridge.utils.StringUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

public class DiscordPresenceUpdatingListener implements Listener {
    private final Presence presence;
    
    public DiscordPresenceUpdatingListener(Presence presence) {
        this.presence = presence;
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
        int playerCount = Bukkit.getOnlinePlayers().size();
        presence.setActivity(Activity.playing(playerCount + " " + StringUtils.pluralize("player", playerCount) + " online"));
    }
}
