package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.songoda.kingdoms.constants.StructureType;

public class StructureBreakEvent extends Event{

	private Land land;
	private Location location;
	private Kingdom kingdom;
	private KingdomPlayer kp;
	private StructureType type;
	
	public StructureBreakEvent(Land land, Location loc, StructureType type, Kingdom kingdom, KingdomPlayer kp){
		this.land = land;
		this.kingdom = kingdom;
		this.location = loc;
		this.kp = kp;
		this.type = type;
	}
	
	public StructureType getStructureType(){
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

	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
