package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.PermissionsInfo;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class PermissionsGUIManager extends Manager implements Listener {
	
	protected PermissionsGUIManager(Plugin plugin) {
		super(plugin);
		
		
	}

	public ItemStack getPermissionMaterial(Rank rank){
		ItemStack wool = new ItemStack(Materials.GRAY_WOOL.parseMaterial(), 1, DyeColor.GRAY.getWoolData());
		if(rank == Rank.ALL){
			wool = new ItemStack(Materials.GREEN_WOOL.parseMaterial(), 1, DyeColor.GREEN.getWoolData());
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_RankAll"));
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_Click_To_Change"));
			return wool;
		}else if(rank == Rank.MODS){
			wool = new ItemStack(Materials.BLUE_WOOL.parseMaterial(), 1, DyeColor.BLUE.getWoolData());
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_RankMod"));
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_Click_To_Change"));
			return wool;
		}else if(rank == Rank.GENERALS){
			wool = new ItemStack(Materials.YELLOW_WOOL.parseMaterial(), 1, DyeColor.YELLOW.getWoolData());
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_RankGeneral"));
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_Click_To_Change"));
			return wool;
		}else if(rank == Rank.KING){
			wool = new ItemStack(Materials.RED_WOOL.parseMaterial(), 1, DyeColor.RED.getWoolData());
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_RankKing"));
			addLore(wool, Kingdoms.getLang().getString("Guis_Permissions_Click_To_Change"));
			return wool;
		}
		return wool;
	}
	
	@EventHandler
	public void onPermissionsClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		if(event.getInventory().getName() == null) return;
		if(!event.getInventory().getName().equals(Kingdoms.getLang().getString("Guis_Permissions_Title"))) return;
		event.setCancelled(true);
		
		if(event.getCurrentItem() == null) return;
		if(event.getCurrentItem().getItemMeta() == null) return;
		if(event.getCurrentItem().getItemMeta().getLore() == null) return;
		if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		if(!event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "Permission"))
			return;
		
		ItemStack wool = event.getCurrentItem();
		String displayName = wool.getItemMeta().getDisplayName();
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) event.getWhoClicked());
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		
		Rank changeTo = Rank.ALL;
		if (wool.getData().getData() == DyeColor.GREEN.getWoolData()) {
			changeTo = Rank.MODS;
		} else if (wool.getData().getData() == DyeColor.BLUE.getWoolData()) {
			changeTo = Rank.GENERALS;
		} else if (wool.getData().getData() == DyeColor.YELLOW.getWoolData()) {
			changeTo = Rank.KING;
		} else if (wool.getData().getData() == DyeColor.RED.getWoolData()) {
			changeTo = Rank.ALL;
		} else if (wool.getData().getData() == DyeColor.GRAY.getWoolData()) {
			changeTo = Rank.ALL;
		}
		
		PermissionsInfo perm = kingdom.getPermissionsInfo();
		if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexus", kp.getLang()))){
			perm.setNexus(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Claim", kp.getLang()))){
			perm.setClaim(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Unclaim", kp.getLang()))){
			perm.setUnclaim(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Invade", kp.getLang()))){
			perm.setInvade(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_AllyFunc", kp.getLang()))){
			perm.setAlly(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Turret", kp.getLang()))){
			perm.setTurret(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Sethome", kp.getLang()))){
			perm.setSethome(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexuschest", kp.getLang()))){
			perm.setChest(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Protectedchestbypass", kp.getLang()))){
			perm.setOpenallchest(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Invite", kp.getLang()))){
			perm.setInvite(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Broadcast", kp.getLang()))){
			perm.setBroad(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Donate", kp.getLang()))){
			perm.setRPConvert(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Structures", kp.getLang()))){
			perm.setStructures(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexusbuild", kp.getLang()))){
			perm.setBuildInNexus(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_UseKHome", kp.getLang()))){
			perm.setUseKHome(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_OverrideRegulator", kp.getLang()))){
			perm.setOverrideRegulator(changeTo);
			openMenu(kp);
		}else if(displayName.equals(Kingdoms.getLang().getString("Guis_Permissions_Title_BuyXpBottles", kp.getLang()))){
			perm.setBuyXpBottles(changeTo);
			openMenu(kp);
		}
		
	}
	
	public void openMenu(KingdomPlayer kp){
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		Kingdoms.logDebug("has kingdom");
		
		if(!kp.getRank().isHigherOrEqualTo(Rank.KING)){
			kp.sendMessage(ChatColor.RED + "Only kingdom kings can edit permissions");
			return;
		}
		Kingdoms.logDebug("only kings pass");
		
	    Inventory champions = Bukkit.createInventory(null, 27, Kingdoms.getLang().getString("Guis_Permissions_Title", kp.getLang()));

	    PermissionsInfo perm = kingdom.getPermissionsInfo();
	    
	    ItemStack i1 = getPermissionMaterial(perm.getNexus());
	    ItemMeta i1m = i1.getItemMeta();
	    i1m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexus", kp.getLang()));
	    List<String> i1l = i1m.getLore();
	    i1l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Nexus", kp.getLang()));
	    i1l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i1m.setLore(LoreOrganizer.organize(i1l));
	    i1.setItemMeta(i1m);

	    ItemStack i2 = getPermissionMaterial(perm.getClaim());
	    ItemMeta i2m = i2.getItemMeta();
	    i2m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Claim", kp.getLang()));
	    List<String> i2l = i2m.getLore();
	    i2l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Claim", kp.getLang()));
	    i2l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i2m.setLore(LoreOrganizer.organize(i2l));
	    i2.setItemMeta(i2m);

	    ItemStack i3 = getPermissionMaterial(perm.getUnclaim());
	    ItemMeta i3m = i3.getItemMeta();
	    i3m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Unclaim", kp.getLang()));
	    List<String> i3l = i3m.getLore();
	    i3l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Unclaim", kp.getLang()));
	    i3l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i3m.setLore(LoreOrganizer.organize(i3l));
	    i3.setItemMeta(i3m);
	    
	    ItemStack i4 = getPermissionMaterial(perm.getInvade());
	    ItemMeta i4m = i4.getItemMeta();
	    i4m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Invade", kp.getLang()));
	    List<String> i4l = i4m.getLore();
	    i4l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Invade", kp.getLang()));
	    i4l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i4m.setLore(LoreOrganizer.organize(i4l));
	    i4.setItemMeta(i4m);
	    
	    ItemStack i5 = getPermissionMaterial(perm.getAlly());
	    ItemMeta i5m = i5.getItemMeta();
	    i5m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_AllyFunc", kp.getLang()));
	    List<String> i5l = i5m.getLore();
	    i5l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_AllyFunc", kp.getLang()));
	    i5l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i5m.setLore(LoreOrganizer.organize(i5l));
	    i5.setItemMeta(i5m);
	    
	    ItemStack i6 = getPermissionMaterial(perm.getTurret());
	    ItemMeta i6m = i6.getItemMeta();
	    i6m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Turret", kp.getLang()));
	    List<String> i6l = i6m.getLore();
	    i6l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Turrets", kp.getLang()));
	    i6l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i6m.setLore(LoreOrganizer.organize(i6l));
	    i6.setItemMeta(i6m);
	    
	    ItemStack i7 = getPermissionMaterial(perm.getSethome());
	    ItemMeta i7m = i7.getItemMeta();
	    i7m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Sethome", kp.getLang()));
	    List<String> i7l = i7m.getLore();
	    i7l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Sethome", kp.getLang()));
	    i7l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i7m.setLore(LoreOrganizer.organize(i7l));
	    i7.setItemMeta(i7m);
	    
	    ItemStack i8 = getPermissionMaterial(perm.getChest());
	    ItemMeta i8m = i8.getItemMeta();
	    i8m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexuschest", kp.getLang()));
	    List<String> i8l = i8m.getLore();
	    i8l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Nexuschest", kp.getLang()));
	    i8l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i8m.setLore(LoreOrganizer.organize(i8l));
	    i8.setItemMeta(i8m);

	    
	    ItemStack i9 = getPermissionMaterial(perm.getOpenallchest());
	    ItemMeta i9m = i9.getItemMeta();
	    i9m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Protectedchestbypass", kp.getLang()));
	    List<String> i9l = i9m.getLore();
	    i9l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Protectedchestbypass", kp.getLang()));
	    i9l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i9m.setLore(LoreOrganizer.organize(i9l));
	    i9.setItemMeta(i9m);
	    
	    ItemStack i10 = getPermissionMaterial(perm.getInvite());
	    ItemMeta i10m = i10.getItemMeta();
	    i10m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Invite", kp.getLang()));
	    List<String> i10l = i10m.getLore();
	    i10l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Invite", kp.getLang()));
	    i10l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i10m.setLore(LoreOrganizer.organize(i10l));
	    i10.setItemMeta(i10m);
	    
	    ItemStack i11 = getPermissionMaterial(perm.getBroad());
	    ItemMeta i11m = i11.getItemMeta();
	    i11m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Broadcast", kp.getLang()));
	    List<String> i11l = i11m.getLore();
	    i11l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Broadcast", kp.getLang()));
	    i11l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i11m.setLore(LoreOrganizer.organize(i11l));
	    i11.setItemMeta(i11m);
	    
	    ItemStack i12 = getPermissionMaterial(perm.getRPConvert());
	    ItemMeta i12m = i12.getItemMeta();
	    i12m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Donate", kp.getLang()));
	    List<String> i12l = i12m.getLore();
	    i12l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Donate", kp.getLang()));
	    i12l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i12m.setLore(LoreOrganizer.organize(i12l));
	    i12.setItemMeta(i12m);
	    
	    ItemStack i13 = getPermissionMaterial(perm.getStructures());
	    ItemMeta i13m = i13.getItemMeta();
	    i13m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Structures", kp.getLang()));
	    List<String> i13l = i13m.getLore();
	    i13l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Structures", kp.getLang()));
	    i13l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i13m.setLore(LoreOrganizer.organize(i13l));
	    i13.setItemMeta(i13m);
	    
	    ItemStack i14 = getPermissionMaterial(perm.getBuildInNexus());
	    ItemMeta i14m = i14.getItemMeta();
	    i14m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_Nexusbuild", kp.getLang()));
	    List<String> i14l = i14m.getLore();
	    i14l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_Nexusbuild", kp.getLang()));
	    i14l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i14m.setLore(LoreOrganizer.organize(i14l));
	    i14.setItemMeta(i14m);
	    
	    ItemStack i15 = getPermissionMaterial(perm.getUseKHome());
	    ItemMeta i15m = i15.getItemMeta();
	    i15m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_UseKHome", kp.getLang()));
	    List<String> i15l = i15m.getLore();
	    i15l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_UseKHome", kp.getLang()));
	    i15l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i15m.setLore(LoreOrganizer.organize(i15l));
	    i15.setItemMeta(i15m);
	    
	    ItemStack i16 = getPermissionMaterial(perm.getOverrideRegulator());
	    ItemMeta i16m = i16.getItemMeta();
	    i16m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_OverrideRegulator", kp.getLang()));
	    List<String> i16l = i16m.getLore();
	    i16l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_OverrideRegulator", kp.getLang()));
	    i16l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i16m.setLore(LoreOrganizer.organize(i16l));
	    i16.setItemMeta(i16m);
	    
	    ItemStack i17 = getPermissionMaterial(perm.getBuyXpBottles());
	    ItemMeta i17m = i17.getItemMeta();
	    i17m.setDisplayName(Kingdoms.getLang().getString("Guis_Permissions_Title_BuyXpBottles", kp.getLang()));
	    List<String> i17l = i17m.getLore();
	    i17l.add(Kingdoms.getLang().getString("Guis_Permissions_Desc_BuyXpBottles", kp.getLang()));
	    i17l.add(ChatColor.LIGHT_PURPLE + "Permission");
	    i17m.setLore(LoreOrganizer.organize(i17l));
	    i17.setItemMeta(i17m);
	    
	    ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
	    ItemMeta backbtnmeta = backbtn.getItemMeta();
	    backbtnmeta.setDisplayName(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn", kp.getLang()));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "" + ChatColor.YELLOW + "" + ChatColor.GREEN);
		backbtnmeta.setLore(lore);
	    backbtn.setItemMeta(backbtnmeta);
	    champions.setItem(26, backbtn);
	    
	    champions.addItem(i1);
	    champions.addItem(i2);
	    champions.addItem(i3);
	    champions.addItem(i4);
	    champions.addItem(i5);
	    champions.addItem(i6);
	    champions.addItem(i7);
	    champions.addItem(i8);
	    champions.addItem(i9);
	    champions.addItem(i10);
	    champions.addItem(i11);
	    champions.addItem(i12);
	    champions.addItem(i13);
	    champions.addItem(i14);
	    champions.addItem(i15);
	    champions.addItem(i16);
	    champions.addItem(i17);
	    kp.getPlayer().openInventory(champions);
	}
	
	private void addLore(ItemStack i, String add){
		ItemMeta meta = i.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if(meta.hasLore()){
			lore = meta.getLore();
		}
		lore.add(add);
		meta.setLore(lore);
		i.setItemMeta(meta);
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
