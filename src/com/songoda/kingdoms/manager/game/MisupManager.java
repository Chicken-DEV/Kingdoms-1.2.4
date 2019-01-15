package com.songoda.kingdoms.manager.game;

import java.util.Iterator;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.MisupgradeInfo;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.external.ExternalManager;

public class MisupManager extends Manager implements Listener {

	protected MisupManager(Plugin plugin) {
		super(plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onExplosionDamage(EntityDamageEvent e){
		if(Config.getConfig().getStringList("worlds-where-land-is-explosive-vulnerable")
				.contains(e.getEntity().getWorld().getName())) return;
		if(ExternalManager.isCitizen(e.getEntity())) return;
		if(e.getCause() != DamageCause.ENTITY_EXPLOSION) return;
		
	    SimpleLocation loc = new SimpleLocation(e.getEntity().getLocation());
	    SimpleChunkLocation chunk = loc.toSimpleChunk();
	    Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	    if(land == null) return;
	    
	    if(land.getOwnerUUID() == null) return;

	    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	    if(kingdom == null) return; //safe/war zone
	    
	    if(!kingdom.getMisupgradeInfo().isEnabledanticreeper() ||!kingdom.getMisupgradeInfo().isAnticreeper() || !Config.getConfig().getBoolean("enable.misc.anticreeper.enabled")) return;
	    
	    if(e.getEntity() instanceof Animals) e.setCancelled(true);
	    
		if(!(e.getEntity() instanceof Player)) return;
	    KingdomPlayer target = GameManagement.getPlayerManager().getSession((Player) e.getEntity());
	    
	    if(kingdom.equals(target.getKingdom())){
	    	e.setDamage(0.0D);
	    	e.setCancelled(true);
	    	
	    	return;
	    }else if(kingdom.isAllianceWith(target.getKingdom())){
	    	e.setDamage(0.0D);
	    	e.setCancelled(true);
	    	
	    	return;
	    }
	}
	
	//antitrample
	@EventHandler
	public void onTrample(PlayerInteractEvent e){
		if(e.getAction() != Action.PHYSICAL) return;
		if(e.getClickedBlock().getType() != Materials.FARMLAND.parseMaterial()) return;
		
	    SimpleLocation loc = new SimpleLocation(e.getClickedBlock().getLocation());
	    SimpleChunkLocation chunk = loc.toSimpleChunk();
	    Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	    if(land == null) return;
	    
	    if(land.getOwnerUUID() == null) return;
	    
	    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	    if(kingdom == null) return;
	    
	    if(kingdom.getMisupgradeInfo().isEnabledantitrample() && kingdom.getMisupgradeInfo().isAntitrample() && Config.getConfig().getBoolean("enable.misc.antitrample")) e.setCancelled(true);
	}
	
	//glory
	@EventHandler
	public void onExpGain(EntityDeathEvent e){
		Player p = e.getEntity().getKiller();
		if(p == null) return;
		if(ExternalManager.isCitizen(p)) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		if(kp == null) return;
		if(kp.getKingdom() == null) return;
		
	    SimpleLocation loc = new SimpleLocation(p.getLocation());
	    SimpleChunkLocation chunk = loc.toSimpleChunk();
	    Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	    if(land == null) return;
	    
	    if(land.getOwnerUUID() == null) return;
	    
	    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	    if(kingdom == null) return;
	    
		if(!kingdom.getMisupgradeInfo().isGlory()) return;
	    
	    if(!kingdom.equals(kp.getKingdom())) return;
	    
	    if(kingdom.getMisupgradeInfo().isEnabledglory() && kingdom.getMisupgradeInfo().isGlory() && Config.getConfig().getBoolean("enable.misc.glory")) e.setDroppedExp(e.getDroppedExp()*3);
	}
	
	//nexus Guard
	@EventHandler(priority = EventPriority.LOWEST)
	public void onNexusMined(BlockBreakEvent e){
		if(e.isCancelled()) return;
		if(!Config.getConfig().getBoolean("enable.misc.nexusguard")) return;
		
		if(e.getBlock() == null) return;
		
		if(e.getBlock().getType() != Materials.BEACON.parseMaterial()) return;
		
		Block nexusBlock = e.getBlock();
		if(!nexusBlock.hasMetadata(StructureType.NEXUS.getMetaData())) return;
		
		KingdomPlayer invader = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(invader == null) return;
		
		Kingdom kInvader = invader.getKingdom();
		if(kInvader == null) return;
		
		SimpleLocation loc = new SimpleLocation(nexusBlock.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		
		
		
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land == null) return; //something is not good if it's null

		if(land.getOwnerUUID() == null){
			Kingdoms.logInfo("There was a nexus at ["+chunk.toString()+"] but no owner.");
			Kingdoms.logInfo("Removed nexus.");
			
			e.getBlock().setType(Material.AIR);
			e.setCancelled(true);
			return;
		}
		
		Kingdom kNexusOwner = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		
		if(kNexusOwner == null) return;
		if(kInvader.getKingdomName().equals(kNexusOwner.getKingdomName())){
			invader.sendMessage(Kingdoms.getLang().getString("Nexus_Mining_isOwn", invader.getLang()));
			return;
		}
		if(!kNexusOwner.getMisupgradeInfo().isNexusguard()) return;
		if(!kNexusOwner.getMisupgradeInfo().isEnablednexusguard()) return;
		Kingdoms.getManagers().getGuardsManager().spawnNexusGuard(invader.getPlayer().getLocation(), kNexusOwner, invader);
	}
	
	
	
	//Anticreeper
	@EventHandler
	public void onAntiCreeperTrigger(EntityExplodeEvent e){
		if(Config.getConfig().getStringList("worlds-where-land-is-explosive-vulnerable")
				.contains(e.getEntity().getWorld().getName())) return;
		if(!Config.getConfig().getBoolean("enable.misc.anticreeper.enabled")) return;
		if(e.getEntity() == null) return;
		if(e.getEntity().getType() != EntityType.CREEPER) return;
		
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;
			
			SimpleLocation loc = new SimpleLocation(block.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) continue;
			
			MisupgradeInfo info = kingdom.getMisupgradeInfo();
			
			if(info.isAnticreeper()&&info.isEnabledanticreeper()) iter.remove();
		}
	}
	
	//bombshard
	@EventHandler
	public void onExplodeWhileBombshard(EntityExplodeEvent e){
		if(Config.getConfig().getStringList("worlds-where-land-is-explosive-vulnerable")
				.contains(e.getEntity().getWorld().getName())) return;
		if(e.getEntity() == null) return;
		if(e.getEntity().getType() != EntityType.PRIMED_TNT) return;
		
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;
			
			SimpleLocation loc = new SimpleLocation(block.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) continue;
			
			MisupgradeInfo info = kingdom.getMisupgradeInfo();
			
			if(info.isEnabledbombshards() && info.isBombshards() && Config.getConfig().getBoolean("enable.misc.bombshards.enabled")) iter.remove();
		}
	}
	
