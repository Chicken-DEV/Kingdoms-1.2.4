package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.gui.InteractiveGUI;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomNexusGUIOpenEvent extends Event implements Cancellable{
	private KingdomPlayer kingdomPlayer;
	public KingdomPlayer getKingdomPlayer() {
		return kingdomPlayer;
	}

	public InteractiveGUI getGui() {
		return gui;
	}

	private InteractiveGUI gui;
	
	public KingdomNexusGUIOpenEvent(KingdomPlayer kingdomPlayer,
			InteractiveGUI gui) {
		super();
		this.kingdomPlayer = kingdomPlayer;
		this.gui = gui;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled = false;
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
		
	}

}
