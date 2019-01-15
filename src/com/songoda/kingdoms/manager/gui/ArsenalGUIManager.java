package com.songoda.kingdoms.manager.gui;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.ArsenalItem;
import com.songoda.kingdoms.main.Kingdoms;

public class ArsenalGUIManager extends Manager implements Listener {

	protected ArsenalGUIManager(Plugin plugin) {
		super(plugin);
	}
	
	
	public void openMenu(KingdomPlayer kp) {
		
		InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Structures_Arsenal"), 9*1);
		int i = 0;
		for(ArsenalItem item:ArsenalItem.values()){
			if(!item.isEnabled()) continue;
			gui.getInventory().setItem(i, item.getShopDisk());
			gui.setAction(i, new Runnable(){

				@Override
				public void run() {
					Kingdom kingdom = kp.getKingdom();
					int cost = item.getCost();
					if(kingdom.getResourcepoints() - cost < 0){
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
						return;
					}
					
					if(kp.getPlayer().getInventory().firstEmpty() == -1){
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Inventory_Full", kp.getLang()));
						
						return;
					}
					
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					kp.getPlayer().getInventory().addItem(item.getDisk());
					openMenu(kp);
				}
				
			});
			i++;
		}

		gui.openInventory(kp.getPlayer());
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
