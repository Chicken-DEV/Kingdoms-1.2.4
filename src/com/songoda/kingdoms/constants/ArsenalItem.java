package com.songoda.kingdoms.constants;

import java.util.ArrayList;

import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ArsenalItem {
	
	TURRET_BREAKER(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Title"),
			Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Desc"),
			ChatColor.RED + "X-0-X-0-X"),
	SIEGE_ROCKET(Kingdoms.getLang().getString("Arsenal_Item_Siege_Rocket_Title"),
			Kingdoms.getLang().getString("Arsenal_Item_Siege_Rocket_Desc"),
			ChatColor.GOLD + ">=XX=>");
	;
	String title;
	String desc;
	String unique;
	
	ArsenalItem(String title, String desc, String unique){
		this.title = title;
		this.desc = desc;
		this.unique = unique;
	}
	
	public ItemStack getDisk(){
		ItemStack item = new ItemStack(Materials.MUSIC_DISC_CAT.parseMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(LoreOrganizer.organize(new ArrayList<String>(){{
			add(desc);
			add(unique);
			}}));
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getShopDisk(){
		ItemStack item = new ItemStack(Materials.MUSIC_DISC_CAT.parseMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(LoreOrganizer.organize(new ArrayList<String>(){{
			add(desc);
			add(Kingdoms.getLang().getString("Guis_Cost_Text").replaceAll("%cost%", ""+getCost()));
			add(unique);
			}}));
		item.setItemMeta(meta);
		return item;
	}
	
	public int getCost(){
		switch(this){
		case SIEGE_ROCKET:
			return Config.getConfig().getInt("cost.arsenal.siegerocket");
		case TURRET_BREAKER:
			return Config.getConfig().getInt("cost.arsenal.turretbreaker");
		
		}
		return 0;
	}
	public boolean isEnabled(){
		switch(this){
		case SIEGE_ROCKET:
			return Config.getConfig().getBoolean("enable.arsenal.siegerocket");
		case TURRET_BREAKER:
			return Config.getConfig().getBoolean("enable.arsenal.turretbreaker");
		
		}
		return false;
	}

	public Object getUnique() {
		return unique;
	}

}
