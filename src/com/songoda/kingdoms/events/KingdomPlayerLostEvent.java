package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomPlayerLostEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private KingdomPlayer challenger;
	private Kingdom defender;
	private SimpleChunkLocation loc;
	
	public KingdomPlayerLostEvent(KingdomPlayer challenger, Kingdom defender, SimpleChunkLocation loc) {
		this.challenger = challenger;
		this.defender = defender;
		this.loc = loc;
	}

	public KingdomPlayer getChallenger() {
		return challenger;
	}

	public Kingdom getDefender() {
		return defender;
	}

	public SimpleChunkLocation getLoc() {
		return loc;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
