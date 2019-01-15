package com.songoda.kingdoms.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class ChampionPreMockEvent extends AbstractChampionEvent {

  @Getter
  @Setter
  private int mockRange;

  public ChampionPreMockEvent(Entity champion, int mockRange){
    super(champion);
    this.mockRange = mockRange;
  }
}
