package com.songoda.kingdoms.api.events;

import org.bukkit.entity.Entity;

public class ChampionIgnoreKnockbackEvent extends AbstractChampionEvent {

  public ChampionIgnoreKnockbackEvent(Entity champion){
    super(champion);
  }
}
