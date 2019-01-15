package com.songoda.kingdoms.api.events;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AbstractChampionEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();

  protected boolean isCancelled;

  @Getter
  private Entity champion;

  public AbstractChampionEvent(Entity champion){
    this.champion = champion;
  }

  @Override
  public boolean isCancelled(){
	return isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled){
	this.isCancelled = isCancelled;
  }

  @Override
  public HandlerList getHandlers(){
	return handlers;
  }

  public static HandlerList getHandlerList() {
	return handlers;
  }
}
