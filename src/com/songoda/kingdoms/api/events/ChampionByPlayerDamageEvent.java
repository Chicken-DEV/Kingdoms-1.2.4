package com.songoda.kingdoms.api.events;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class ChampionByPlayerDamageEvent extends ChampionDamageEvent {

  @Getter
  private KingdomPlayer attacker;

  public ChampionByPlayerDamageEvent(Entity champion, KingdomPlayer attacker, double damage){
    super(champion, damage, ChampionDamageCause.PLAYER);
    this.attacker = attacker;
  }
}
