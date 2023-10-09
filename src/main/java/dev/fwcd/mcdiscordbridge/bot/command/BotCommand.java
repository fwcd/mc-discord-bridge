package dev.fwcd.mcdiscordbridge.bot.command;

import net.dv8tion.jda.api.entities.Message;

public interface BotCommand {
    void invoke(String args, Message message);
}
