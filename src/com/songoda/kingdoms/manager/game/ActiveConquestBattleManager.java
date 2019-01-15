package com.songoda.kingdoms.manager.game;

import java.util.Iterator;

import com.songoda.kingdoms.constants.conquest.ActiveConquestBattle;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import com.songoda.kingdoms.main.Kingdoms;

public class ActiveConquestBattleManager extends Manager implements Listener{

	protected ActiveConquestBattleManager(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event){
		if(event.getEntity().getWorld().equals(ConquestManager.world)){
			if(event.getSpawnReason() == SpawnReason.NATURAL||
					event.getSpawnReason() == SpawnReason.CHUNK_GEN){
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player p = event.getPlayer();
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);
		Kingdom kingdom = kp.getKingdom();
		ActiveConquestBattle battle = ConquestManager.kingdomsMissions.get(kingdom);
		if(battle != null){
			if(battle.invaders.contains(kp)){
				battle.modifiedBlocks.add(event.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(EntityDamageEvent event){
		if(event.isCancelled()) return;
		if(GameManagement.getApiManager().isCitizen(event.getEntity())) return;
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(event.getFinalDamage() >= p.getHealth()){
				KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);
				Kingdom kingdom = kp.getKingdom();
				ActiveConquestBattle battle = ConquestManager.kingdomsMissions.get(kingdom);
				if(battle != null){
					if(battle.invaders.contains(kp)){
						event.setCancelled(true);
						for(ItemStack item:kp.getPlayer().getInventory().getContents()){
							if(item == null)continue;
							if(item.getType() == Material.AIR)continue;
							battle.moblist.add(ConquestManager.world.dropItemNaturally(kp.getPlayer().getLocation(),item));
						}
						for(ItemStack item:kp.getPlayer().getInventory().getArmorContents()){
							if(item == null)continue;
							if(item.getType() == Material.AIR)continue;
							battle.moblist.add(ConquestManager.world.dropItemNaturally(kp.getPlayer().getLocation(),item));
						}
						kp.getPlayer().getInventory().clear();
						kp.getPlayer().getInventory().setHelmet(null);
						kp.getPlayer().getInventory().setChestplate(null);
						kp.getPlayer().getInventory().setLeggings(null);
						kp.getPlayer().getInventory().setBoots(null);
						ConquestManager.leaveOffensive(kp);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandWhileFight(PlayerCommandPreprocessEvent e){
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		
		Kingdom kingdom = kp.getKingdom();
		ActiveConquestBattle battle = ConquestManager.kingdomsMissions.get(kingdom);
		if(battle != null){
			if(battle.invaders.contains(kp)){
				if(!kp.getPlayer().getWorld().equals(ConquestManager.world)){
					battle.invaders.remove(kp);
					return;
				}
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Conquests_Kingdom_No_Commands", kp.getLang()));
			}
		}
	
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);

		Kingdom kingdom = kp.getKingdom();
		ActiveConquestBattle battle = ConquestManager.kingdomsMissions.get(kingdom);
		if(battle != null){
			if(battle.invaders.contains(kp)){
				ConquestManager.leaveOffensive(kp);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getWorld().equals(ConquestManager.world)){
			event.setCancelled(true);

			
			KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer());
			Kingdom kingdom = kp.getKingdom();
			if(ConquestManager.kingdomsMissions.get(kingdom) != null){
				Kingdoms.logDebug("ActiveBattle not null");
				ActiveConquestBattle battle = ConquestManager.kingdomsMissions.get(kingdom);
				if(event.getBlock().getType() == Material.BEACON){
					Kingdoms.logDebug("Block is beacon");
					battle.concludeVictory();
				}
				if(battle.modifiedBlocks.contains(event.getBlock().getLocation())){
					event.setCancelled(false);
					return;
				}
				if(canBeDestroyed(event.getBlock())){
					event.getBlock().setType(Material.AIR);
				}
			}	
		}
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event){
		if(event.getEntity().getWorld().equals(ConquestManager.world)){
			if(event.getEntity() instanceof Creeper){
				Creeper c = (Creeper) event.getEntity();
				if(c.hasPotionEffect(PotionEffectType.SPEED))
					c.removePotionEffect(PotionEffectType.SPEED);
			}
			for(Iterator<Block> iter = event.blockList().iterator(); iter.hasNext();){
				Block block = iter.next();
				iter.remove();
				if(block == null || block.getType() == Material.AIR) continue;
				
				if(canBeDestroyed(block)){
					block.setType(Material.AIR);
				}
			}
		}
	}

	@EventHandler
	public void onTurretDamageGuard(EntityDamageByEntityEvent event){
		if(GameManagement.getApiManager().isCitizen(event.getEntity())) return;
		if(ConquestManager.world.equals(event.getEntity().getWorld())){
			if(event.getEntity() instanceof Monster){
				if(((Monster) event.getEntity()).getCustomName().equals(GuardsManager.GUARDNAME)){
					if(event.getDamager().hasMetadata("CONQUESTARROW")){
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPistonPushTurret(BlockPistonExtendEvent e){
		if(!e.getBlock().getWorld().equals(ConquestManager.world)) return;	
		for(Iterator<Block> iter = e.getBlocks().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;
			
			if(canBeDestroyed(block)) continue;
			
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPistonPullTurret(BlockPistonRetractEvent e){
		if(!e.getBlock().getWorld().equals(ConquestManager.world)) return;
		for(Iterator<Block> iter = e.getBlocks().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;
			
			if(canBeDestroyed(block)) continue;
			
			e.setCancelled(true);
			return;
		}
	}
	
	public boolean canBeDestroyed(Block block){
		Chunk c = block.getChunk();
		if(!block.getType().isSolid()) return true;
		if(block.getType() == Materials.SKELETON_SKULL.parseMaterial()) return true;
		if(block.getType() == Materials.OAK_FENCE.parseMaterial()) return true;
		if(block.getY()<101){
			Kingdoms.logDebug("canBeDestroyed - false1");
				return false;
		}
		if(block.getY()>107){
			Kingdoms.logDebug("canBeDestroyed - false2");
			return false;
		}
		int edgeX = c.getBlock(0, 0, 0).getX();
		if(block.getX() == edgeX){
			Kingdoms.logDebug("canBeDestroyed - false3");
			return false;
		}
		if(block.getX() == edgeX + 15){
			Kingdoms.logDebug("canBeDestroyed - false4");
			return false;
		}
		if(c.getBlock(block.getX(), block.getY(), 14).getLocation().equals(block.getLocation())&&
				c.getBlock(block.getX(), 100, 15).getLocation().getBlock().getType() == Material.AIR){
			Kingdoms.logDebug("canBeDestroyed - false5");
			return false;
		}
		if(c.getBlock(block.getX(), block.getY(), 0).getLocation().equals(block.getLocation())){
			Kingdoms.logDebug("canBeDestroyed - false6");
			return false;
		}
		Kingdoms.logDebug("canBeDestroyed - true");
		return true;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

}
