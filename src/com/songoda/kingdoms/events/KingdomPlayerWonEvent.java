package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomPlayerWonEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private KingdomPlayer challenger;
	private Kingdom lostKingdom;
	private SimpleChunkLocation chunk;

	public KingdomPlayerWonEvent(KingdomPlayer challenger, Kingdom lostKingdom, SimpleChunkLocation chunk) {
		this.challenger = challenger;
		this.lostKingdom = lostKingdom;
		this.chunk = chunk;
	}

	public KingdomPlayer getChallenger() {
		return challenger;
	}

	public Kingdom getLostKingdom() {
		return lostKingdom;
	}

	public SimpleChunkLocation getChunk() {
		return chunk;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
