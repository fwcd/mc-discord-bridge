package dev.fwcd.mcdiscordbridge.bot.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import dev.fwcd.mcdiscordbridge.plugin.DiscordBridgeLogger;
import dev.fwcd.mcdiscordbridge.utils.MinecraftProfileQuery;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class WhitelistCommand implements BotCommand {
    private static final Pattern SUBCOMMAND_PATTERN = Pattern.compile("(\\w+)(?:\\s+(.+))?");
    private final JavaPlugin plugin;
    private final Map<String, BiConsumer<String, MessageChannel>> subcommands = new HashMap<>();
    
    {
        subcommands.put("add", (args, channel) -> {
            setPlayerWhitelisted(args, true, channel);
        });
        subcommands.put("remove", (args, channel) -> {
            setPlayerWhitelisted(args, false, channel);
        });
    }

    public WhitelistCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void invoke(String args, Message message) {
        Matcher matcher = SUBCOMMAND_PATTERN.matcher(args);
        if (matcher.find()) {
            String subcommand = matcher.group(1);
            String subArgs = matcher.group(2);

            if (subcommands.containsKey(subcommand)) {
                subcommands.get(subcommand).accept(subArgs, message.getChannel());
            } else {
                message.getChannel().sendMessage("Unrecognized subcommand `" + subcommand + "`, please use one of these: `" + subcommands.keySet() + "`").queue();
            }
        } else {
            message.getChannel().sendMessage("Please use one of these subcommands: `" + subcommands.keySet() + "`").queue();
        }
    }
    
    private void setPlayerWhitelisted(String name, boolean whitelisted, MessageChannel channel) {
        new MinecraftProfileQuery(name)
            .getUUIDAsync()
            .thenCompose(uuid -> {
                DiscordBridgeLogger.get().fine(() -> "Fetching OfflinePlayer for " + uuid);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                CompletableFuture<Boolean> future = new CompletableFuture<>();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        DiscordBridgeLogger.get().fine(() -> "Whitelisting player " + uuid);
                        boolean isNoop = player.isWhitelisted() == whitelisted;
                        if (!isNoop) {
                            player.setWhitelisted(whitelisted);
                        }
                        future.complete(isNoop);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                });

                return future;
            })
            .thenAccept(isNoop -> {
                String emoji = isNoop ? ":information_source:" : whitelisted ? ":scroll:" : ":wastebasket:";
                String word = (whitelisted ? "" : "un") + "whitelisted";
                String message = isNoop
                    ? "`" + name + "` is already " + word
                    : "Successfully " + word + " `" + name + "`";
                DiscordBridgeLogger.get().info(() -> message);
                channel.sendMessage(emoji + " " + message).queue();
            })
            .exceptionally(e -> {
                DiscordBridgeLogger.get().log(Level.WARNING, e, () -> "Could not whitelist `" + name + "`");
                channel.sendMessage("Could not whitelist '" + name + "': `" + e.getMessage() + "`").queue();
                return null;
            });
    }
}
