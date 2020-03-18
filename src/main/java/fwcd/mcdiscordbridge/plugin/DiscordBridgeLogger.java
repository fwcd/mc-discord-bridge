package fwcd.mcdiscordbridge.plugin;

import java.util.logging.Logger;

/**
 * A global logger used throughout the plugin.
 */
public final class DiscordBridgeLogger {
    private static Logger instance = null;
    
    public static void set(Logger instance) {
        DiscordBridgeLogger.instance = instance;
    }
    
    public static Logger get() {
        if (instance == null) {
            throw new IllegalStateException("Logger not initialized yet.");
        }
        return instance;
    }
}
