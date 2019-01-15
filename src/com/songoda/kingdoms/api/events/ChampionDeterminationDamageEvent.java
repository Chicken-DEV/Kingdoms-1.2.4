package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class ChampionDeterminationDamageEvent extends ChampionByPlayerDamageEvent {

  @Getter
  private int determination;

  public ChampionDeterminationDamageEvent(Entity champion, double damage, int determination, KingdomPlayer player){
    super(champion, player, damage);
    this.determination = determination;
  }

}
