package fwcd.mcdiscordbridge.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class DiscordWebhookWebChatForwarder implements Listener {
    private final WebhookClient client;
    
    public DiscordWebhookWebChatForwarder(WebhookClient client) {
        this.client = client;
    }
    
    @EventHandler
    public void onWebChat(DynmapWebChatEvent event) {
        String name = event.getName();
        if (name.isEmpty()) {
            name = "Mysterious web user";
        }
        client.send(new WebhookMessageBuilder()
            .setUsername(name)
            .setContent(event.getMessage())
            .build());
    }
}
