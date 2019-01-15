package com.songoda.kingdoms.events;

import com.songoda.kingdoms.constants.TurretType;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomTurretFireEvent extends Event implements Cancellable {

  @Getter
  private Location location;
  @Getter
  private TurretType type;
  @Getter
  private Player target;
  @Getter
  private Kingdom owner;
  private boolean cancelled;

  public KingdomTurretFireEvent(Location location, TurretType type, Player target, Kingdom owner){
	this.location = location;
	this.type = type;
	this.target = target;
	this.owner = owner;
  }

  @Override
  public boolean isCancelled(){
	return cancelled;
  }

  @Override
  public void setCancelled(boolean b){
	cancelled = b;
  }


  private static final HandlerList handlers = new HandlerList();

  @Override
  public HandlerList getHandlers(){
	return handlers;
  }

  public static HandlerList getHandlerList(){
	return handlers;
  }

}
