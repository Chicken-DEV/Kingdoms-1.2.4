package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomLoadEvent extends Event {

	private Kingdom kingdom;
	private static final HandlerList handlers = new HandlerList();

	public KingdomLoadEvent(Kingdom kingdom) {
		this.kingdom = kingdom;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}
	
}
