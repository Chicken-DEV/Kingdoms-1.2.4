package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class CitizensManager extends Manager {

	private net.citizensnpcs.Citizens citizens;
	protected CitizensManager(Plugin plugin) {
		super(plugin);

		citizens = (net.citizensnpcs.Citizens) plugin.getServer().getPluginManager().getPlugin("Citizens");
		if(citizens != null){
			Kingdoms.logInfo("Citizens Hooked!");
			Kingdoms.logInfo("Version: " + citizens.getDescription().getVersion());
		}
	
	}

	public boolean isCitizen(Entity e){
		if(e == null) return false;
		if(e.hasMetadata("NPC")) return true;
		if(citizens == null) return false;
		if(citizens.getNPCRegistry().isNPC(e)) return true;
		return false;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
