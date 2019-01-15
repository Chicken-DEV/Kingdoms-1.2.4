package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
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

public class TurretUpgradeGUIManager extends Manager implements Listener{
	protected TurretUpgradeGUIManager(Plugin plugin) {
		super(plugin);
		
		
	}

	@EventHandler
	public void onTurretUpgradeClick(InventoryClickEvent event) {
		if(event.getInventory().getName() == null) return;
		if(!event.getInventory().getName().equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_Title", Kingdoms.getManagers().getPlayerManager().getSession((Player) event.getWhoClicked()).getLang()))) return;
		event.setCancelled(true);
		if(event.getCurrentItem() == null) return;
		if(event.getCurrentItem().getItemMeta() == null) return;
		if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		if(event.getCurrentItem().getItemMeta().getLore() == null) return;
		if(event.getCurrentItem().getItemMeta().getDisplayName()
				.equals(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn", Kingdoms.getManagers().getPlayerManager().getSession((Player) event.getWhoClicked()).getLang()))) return;
		if(!(event.getWhoClicked() instanceof Player))return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) event.getWhoClicked());
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null){
			Kingdoms.logInfo("kingdom was null!");
			return;
		}
		
		String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
		List<String> lores = event.getCurrentItem().getItemMeta().getLore();
		if(lores.contains(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()))) return;
		if(upgradeTurretUpgrade(kingdom, displayName)){
			openMenu(kp);
		}else{
			openMenu(kp);
		}
	}
	
	public boolean upgradeTurretUpgrade(Kingdom kingdom, String type){
		int cost = 0;
		
		
		
		if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_SimplifiedModel_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.simplified-model");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setSimplifiedModel(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_Flurry_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.flurry");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setFlurry(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_ConcentratedBlast_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.concentrated-blast");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setConcentratedBlast(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_VirulentPlague_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.virulent-plague");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setVirulentPlague(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_ImprovedHeal_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.improved-healing");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setImprovedHeal(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_Voodoo_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.voodoo");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setVoodoo(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_FinalService_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.final-service");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setFinalService(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_Hellstorm_Title"))){
			cost = Config.getConfig().getInt("cost.turretupgrades.hellstorm");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setHellstorm(true);
				return true;
			}
		}else if(type.equals(Kingdoms.getLang().getString("Guis_TurretUpgrades_UnrelentingGaze_Title"))){
			cost =Config.getConfig().getInt("cost.turretupgrades.unrelenting-gaze");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				kingdom.getTurretUpgrades().setUnrelentingGaze(true);
				return true;
			}
		}
		
		
		return false;
	}

	public void openMenu(KingdomPlayer kp) {
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		Inventory menu = Bukkit.createInventory(null, 27, Kingdoms.getLang().getString("Guis_TurretUpgrades_Title", kp.getLang()));

		ItemStack t1 = new ItemStack(Material.STONE);
		ItemMeta t1m = t1.getItemMeta();
		t1m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_SimplifiedModel_Title", kp.getLang()));
		ArrayList t1l = new ArrayList();
		t1l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_SimplifiedModel_Lore", kp.getLang()));
		t1l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_SimplifiedModel_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isSimplifiedModel()){
			t1l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t1l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.simplified-model") + "RP");
		}
		t1m.setLore(LoreOrganizer.organize(t1l));
		t1.setItemMeta(t1m);
		
		ItemStack t2 = new ItemStack(Materials.SNOWBALL.parseMaterial());
		ItemMeta t2m = t2.getItemMeta();
		t2m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_Flurry_Title", kp.getLang()));
		ArrayList t2l = new ArrayList();
		t2l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Flurry_Lore", kp.getLang()));
		t2l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Flurry_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isFlurry()){
			t2l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t2l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.flurry") + "RP");
		}
		t2m.setLore(LoreOrganizer.organize(t2l));
		t2.setItemMeta(t2m);
		
		ItemStack t3 = new ItemStack(Material.TNT);
		ItemMeta t3m = t3.getItemMeta();
		t3m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_ConcentratedBlast_Title", kp.getLang()));
		ArrayList t3l = new ArrayList();
		t3l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_ConcentratedBlast_Lore", kp.getLang()));
		t3l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_ConcentratedBlast_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isConcentratedBlast()){
			t3l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t3l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.concentrated-blast") + "RP");
		}
		t3m.setLore(LoreOrganizer.organize(t3l));
		t3.setItemMeta(t3m);
		
		ItemStack t4 = new ItemStack(Material.POTION,1,(byte)8228);
		ItemMeta t4m = t4.getItemMeta();
		t4m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_VirulentPlague_Title", kp.getLang()));
		ArrayList t4l = new ArrayList();
		t4l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_VirulentPlague_Lore", kp.getLang()));
		t4l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_VirulentPlague_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isVirulentPlague()){
			t4l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t4l.add(ChatColor.RED + "" +Config.getConfig().getInt("cost.turretupgrades.virulent-plague") + "RP");
		}
		t4m.setLore(LoreOrganizer.organize(t4l));
		t4.setItemMeta(t4m);
		
		ItemStack t5 = new ItemStack(Material.POTION,1,(byte)8225);
		ItemMeta t5m = t5.getItemMeta();
		t5m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_ImprovedHeal_Title", kp.getLang()));
		ArrayList t5l = new ArrayList();
		t5l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_ImprovedHeal_Lore", kp.getLang()));
		t5l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_ImprovedHeal_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isImprovedHeal()){
			t5l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t5l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.improved-healing") + "RP");
		}
		t5m.setLore(LoreOrganizer.organize(t5l));
		t5.setItemMeta(t5m);

		ItemStack t6 = new ItemStack(Materials.ENDER_EYE.parseMaterial());
		ItemMeta t6m = t6.getItemMeta();
		t6m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_Voodoo_Title", kp.getLang()));
		ArrayList t6l = new ArrayList();
		t6l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Voodoo_Lore", kp.getLang()));
		t6l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Voodoo_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isVoodoo()){
			t6l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t6l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.voodoo") + "RP");
		}
		t6m.setLore(LoreOrganizer.organize(t6l));
		t6.setItemMeta(t6m);
		
		ItemStack t7 = new ItemStack(Material.NETHER_STAR);
		ItemMeta t7m = t7.getItemMeta();
		t7m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_FinalService_Title", kp.getLang()));
		ArrayList t7l = new ArrayList();
		t7l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_FinalService_Lore", kp.getLang()));
		t7l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_FinalService_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isFinalService()){
			t7l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t7l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.final-service") + "RP");
		}
		t7m.setLore(LoreOrganizer.organize(t7l));
		t7.setItemMeta(t7m);
		
		ItemStack t8 = new ItemStack(Material.ARROW);
		ItemMeta t8m = t8.getItemMeta();
		t8m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_Hellstorm_Title", kp.getLang()));
		ArrayList t8l = new ArrayList();
		t8l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Hellstorm_Lore", kp.getLang()));
		t8l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_Hellstorm_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isHellstorm()){
			t8l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t8l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.hellstorm") + "RP");
		}
		t8m.setLore(LoreOrganizer.organize(t8l));
		t8.setItemMeta(t8m);
		
		ItemStack t9 = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta t9m = t9.getItemMeta();
		t9m.setDisplayName(Kingdoms.getLang().getString("Guis_TurretUpgrades_UnrelentingGaze_Title", kp.getLang()));
		ArrayList t9l = new ArrayList();
		t9l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_UnrelentingGaze_Lore", kp.getLang()));
		t9l.add(Kingdoms.getLang().getString("Guis_TurretUpgrades_UnrelentingGaze_Changes", kp.getLang()));
		if(kingdom.getTurretUpgrades().isUnrelentingGaze()){
			t9l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			t9l.add(ChatColor.RED + "" + Config.getConfig().getInt("cost.turretupgrades.unrelenting-gaze") + "RP");
		}
		t9m.setLore(LoreOrganizer.organize(t9l));
		t9.setItemMeta(t9m);		
		
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
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "" + ChatColor.YELLOW + "" + ChatColor.GREEN);
		backbtnmeta.setLore(lore);
		backbtnmeta.setDisplayName(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn", kp.getLang()));
		backbtn.setItemMeta(backbtnmeta);

		if(Config.getConfig().getBoolean("enable.turretupgrades.simplified-model"))menu.addItem(t1);
		if(Config.getConfig().getBoolean("enable.turretupgrades.flurry"))menu.addItem(t2);
		if(Config.getConfig().getBoolean("enable.turretupgrades.concentrated-blast"))menu.addItem(t3);
		if(Config.getConfig().getBoolean("enable.turretupgrades.virulent-plague"))menu.addItem(t4);
		if(Config.getConfig().getBoolean("enable.turretupgrades.improved-healing"))menu.addItem(t5);
		if(Config.getConfig().getBoolean("enable.turretupgrades.voodoo"))menu.addItem(t6);
		if(Config.getConfig().getBoolean("enable.turretupgrades.final-service"))menu.addItem(t7);
		if(Config.getConfig().getBoolean("enable.turretupgrades.hellstorm"))menu.addItem(t8);
		if(Config.getConfig().getBoolean("enable.turretupgrades.unrelenting-gaze"))menu.addItem(t9);
		menu.setItem(17, r);
		menu.setItem(26, backbtn);

		kp.getPlayer().openInventory(menu);
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}


}
