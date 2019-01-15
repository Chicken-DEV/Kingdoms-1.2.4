package com.songoda.kingdoms.events;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeChunkEvent extends Event implements Cancellable{

	private Player p;
	private Chunk from;
	private Chunk to;
	private static final HandlerList handlers = new HandlerList();

	public PlayerChangeChunkEvent(Player p, Chunk from, Chunk to) {
		this.p = p;
		this.from = from;
		this.to = to;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return p;
	}

	public Chunk getFromChunk() {
		return from;
	}

	public Chunk getToChunk() {
		return to;
	}

	private boolean isCancelled = false;
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.isCancelled = arg0;
	}
}
