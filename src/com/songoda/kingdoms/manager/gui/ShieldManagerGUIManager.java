package com.songoda.kingdoms.manager.gui;


import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class ShieldManagerGUIManager extends Manager {

	protected ShieldManagerGUIManager(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	public void openMenu(KingdomPlayer kp){
		InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Guis_Turret_Title", kp.getLang()), 27);
		Kingdom kingdom = kp.getKingdom();
		ItemStack shield = new ItemStack(Material.OBSIDIAN);
		ItemMeta shieldm = shield.getItemMeta();
		shieldm.setDisplayName(Kingdoms.getLang().getString("Guis_Nexus_Shield_Title", kp.getLang()));
		ArrayList shieldl = new ArrayList();
		int value = kingdom.getShieldValue();
		int max = kingdom.getShieldMax();
		
		shieldl.add(Kingdoms.getLang().getString("Guis_Nexus_Shield_Value", kp.getLang())
				.replaceAll("%value%",""+value)
				.replaceAll("%max%",""+max));
		if(value < max){
			shieldl.add(Kingdoms.getLang().getString("Guis_Shield_Click_To_Recharge", kp.getLang())
					.replaceAll("%cost%",""+kingdom.getShieldRechargeCost())
					.replaceAll("%amt%",""+kingdom.getShieldRecharge()));
		}else{
			shieldl.add(Kingdoms.getLang().getString("Guis_Nexus_Shield_Recharge_Amount", kp.getLang())
					.replaceAll("%amt%",""+kingdom.getShieldRecharge()));
			shieldl.add(Kingdoms.getLang().getString("Guis_Nexus_Shield_Recharge_Cost", kp.getLang())
					.replaceAll("%cost%",""+kingdom.getShieldRechargeCost()));
		}
		
		if(kingdom.isRechargingShield()){
			shieldl.add(ChatColor.RED + TimeUtils.parseTimeMillis(kingdom.getTimeLeft(Kingdom.RECHARGE_COOLDOWN)));
		}
		shieldm.setLore(LoreOrganizer.organize(shieldl));
		shield.setItemMeta(shieldm);
		gui.getInventory().setItem(0, shield);
		gui.setAction(0, new Runnable(){
			@Override
			public void run(){
				//Recharge
			}
		});
		
		
		
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
		

		
		gui.getInventory().setItem(17, r);
		gui.getInventory().setItem(26, backbtn);
		
		gui.openInventory(kp.getPlayer());
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
