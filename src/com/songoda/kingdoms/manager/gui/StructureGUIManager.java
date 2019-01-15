package com.songoda.kingdoms.manager.gui;
import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;

public class StructureGUIManager extends Manager implements Listener{

	protected StructureGUIManager(Plugin plugin) {
		super(plugin);
	}
	
	public void addStructureToGUI(InteractiveGUI gui, int slot, StructureType type, KingdomPlayer kp){
		if(!type.isEnabled()) return;
		ItemStack i1 = new ItemStack(Materials.MUSIC_DISC_BLOCKS.parseMaterial());
		ItemMeta i1m = i1.getItemMeta();
		i1m.setDisplayName((type.getTitle()));
		ArrayList i1l = new ArrayList();
		i1l.add(type.getDesc());
		i1l.add(Kingdoms.getLang().getString("Structures_Placement_Instructions", kp.getLang()));
		String cost = ""+type.getCost();
		i1l.add(Kingdoms.getLang().getString("Guis_Cost_Text", kp.getLang()).replaceAll("%cost%", cost));
		//i1l.add(ChatColor.LIGHT_PURPLE + "Placable Structure");
		i1m.setLore(LoreOrganizer.organize(i1l));
		i1.setItemMeta(i1m);
		if(!type.isEnabled()){
			i1  = new ItemStack(Material.AIR);
		}
		gui.getInventory().setItem(slot, i1);
		gui.setAction(slot, new Runnable(){
			@Override
			public void run(){
				
				Kingdom kingdom = kp.getKingdom();
				int cost = type.getCost();
				if(kingdom.getResourcepoints() - cost < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
				
				if(kp.getPlayer().getInventory().firstEmpty() == -1){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Inventory_Full", kp.getLang()));
					return;
				}
				
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				
				ItemStack IS = type.getDisk();
				kp.getPlayer().getInventory().addItem(IS);
			
				
			}
		});
	}

	public void openMenu(KingdomPlayer kp) {
		Kingdom kingdom = kp.getKingdom();
		InteractiveGUI gui = new InteractiveGUI(ChatColor.AQUA + "Structure Shop", 27);
		addStructureToGUI(gui, 0, StructureType.POWERCELL, kp);
		addStructureToGUI(gui, 1, StructureType.OUTPOST, kp);
		addStructureToGUI(gui, 2, StructureType.EXTRACTOR, kp);
		addStructureToGUI(gui, 3, StructureType.WARPPAD, kp);
		addStructureToGUI(gui, 4, StructureType.REGULATOR, kp);
		addStructureToGUI(gui, 5, StructureType.RADAR, kp);
		addStructureToGUI(gui, 6, StructureType.ARSENAL, kp);
		//addStructureToGUI(gui, 7, StructureType.SIEGEENGINE, kp);
		
		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(Kingdoms.getLang().getString("Guis_ResourcePoints_Title", kp.getLang()));
		ArrayList rl = new ArrayList();
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Desc", kp.getLang()));
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Count", kp.getLang()).replaceAll("%amount%", ""+kingdom.getResourcepoints()));
		rm.setLore(LoreOrganizer.organize(rl));
		r.setItemMeta(rm);

		ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta backbtnmeta = backbtn.getItemMeta();
		backbtnmeta.setDisplayName(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn", kp.getLang()));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "" + ChatColor.YELLOW + "" + ChatColor.GREEN);
		backbtnmeta.setLore(lore);
		backbtn.setItemMeta(backbtnmeta);
		
		gui.getInventory().setItem(17, r);
		gui.getInventory().setItem(26, backbtn);
		gui.openInventory(kp.getPlayer());

	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
