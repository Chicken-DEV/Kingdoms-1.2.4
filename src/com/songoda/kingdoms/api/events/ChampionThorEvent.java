package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class ChampionThorEvent extends AbstractChampionEvent {

  @Getter
  @Setter
  private int dmg;

  @Getter
  private KingdomPlayer player;

  public ChampionThorEvent(Entity champion, KingdomPlayer player){
    super(champion);
    this.player = player;
    this.dmg = player.getKingdom().getChampionInfo().getThor() + 2;
  }

}
