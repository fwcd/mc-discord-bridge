package dev.fwcd.mcdiscordbridge.bot.command;

import net.dv8tion.jda.api.entities.Message;

public class EchoCommand implements BotCommand {
    @Override
    public void invoke(String args, Message message) {
        message.getChannel().sendMessage(args).queue();
    }
}
