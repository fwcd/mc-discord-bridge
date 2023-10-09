package fwcd.mcdiscordbridge.bot.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * An unordered collection of guild-associated Discord text channels.
 */
public class TextChannelRegistry implements Iterable<String> {
    private final List<String> channelIds;
    private final Consumer<List<String>> updateListener;
    
    public TextChannelRegistry() {
        this(Collections.emptyList(), ids -> {});
    }
    
    public TextChannelRegistry(List<String> channelIds, Consumer<List<String>> updateListener) {
        this.channelIds = new ArrayList<>(channelIds);
        this.updateListener = updateListener;
    }

    public void addChannel(String channelId) {
        channelIds.add(channelId);
        fireListener();
    }
    
    public void addChannelOf(Message message) {
        addChannel(message.getChannel().getId());
    }
    
    public void removeChannel(String channelId) {
        channelIds.remove(channelId);
        fireListener();
    }
    
    public void removeChannelOf(Message message) {
        removeChannel(message.getChannel().getId());
    }
    
    public boolean containsChannel(String channelId) {
        return channelIds.contains(channelId);
    }
    
    public boolean containsChannelOf(Message message) {
        return containsChannel(message.getChannel().getId());
    }
    
    private void fireListener() {
        updateListener.accept(Collections.unmodifiableList(channelIds));
    }
    
    public void broadcastMessage(MessageCreateData message, JDA jda) {
        for (String channelId : channelIds) {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(message).queue();
            }
        }
    }
    
    public void broadcastMessage(String content, JDA jda) {
        broadcastMessage(new MessageCreateBuilder().setContent(content).build(), jda);
    }
    
    public void broadcastMessage(MessageEmbed embed, JDA jda) {
        broadcastMessage(new MessageCreateBuilder().setEmbeds(embed).build(), jda);
    }
    
    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(channelIds).iterator();
    }
}
