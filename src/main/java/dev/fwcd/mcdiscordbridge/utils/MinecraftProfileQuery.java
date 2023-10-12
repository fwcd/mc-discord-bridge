package dev.fwcd.mcdiscordbridge.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeLogger;

import org.apache.commons.io.IOUtils;

public class MinecraftProfileQuery {
    private static final Gson GSON = new Gson();
    private final String name;
    
    public MinecraftProfileQuery(String name) {
        this.name = name;
    }
    
    public static record Response(String id, String name) {}
    
    public UUID getUUIDSync() {
        try {
            DiscordBridgeLogger.get().fine(() -> "Querying UUID for user `" + name + "`");

            String json = IOUtils.toString(new URL("https", "api.mojang.com", "/users/profiles/minecraft/" + name), StandardCharsets.UTF_8);
            Response response = GSON.fromJson(json, Response.class);
            DiscordBridgeLogger.get().fine(() -> "Got API response " + response);

            String uuidString = response.id.replaceAll("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
            UUID uuid = UUID.fromString(uuidString);
            DiscordBridgeLogger.get().fine(() -> "Got UUID " + uuid + " for `" + name + "`");
            
            return uuid;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public CompletableFuture<UUID> getUUIDAsync() {
        return CompletableFuture.supplyAsync(this::getUUIDSync);
    }
}
