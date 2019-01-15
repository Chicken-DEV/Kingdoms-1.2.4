package com.songoda.kingdoms.manager.gui;


import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.TurretType;
import com.songoda.kingdoms.main.Kingdoms;

public class TurretGUIManager extends Manager {

	protected TurretGUIManager(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	public void openMenu(KingdomPlayer kp){
		InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Guis_Turret_Title", kp.getLang()), 27);
		int i = 0;
		for(TurretType type:TurretType.values()){
			if(!type.isEnabled()) continue;
			gui.setAction(i, new Runnable(){
				@Override
				public void run(){
					Kingdom kingdom = kp.getKingdom();
					int cost = type.getCost();
					
					if(type == TurretType.ARROW &&
							kingdom.getTurretUpgrades().isSimplifiedModel()){
						cost = (int) (cost*0.7);
					}
					
					if(kingdom.getResourcepoints() - cost < 0){
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
						return;
					}
					
					if(kp.getPlayer().getInventory().firstEmpty() == -1){
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Inventory_Full", kp.getLang()));
						
						return;
					}
					
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					kp.getPlayer().getInventory().addItem(type.getTurretDisk());
					openMenu(kp);
				}
			});
			gui.getInventory().setItem(i, type.getGUITurretDisk());
			i++;
		}
		

		ItemStack i11 = new ItemStack(Material.BOOK);
		ItemMeta i11m = i11.getItemMeta();
		i11m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_Title", kp.getLang()));
		ArrayList<String> i11l = new ArrayList<String>();
		i11l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Lore", kp.getLang()));
		i11m.setLore(LoreOrganizer.organize(i11l));
		i11.setItemMeta(i11m);
		
		
		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(Kingdoms.getLang().getString("Guis_ResourcePoints_Title", kp.getLang()));
		ArrayList rl = new ArrayList();
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Desc", kp.getLang()));
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Count", kp.getLang()).replaceAll("%amount%", ""+kp.getKingdom().getResourcepoints()));
		rm.setLore(LoreOrganizer.organize(rl));
		r.setItemMeta(rm);
		
		ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta backbtnmeta = backbtn.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "" + ChatColor.YELLOW + "" + ChatColor.GREEN);
		backbtnmeta.setLore(lore);
		backbtnmeta.setDisplayName(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn", kp.getLang()));
		backbtn.setItemMeta(backbtnmeta);
		

		gui.getInventory().setItem(9, i11);
		gui.setAction(9, new Runnable(){
			@Override
			public void run(){
				Kingdoms.getGuiManagement().getTurretUpgradeGUIManager().openMenu(kp);
			}
		});
		
		gui.getInventory().setItem(17, r);
		gui.getInventory().setItem(26, backbtn);
		
		gui.openInventory(kp.getPlayer());
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
