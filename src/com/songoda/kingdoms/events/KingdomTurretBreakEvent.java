package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.songoda.kingdoms.constants.TurretType;

public class KingdomTurretBreakEvent extends Event implements Cancellable {
    Location location;
    TurretType type;
    Kingdom owner;
    boolean cancelled;
    public KingdomTurretBreakEvent(Location location, TurretType type, Kingdom owner) {
        this.location = location;
        this.type = type;
        this.owner = owner;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

	private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

	public static HandlerList getHandlerList() {
		return handlers;
	}
    public Location getLocation() {
        return location;
    }

    public TurretType getType() {
        return type;
    }

    public Kingdom getOwner() {
        return owner;
    }
}
