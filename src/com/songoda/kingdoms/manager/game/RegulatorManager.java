package com.songoda.kingdoms.manager.game;

import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.Regulator;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

//2016-07-02 -- plain File accessing database is now changed to abstract Database form
public class RegulatorManager extends Manager implements Listener{
	
	RegulatorManager(Plugin plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractRegulatorCheck(PlayerInteractEvent e){
		if(e.isCancelled()) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(e.getClickedBlock().getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(e.getPlayer());
				if(kp.getKingdom() != null && kp.getKingdom().getKingdomName().equals(land.getOwnerUUID())){
					if(kp.getRank().isHigherOrEqualTo(kp.getKingdom().getPermissionsInfo().getOverrideRegulator())) return;
					if(!reg.getWhoCanInteract().contains(kp.getUuid())){
						e.setCancelled(true);
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Regulator_Cannot_Interact", kp.getLang()));
					}
				}
			}
		}
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBuildRegulatorCheck(BlockPlaceEvent e){
		if(e.isCancelled()) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(e.getBlock().getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(e.getPlayer());
				if(kp.getKingdom() != null && kp.getKingdom().getKingdomName().equals(land.getOwnerUUID())){
					if(kp.getRank().isHigherOrEqualTo(kp.getKingdom().getPermissionsInfo().getOverrideRegulator())) return;
					if(!reg.getWhoCanBuild().contains(kp.getUuid())){
						e.setCancelled(true);
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Regulator_Cannot_Build", kp.getLang()));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreakRegulatorCheck(BlockBreakEvent e){
		if(e.isCancelled()) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(e.getBlock().getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(e.getPlayer());
				if(kp.getKingdom() != null && kp.getKingdom().getKingdomName().equals(land.getOwnerUUID())){
					if(kp.getRank().isHigherOrEqualTo(kp.getKingdom().getPermissionsInfo().getOverrideRegulator())) return;
					if(!reg.getWhoCanBuild().contains(kp.getUuid())){
						e.setCancelled(true);
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Regulator_Cannot_Build", kp.getLang()));
					}
				}
			}
		}
	}
//	
//	@EventHandler
//	public void onSpecialLandExplode(EntityExplodeEvent e){
//		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
//			Block block = iter.next();
//			if(block == null || block.getType() == Material.AIR) continue;
//			
//			SimpleLocation loc = new SimpleLocation(block.getLocation());
//			SimpleChunkLocation chunk = loc.toSimpleChunk();
//			
//			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
//			if(land.getOwner() == null) continue;
//			
//			if(land.getOwner().equals(RegulatorManager.WARZONE)){
//				iter.remove();
//			}else if(land.getOwner().equals(RegulatorManager.SAFEZONE)){
//				iter.remove();
//			}
//		}
//		
//
//	}
	
	@EventHandler
	public void onBucketEmptyRegulatorCheck(PlayerBucketEmptyEvent e) {
		if(e.isCancelled()) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(e.getBlockClicked().getRelative(e.getBlockFace()).getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(e.getPlayer());
				if(kp.getKingdom() != null && kp.getKingdom().getKingdomUuid().equals(land.getOwnerUUID())){
					if(kp.getRank().isHigherOrEqualTo(kp.getKingdom().getPermissionsInfo().getOverrideRegulator())) return;
					if(!reg.getWhoCanBuild().contains(kp.getUuid())){
						e.setCancelled(true);
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Regulator_Cannot_Build", kp.getLang()));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBucketFillRegulatorCheck(PlayerBucketFillEvent e) {
		if(e.isCancelled()) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(e.getBlockClicked().getRelative(e.getBlockFace()).getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(e.getPlayer());
				if(kp.getKingdom() != null && kp.getKingdom().getKingdomUuid().equals(land.getOwnerUUID())){
					if(kp.getRank().isHigherOrEqualTo(kp.getKingdom().getPermissionsInfo().getOverrideRegulator())) return;
					if(!reg.getWhoCanBuild().contains(kp.getUuid())){
						e.setCancelled(true);
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Regulator_Cannot_Build", kp.getLang()));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onMobSpawnRegulatorCheck(CreatureSpawnEvent event){
		if(event.isCancelled()) return;
		if(event.getSpawnReason() == SpawnReason.CUSTOM) return;
		if(!(event.getEntity() instanceof Monster)&&
				!(event.getEntity() instanceof Animals)&&
				!(event.getEntity() instanceof Slime)&&
				!(event.getEntity() instanceof MagmaCube)) return;
		if(event.getEntity() instanceof EnderDragon||
				event.getEntity() instanceof Wither)return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(event.getEntity().getWorld().getName())){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(event.getEntity().getLocation().getChunk()));
			if(land == null) return;
			if(land.getOwnerUUID() == null) return;
			if(land.getStructure() == null) return;
			if(land.getStructure() instanceof Regulator){
				Regulator reg = (Regulator) land.getStructure();
				if((event.getEntity() instanceof Monster)||
						(event.getEntity() instanceof Slime)||
						(event.getEntity() instanceof MagmaCube)){
					if(!reg.isAllowMonsterSpawning()) event.setCancelled(true);
				}
				
				if((event.getEntity() instanceof Animals)){
					if(!reg.isAllowAnimalSpawning()) event.setCancelled(true);
				}
			}
		}
	}
	
//	@EventHandler
//	public void onFlowIntoKingdomLand(BlockFromToEvent e){
//		if(!Kingdoms.config.disableFlowIntoLand) return;
//		
//		SimpleLocation locFrom = new SimpleLocation(e.getBlock().getLocation());
//		SimpleLocation locTo = new SimpleLocation(e.getToBlock().getLocation());
//		
//		Land landFrom = GameManagement.getLandManager().getOrLoadLand(locFrom.toSimpleChunk());
//		Land landTo = GameManagement.getLandManager().getOrLoadLand(locTo.toSimpleChunk());
//		
//		if(landFrom.getOwner() == null){
//			if(landTo.getOwner() != null){
//				e.setCancelled(true);
//			}
//		}else if(landFrom.getOwner().equals(landTo.getOwner())){
//		}else{
//			e.setCancelled(true);
//		}
//	}

	@Override
	public void onDisable() {
		
	}

}
