package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomPlayerPlaceNexusEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private KingdomPlayer player;
	private Kingdom kingdom;
	private Location nexusLoc;
	private SimpleChunkLocation chunk;

	public KingdomPlayerPlaceNexusEvent(KingdomPlayer player, Kingdom kingdom, Location nexusLoc, SimpleChunkLocation chunk) {
		this.player = player;
		this.kingdom = kingdom;
		this.nexusLoc = nexusLoc;
		this.chunk = chunk;
	}

	public KingdomPlayer getKingdomPlayer() {
		return player;
	}

	public Location getNexusLoc() {
		return nexusLoc;
	}

	public Kingdom getKingdom() {
		return kingdom;
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
