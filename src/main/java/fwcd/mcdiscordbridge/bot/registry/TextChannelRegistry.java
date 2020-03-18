package fwcd.mcdiscordbridge.bot.registry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.dv8tion.jda.api.entities.Message;

/**
 * An unordered collection of guild-associated Discord text channels.
 */
public class TextChannelRegistry implements Iterable<String> {
    private final Set<String> channelIds = new HashSet<>();

    public void addChannel(String channelId) {
        channelIds.add(channelId);
    }
    
    public void addChannelOf(Message message) {
        addChannel(message.getChannel().getId());
    }
    
    public void removeChannel(String channelId) {
        channelIds.remove(channelId);
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
    
    @Override
    public Iterator<String> iterator() {
        return channelIds.iterator();
    }
}
