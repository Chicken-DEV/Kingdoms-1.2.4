package com.songoda.kingdoms.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomBuyUpgradeEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    public KingdomBuyUpgradeEvent(){

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
