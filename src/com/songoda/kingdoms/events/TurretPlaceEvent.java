package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.songoda.kingdoms.constants.TurretType;

public class TurretPlaceEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private Land land;
	private Location location;
	private Kingdom kingdom;
	private KingdomPlayer kp;
	private TurretType type;
	private boolean isCancelled = false;
	
	public TurretPlaceEvent(Land land, Location loc, TurretType type, Kingdom kingdom, KingdomPlayer kp){
		this.land = land;
		this.kingdom = kingdom;
		this.location = loc;
		this.kp = kp;
		this.type = type;
	}
	
	public TurretType getTurretType(){
		return type;
	}
	
	public KingdomPlayer getKingdomPlayer(){
		return kp;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public Land getLand() {
		return land;
	}
	
	public Kingdom getKingdom() {
		return kingdom;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

}
