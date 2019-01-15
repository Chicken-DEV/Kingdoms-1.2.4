package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.songoda.kingdoms.constants.conquest.ConquestLand;
import com.songoda.kingdoms.constants.conquest.ConquestMap;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.KingdomCooldown;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
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
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.ConquestManager;
import com.songoda.kingdoms.manager.game.GameManagement;

public class ConquestMapGUIManager extends Manager implements Listener{

	protected ConquestMapGUIManager(Plugin plugin) {
		super(plugin);
	}
	
	public HashMap<KingdomPlayer, ConquestLand> selectedLands = new HashMap<KingdomPlayer, ConquestLand>();

	@EventHandler
	public void onConquestButtonClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player p = (Player) event.getWhoClicked();
		//Kingdoms.logDebug("conquests1");
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		Kingdoms.logDebug("conquests2");
		if(event.getInventory().getName() == null)return;
		Kingdoms.logDebug("conquests3");
		if(!ConquestManager.maps.containsKey(event.getInventory().getName().toLowerCase()))
			return;
		Kingdoms.logDebug("conquests3-1");
		event.setCancelled(true);
		if(event.getCurrentItem() == null) return;
		Kingdoms.logDebug("conquests3-2");
		if(event.getCurrentItem().getItemMeta() == null) return;
		Kingdoms.logDebug("conquests3-3");
		if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		Kingdoms.logDebug("conquests3-4");
		if(event.getCurrentItem().getItemMeta().getLore() == null) return;
		Kingdoms.logDebug("conquests3-5");
		if(event.getCurrentItem().getItemMeta().getLore().contains(Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore"))){

			Kingdoms.logDebug("conquests4");
			ConquestLand land = titleToLand(event.getCurrentItem().getItemMeta().getDisplayName());
			updateLandDetails(land,kingdom, kp, event.getInventory());
			selectedLands.put(kp, land);
		}
		if(event.getCurrentItem().getItemMeta().getLore().contains(Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"))) return;
		
		if(!selectedLands.containsKey(kp)) return;
		Kingdoms.logDebug("conquests5");
		ConquestLand land = ConquestLand.getLandAt(ConquestManager.maps.get(selectedLands.get(kp).map), selectedLands.get(kp).x, selectedLands.get(kp).y);
		
		
		//ATTACK button manager
		if(containsLore(event.getCurrentItem(), Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Area"))){
			
			if(containsLore(event.getCurrentItem(), Kingdoms.getLang().getString("Guis_ConquestMap_On_Cooldown"))){
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}else if(!land.canBeAttackedBy(kingdom)){
				kp.sendMessage(Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Encircled", kp.getLang()));
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}else if(land.isUnderSiege){
				kp.sendMessage(Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Already_Ongoing", kp.getLang()));
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}
			int cost;
			if(land.getOwner() != null){
				if(land.getOwner().equals(kingdom.getKingdomUuid())){
					openMenu(kp, ConquestManager.maps.get(land.map));
					return;
				}
				if(kingdom.getAlliesList().contains(land.getOwner())){
					openMenu(kp, ConquestManager.maps.get(land.map));
					return;
				}
			}else{
				openMenu(kp, ConquestManager.maps.get(land.map));
				return;
			}
			if(land.isCapital()){
				cost = Config.getConfig().getInt("conquest.attack-costs.enemy-capital-land");
				if(kingdom.getResourcepoints() >= cost){
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					ConquestManager.startOffensive(kp, kingdom, land);
					kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Attacking_Land").replaceAll("%land%", capitalize(land.getDataID())), true);
					return;
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
				
			}else{
				cost = Config.getConfig().getInt("conquests.attack-cost.");
				if(kingdom.getResourcepoints() >= cost){
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					ConquestManager.startOffensive(kp, kingdom, land);
					kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Attacking_Land").replaceAll("%land%", capitalize(land.getDataID())), true);
					return;
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
				
			}
		}else if(containsLore(event.getCurrentItem(), Kingdoms.getLang().getString("Guis_ConquestMap_Claim_Land"))){
			if(containsLore(event.getCurrentItem(), Kingdoms.getLang().getString("Guis_ConquestMap_On_Cooldown"))){
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}else if(!land.canBeAttackedBy(kingdom)){
				kp.sendMessage(Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Encircled", kp.getLang()));
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}else if(land.isUnderSiege){
				kp.sendMessage(Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Already_Ongoing", kp.getLang()));
				updateLandDetails(land,kingdom, kp, event.getInventory());
				return;
			}
			int cost = Config.getConfig().getInt("conquests.attack-costs.empty-land");
			if(land.getOwner() != null){
					openMenu(kp, ConquestManager.maps.get(land.map));
					return;
			}
			if(land.isCapital()){
				cost = Config.getConfig().getInt("conquests.attack-costs.empty-capital");
			}
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				land.setOwner(kingdom.getKingdomUuid());
				KingdomCooldown cooldown = new KingdomCooldown(kingdom.getKingdomName(), "attackcd", 60*Config.getConfig().getInt("attack-cooldown-in-minutes"));
				cooldown.start();
				openMenu(kp, ConquestManager.maps.get(land.map));
				kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Conquered_Land").replaceAll("%land%", capitalize(land.getDataID())), true);
				return;
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
				return;
			}
			
		}else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Attack").replaceAll("%cost%", "" + Config.getConfig().getInt("conquests.attack-costs.empty-capital")))&&
				containsLore(event.getCurrentItem(), Kingdoms.getLang().getString("Guis_ConquestMap_Claim_Capital"))){
			int cost = Config.getConfig().getInt("conquests.attack-costs.empty-capital");
			if(land.getOwner() == null){
				if(kingdom.getResourcepoints() >= cost){
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					land.setOwner(kingdom.getKingdomUuid());
					KingdomCooldown cooldown = new KingdomCooldown(kingdom.getKingdomName(), "attackcd", 60*Config.getConfig().getInt("attack-cooldown-in-minutes"));
					cooldown.start();
					openMenu(kp, ConquestManager.maps.get(land.map));
					kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Joined_Map").replaceAll("%map%", capitalize(land.map)), true);
					return;
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
				}
			}else{
				openMenu(kp, ConquestManager.maps.get(land.map));
				return;
			}
		}

		Kingdoms.logDebug("conquests6");
		
		//BUYING AND SELLING
		ItemStack item = event.getCurrentItem();
		if(land.getOwner() != null)if(!land.getOwner().equals(kingdom.getKingdomUuid())) return;
		Kingdoms.logDebug("conquests7");
		List<String> lore = event.getCurrentItem().getItemMeta().getLore();
		//WALL upgrading/building
		if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Wall"))){
			int cost = Config.getConfig().getInt("conquests.upkeep.wall");
			if(containsLore(item, Kingdoms.getLang().getString("Guis_ConquestMap_Buy").replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.wall")))||
					containsLore(item, Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade").replaceAll("%cost%", "" + cost))){
				if(land.getWalllevel() >= 3){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Level_Reached", kp.getLang()));
					return;
				}
				if(kingdom.getResourcepoints() >= cost){
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					land.setWalllevel(land.getWalllevel() + 1);
					updateLandDetails(land, kingdom, kp, event.getInventory());
					return;
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
			}
		}
		Kingdoms.logDebug("conquests8");
		//SPAWNER
		if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Spawner"))){
			int cost = Config.getConfig().getInt("conquests.upkeep.spawner");
			if(containsLore(item, Kingdoms.getLang().getString("Guis_ConquestMap_Buy").replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.spawner")))||
					containsLore(item, Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade").replaceAll("%cost%", "" + cost))){
				if(land.getSpawnerlevel() >= 8){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Level_Reached", kp.getLang()));
					return;
				}
				if(kingdom.getResourcepoints() >= cost){
					kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
					land.setSpawnerlevel(land.getSpawnerlevel() + 1);
					updateLandDetails(land, kingdom, kp, event.getInventory());
					return;
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
					return;
				}
			}
		}
		Kingdoms.logDebug("conquests9");
		//TURRETS
		boolean isBuyingTurret = false;
		int turretSelected = 0;
		if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "1"))){
			isBuyingTurret = true;
			turretSelected = 1;
		}else if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "2"))){
			isBuyingTurret = true;
			turretSelected = 2;
		}else if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "3"))){
			isBuyingTurret = true;
			turretSelected = 3;
		}else if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "4"))){
			isBuyingTurret = true;
			turretSelected = 4;
		}
		
		if(isBuyingTurret){
			if(land.getTurretLevelAtSlot(turretSelected) >= 15){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Level_Reached", kp.getLang()));
				return;
			}
			int cost = Config.getConfig().getInt("conquests.upkeep.wall");
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				land.setTurretLevelAtSlot(turretSelected, land.getTurretLevelAtSlot(turretSelected) + 1);
				updateLandDetails(land, kingdom, kp, event.getInventory());
				return;
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
				return;
			}
			
		}
		Kingdoms.logDebug("conquests10");
		//SELLALL
		int sellAmount = (int) (0.5*(land.getWalllevel()*Config.getConfig().getInt("conquests.cost.wall") +
				land.getSpawnerlevel()*Config.getConfig().getInt("conquests.cost.spawner") +
				land.getTurretLevelAtSlot(1)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(2)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(3)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(4)*Config.getConfig().getInt("conquests.cost.turret")));
		Kingdoms.logDebug(Kingdoms.getLang().getString("Guis_ConquestMap_Sell").replaceAll("%amount%", "" + sellAmount));
		if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Sell").replaceAll("%amount%", "" + sellAmount))){
			land.setSpawnerlevel(0);
			land.setWalllevel(0);
			land.setTurretLevelAtSlot(1, 0);
			land.setTurretLevelAtSlot(2, 0);
			land.setTurretLevelAtSlot(3, 0);
			land.setTurretLevelAtSlot(4, 0);
			kingdom.setResourcepoints(kingdom.getResourcepoints() + sellAmount);
			updateLandDetails(land, kingdom, kp, event.getInventory());
			return;
		}
		
		//RESUPPLY
		int cost = Config.getConfig().getInt("conquests.upkeep.max-supply-for-one-land") - land.getSupplylevel();
		if(item.getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_ConquestMap_Resupply").replaceAll("%cost%", "" + cost))){
			if(kingdom.getResourcepoints() >= cost){
				kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
				land.setSupplylevel(land.getSupplylevel() + cost);
				updateLandDetails(land, kingdom, kp, event.getInventory());
				return;
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
				return;
			}
		}
		
		updateLandDetails(land, kingdom, kp, event.getInventory());		
	}
	
	public void updateLandDetails(ConquestLand land, Kingdom kingdom, KingdomPlayer kp, Inventory invMap){
	
		DyeColor dye = DyeColor.YELLOW;
		ChatColor color = ChatColor.YELLOW;
		int maxSupply = Config.getConfig().getInt("conquests.upkeep.max-supply-for-one-land");
		if(land.getSupplylevel() >= maxSupply*0.9){
			color = ChatColor.GREEN;
		}else if(land.getSupplylevel() >= maxSupply*0.5){
			color = ChatColor.YELLOW;
		}else if(land.getSupplylevel() < maxSupply*0.5){
			color = ChatColor.RED;
		}else if(land.getSupplylevel() == 0){
			color = ChatColor.DARK_RED;
		}
		
		
		
		ItemStack selected = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), dye, coordsToTitle(land),
				color + Kingdoms.getLang().getString("Guis_ConquestMap_Supplies", kp.getLang()).replaceAll("%amount%", "" + land.getSupplylevel()).replaceAll("%max%", maxSupply + ""));
		if(land.isEncircled()){
			dye = DyeColor.RED;
			selected = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), dye, coordsToTitle(land),
					color + Kingdoms.getLang().getString("Guis_ConquestMap_Supplies").replaceAll("%amount%", "" + land.getSupplylevel()).replaceAll("%max%", maxSupply + ""),
					Kingdoms.getLang().getString("Guis_ConquestMap_Encircled", kp.getLang()));
		}
		
		
		if(land.getOwner() == null){
			dye = DyeColor.YELLOW;
			color = ChatColor.GRAY;
		}else if(land.getOwner().equals(kingdom.getKingdomUuid())){
			dye = DyeColor.LIME;
			color = ChatColor.GREEN;
		}else if(kingdom.getAlliesList().contains(land.getOwner())){
			dye = DyeColor.MAGENTA;
			color = ChatColor.LIGHT_PURPLE;
		}else{
			dye = DyeColor.RED;
			color = ChatColor.RED;
		}
		
		String ownername = land.getOwnerName();
		if(ownername == null) ownername = Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang());
		
		ItemStack owner = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), dye, Kingdoms.getLang().getString("Guis_ConquestMap_Owned_By", kp.getLang()),
				color + ownername);
		
		
		int cost = Config.getConfig().getInt("conquests.upkeep.wall");
		String costText = "";
		if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid())){
			if(land.getWalllevel() == 0){
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.wall"));
			}else{
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			}
		}
		
		ItemStack wall = makeButton(Material.COBBLESTONE, Kingdoms.getLang().getString("Guis_ConquestMap_Wall", kp.getLang()),
				ChatColor.RED + "Lvl " + land.getWalllevel(), costText);
		
		String status = "-";
		
		cost =Config.getConfig().getInt("conquests.upkeep.per-turret");
		costText = "";

