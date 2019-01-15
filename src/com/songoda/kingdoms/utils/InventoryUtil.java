package com.songoda.kingdoms.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
	

	public static boolean hasEnough(Player p, Material mat, int amount){
		int amt = 0;
		for(ItemStack item:p.getInventory()){
			if(item == null){
				continue;
			}
			if(item.getType().equals(mat)){
				amt += item.getAmount();
				if(amt >= amount){
					return true;
				}
			}
		}
		return false;
	}
	
	public static void removeMaterial(Player p, Material mat, int amount){
		int amt = 0;
		amt += amount;
		for(ItemStack item:p.getInventory()){
			if(item == null){
				continue;
			}
			if(item.getType().equals(mat)){
				amt -= item.getAmount();
				p.getInventory().remove(item);
				p.updateInventory();
				if(amt <= 0){
					break;
				}
			}
		}
		if(amt < 0){
			p.getInventory().addItem(new ItemStack(mat, amt*-1));
		}
		p.updateInventory();
	}

}