	@EventHandler
	public void onExplodeDamageWhileBombshard(EntityDamageEvent e){
		if(Config.getConfig().getStringList("worlds-where-land-is-explosive-vulnerable")
				.contains(e.getEntity().getWorld().getName())) return;
		if(ExternalManager.isCitizen(e.getEntity())) return;
		if(e.getCause() != DamageCause.BLOCK_EXPLOSION) return;
		
	    SimpleLocation loc = new SimpleLocation(e.getEntity().getLocation());
	    SimpleChunkLocation chunk = loc.toSimpleChunk();
	    Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	    if(land == null) return;
	    
	    if(land.getOwnerUUID() == null) return;
	    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	    if(kingdom == null) return; //safe/war zone
	    
	    if(!kingdom.getMisupgradeInfo().isEnabledbombshards() || !kingdom.getMisupgradeInfo().isBombshards() || !Config.getConfig().getBoolean("enable.misc.bombshards.enabled")) return;
	    
	    if(e.getEntity() instanceof Animals) e.setCancelled(true);
	    
		if(!(e.getEntity() instanceof Player)) return;
	    KingdomPlayer target = GameManagement.getPlayerManager().getSession((Player) e.getEntity());
	    
	    
	    if(kingdom.equals(target.getKingdom())){
	    	e.setDamage(0.0D);
	    	e.setCancelled(true);
	    	
	    	return;
	    }else if(kingdom.isAllianceWith(target.getKingdom())){
	    	e.setDamage(0.0D);
	    	e.setCancelled(true);
	    	
	    	return;
	    }else{
	    	e.setDamage(e.getDamage() + 5.0D);
	    }
	}

	
//	private static final int psionicTimer = 10;//10sec
	//psionic core

//	private class PsionicBuffTask implements Runnable {
//
//		@Override
//		public void run() {
//			while (plugin.isEnabled()) {
//				try {
//					Thread.sleep(psionicTimer * 1000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//
//			}
//		}
//
//	}
//	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
