package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomMemberLeaveEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private OfflineKingdomPlayer kp;
	private String kingdomName;
	
	public KingdomMemberLeaveEvent(OfflineKingdomPlayer kp, String kingdomName) {
		this.kp = kp;
		this.kingdomName = kingdomName;
	}

	public OfflineKingdomPlayer getKp() {
		return kp;
	}

	public String getKingdomName() {
		return kingdomName;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
