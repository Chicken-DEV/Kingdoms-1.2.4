package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomPlayerLogoffEvent extends Event {
	private KingdomPlayer kp;
	private static final HandlerList handlers = new HandlerList();

	public KingdomPlayerLogoffEvent(KingdomPlayer kp) {
		this.kp = kp;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public KingdomPlayer getKp() {
		return kp;
	}
}
