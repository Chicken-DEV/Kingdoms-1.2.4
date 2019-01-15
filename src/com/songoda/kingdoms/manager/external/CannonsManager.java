package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.MisupgradeInfo;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.event.ProjectileImpactEvent;

public class CannonsManager extends Manager {

	protected CannonsManager(Plugin plugin) {
		super(plugin);

		Cannons cannons = (Cannons) plugin.getServer().getPluginManager().getPlugin("Cannons");
		if (cannons != null) {
			Kingdoms.logInfo("Cannons Hooked!");
			Kingdoms.logInfo("Version: " + cannons.getDescription().getVersion());
			if(cannons.isEnabled()){
			}else{
				Kingdoms.logInfo("Cannons is not enabled!");
				Kingdoms.logInfo("Disabled support for Cannons.");						
			}
		}
	
	}

	@EventHandler
	public void onExplodeWhileBombshard(ProjectileImpactEvent e){
			Block block = e.getImpactLocation().getBlock();
			if(block == null || block.getType() == Material.AIR) return;
			
			SimpleLocation loc = new SimpleLocation(block.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) return;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) return;
			
			MisupgradeInfo info = kingdom.getMisupgradeInfo();
			
			if(info.isEnabledbombshards() && info.isBombshards() && Config.getConfig().getBoolean("misc_upgrades_bombshards_enabled"))
				e.setCancelled(true);
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}


}
