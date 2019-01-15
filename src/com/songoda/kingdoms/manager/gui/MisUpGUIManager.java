package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.MiscUpgrade;
import com.songoda.kingdoms.constants.kingdom.MisupgradeInfo;
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

public class MisUpGUIManager extends Manager implements Listener{
	protected MisUpGUIManager(Plugin plugin) {
		super(plugin);
		
		
	}
	
	public Runnable getClickRunnable(KingdomPlayer kp, Kingdom kingdom, MiscUpgrade upgrade){

		return new Runnable(){
			@Override
			public void run(){
				if(kingdom.getMisupgradeInfo().isBought(upgrade)){
					if(kingdom.getMisupgradeInfo().isEnabled(upgrade)){
						kingdom.getMisupgradeInfo().setEnabled(upgrade, false);
					}else{
						kingdom.getMisupgradeInfo().setEnabled(upgrade, true);
						
					}
					openMenu(kp);
					return;
				}
				int cost = upgrade.getCost();
				if(kingdom.getResourcepoints() - cost < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
				
				
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getMisupgradeInfo().setBought(upgrade, true);
				kingdom.getMisupgradeInfo().setEnabled(upgrade, true);

				openMenu(kp);
			}
		};
	}

	public void setItemToGUI(int slot, InteractiveGUI gui, KingdomPlayer kp, MisupgradeInfo info, MiscUpgrade upgrade){
		ItemStack i1 = new ItemStack(upgrade.getDisplay());
		ItemMeta i1m = i1.getItemMeta();
		i1m.setDisplayName(upgrade.getTitle());
		ArrayList i1l = new ArrayList();
		i1l.add(ChatColor.GREEN+upgrade.getDesc());
		if (info.isBought(upgrade)) {
			if(info.isEnabled(upgrade)){
				i1l.add(Kingdoms.getLang().getString("Guis_Misc_ToggledOn", kp.getLang()));
			}else{
				i1l.add(Kingdoms.getLang().getString("Guis_Misc_ToggledOff", kp.getLang()));
			}
		} else {
			int cost = upgrade.getCost();
			i1l.add(Kingdoms.getLang().getString("Guis_Cost_Text", kp.getLang()).replaceAll("%cost%", "" + cost));
		}
		i1m.setLore(LoreOrganizer.organize(i1l));
		i1.setItemMeta(i1m);
		if(!upgrade.isConfigEnabled()){
			i1  = new ItemStack(Material.AIR);
			if(info.isBought(upgrade)){
				info.setBought(upgrade,false);
				kp.getKingdom().setResourcepoints(kp.getKingdom().getResourcepoints() + 
						upgrade.getCost());
			}
		}
		gui.getInventory().setItem(slot, i1);
		gui.setAction(slot, getClickRunnable(kp, kp.getKingdom(), upgrade));
		
	}
	
	public void openMenu(KingdomPlayer kp) {
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		Kingdoms.logDebug("has kingdom");
		String cost = "0";
		MisupgradeInfo info = kingdom.getMisupgradeInfo();
		InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Guis_Nexus_MiscUpgrades_Title", kp.getLang()), 27);
		setItemToGUI(0, gui, kp, info, MiscUpgrade.ANTITRAMPLE);
		setItemToGUI(1, gui, kp, info, MiscUpgrade.ANTICREEPER);
		setItemToGUI(2, gui, kp, info, MiscUpgrade.NEXUSGUARD);
		setItemToGUI(3, gui, kp, info, MiscUpgrade.GLORY);
		setItemToGUI(4, gui, kp, info, MiscUpgrade.BOMBSHARDS);
		setItemToGUI(5, gui, kp, info, MiscUpgrade.PSIONICCORE);
		
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
