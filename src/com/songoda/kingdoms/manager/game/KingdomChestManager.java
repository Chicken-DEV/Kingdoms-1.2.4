package com.songoda.kingdoms.manager.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.KingdomChest;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class KingdomChestManager extends Manager implements Listener{
	HashMap<KingdomPlayer, Kingdom> userChest = new HashMap<KingdomPlayer, Kingdom>();
	protected KingdomChestManager(Plugin plugin) {
		super(plugin);
	}
	
	/**
	 * open chest inventory to specified kp
	 * @param kp KingdomPlayer (check if player is in kingdom. It doesn't test null)
	 * @return true - success, false - someone is using chest
	 */
	public boolean useKingdomChest(KingdomPlayer kp, Kingdom kingdom){
		KingdomChest chest = kingdom.getKingdomChest();
		if(chest.getUsing() != null) return false;
		chest.setUsing(kp);
		userChest.put(kp, kingdom);
		Inventory kchest = Bukkit.createInventory(null, 27,
			     Kingdoms.getLang().getString("Guis_Nexus_KingdomChest_Title"));
		if(kp.getKingdom() != null)kchest = Bukkit.createInventory(null, kp.getKingdom().getChestsize(),
		     Kingdoms.getLang().getString("Guis_Nexus_KingdomChest_Title"));
		if(chest.getInv() != null){
			for(ItemStack item:chest.getInv()) kchest.addItem(item);
		}
			//kchest.setContents(chest.getInv().toArray(new ItemStack[chest.getInv().size()]));
		
		kp.getPlayer().openInventory(kchest);
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKingdomChestClose(InventoryCloseEvent e){
		String invName = e.getInventory().getName();
		if(invName.equals(Kingdoms.getLang().getString("Guis_Nexus_KingdomChest_Title"))){
			if(!(e.getPlayer() instanceof Player)) return;
			Kingdoms.logDebug("instance");
			KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getPlayer());
			
			//if(kp.getKingdom() == null) return;
			if(userChest.get(kp).getKingdomChest() == null)return;
			Kingdoms.logDebug("kingdomchest not null");
			KingdomChest chest = userChest.get(kp).getKingdomChest();
			
			List<ItemStack> ISs = new ArrayList<ItemStack>();
			for(ItemStack IS : e.getInventory().getContents()){
				if(IS == null) continue;
				
				ISs.add(IS);
			}
			
			chest.setInv(ISs);
			chest.setUsing(null);
			
			userChest.remove(kp);
		}
	}

	@Override
	public void onDisable() {
		
	}



}
