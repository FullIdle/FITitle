package me.gsqfi.fititle.fititle.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
public class PlayerTitleChangeEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Setter
    private boolean cancelled = false;

    private final String playerName;
    private final String originalTitle;
    @Setter
    private List<String> newTitle;
    private final Type type;


    public PlayerTitleChangeEvent(Type type,String playerName,String originalTitle,List<String> newTitles){
        this.type = type;
        this.playerName = playerName;
        this.originalTitle = originalTitle;
        this.newTitle = newTitles;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum Type{
        ALL,NOW;
    }
}
