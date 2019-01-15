package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.songoda.kingdoms.main.Kingdoms;

public class ScrollerInventory implements Listener{
	
	public ArrayList<Inventory> pages = new ArrayList<Inventory>();
	public UUID id;
	public int currpage = 0;
	public static HashMap<UUID, ScrollerInventory> users = new HashMap<UUID, ScrollerInventory>();
	
	public ScrollerInventory(ArrayList<ItemStack> items, String originalname, Player p){
		this.id = UUID.randomUUID();
		String name = originalname;
		if(originalname.length() > 32){
			name = originalname.substring(0, 32);
		}
		
		Inventory page = getBlankPage(name);
		
		for(int i = 0;i < items.size(); i++){
			if(page.firstEmpty() == 46){
				pages.add(page);
				page = getBlankPage(name);
				page.addItem(items.get(i));
			}else{
				page.addItem(items.get(i));
			}
		}
		pages.add(page);
		p.openInventory(pages.get(currpage));
		users.put(p.getUniqueId(), this);
	}
	

	
	
	
	private Inventory getBlankPage(String name){
		Inventory page = Bukkit.createInventory(null, 54, name);
		
		ItemStack nextpage =  new ItemStack(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), 1, (byte) 5);
		ItemMeta meta = nextpage.getItemMeta();
		meta.setDisplayName(Kingdoms.getLang().getString("Guis_NextPage"));
		nextpage.setItemMeta(meta);
		
		ItemStack prevpage = new ItemStack(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), 1, (byte) 2);
		meta = prevpage.getItemMeta();
		meta.setDisplayName(Kingdoms.getLang().getString("Guis_PreviousPage"));
		prevpage.setItemMeta(meta);
		
		
		page.setItem(53, nextpage);
		page.setItem(45, prevpage);
		return page;
	}
}
