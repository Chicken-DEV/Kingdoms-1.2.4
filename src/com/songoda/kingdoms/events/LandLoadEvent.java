package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.land.Land;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandLoadEvent extends Event {
	private Land land;
	private static final HandlerList handlers = new HandlerList();

	public LandLoadEvent(Land land) {
		this.land = land;
	}

	public Land getLand() {
		return land;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
