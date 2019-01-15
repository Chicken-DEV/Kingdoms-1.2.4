package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.Manager;
import es.pollitoyeye.Bikes.CarPlaceEvent;
import es.pollitoyeye.Bikes.VehiclesMain;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;


public class VehiclesManager extends Manager {

	protected VehiclesManager(Plugin plugin) {
		super(plugin);

		VehiclesMain vehicles = (VehiclesMain) plugin.getServer().getPluginManager().getPlugin("Vehicles");
		if(vehicles != null){
			Kingdoms.logInfo("Vehicles Hooked!");
			Kingdoms.logInfo("Version: " + vehicles.getDescription().getVersion());
		}
	
	}
	
	@EventHandler
	public void onVehiclePlace(CarPlaceEvent event){
		Player p = event.getOwner();
		Location bukkitLoc = event.getLocation();
		
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
			
			event.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();

			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
				
				event.setCancelled(true);
				return;
			}
			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				event.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}
		}
		
	}
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
