package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class ChampionFocusEvent extends AbstractChampionEvent {

  @Getter
  private KingdomPlayer target;

  public ChampionFocusEvent(Entity champion, KingdomPlayer target){
    super(champion);
    this.target = target;
  }
}
