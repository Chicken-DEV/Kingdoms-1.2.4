package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Extractor;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.StructureManager;

public class ExtractorGUIManager extends Manager implements Listener {

	protected ExtractorGUIManager(Plugin plugin) {
		super(plugin);
	}


	@EventHandler
	public void onExtractorClickButton(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getItemMeta() == null) return;
		if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
		if(kp.getKingdom() == null) return;

		Kingdom kingdom = kp.getKingdom();
		if(e.getInventory().getName() == null) return;
		if(e.getInventory().getName().equals(ChatColor.AQUA + "Extractor")){
			e.setCancelled(true);
			List<String> lores = e.getCurrentItem().getItemMeta().getLore();
			String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
			if(displayName != null){
				e.setCancelled(true);
			}else{
				return;
			}
			if(displayName.equals(Kingdoms.getLang().getString("Guis_Extractor_Collect", kp.getLang()).replaceAll("%amount%", "" +Config.getConfig().getInt("mine.reward-amount")))){
				Extractor extractor = StructureManager.extractors.get(kp.getUuid());
				if(!extractor.isReady()){
					openExtractorMenu(extractor,kp);
					return;
				}
				extractor.collect();
				openExtractorMenu(extractor,kp);
				return;
			}else{
				Extractor extractor = StructureManager.extractors.get(kp.getUuid());
				openExtractorMenu(extractor,kp);
				
			}
		}
		
	}



	public void openExtractorMenu(Extractor extractor, KingdomPlayer kp) {
		Kingdom kingdom = kp.getKingdom();

		Inventory extactorinv = Bukkit.createInventory(null, 9,
				ChatColor.AQUA + "Extractor");

		ItemStack i3 = new ItemStack(Material.WHEAT);
		ItemMeta i3m = i3.getItemMeta();
		i3m.setDisplayName(Kingdoms.getLang().getString("Guis_Extractor_Collect", kp.getLang()).replaceAll("%amount%", "" +Config.getConfig().getInt("mine.reward-amount")));
		i3.setItemMeta(i3m);

		if(extractor.getTimeLeft() > Config.getConfig().getInt("mine.reward-delay-in-minutes")){
			extractor.resetTime();
		}
		
		ItemStack i5 = new ItemStack(Materials.CLOCK.parseMaterial());
		ItemMeta i5m = i5.getItemMeta();
		i5m.setDisplayName(Kingdoms.getLang().getString("Guis_Extractor_TimeToNextCollection", kp.getLang()).replaceAll("%time%", "" + extractor.getTimeLeft()));
		i5.setItemMeta(i5m);

		ItemStack i6 = new ItemStack(Material.PAPER);
		ItemMeta i6m = i6.getItemMeta();
		i6m.setDisplayName(Kingdoms.getLang().getString("Guis_Extractor_Refresh_Title", kp.getLang()));
		ArrayList i6l = new ArrayList();
		i6l.add(ChatColor.GREEN + Kingdoms.getLang().getString("Guis_Extractor_Refresh_Desc", kp.getLang()));
		i6m.setLore(LoreOrganizer.organize(i6l));
		i6.setItemMeta(i6m);
		
		if(extractor.isReady()){
			extactorinv.addItem(i3);
		}else{
		extactorinv.addItem(i5);
		}
		extactorinv.addItem(i6);
		kp.getPlayer().openInventory(extactorinv);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
