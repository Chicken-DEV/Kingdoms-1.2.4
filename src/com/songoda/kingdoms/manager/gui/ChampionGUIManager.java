package com.songoda.kingdoms.manager.gui;


import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.ChampionInfo;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.champion.ChampionUpgrade;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class ChampionGUIManager extends Manager implements Listener {

	protected ChampionGUIManager(Plugin plugin) {
		super(plugin);
	}

	public void upgrade(KingdomPlayer kp, Kingdom kingdom, ChampionUpgrade upgrade, int levels){
		int cost = upgrade.getUpgradeCost(upgrade);
		int max = upgrade.getUpgradeMax(upgrade);
		if(kingdom.getResourcepoints() - cost < 0){
			if(kp != null)
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
			return;
		}
		
		if(kingdom.getChampionInfo().getUpgradeLevel(upgrade) + levels > max){
			if(kp != null)
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Level_Reached", kp.getLang()));
			return;
		}
		
		kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
		kingdom.getChampionInfo().setUpgradeLevel(upgrade, kingdom.getChampionInfo().getUpgradeLevel(upgrade) + levels);
	}
	
	public void addItemToGUI(InteractiveGUI gui, KingdomPlayer kp, Kingdom kingdom, ChampionUpgrade upgrade, int slot){
		ChampionInfo info = kingdom.getChampionInfo();
		ItemStack item = new ItemStack(upgrade.getDisplay());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + upgrade.getTitle());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + upgrade.getDesc());
		if(upgrade.isToggle()){
			if(info.getUpgradeLevel(upgrade) >= 1){
				lore.add(ChatColor.RED + upgrade.getCurr().replaceAll("%level%", "" + true));
			}else{
				lore.add(ChatColor.RED + upgrade.getCurr().replaceAll("%level%", "" + false));
			}
		}else{
			lore.add(ChatColor.RED + upgrade.getCurr().replaceAll("%level%", "" + info.getUpgradeLevel(upgrade)));
		}
		lore.add(ChatColor.RED + Kingdoms.getLang().getString("Guis_Cost_Text", kp.getLang()).replaceAll("%cost%", "" + upgrade.getUpgradeCost(upgrade)));
		meta.setLore(LoreOrganizer.organize(lore));
		item.setItemMeta(meta);
		gui.getInventory().setItem(slot, item);
		gui.setAction(slot, new Runnable(){
			@Override
			public void run(){
				upgrade(kp, kingdom, upgrade, upgrade.getLevels());
				openMenu(kp);
			}
		});
	}

	public void openMenu(KingdomPlayer kp) {
		{
			Kingdom kingdom = kp.getKingdom();
			InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Guis_ChampionUpgrades_Title", kp.getLang()), 54);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.HEALTH,0);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.HEALTHII,9);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.RESISTANCE,18);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.DAMAGE_CAP,27);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.ARMOR,36);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.MIMIC,45);

			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.WEAPON,1);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.THOR,10);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.DRAG,19);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.MOCK,28);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.DEATH_DUEL,37);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.STRENGTH,46);

			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.SPEED,2);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.PLOW,11);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.REINFORCEMENTS,20);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.FOCUS,29);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.AQUA,38);
			
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.DETERMINATION,3);
			addItemToGUI(gui, kp,kingdom, ChampionUpgrade.DETERMINATIONII,12);
			
			
			
			ItemStack r = new ItemStack(Material.HAY_BLOCK);
			ItemMeta rm = r.getItemMeta();
			rm.setDisplayName(Kingdoms.getLang().getString("Guis_ResourcePoints_Title", kp.getLang()));
			ArrayList<String> rl = new ArrayList<String>();
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

			gui.getInventory().setItem(44, r);
			gui.getInventory().setItem(53, backbtn);

			gui.openInventory(kp.getPlayer());
		}
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
