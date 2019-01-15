package com.songoda.kingdoms.constants;

import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import com.songoda.kingdoms.utils.TurretUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum TurretType {
  ARROW(Kingdoms.getLang().getString("Guis_Turret_Arrow"),
	  Kingdoms.getLang().getString("Guis_Turret_Arrow_Desc"),
	  ChatColor.YELLOW + "=-=-=-=-=",
	  "_LifeBlood_",
	  1,
	  TurretTargetType.MONSTERS, TurretTargetType.ENEMY_PLAYERS),
  FLAME(Kingdoms.getLang().getString("Guis_Turret_Flame"),
	  Kingdoms.getLang().getString("Guis_Turret_Flame_Desc"),
	  ChatColor.RED + "=-=-=-=-=",
	  "MHF_WSkeleton",
	  2,
	  TurretTargetType.MONSTERS, TurretTargetType.ENEMY_PLAYERS),
  HEALING(Kingdoms.getLang().getString("Guis_Turret_Healing"),
	  Kingdoms.getLang().getString("Guis_Turret_Healing_Desc"),
	  ChatColor.GREEN + "=-=-=-=-=",
	  "MHF_Zombie",
	  1,
	  TurretTargetType.ALLY_PLAYERS),
  HEATBEAM(Kingdoms.getLang().getString("Guis_Turret_Heatbeam"),
	  Kingdoms.getLang().getString("Guis_Turret_Heatbeam_Desc"),
	  ChatColor.GREEN + "=========",
	  "MHF_Guardian",
	  4,
	  TurretTargetType.MONSTERS, TurretTargetType.ENEMY_PLAYERS),
  HELLFIRE(Kingdoms.getLang().getString("Guis_Turret_Hellfire"),
	  Kingdoms.getLang().getString("Guis_Turret_Hellfire_Desc"),
	  ChatColor.RED + "---------",
	  "BadLuck",
	  2,
	  TurretTargetType.MONSTERS, TurretTargetType.ENEMY_PLAYERS),
  MINE_CHEMICAL(Kingdoms.getLang().getString("Guis_Turret_ChemicalMine"),
	  Kingdoms.getLang().getString("Guis_Turret_ChemicalMine_Desc"),
	  ChatColor.DARK_GREEN + "______",
	  "lol",
	  0,
	  TurretTargetType.ENEMY_PLAYERS),
  MINE_PRESSURE(Kingdoms.getLang().getString("Guis_Turret_PressureMine"),
	  Kingdoms.getLang().getString("Guis_Turret_PressureMine_Desc"),
	  ChatColor.DARK_AQUA + "______",
	  "lol",
	  0,
	  TurretTargetType.ENEMY_PLAYERS),
  PSIONIC(Kingdoms.getLang().getString("Guis_Turret_Psionic"),
	  Kingdoms.getLang().getString("Guis_Turret_Psionic_Desc"),
	  ChatColor.GREEN + "O-O-O-O-O",
	  "MHF_Creeper",
	  1,
	  TurretTargetType.ENEMY_PLAYERS),
  SOLDIER(Kingdoms.getLang().getString("Guis_Turret_Soldier"),
	  Kingdoms.getLang().getString("Guis_Turret_Soldier_Desc"),
	  ChatColor.GOLD + "=|=======>",
	  "CybermanAC",
	  8,
	  TurretTargetType.ENEMY_PLAYERS);

  private final Collection<TurretTargetType> targets = new ArrayList();
  private final String title;
  private final String desc;
  private final String typeDecal;
  private final String skin;
  private final int fireCD;

  private TurretType(String title, String desc, String typeDecal,
					 String skin, int fireCD, TurretTargetType... targets){
	this.targets.addAll(Arrays.asList(targets));
	this.typeDecal = typeDecal;
	this.title = title;
	this.desc = desc;
	this.skin = skin;
	this.fireCD = fireCD;
  }
/*
  public int getFireRate(){
	if(fireCD == 0) return 0;
	return Math.round(20 / (fireCD * 5));
  }*/

  public Collection<TurretTargetType> getTargetTypes(){
	return targets;
  }

  public int getFireCD(){
	return fireCD;
  }

  public int getCost(){
	switch(this){
	  case ARROW:
		return Config.getConfig().getInt("cost.turrets.arrowturret");
	  case FLAME:
		return Config.getConfig().getInt("cost.turrets.flameturret");
	  case HEALING:
		return Config.getConfig().getInt("cost.turrets.healingstation");
	  case HEATBEAM:
		return Config.getConfig().getInt("cost.turrets.heatbeamturret");
	  case HELLFIRE:
		return Config.getConfig().getInt("cost.turrets.hellfireturret");
	  case MINE_CHEMICAL:
		return Config.getConfig().getInt("cost.turrets.chemicalmine");
	  case MINE_PRESSURE:
		return Config.getConfig().getInt("cost.turrets.pressuremine");
	  case PSIONIC:
		return Config.getConfig().getInt("cost.turrets.psionictotem");
	  case SOLDIER:
		return Config.getConfig().getInt("cost.turrets.soldierspawner");
	}
	return 0;
  }

  public boolean isEnabled(){
	switch(this){
	  case ARROW:
		return Config.getConfig().getBoolean("enable.turret.arrow");
	  case FLAME:
		return Config.getConfig().getBoolean("enable.turret.flameturret");
	  case HEALING:
		return Config.getConfig().getBoolean("enable.turret.healingtower");
	  case HEATBEAM:
		return Config.getConfig().getBoolean("enable.turret.heatbeam");
	  case HELLFIRE:
		return Config.getConfig().getBoolean("enable.turret.hellfire");
	  case MINE_CHEMICAL:
		return Config.getConfig().getBoolean("enable.turret.chemicalmine");
	  case MINE_PRESSURE:
		return Config.getConfig().getBoolean("enable.turret.pressuremine");
	  case PSIONIC:
		return Config.getConfig().getBoolean("enable.turret.psionictotem");
	  case SOLDIER:
		return Config.getConfig().getBoolean("enable.turret.soldierspawner");
	}
	return false;
  }

  public int getDamage(){
	switch(this){
	  case ARROW:
		return Config.getConfig().getInt("turret-specs.arrowturret.damage");
	  case FLAME:
		return Config.getConfig().getInt("turret-specs.flameturret.damage");
	  case HEALING:
		return Config.getConfig().getInt("turret-specs.healingstation.damage");
	  case HEATBEAM:
		return Config.getConfig().getInt("turret-specs.heatbeamturret.damage");
	  case HELLFIRE:
		return Config.getConfig().getInt("turret-specs.hellfireturret.damage");
	  case MINE_CHEMICAL:
		return Config.getConfig().getInt("turret-specs.chemicalmine.poison-potency");
	  case MINE_PRESSURE:
		return Config.getConfig().getInt("turret-specs.pressuremine.blast");
	  case PSIONIC:
		return Config.getConfig().getInt("turret-specs.psionictotem.damage");
	  case SOLDIER:
		return Config.getConfig().getInt("turret-specs.soldierspawner.strength-level");
	}
	return 0;
  }

  public int getRange(){
	switch(this){
	  case ARROW:
		return Config.getConfig().getInt("turret-specs.arrowturret.range");
	  case FLAME:
		return Config.getConfig().getInt("turret-specs.flameturret.range");
	  case HEALING:
		return Config.getConfig().getInt("turret-specs.healingstation.range");
	  case HEATBEAM:
		return Config.getConfig().getInt("turret-specs.heatbeamturret.range");
	  case HELLFIRE:
		return Config.getConfig().getInt("turret-specs.hellfireturret.range");
	  case MINE_CHEMICAL:
		return 0;
	  case MINE_PRESSURE:
		return 0;
	  case PSIONIC:
		return Config.getConfig().getInt("turret-specs.psionictotem.damage");
	  case SOLDIER:
		return Config.getConfig().getInt("turret-specs.soldierspawner.range");
	}
	return 0;
  }

  public int getPerLandMaxLimit(){
	switch(this){
	  case ARROW:
		return Config.getConfig().getInt("max-per-land.turrets.arrowturret");
	  case FLAME:
		return Config.getConfig().getInt("max-per-land.turrets.flameturret");
	  case HEALING:
		return Config.getConfig().getInt("max-per-land.turrets.healingstation");
	  case HEATBEAM:
		return Config.getConfig().getInt("max-per-land.turrets.heatbeamturret");
	  case HELLFIRE:
		return Config.getConfig().getInt("max-per-land.turrets.hellfireturret");
	  case MINE_CHEMICAL:
		return Config.getConfig().getInt("max-per-land.turrets.chemicalmine");
	  case MINE_PRESSURE:
		return Config.getConfig().getInt("max-per-land.turrets.pressuremine");
	  case PSIONIC:
		return Config.getConfig().getInt("max-per-land.turrets.psionictotem");
	  case SOLDIER:
		return Config.getConfig().getInt("max-per-land.turrets.soldierspawner");
	}
	return 0;
  }

  public ItemStack getTurretDisk(){
	ItemStack turret = new ItemStack(Materials.MUSIC_DISC_STAL.parseMaterial());
	ItemMeta meta = turret.getItemMeta();
	meta.setDisplayName(title);
	List<String> lore = new ArrayList();
	lore.add(desc);
	lore = LoreOrganizer.organize(lore);
	lore.add(Kingdoms.getLang().getString("Turrets_Range") + "" + getRange());
	lore.add(Kingdoms.getLang().getString("Turrets_Damage") + "" + getDamage());
	lore.add(Kingdoms.getLang().getString("Turrets_AttackSpeed") + "" + fireCD + "/s");
	lore.add(TurretUtil.turretDecal);
	lore.add(typeDecal);
	meta.setLore(lore);
	turret.setItemMeta(meta);
	return turret;
  }

  public ItemStack getGUITurretDisk(){
	ItemStack turret = new ItemStack(Materials.MUSIC_DISC_STAL.parseMaterial());
	ItemMeta meta = turret.getItemMeta();
	meta.setDisplayName(title);
	List<String> lore = new ArrayList();
	lore.add(desc);
	lore = LoreOrganizer.organize(lore);
	lore.add(Kingdoms.getLang().getString("Turrets_Range") + "" + getRange());
	lore.add(Kingdoms.getLang().getString("Turrets_Damage") + "" + getDamage());
	lore.add(Kingdoms.getLang().getString("Turrets_AttackSpeed") + "" + fireCD + "/s");
	lore.add(Kingdoms.getLang().getString("Guis_Cost_Text").replaceAll("%cost%", "" + getCost()));
	lore.add(TurretUtil.turretDecal);
	lore.add(typeDecal);
	meta.setLore(lore);
	turret.setItemMeta(meta);
	return turret;
  }

  public static TurretType identifyTurret(ItemStack item){
	if(item == null) return null;
	if(item.getItemMeta() == null) return null;
	if(item.getItemMeta().getLore() == null) return null;
	for(TurretType type : TurretType.values()){
	  if(item.getItemMeta().getLore().contains(type.getTypeDecal())){
		return type;
	  }
	}
	if(item.getItemMeta().getDisplayName() == null) return null;

	String displayname = item.getItemMeta().getDisplayName();
	for(TurretType type : TurretType.values()){
	  if(displayname.equals(type.getTurretDisk().getItemMeta().getDisplayName())){
		return type;
	  }
	}
	return null;

  }

  public String getSkin(){
	return skin;
  }

  public String getTypeDecal(){
	return typeDecal;
  }
}
