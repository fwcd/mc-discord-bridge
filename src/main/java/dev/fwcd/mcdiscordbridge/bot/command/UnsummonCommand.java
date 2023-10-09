package dev.fwcd.mcdiscordbridge.bot.command;

import dev.fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.entities.Message;

public class UnsummonCommand implements BotCommand {
    private final TextChannelRegistry subscribedChannels;
    
    public UnsummonCommand(TextChannelRegistry subscribedChannels) {
        this.subscribedChannels = subscribedChannels;
    }
    
    @Override
    public void invoke(String args, Message message) {
        subscribedChannels.removeChannelOf(message);
        message.getChannel().sendMessage(":x: This channel will no longer receive chat messages from Minecraft.").queue();
    }
}
