package dev.fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class DiscordWebhookChatForwarder implements Listener {
    private final WebhookClient client;
    
    public DiscordWebhookChatForwarder(WebhookClient client) {
        this.client = client;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        client.send(new WebhookMessageBuilder()
            .setUsername(event.getPlayer().getName())
            .setAvatarUrl("https://crafatar.com/avatars/" + event.getPlayer().getUniqueId().toString() + "?size=100")
            .setContent(event.getMessage())
            .build());
    }
}
