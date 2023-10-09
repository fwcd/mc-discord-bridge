package dev.fwcd.mcdiscordbridge.bot.command;

import dev.fwcd.mcdiscordbridge.bot.registry.TextChannelRegistry;
import net.dv8tion.jda.api.entities.Message;

public class SummonCommand implements BotCommand {
    private final TextChannelRegistry subscribedChannels;
    
    public SummonCommand(TextChannelRegistry subscribedChannels) {
        this.subscribedChannels = subscribedChannels;
    }
    
    @Override
    public void invoke(String args, Message message) {
        subscribedChannels.addChannelOf(message);
        message.getChannel().sendMessage(":white_check_mark: This channel will now receive chat messages from Minecraft.").queue();
    }
}
