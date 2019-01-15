package com.songoda.kingdoms.manager.gui;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class ScrollerInventoryManager extends Manager {

	public ScrollerInventoryManager(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event){
		Player p = (Player) event.getWhoClicked();
		if(ScrollerInventory.users.containsKey(p.getUniqueId())){
			ScrollerInventory inv = ScrollerInventory.users.get(p.getUniqueId());
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getItemMeta() != null){
					if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
					if(event.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_NextPage"))){
						event.setCancelled(true);
						if(inv.currpage >= inv.pages.size()-1){
							return;
						}else{
							inv.currpage += 1;
							p.openInventory(inv.pages.get(inv.currpage));
						}
					}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_PreviousPage"))){
						event.setCancelled(true);
						if(inv.currpage > 0){
							inv.currpage -= 1;
							p.openInventory(inv.pages.get(inv.currpage));
						}
					}
				}
			}
		}
	}
	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
