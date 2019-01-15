package com.songoda.kingdoms.manager.game;

import java.util.Iterator;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.PowerUp;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class KingdomPowerUpManager extends Manager implements Listener{
	protected KingdomPowerUpManager(Plugin plugin) {
		super(plugin);
	}

	@Override
	public void onDisable() {
		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAttack(EntityDamageByEntityEvent event) {
		
		if(!(event.getDamager() instanceof Player)) return;
		if(GameManagement.getApiManager().isCitizen(event.getDamager())) return;
		
		Player p = (Player) event.getDamager();
		for(String s: Config.getConfig().getStringList("enabled-worlds")){
			Kingdoms.logDebug(s);
		}
		if(!Config.getConfig().getStringList("enabled-worlds").contains(p.getWorld().getName())) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		if(kp == null) return;
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		
		PowerUp pu = kingdom.getPowerUp();
		int level = pu.getDmgboost();
		
			event.setDamage(event.getDamage() * (1.0D + level/100.0D));
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAttackArrow(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Arrow)) return;
		Arrow arrow = (Arrow) event.getDamager();
		
		if(!Config.getConfig().getStringList("enabled-worlds").contains(arrow.getWorld().getName())) return;
		
		if(!(arrow.getShooter() instanceof Player)) return;
		if(GameManagement.getApiManager().isCitizen((Entity) arrow.getShooter())) return;
		
		Player p = (Player) arrow.getShooter();
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		if(kp == null) return;
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		
		PowerUp pu = kingdom.getPowerUp();
		int level = pu.getDmgboost();
		
			event.setDamage(event.getDamage() * (1.0D + level/100.0D));

			Kingdoms.logDebug("" + event.getDamage());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDefend(EntityDamageEvent event) {
		
		if(!(event.getEntity() instanceof Player)) return;
		if(GameManagement.getApiManager().isCitizen(event.getEntity())) return;
		
		Player p = (Player) event.getEntity();
		if(!Config.getConfig().getStringList("enabled-worlds").contains(p.getWorld().getName())) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		if(kp == null) return;
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		
		PowerUp pu = kingdom.getPowerUp();
		double level = pu.getDmgreduction()/100.0D;
		
			event.setDamage(event.getDamage() * (1.0D - level));

			Kingdoms.logDebug("" + event.getDamage());
	}
	
	@EventHandler
	public void onNeutralLandExplode(EntityExplodeEvent e){
		if(!(Config.getConfig().getBoolean("protectNeutralLandFromExplosions"))) return;
		if(e.getEntity() == null) return;
		
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;
			
			SimpleLocation loc = new SimpleLocation(block.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) continue;
			
			if(kingdom.isNeutral()) iter.remove();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHealthRegain(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(GameManagement.getApiManager().isCitizen(event.getEntity())) return;
		Player player = (Player) event.getEntity();
		
		if(!Config.getConfig().getStringList("enabled-worlds").contains(player.getWorld().getName())) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(player);
		if(kp == null) return;
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(kp.getLoc());
		if(land.getOwnerUUID() == null) return;
		if(!land.getOwnerUUID().equals(kingdom.getKingdomUuid())) return;
		PowerUp pu = kingdom.getPowerUp();
		int level = pu.getRegenboost();
		
		event.setAmount(event.getAmount() * (1.0D + level/100.0D));

		Kingdoms.logDebug(event.getAmount() + "|" + (1.0D + level/100.0D));
	}

}
