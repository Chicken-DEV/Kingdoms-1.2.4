package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class GriefPreventionManager extends Manager {

	protected GriefPreventionManager(Plugin plugin) {
		super(plugin);

		griefPrevention = (GriefPrevention) plugin.getServer().getPluginManager().getPlugin("GriefPrevention");
		if (griefPrevention != null) {
			Kingdoms.logInfo("GriefPrevention Hooked!");
			Kingdoms.logInfo("Version: " + griefPrevention.getDescription().getVersion());
			if(griefPrevention.isEnabled()){
			}else{
				Kingdoms.logInfo("GriefPrevention is not enabled!");
				Kingdoms.logInfo("Disabled support for GriefPrevention.");						
			}
		}
	
	}


	private static GriefPrevention griefPrevention;
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	public GriefPrevention getGriefPrevention() {
		return griefPrevention;
	}
	
	public boolean canBuild(Player p, Location loc){
		
		if(isInRegion(loc)) return false;
		
		return true;
	}

	public static boolean isInRegion(Location loc)
	{
		if(griefPrevention != null){
			
			Claim claim = griefPrevention.dataStore.getClaimAt(loc, false, null);
            if(claim == null)
            {
                return false;
            }
            return true;
			
		}
		
		
		return false;
	}

}
