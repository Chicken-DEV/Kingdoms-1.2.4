package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class ChampionStrengthEvent extends AbstractChampionEvent {

  @Getter
  private KingdomPlayer target;

  public ChampionStrengthEvent(Entity champion, KingdomPlayer target){
    super(champion);
    this.target = target;
  }

}
