package com.songoda.kingdoms.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class ChampionDamageCapEvent extends AbstractChampionEvent {

  @Getter
  @Setter
  private int damageCap;

  @Getter
  private double damageDealt;

  @Setter
  private Entity attacker;

  public ChampionDamageCapEvent(Entity champion, Entity attacker, int damageCap, double damageDealt){
    super(champion);
    this.damageCap = damageCap;
    this.attacker = attacker;
    this.damageDealt = damageDealt;
  }
}
