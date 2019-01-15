package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomInvadeEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private Kingdom target;
    private SimpleChunkLocation loc;
    private KingdomPlayer attacker;
    private boolean cancelled;
    public KingdomInvadeEvent(Kingdom target, KingdomPlayer attacker, SimpleChunkLocation loc){
        this.target = target;
        this.attacker = attacker;
        this.loc = loc;
    }
    
    public SimpleChunkLocation getInvadedLoc(){
    	return loc;
    }
    
    public Kingdom getTarget() {
        return target;
    }

    public KingdomPlayer getAttacker() {
        return attacker;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers(){
        return handlers;
    }
}
