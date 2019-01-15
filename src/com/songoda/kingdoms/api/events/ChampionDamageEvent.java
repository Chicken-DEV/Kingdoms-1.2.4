package com.songoda.kingdoms.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class ChampionDamageEvent extends AbstractChampionEvent {

  @Getter
  @Setter
  private double damage;

  @Getter
  private ChampionDamageCause cause;

  public ChampionDamageEvent(Entity champion, double damage, ChampionDamageCause cause){
	super(champion);
	this.damage = damage;
	this.cause = cause;
  }

  public enum ChampionDamageCause{
    PLAYER,
	TURRET,
	POTION
  }

}
