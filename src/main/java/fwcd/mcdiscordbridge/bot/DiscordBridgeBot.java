package fwcd.mcdiscordbridge.bot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fwcd.mcdiscordbridge.bot.command.BotCommand;
import fwcd.mcdiscordbridge.bot.command.EchoCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordBridgeBot extends ListenerAdapter {
    private final Pattern commandPattern;
    private final Map<String, BotCommand> commands = new HashMap<>();
    
    public DiscordBridgeBot(String commandPrefix) {
        commandPattern = Pattern.compile(Pattern.quote(commandPrefix) + "(\\w+)\\s+(.+)");

        commands.put("echo", new EchoCommand());
        commands.put("help", (args, msg) -> msg.getChannel().sendMessage(new EmbedBuilder()
            .setTitle(":?: Available Commands")
            .setDescription(commands.keySet().stream()
                .map(name -> commandPrefix + name)
                .reduce((x, y) -> x + "\n" + y).orElse("_none_"))
            .build()));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        Matcher matcher = commandPattern.matcher(message.getContentDisplay());
        if (matcher.matches()) {
            String commandName = matcher.group(1);
            String args = matcher.group(2);
            BotCommand command = commands.get(commandName);
            if (command != null) {
                command.invoke(args, message);
            } else {
                event.getChannel().sendMessage("Sorry, I could not find the command `" + commandName + "`");
            }
        }
    }
}
