package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private Land land;
	private Kingdom kingdom;
	private boolean isCancelled = false;
	
	public LandUnclaimEvent(Land land, Kingdom kingdom){
		this.land = land;
		this.kingdom = kingdom;
	}
	
	public Land getLand() {
		return land;
	}
	
	public Kingdom getUnclaimingKingdom() {
		return kingdom;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

}
