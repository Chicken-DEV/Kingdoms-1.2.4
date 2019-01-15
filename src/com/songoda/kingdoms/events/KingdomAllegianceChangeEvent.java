package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Allegiance;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomAllegianceChangeEvent extends Event {
	private Kingdom kingdom;
	private Kingdom target;
	private Allegiance oldAllegiance;
	private Allegiance newAllegiance;
	private static final HandlerList handlers = new HandlerList();

	public KingdomAllegianceChangeEvent(Kingdom kingdom, Kingdom target, Allegiance oldAllegiance, Allegiance newAllegiance) {
		this.kingdom = kingdom;
		this.target = target;
		this.oldAllegiance = oldAllegiance;
		this.newAllegiance = newAllegiance;
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
	
	

	/**
	 * @return returns the kingdom that the original kingdom used with the command (eg, /k ally add [kingdom] <-)
	 */
	public Kingdom getTarget(){
		return target;
	}

	public Allegiance getOldAllegiance() {
		return oldAllegiance;
	}

	public Allegiance getNewAllegiance() {
		return newAllegiance;
	}
}
