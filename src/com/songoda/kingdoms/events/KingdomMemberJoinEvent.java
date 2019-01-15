package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomMemberJoinEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private OfflineKingdomPlayer kp;
	private Kingdom kingdom;
	
	public KingdomMemberJoinEvent(KingdomPlayer kp, Kingdom kingdom) {
		this.kp = kp;
		this.kingdom = kingdom;
	}
	
	public KingdomMemberJoinEvent(OfflineKingdomPlayer kp, Kingdom kingdom) {
		this.kp = kp;
		this.kingdom = kingdom;
	}

	public OfflineKingdomPlayer getKp() {
		return kp;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
