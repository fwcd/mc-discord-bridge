package fwcd.mcdiscordbridge.bot.command;

import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.stream.Collectors;

public class ListCommand implements BotCommand {
    @Override
    public void invoke(String args, Message message) {
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        String output = "";

        if (onlinePlayers.isEmpty()) {
            output = "No players online.";
        } else {
            output = onlinePlayers.size() + " players online: "
              + onlinePlayers.stream().map(p -> p.getPlayerListName()).collect(Collectors.joining(", "));
        }

        message.getChannel().sendMessage(output).queue();
    }
}
