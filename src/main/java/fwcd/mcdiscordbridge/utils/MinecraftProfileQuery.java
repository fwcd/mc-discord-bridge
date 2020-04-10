package fwcd.mcdiscordbridge.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

public class MinecraftProfileQuery {
    private static final Gson GSON = new Gson();
    private final String name;
    
    public MinecraftProfileQuery(String name) {
        this.name = name;
    }
    
    public static class Response {
        private String id;
        private String name;
    }
    
    public UUID getUUIDSync() {
        try {
            String json = IOUtils.toString(new URL("https", "api.mojang.com", "/users/profiles/minecraft/" + name), StandardCharsets.UTF_8);
            Response response = GSON.fromJson(json, Response.class);
            return UUID.fromString(response.id);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public CompletableFuture<UUID> getUUIDAsync() {
        return CompletableFuture.supplyAsync(this::getUUIDSync);
    }
}
