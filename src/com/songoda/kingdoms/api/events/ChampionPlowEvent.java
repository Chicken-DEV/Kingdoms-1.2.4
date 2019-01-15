package com.songoda.kingdoms.api.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class ChampionPlowEvent extends AbstractChampionEvent {

  @Getter
  private Block blockPlowed;

  public ChampionPlowEvent(Entity champion, Block blockPlowed){
    super(champion);
    this.blockPlowed = blockPlowed;
  }

}
