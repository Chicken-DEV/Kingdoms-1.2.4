package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class PlaceholderAPIManager extends Manager {

	protected PlaceholderAPIManager(Plugin plugin) {
		super(plugin);
		new PlaceholderHook((Kingdoms) plugin).hook();
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

}
