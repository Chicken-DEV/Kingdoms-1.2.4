package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import com.connorlinfoot.titleapi.TitleAPI;

public class TitleAPIManager extends Manager {

	
	protected TitleAPIManager(Plugin plugin) {
		super(plugin);

		Kingdoms.logInfo("TitleAPI Hooked!");
		Kingdoms.logInfo("Version: " + Bukkit.getPluginManager().getPlugin("TitleAPI").getDescription().getVersion());

	
		
	}
	private static final int SEC = 20;
	public static void sendTitle(Player p, String title, String lore){
		TitleAPI.sendTitle(p, 1*SEC, 3*SEC, 1*SEC, title, lore);
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
