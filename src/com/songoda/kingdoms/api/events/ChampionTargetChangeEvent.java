package com.songoda.kingdoms.api.events;

import lombok.Getter;
import org.bukkit.entity.Entity;

public class ChampionTargetChangeEvent extends AbstractChampionEvent {

  @Getter
  private Entity newTarg;

  public ChampionTargetChangeEvent(Entity champion, Entity target){
    super(champion);
    this.newTarg = target;
  }

}
