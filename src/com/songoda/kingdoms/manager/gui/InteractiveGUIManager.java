
package com.songoda.kingdoms.manager.gui;

import java.util.HashMap;
import java.util.UUID;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;


public class InteractiveGUIManager extends Manager {

	protected static HashMap<UUID, InteractiveGUI> guis = new HashMap<UUID, InteractiveGUI>();
	
	protected InteractiveGUIManager(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event){
		Player p = (Player) event.getWhoClicked();
		if(event.getCurrentItem() == null) return;
		if(!guis.containsKey(p.getUniqueId())) return;
		event.setCancelled(true);
		if(event.getRawSlot() >= event.getInventory().getSize()) return;
		if(guis.get(p.getUniqueId()).getAction(event.getSlot()) == null) return;
		guis.get(p.getUniqueId()).getAction(event.getSlot()).run();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		guis.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public void onDisable() {
	}

}
