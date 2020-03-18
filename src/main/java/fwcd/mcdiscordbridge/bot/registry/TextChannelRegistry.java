package fwcd.mcdiscordbridge.bot.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;

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
    
    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(channelIds).iterator();
    }
}
