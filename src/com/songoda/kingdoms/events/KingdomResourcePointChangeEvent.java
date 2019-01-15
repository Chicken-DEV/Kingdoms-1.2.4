package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.manager.game.GameManagement;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomResourcePointChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private int InvadedAmount = 0;
	private OfflineKingdom kingdom;

	public KingdomResourcePointChangeEvent(OfflineKingdom kingdom) {
		this.kingdom = kingdom;
	}
	
	public KingdomResourcePointChangeEvent(OfflineKingdom kingdom, int InvadedAmount) {
		this.kingdom = kingdom;
		this.InvadedAmount = InvadedAmount;
	}

	public OfflineKingdom getKingdom() {
		return kingdom;
	}
	
	public Kingdom getOnlineKingdom() {
		return GameManagement.getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName());
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
