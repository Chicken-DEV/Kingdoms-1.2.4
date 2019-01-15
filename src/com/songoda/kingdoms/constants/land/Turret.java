package com.songoda.kingdoms.constants.land;

import com.songoda.kingdoms.constants.TurretType;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.events.KingdomTurretFireEvent;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.utils.Materials;
import com.songoda.kingdoms.utils.TurretUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Calendar;

//2017-05-04 -- moved static methods into TurretUtil
public class Turret {

  @Getter
  private SimpleLocation loc;
  //2017-04-27
  @Getter
  private TurretType type;
  private long cooldownExpire = 0;


  private Turret(){
	type = TurretType.ARROW;
  }

  public Turret(SimpleLocation loc, TurretType type){
	this.loc = loc;
	this.type = type;
  }

  public void setType(TurretType type){
	this.type = type;
  }

  public boolean isValid(){

	if(this.getType() == null){
	  return false;
	}
	if(this.getLoc() == null){
	  return false;
	}
	if(this.getLoc().toLocation() == null){
	  return false;
	}
	if(!this.getLoc().toLocation().getBlock().getRelative(0, -1, 0).getType().toString()
		.endsWith("FENCE")){
	  return false;
	}

	return this.getLoc().toLocation().getBlock().getType() == Materials.SKELETON_SKULL.parseMaterial();
  }

  public static Turret fromString(String str){
	String[] split = str.split(":");
	if(split.length != 2){
	  //Kingdoms.logDebug("Invailed turret data: "+str);
	  return null;
	}

	Location bukkitLoc = SimpleLocation.strToLoc(split[0]);

	SimpleLocation loc = new SimpleLocation(bukkitLoc);
	TurretType type = TurretType.valueOf(split[1]);

	return new Turret(loc, type);
  }

  @Override
  public String toString(){
	return loc.toString() + ":" + type.toString();
  }

  public void onTurretLoop(){

  }

  public void destroy(){
	Location loc = this.loc.toLocation();
	Land land = GameManagement.getLandManager().getOrLoadLand(this.loc.toSimpleChunk());
	land.getTurrets().remove(this);
	loc.getBlock().setType(Material.AIR);
  }

  public void breakTurret(){
	this.loc.toLocation().getWorld().dropItem(this.loc.toLocation(), type.getTurretDisk());
	destroy();
  }

  public void fire(Player target){
	Land land = GameManagement.getLandManager().getOrLoadLand(loc.toSimpleChunk());
	Kingdom shooter = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	//if(type == TurretType.HELLFIRE && shooter.getTurretUpgrades().isHellstorm()) fireTimer--;
	Calendar cal = Calendar.getInstance();
	if(cooldownExpire > cal.getTimeInMillis()) return;
	if(target == null) return;
	if(!TurretUtil.canBeTarget(this, target)){
	  return;
	}
	KingdomTurretFireEvent event = new KingdomTurretFireEvent(loc.toLocation(), type, target, shooter);
	Bukkit.getPluginManager().callEvent(event);
	if(!event.isCancelled()){
	  cal.add(Calendar.SECOND, type.getFireCD());
	  cooldownExpire = cal.getTimeInMillis();
	  target = event.getTarget();
	  switch(type){
		case ARROW:
		  TurretUtil.shootArrow(shooter, target.getLocation(), loc.toLocation(), false, false, type.getDamage());
		  break;
		case FLAME:
		  TurretUtil.shootArrow(shooter, target.getLocation(), loc.toLocation(), false, true, type.getDamage());
		  break;
		case HEALING:
		  TurretUtil.healEffect((Player) target, type.getDamage());
		  if(shooter.getTurretUpgrades().isImprovedHeal())
			TurretUtil.regenHealEffect((Player) target, (float) type.getDamage() / 2);
		  break;
		case HEATBEAM:
		  TurretUtil.heatbeamAttack(target, loc.toLocation(), type.getDamage(), shooter.getTurretUpgrades().isUnrelentingGaze());
		  break;
		case HELLFIRE:
		  TurretUtil.shootArrow(shooter, target.getLocation(), loc.toLocation(), true, false, type.getDamage());
		  break;
		case MINE_CHEMICAL:
		  int dur = 100;
		  if(shooter.getTurretUpgrades().isVirulentPlague()) dur = 200;
		  target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
		  target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1));
		  target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, dur, type.getDamage()));
		  destroy();
		  break;
		case MINE_PRESSURE:
		  loc.toLocation().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), type.getDamage(), false, false);
		  if(shooter.getTurretUpgrades().isConcentratedBlast())
			loc.toLocation().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), (float) (type.getDamage() * 0.5), false, false);
		  destroy();
		  break;
		case PSIONIC:
		  TurretUtil.psionicEffect(target, type.getDamage(), shooter.getTurretUpgrades().isVoodoo());
		  break;
		case SOLDIER:
		  GameManagement.getSoldierTurretManager().turretSpawnSoldier(shooter, target.getLocation(), loc.toLocation(), type.getDamage(), (Player) target);
		  break;

	  }
	}
  }

}