//		if(land.getTurretLevelAtSlot(1) > 0){
//			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Online).replaceAll("%level%", "" + land.getTurretLevelAtSlot(1));
//			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomName()))
//				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy).replaceAll("%cost%", "" + cost);
//		}else{
//			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Offline);
//			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomName()))
//				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade).replaceAll("%cost%", "" + cost);
//		}
		if(land.getTurretLevelAtSlot(1) > 0 && land.getSupplylevel() > 0){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Online", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(1));
		}else if(land.getTurretLevelAtSlot(1) > 0 ){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_No_Supplies", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(1));
		}else{
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.per-turret"));
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Offline", kp.getLang());
		}
		ItemStack turret1 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret", kp.getLang()).replaceAll("%number%", "1"),
				status, costText);
		
		if(land.getTurretLevelAtSlot(2) > 0 && land.getSupplylevel() > 0){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Online", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(2));
		}else if(land.getTurretLevelAtSlot(2) > 0 ){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_No_Supplies", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(2));
		}else{
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.per-turret"));
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Offline", kp.getLang());
		}
		ItemStack turret2 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret", kp.getLang()).replaceAll("%number%", "2"),
				status, costText);
		//Make cost texts
		if(land.getTurretLevelAtSlot(3) > 0 && land.getSupplylevel() > 0){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Online").replaceAll("%level%", "" + land.getTurretLevelAtSlot(3));
		}else if(land.getTurretLevelAtSlot(3) > 0 ){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_No_Supplies").replaceAll("%level%", "" + land.getTurretLevelAtSlot(3));
		}else{
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.per-turret"));
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Offline", kp.getLang());
		}
		ItemStack turret3 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret", kp.getLang()).replaceAll("%number%", "3"),
				status, costText);
		
		if(land.getTurretLevelAtSlot(4) > 0 && land.getSupplylevel() > 0){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Online", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(4));
		}else if(land.getTurretLevelAtSlot(4) > 0 ){
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_No_Supplies", kp.getLang()).replaceAll("%level%", "" + land.getTurretLevelAtSlot(4));
		}else{
			if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()))
				costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + Config.getConfig().getInt("conquests.upkeep.per-turret"));
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Turret_Status_Offline", kp.getLang());
		}
		
		ItemStack turret4 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret", kp.getLang()).replaceAll("%number%", "4"),
				status, costText);
		
		int sellAmount = (int) (0.5*(land.getWalllevel()*Config.getConfig().getInt("conquests.cost.wall") +
				land.getSpawnerlevel()*Config.getConfig().getInt("conquests.cost.spawner") +
				land.getTurretLevelAtSlot(1)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(2)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(3)*Config.getConfig().getInt("conquests.cost.turret") +
				land.getTurretLevelAtSlot(4)*Config.getConfig().getInt("conquests.cost.turret")));
		
		ItemStack sellAll = makeButton(Material.GOLD_INGOT, Kingdoms.getLang().getString("Guis_ConquestMap_Sell", kp.getLang()).replaceAll("%amount%", "" + sellAmount), "");
		//Kingdoms.logDebug("::::" + sellAll.getItemMeta().getDisplayName());
		cost =Config.getConfig().getInt("conquests.costs.spawner");
		int upkeep = Config.getConfig().getInt("conquests.upkeep.spawner");
		if(land.getSpawnerlevel() > 0 && land.getSupplylevel() > 0){
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Spawner_Status_Online", kp.getLang()).replaceAll("%level%", "" + land.getSpawnerlevel());
			costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
		}else if(land.getSpawnerlevel() > 0 ){
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Spawner_Status_No_Supplies", kp.getLang()).replaceAll("%level%", "" + land.getSpawnerlevel());
			costText = Kingdoms.getLang().getString("Guis_ConquestMap_Upgrade", kp.getLang()).replaceAll("%cost%", "" + cost);
		}else{
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Spawner_Status_Offline", kp.getLang());
			costText = Kingdoms.getLang().getString("Guis_ConquestMap_Buy", kp.getLang()).replaceAll("%cost%", "" + cost).replaceAll("%upkeep%", "" + upkeep);
		}
		if(land.getOwner() == null||
				!land.getOwner().equals(kingdom.getKingdomUuid())){
			costText = "";
		}
		
		ItemStack spawner = makeButton(Materials.SPAWNER.parseMaterial(), Kingdoms.getLang().getString("Guis_ConquestMap_Spawner", kp.getLang()),
				status, costText);
		
		
		boolean hasFoothold = false;
		for(ConquestLand l:ConquestManager.maps.get(land.map).lands){
			if(l.getOwner() != null){
				if(l.getOwner().equals(kingdom.getKingdomUuid())){
					hasFoothold = true;
				}
			}
		}
		cost = 0;
		String addon = "";
		if(land.getOwner() != null){
			if(land.getOwner().equals(kingdom.getKingdomUuid())){
				status = Kingdoms.getLang().getString("Guis_ConquestMap_Cannot_Attack_Own_Land", kp.getLang());
				cost = 0;
			}else{
				status = Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Area", kp.getLang());
				cost = Config.getConfig().getInt("conquests.attack-costs.enemy-land");
				//addon = ChatColor.WHITE + "aa";
			}
		}else{
			if(land.isCapital() && !hasFoothold){
				status = Kingdoms.getLang().getString("Guis_ConquestMap_Claim_Capital", kp.getLang());
				cost = Config.getConfig().getInt("conquests.attack-costs.empty-capital");
				//addon = ChatColor.WHITE + "cc";
			}else if(hasFoothold){
				status = Kingdoms.getLang().getString("Guis_ConquestMap_Claim_Land", kp.getLang());
				cost = Config.getConfig().getInt("conquests.attack-costs.empty-land");
				if(land.isCapital()) cost = Config.getConfig().getInt("conquests.attack-costs.empty-capital");
				//addon = ChatColor.WHITE + "cl";
			}
		}
		
		if(!land.anyCloseBy(kingdom) && hasFoothold){
			status = Kingdoms.getLang().getString("Guis_ConquestMap_Attack_Out_Of_Range", kp.getLang());
			addon = "";
		}
		
		String cd = "";
		String timecd = "";
		if(KingdomCooldown.isInCooldown(kingdom.getKingdomName(), "attackcd")){
			int timeleftSecs = KingdomCooldown.getTimeLeft(kingdom.getKingdomName(), "attackcd")/60;
			cd = Kingdoms.getLang().getString("Guis_ConquestMap_On_Cooldown", kp.getLang());
			timecd = ChatColor.RED + "" + timeleftSecs + "min";
		}
		ItemStack attack = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.RED, Kingdoms.getLang().getString("Guis_ConquestMap_Attack", kp.getLang()).replaceAll("%cost%", "" + cost),
				status, addon, cd, timecd);
		
		cost = maxSupply - land.getSupplylevel();
		ItemStack resupply = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.LIME, Kingdoms.getLang().getString("Guis_ConquestMap_Resupply", kp.getLang()).replaceAll("%cost%", "" + cost),"");
		
		invMap.setItem(7, selected);
		invMap.setItem(8, owner);
		invMap.setItem(16, wall);
		invMap.setItem(17, spawner);
		invMap.setItem(25, turret1);
		invMap.setItem(26, turret2);
		invMap.setItem(34, turret3);
		invMap.setItem(35, turret4);
		if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid()) && sellAmount > 0){
			invMap.setItem(44, sellAll);
		}else{
			invMap.setItem(44, new ItemStack(Material.AIR));
		}
		if(land.getOwner() != null && land.getOwner().equals(kingdom.getKingdomUuid())&&
				land.getSupplylevel() < maxSupply){
			invMap.setItem(52, resupply);
		}else{
			invMap.setItem(52, new ItemStack(Material.AIR));
		}
		invMap.setItem(53, attack);
		kp.getPlayer().updateInventory();
	}
	
	public void openMenu(KingdomPlayer kp, ConquestMap map) {
		
			
		
			Kingdom kingdom = kp.getKingdom();
			ArrayList<ItemStack> conquests = new ArrayList<ItemStack>();
			Inventory invMap = Bukkit.createInventory(null, 54,capitalize(map.name));
			for(int y = 0; y < 6; y++){
				ItemStack buffer = new ItemStack(Materials.GLASS_PANE.parseMaterial());
				ItemMeta meta = buffer.getItemMeta();
				meta.setDisplayName(ChatColor.GRAY + "");
				buffer.setItemMeta(meta);
				invMap.setItem(y*9 + 6, buffer);
			}
			
//			ItemStack selected = new ItemStack(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial());
//			ItemMeta meta = selected.getItemMeta();
//			meta.setDisplayName(ChatColor.GRAY + "[-,-]");
//			List<String> lore = new ArrayList<String>();
//			lore.add(Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land));
//			meta.setLore(LoreOrganizer.organize(lore));
//			selected.setItemMeta(meta);
			ItemStack selected = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.BLACK, ChatColor.GRAY + "[-,-]",
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack owner = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.GRAY, Kingdoms.getLang().getString("Guis_ConquestMap_Owned_By"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack wall = makeButton(Material.COBBLESTONE, Kingdoms.getLang().getString("Guis_ConquestMap_Wall"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack turret1 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "1"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack turret2 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "2"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack turret3 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "3"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack turret4 = makeButton(Material.ARROW, Kingdoms.getLang().getString("Guis_ConquestMap_Turret").replaceAll("%number%", "4"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack spawner = makeButton(Materials.SPAWNER.parseMaterial(), Kingdoms.getLang().getString("Guis_ConquestMap_Spawner"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			ItemStack attack = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.RED, Kingdoms.getLang().getString("Guis_ConquestMap_Attack").replaceAll("%cost%", "0"),
					Kingdoms.getLang().getString("Guis_ConquestMap_Click_A_Land"));
			
			invMap.setItem(7, selected);
			invMap.setItem(8, owner);
			invMap.setItem(16, wall);
			invMap.setItem(17, spawner);
			invMap.setItem(25, turret1);
			invMap.setItem(26, turret2);
			invMap.setItem(34, turret3);
			invMap.setItem(35, turret4);
			invMap.setItem(53, attack);
			
			
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
				@Override
				public void run() {
					boolean hasFoothold = false;
					for(ConquestLand land:map.lands){
						if(land.getOwner() != null){
							if(land.getOwner().equals(kingdom.getKingdomUuid())){
								hasFoothold = true;
							}
						}
					}
					final boolean hasFootHold = hasFoothold;
					for(ConquestLand land:map.lands){
						
						Bukkit.getScheduler().runTask(plugin, new Runnable(){
							@Override
							public void run() {
								
								int guiX = land.x;
								if(land.y == 1) guiX += 9;
								if(land.y == 2) guiX += 18;
								if(land.y == 3) guiX += 27;
								if(land.y == 4) guiX += 36;
								if(land.y == 5) guiX += 45;
								
								ItemStack item = null;
								if(land.getOwner() == null){
									item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.GRAY, coordsToTitle(land),
											Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.GRAY + Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang()));
								}else if(land.getOwner().equals(kingdom.getKingdomUuid())){
									item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.LIME, coordsToTitle(land),
											Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.GREEN + kingdom.getKingdomName());
								}else if(Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerName()).isAllianceWith(kingdom)){
									item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.MAGENTA, coordsToTitle(land),
											Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.LIGHT_PURPLE + land.getOwnerName());
								}else{
									item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.RED, coordsToTitle(land),
											Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.RED + land.getOwnerName());
								}
								if(land.isCapital()){
									if(land.getOwner() == null && !hasFootHold){
										item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.YELLOW, coordsToTitle(land),
												Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), Kingdoms.getLang().getString("Guis_ConquestMap_Claim_Capital", kp.getLang()));
									}else if(land.getOwner() == null && hasFootHold){
										item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.BLACK, coordsToTitle(land),
												Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.GRAY + Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang()));
									}else if(land.getOwner().equals(kingdom.getKingdomUuid())){
										item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.GREEN, coordsToTitle(land),
												Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.GREEN + kingdom.getKingdomName());
									}else if(Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerName()).isAllianceWith(kingdom)){
										item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.PURPLE, coordsToTitle(land),
												Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.LIGHT_PURPLE + land.getOwnerName());
									}else{
										item = makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.PINK, coordsToTitle(land),
												Kingdoms.getLang().getString("Guis_ConquestMap_ButtonLore", kp.getLang()), ChatColor.RED + land.getOwnerName());
									}
								}
								invMap.setItem(guiX, item);
						

							}
						});
					}
					
					
				}
			});
			kp.getPlayer().openInventory(invMap);
			
	}
	
	private String coordsToTitle(ConquestLand land){
		if(!land.isCapital()){
			return ChatColor.AQUA + "[" + ChatColor.YELLOW + capitalize(land.getDataID()) +	ChatColor.AQUA + "]";
		}else{
			return ChatColor.YELLOW + "Capital " + ChatColor.AQUA + "[" + ChatColor.YELLOW + capitalize(land.getDataID()) +	ChatColor.AQUA + "]";
		}
		
	}
	
	private ConquestLand titleToLand(String title){
		ConquestLand duplicate = new ConquestLand(ChatColor.stripColor(title.toLowerCase()).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("capital ", ""));
		
		return ConquestMap.getLandAt(ConquestManager.maps.get(duplicate.map), duplicate.x, duplicate.y);
	}

	private boolean containsLore(ItemStack item, String string){
		List<String> lore = LoreOrganizer.organize(new ArrayList<String>(){{
			add(string);
			}});
		for(String partlore:lore){
			if(!item.getItemMeta().getLore().contains(partlore))return false;
		}
		
		return true;
	}
	
	private ItemStack makeButton(Material mat, DyeColor color, String title,
		String... lore) {
		ItemStack IS = new ItemStack(mat, 1, color.getWoolData());
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(title);
		ArrayList<String> l = new ArrayList<String>();
		for (String lores : lore){
			if(!lores.equals(""))l.add(lores);
		}
		if(l.isEmpty()) l.add("");
		IM.setLore(LoreOrganizer.organize(l));
		IS.setItemMeta(IM);
		return IS;
	}
	
	private ItemStack makeButton(Material mat, String title,
			String... lore) {
			ItemStack IS = new ItemStack(mat);
			ItemMeta IM = IS.getItemMeta();
			IM.setDisplayName(title);
			ArrayList<String> l = new ArrayList<String>();
			for (String lores : lore){
				if(!lores.equals(""))l.add(lores);
			}
			if(l.isEmpty()) l.add("---------");
			IM.setLore(LoreOrganizer.organize(l));
			IS.setItemMeta(IM);
			return IS;
		}

	@Override
	public void onDisable() {
	}
	
	private String capitalize(String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
