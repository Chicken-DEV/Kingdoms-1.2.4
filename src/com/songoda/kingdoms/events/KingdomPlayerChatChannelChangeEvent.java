package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.songoda.kingdoms.constants.ChatChannel;

public class KingdomPlayerChatChannelChangeEvent extends Event implements Cancellable{
	private KingdomPlayer kingdomPlayer;
	private ChatChannel currentChatChannel;
	private ChatChannel newChatChannel;
	private static final HandlerList handlers = new HandlerList();

	public KingdomPlayerChatChannelChangeEvent(KingdomPlayer kingdomPlayer,
			ChatChannel currentChatChannel, ChatChannel newChatChannel) {
		this.kingdomPlayer = kingdomPlayer;
		this.currentChatChannel = currentChatChannel;
		this.newChatChannel = newChatChannel;
	}

	public KingdomPlayer getKingdomPlayer() {
		return kingdomPlayer;
	}

	public ChatChannel getCurrentChatChannel() {
		return currentChatChannel;
	}

	public ChatChannel getNewChatChannel() {
		return newChatChannel;
	}
	
	public void setNewChatChannel(ChatChannel newChatChannel) {
		this.newChatChannel = newChatChannel;
	}

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
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
