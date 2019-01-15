package com.songoda.kingdoms.manager.game;

import java.util.Date;
import java.util.UUID;

import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.events.PlayerChangeChunkEvent;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.songoda.kingdoms.main.Kingdoms;

public class LandVisualizeManager extends Manager implements Listener {

	protected LandVisualizeManager(Plugin plugin) {
		super(plugin);
	}

	public void visualizeLand(KingdomPlayer kp, SimpleChunkLocation chunk){
		if(!Config.getConfig().getStringList("enabled-worlds").contains(chunk.getWorld())) return;
		UUID ownerName = GameManagement.getLandManager().getOrLoadLand(chunk).getOwnerUUID();
		
		Material mat;
		if(kp.getKingdom() == null || ownerName == null){
			mat = Material.QUARTZ_BLOCK;
		}else if (kp.getKingdom().equals(GameManagement.getKingdomManager().getOrLoadKingdom(ownerName))) {
			mat = Material.EMERALD_BLOCK;
		} else if (kp.getKingdom().isAllianceWith(GameManagement.getKingdomManager().getOrLoadKingdom(ownerName))) {
			mat = Material.GOLD_BLOCK;
		} else if (kp.getKingdom().isEnemyWith(GameManagement.getKingdomManager().getOrLoadKingdom(ownerName))) {
			mat = Material.REDSTONE_BLOCK;
		} else {
			mat = Material.QUARTZ_BLOCK;
		}
		
		Chunk bukkitChunk = SimpleChunkLocation.toChunk(chunk);
		sendFalsyBlock(kp, bukkitChunk, 0, 0, Material.SEA_LANTERN);
		sendFalsyBlock(kp, bukkitChunk, 1, 0, mat);
		sendFalsyBlock(kp, bukkitChunk, 0, 1, mat);
		
		sendFalsyBlock(kp, bukkitChunk, 0, 15, Material.SEA_LANTERN);
		sendFalsyBlock(kp, bukkitChunk, 0, 14, mat);
		sendFalsyBlock(kp, bukkitChunk, 1, 15, mat);
		
		
		sendFalsyBlock(kp, bukkitChunk, 15, 15, Material.SEA_LANTERN);
		sendFalsyBlock(kp, bukkitChunk, 15, 14, mat);
		sendFalsyBlock(kp, bukkitChunk, 14, 15, mat);
		
		sendFalsyBlock(kp, bukkitChunk, 15, 0, Material.SEA_LANTERN);
		sendFalsyBlock(kp, bukkitChunk, 14, 0, mat);
		sendFalsyBlock(kp, bukkitChunk, 15, 1, mat);
	}
	
	private void sendFalsyBlock(KingdomPlayer kp, Chunk chunk, int blockX, int blockZ, Material mat){
		World world = chunk.getWorld();
		
		Location loc = chunk.getBlock(blockX, 0, blockZ).getLocation();
		loc.setY(world.getHighestBlockYAt(loc) - 1);
		kp.getPlayer().sendBlockChange(loc, mat, (byte) 0);
		
		kp.getLastMarkedChunk().add(loc.clone());
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onClickBlock(PlayerInteractEvent e){
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp == null) return;
		if(!kp.isMarkDisplaying()) return;
//		if(kp.getPlayer().getItemInHand() != null &&
//				kp.getPlayer().getItemInHand().getType().isBlock()) return;
		if(kp.getLastDisplayTime() + 1*1000L > new Date().getTime()) return;
		kp.setLastDisplayTime(new Date().getTime());
		
		if(e.getClickedBlock() == null) return;
		if(e.getClickedBlock().getChunk() == null) return;
		
		final SimpleChunkLocation clickedChunk = new SimpleChunkLocation(e.getClickedBlock().getChunk());
		
		while(!kp.getLastMarkedChunk().isEmpty()){
			kp.getLastMarkedChunk().poll().getBlock().getState().update();
		}
		
		
		new BukkitRunnable(){
			@Override
			public void run() {
				Land land = GameManagement.getLandManager().getOrLoadLand(clickedChunk);
				if(land.getOwnerUUID() == null) return;
				
				visualizeLand(kp, clickedChunk);
			}
			
		}.runTaskLater(plugin, 5L);

	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent e){
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(!kp.isMarkDisplaying()) return;
		
		if(kp.getLastDisplayTime() + 1*1000L > new Date().getTime()) return;
		kp.setLastDisplayTime(new Date().getTime());
		
		if(e.getBlock() == null) return;
		if(e.getBlock().getChunk() == null) return;
		
		final SimpleChunkLocation clickedChunk = new SimpleChunkLocation(e.getBlock().getChunk());
		
		
		while(!kp.getLastMarkedChunk().isEmpty()){
			kp.getLastMarkedChunk().poll().getBlock().getState().update();
		}
		
		
		new BukkitRunnable(){
			@Override
			public void run() {
				Land land = GameManagement.getLandManager().getOrLoadLand(clickedChunk);
				if(land.getOwnerUUID() == null) return;
				
				visualizeLand(kp, clickedChunk);
			}
			
		}.runTaskLater(plugin, 5L);

	}
	@EventHandler
	public void onChunkChangeEvent(PlayerChangeChunkEvent e){
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		SimpleChunkLocation chunk = new SimpleChunkLocation(e.getToChunk());
		if(kp == null) return;
		
		while(!kp.getLastMarkedChunk().isEmpty()){
			kp.getLastMarkedChunk().poll().getBlock().getState().update();
		}
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
}
