package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomPlayerLoginEvent extends Event {
	private KingdomPlayer kp;
	private static final HandlerList handlers = new HandlerList();

	public KingdomPlayerLoginEvent(KingdomPlayer kp) {
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
