package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.StructureManager;

public class OutpostGUIManager extends Manager implements Listener {

	protected OutpostGUIManager(Plugin plugin) {
		super(plugin);
	}


	@EventHandler
	public void onOutpostClickButton(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getItemMeta() == null) return;
		if(e.getCurrentItem().getItemMeta().getLore() == null) return;
		if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
		if(kp.getKingdom() == null) return;
		
		Kingdom kingdom = kp.getKingdom();
		if(e.getInventory().getName() == null) return;
		if(!e.getInventory().getName().equals(ChatColor.AQUA + kp.getKingdom().getKingdomName() + "'s Outpost")) return;
		e.setCancelled(true);
		
		List<String> lores = e.getCurrentItem().getItemMeta().getLore();
		String displayName = e.getCurrentItem().getItemMeta().getDisplayName();

		if(lores.contains(ChatColor.LIGHT_PURPLE + "Outpost Function")){
			if(displayName.equals(Kingdoms.getLang().getString("Guis_Outpost_OpenNexus_Title", kp.getLang()))){
				if(kingdom.getNexus_loc() == null){
					kp.getPlayer().closeInventory();
					kp.sendMessage(Kingdoms.getLang().getString("Guis_Outpost_OpenNexus_No_Nexus", kp.getLang()));
					return;
				}
				if(kingdom.getNexus_loc().getBlock().getType() != Materials.BEACON.parseMaterial()){
					kp.getPlayer().closeInventory();
					kp.sendMessage(Kingdoms.getLang().getString("Guis_Outpost_OpenNexus_No_Nexus", kp.getLang()));
					return;
				}
				GUIManagement.getNexusGUIManager().openNexusGui(kp);
				return;
			}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Outpost_Buy1XpPotion_Title", kp.getLang()))){
				if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuyXpBottles())){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", "" + kingdom.getPermissionsInfo().getBuyXpBottles().toString()));
					return;
				}
				int cost = Config.getConfig().getInt("cost.outpost.xpbottle");
				
				if (kp.getPlayer().getInventory().firstEmpty() == -1) {
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Inventory_Full", kp.getLang()));
					return;
				}
				
				if(kingdom.getResourcepoints() - cost < 0){
					kp.sendMessage(ChatColor.RED + "Not enought RP.");
					kp.sendMessage(ChatColor.GOLD+"Required: "+cost);
					return;
				}
				
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				
				kp.getPlayer().getInventory().addItem(new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial()));
			}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Outpost_Buy64XpPotion_Title", kp.getLang()))){
				if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuyXpBottles())){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", "" + kingdom.getPermissionsInfo().getBuyXpBottles().toString()));
					return;
				}
				int cost = Config.getConfig().getInt("cost.outpost.xpbottle")*64;
				
				if (kp.getPlayer().getInventory().firstEmpty() == -1) {
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Inventory_Full", kp.getLang()));
					return;
				}
				
				if(kingdom.getResourcepoints() - cost < 0){
					kp.sendMessage(ChatColor.RED + "Not enought RP.");
					kp.sendMessage(ChatColor.GOLD+"Required: "+cost);
					return;
				}
				
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				
				kp.getPlayer().getInventory().addItem(new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial(), 64));
			}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Title", kp.getLang()))){
				renameLand(kp, StructureManager.selected.get(kp));
				return;
			}
			
			openMenu(kp);
		}
	}
	
	public static ArrayList<UUID> isRenaming = new ArrayList<UUID>();
	public static void renameLand(KingdomPlayer kp, Land land){
		kp.getPlayer().closeInventory();
		kp.sendMessage(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Message", kp.getLang()));
		if(!isRenaming.contains(kp.getUuid()))isRenaming.add(kp.getUuid());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatNewName(AsyncPlayerChatEvent event){
		if(isRenaming.contains(event.getPlayer().getUniqueId())){
			isRenaming.remove(event.getPlayer().getUniqueId());
			Land target = StructureManager.selected.get(Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()));
			target.setName(event.getMessage());
			event.getPlayer().sendMessage(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Successful", Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()).getLang()));
			//openMenu(Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()));
			event.setCancelled(true);
		}
	}
	
	public void openMenu(KingdomPlayer kp) {
		Kingdom kingdom = kp.getKingdom();
		Land land = StructureManager.selected.get(kp);
		Inventory outpostinv = Bukkit.createInventory(null, 27,
				ChatColor.AQUA + kingdom.getKingdomName() + "'s Outpost");

		ItemStack i3 = new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial());
		ItemMeta i3m = i3.getItemMeta();
		i3m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_Buy1XpPotion_Title", kp.getLang()));
		ArrayList i3l = new ArrayList();
		i3l.add(Kingdoms.getLang().getString("Guis_Outpost_Buy1XpPotion_Desc", kp.getLang()).replaceAll("%cost%",
				"" + Config.getConfig().getInt("cost.outpost.xpbottle")));
		i3l.add(ChatColor.LIGHT_PURPLE + "Outpost Function");
		i3m.setLore(LoreOrganizer.organize(i3l));
		i3.setItemMeta(i3m);

		ItemStack i5 = new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial());
		ItemMeta i5m = i5.getItemMeta();
		i5m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_Buy64XpPotion_Title", kp.getLang()));
		ArrayList i5l = new ArrayList();
		i5l.add(Kingdoms.getLang().getString("Guis_Outpost_Buy64XpPotion_Desc", kp.getLang()).replaceAll("%cost%",
				"" + Config.getConfig().getInt("cost.outpost.xpbottle") * 64));
		i5l.add(ChatColor.LIGHT_PURPLE + "Outpost Function");
		i5m.setLore(LoreOrganizer.organize(i5l));
		i5.setItemMeta(i5m);

		ItemStack i6 = new ItemStack(Material.PAPER);
		ItemMeta i6m = i6.getItemMeta();
		i6m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Title", kp.getLang()));
		ArrayList i6l = new ArrayList();
		i6l.add(ChatColor.GREEN + Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Desc", kp.getLang()).replaceAll("%name%", land.getName()));
		i6l.add(ChatColor.LIGHT_PURPLE + "Outpost Function");
		i6m.setLore(LoreOrganizer.organize(i6l));
		i6.setItemMeta(i6m);

		ItemStack i4 = new ItemStack(Material.BEACON);
		ItemMeta i4m = i4.getItemMeta();
		i4m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_OpenNexus_Title", kp.getLang()));
		if(kingdom.getNexus_loc() == null){
			i4m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_Nexus_Unavailable", kp.getLang()));
		}
		ArrayList i4l = new ArrayList();
		i4l.add(ChatColor.LIGHT_PURPLE + "Outpost Function");
		i4m.setLore(LoreOrganizer.organize(i4l));
		i4.setItemMeta(i4m);

		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(Kingdoms.getLang().getString("Guis_ResourcePoints_Title", kp.getLang()));
		ArrayList rl = new ArrayList();
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Desc", kp.getLang()));
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Count", kp.getLang()).replaceAll("%amount%", ""+kingdom.getResourcepoints()));
		rm.setLore(LoreOrganizer.organize(rl));
		r.setItemMeta(rm);

		outpostinv.setItem(17, r);

		outpostinv.addItem(i3);
		outpostinv.addItem(i5);
		outpostinv.addItem(i6);
		outpostinv.addItem(i4);
		kp.getPlayer().openInventory(outpostinv);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
