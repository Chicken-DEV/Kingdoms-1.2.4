package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class ChampionDragEvent extends AbstractChampionEvent {


  @Getter
  @Setter
  private double dragRange = Config.getConfig().getDouble("champion-specs.drag-min-range");

  @Getter
  private KingdomPlayer target;

  public ChampionDragEvent(Entity champion, KingdomPlayer target){
    super(champion);
    this.target = target;
  }
}
