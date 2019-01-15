package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class ActionbarAPIManager extends Manager {

	
	protected ActionbarAPIManager(Plugin plugin) {
		super(plugin);

		Kingdoms.logInfo("ActionbarAPI Hooked!");
		Kingdoms.logInfo("Version: " + Bukkit.getPluginManager().getPlugin("ActionBarAPI").getDescription().getVersion());

	
		
	}
	
	public static void sendActionBar(Player p, String message){
		ActionBarAPI.sendActionBar(p, message);
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
