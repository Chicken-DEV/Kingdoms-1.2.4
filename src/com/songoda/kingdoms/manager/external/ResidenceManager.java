package com.songoda.kingdoms.manager.external;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ResidenceManager.ChunkRef;

public class ResidenceManager extends Manager implements Listener{

	private static com.bekvon.bukkit.residence.Residence residence;
	
	protected ResidenceManager(Plugin plugin) {
		super(plugin);
		
		residence = (com.bekvon.bukkit.residence.Residence) plugin.getServer().getPluginManager().getPlugin("Residence");
		if (residence != null) {
			Kingdoms.logInfo("Residence Hooked!");
			Kingdoms.logInfo("Version: " + residence.getDescription().getVersion());
			if(residence.isEnabled()){
			}else{
				Kingdoms.logInfo("Residence is not enabled!");
				Kingdoms.logInfo("Disabled support for Residence.");						
			}
		}
	
	
	}
	
	@EventHandler
	public void onResidenceClaim(ResidenceCreationEvent event){
		if(event.getPlayer().isOp()) return;
		
		for(ChunkRef cf:event.getPhysicalArea().getChunks()){
			//Chunk c = Bukkit.getWorld(event.getResidence().getWorld()).getChunkAt(cf.getChunkCoord(val));
		}
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	public static boolean canBuild(Player p, Location loc)
	{
		return true;
		
	}
	
	public static boolean isInRegion(Location loc)
	{
		if(residence != null){
			com.bekvon.bukkit.residence.protection.ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(loc);
			if(res != null){
				return true;
			}
		}
		return false;
		
	}

}
