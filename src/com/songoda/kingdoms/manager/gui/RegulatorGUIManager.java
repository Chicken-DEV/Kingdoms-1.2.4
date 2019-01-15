package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.Regulator;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class RegulatorGUIManager extends Manager {

	
	
	protected RegulatorGUIManager(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	public void openRegulatorMenu(KingdomPlayer kp, Land land){
		Kingdom kingdom = kp.getKingdom();
		Regulator regulator = (Regulator) land.getStructure();
		Inventory regulatorInv = null;
		
		if((ChatColor.AQUA + "[" + land.getLoc().toString() + "]").length()
				<=32){
			 regulatorInv = Bukkit.createInventory(null, 27,
					ChatColor.AQUA + "[" + land.getLoc().toString() + "]");
		}else{
			regulatorInv = Bukkit.createInventory(null, 27,
					ChatColor.AQUA + "[" + land.getLoc().getWorld() + "]");
		}
//		if("Alfheim , 2865 , 105 , 2805".length() <= 27){
//			 regulatorInv = Bukkit.createInventory(null, 27,
//					ChatColor.AQUA + "[" + "Alfheim , 2865 , 105 , 2805" + "]");
//		}else{
//			regulatorInv = Bukkit.createInventory(null, 27,
//					ChatColor.AQUA + "[" + "Alfheim , 2865 , 105 , 2805".substring(0, Math.min("2865 , 105 , 2805 Alfheim".length(), 27)) + "]");
//		}
		
		ItemStack i3 = new ItemStack(Materials.WHITE_WOOL.parseMaterial());
		ItemMeta i3m = i3.getItemMeta();
		i3m.setDisplayName(Kingdoms.getLang().getString("Guis_Regulator_WhoCanBuild_Title", kp.getLang()));
		ArrayList i3l = new ArrayList();
		i3m.setLore(LoreOrganizer.organize(i3l));
		i3.setItemMeta(i3m);
		
		ItemStack i4 = new ItemStack(Materials.WHITE_WOOL.parseMaterial());
		ItemMeta i4m = i4.getItemMeta();
		i4m.setDisplayName(Kingdoms.getLang().getString("Guis_Regulator_WhoCanInteract_Title", kp.getLang()));
		ArrayList i4l = new ArrayList();
		i4m.setLore(LoreOrganizer.organize(i4l));
		i4.setItemMeta(i4m);
		
		ItemStack i5 = new ItemStack(Material.ROTTEN_FLESH);
		ItemMeta i5m = i5.getItemMeta();
		i5m.setDisplayName(Kingdoms.getLang().getString("Guis_Regulator_CanMonstersSpawn_Title", kp.getLang()));
		ArrayList i5l = new ArrayList();
		if(regulator.isAllowMonsterSpawning()){
			i5l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			i5l.add(Kingdoms.getLang().getString("Guis_Misc_Disabled", kp.getLang()));
		}
		i5m.setLore(LoreOrganizer.organize(i5l));
		i5.setItemMeta(i5m);
		
		ItemStack i6 = new ItemStack(Materials.PORKCHOP.parseMaterial());
		ItemMeta i6m = i6.getItemMeta();
		i6m.setDisplayName(Kingdoms.getLang().getString("Guis_Regulator_CanAnimalsSpawn_Title", kp.getLang()));
		ArrayList i6l = new ArrayList();
		if(regulator.isAllowAnimalSpawning()){
			i6l.add(Kingdoms.getLang().getString("Guis_Misc_Enabled", kp.getLang()));
		}else{
			i6l.add(Kingdoms.getLang().getString("Guis_Misc_Disabled", kp.getLang()));
		}
		i6m.setLore(LoreOrganizer.organize(i6l));
		i6.setItemMeta(i6m);

		regulatorInv.addItem(i3);
		regulatorInv.addItem(i4);
		if(Config.getConfig().getBoolean("regulator.allow-toggling-monster-spawn"))regulatorInv.addItem(i5);
		if(Config.getConfig().getBoolean("regulator.allow-toggling-animal-spawn"))regulatorInv.addItem(i6);
		regulators.put(kp.getUuid(), regulator);
		kp.getPlayer().openInventory(regulatorInv);
	
		
	}
	
	@EventHandler
	public void onClickRegulatorMenu(InventoryClickEvent e){
		if(e.getInventory() == null) return;
		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getItemMeta() == null) return;
		if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		Player p = (Player) e.getWhoClicked();
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);
		if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_Regulator_WhoCanBuild_Title", kp.getLang()))){
			openRegulatorWhoCanBuildMenu(kp, regulators.get(kp.getUuid()));

			e.setCancelled(true);
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_Regulator_WhoCanInteract_Title", kp.getLang()))){
			openRegulatorWhoCanInteractMenu(kp, regulators.get(kp.getUuid()));

			e.setCancelled(true);
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_Regulator_CanMonstersSpawn_Title", kp.getLang()))){
			if(regulators.get(kp.getUuid()).isAllowMonsterSpawning()){
				regulators.get(kp.getUuid()).setAllowMonsterSpawning(false);
			}else{
				regulators.get(kp.getUuid()).setAllowMonsterSpawning(true);
			}
			openRegulatorMenu(kp, Kingdoms.getManagers().getLandManager().getOrLoadLand(regulators.get(kp.getUuid()).getLoc().toSimpleChunk()));

			e.setCancelled(true);
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_Regulator_CanAnimalsSpawn_Title", kp.getLang()))){
			if(regulators.get(kp.getUuid()).isAllowAnimalSpawning()){
				regulators.get(kp.getUuid()).setAllowAnimalSpawning(false);
			}else{
				regulators.get(kp.getUuid()).setAllowAnimalSpawning(true);
			}
			openRegulatorMenu(kp, Kingdoms.getManagers().getLandManager().getOrLoadLand(regulators.get(kp.getUuid()).getLoc().toSimpleChunk()));

			e.setCancelled(true);
		}
	}
	
	HashMap<UUID, Regulator> regulators = new HashMap<UUID, Regulator>();
	public void openRegulatorWhoCanBuildMenu(KingdomPlayer kp, Regulator regulator){
		Kingdom kingdom = kp.getKingdom();
		if(kp.getKingdom() == null) return;

		ArrayList<ItemStack> members = new ArrayList<ItemStack>();
		for(UUID uuid : kingdom.getMembersList()){
			if(uuid.equals(kp.getUuid())) continue;
			
			OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(uuid);
			if(okp == null) continue;
			
			members.add(getByStatusCanBuild(regulator, okp));
		}
		new ScrollerInventory(members, Kingdoms.getLang().getString("Guis_Regulator_WhoCanBuild_Title", kp.getLang()), kp.getPlayer());
	
	}
	
	@EventHandler
	public void onClickCanBuildButton(InventoryClickEvent e){
		if(e.getInventory() == null) return;
		if(e.getInventory().getTitle() == null) return;
		String whoCanBuild = Kingdoms.getLang().getString("Guis_Regulator_WhoCanBuild_Title");
		if(whoCanBuild.length() > 32){
			whoCanBuild = whoCanBuild.substring(0, 32);
		}
		if(!e.getInventory().getTitle().equals(whoCanBuild)) return;
		
		e.setCancelled(true);
		if(!GUIManagement.allowedActions.contains(e.getAction())) return;
		
		ItemStack clickedItem = e.getCurrentItem();
		if(clickedItem.getItemMeta() == null) return;
		if(clickedItem.getItemMeta().getDisplayName() == null) return;
		if(clickedItem.getItemMeta().getLore() == null) return;
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
		Regulator regulator = regulators.get(kp.getUuid());
		
			String playerName = clickedItem.getItemMeta().getDisplayName();
			OfflinePlayer offp = Bukkit.getOfflinePlayer(playerName);
			if(offp != null)
			if(clickedItem.getData().getData() == DyeColor.RED.getWoolData()){
				if(!regulator.getWhoCanBuild().contains(offp.getUniqueId()))
					regulator.getWhoCanBuild().add(offp.getUniqueId());
			}else{
				regulator.getWhoCanBuild().remove(offp.getUniqueId());
			}
		regulators.put(kp.getUuid(), regulator);
		
		openRegulatorWhoCanBuildMenu(kp, regulators.get(kp.getUuid()));
	}

	

	private ItemStack getByStatusCanBuild(Regulator regulator, OfflineKingdomPlayer okp){
		if(regulator.getWhoCanBuild().contains(okp.getUuid())){
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.GREEN.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_Regulator_CanBuild_Title",okp.getLang()
					));
		}else{
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.RED.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_Regulator_CannotBuild_Title",okp.getLang()
					));
		}
	}
	
	public void openRegulatorWhoCanInteractMenu(KingdomPlayer kp, Regulator regulator){
		Kingdom kingdom = kp.getKingdom();
		if(kp.getKingdom() == null) return;

		ArrayList<ItemStack> members = new ArrayList<ItemStack>();
		for(UUID uuid : kingdom.getMembersList()){
			if(uuid.equals(kp.getUuid())) continue;
			
			OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(uuid);
			if(okp == null) continue;
			
			members.add(getByStatusCanInteract(regulator, okp));
		}
		new ScrollerInventory(members, Kingdoms.getLang().getString("Guis_Regulator_WhoCanInteract_Title", kp.getLang()), kp.getPlayer());
	
	}
	
	@EventHandler
	public void onClickCanInteractButton(InventoryClickEvent e){
		if(e.getInventory() == null) return;
		if(e.getInventory().getTitle() == null) return;
		String whoCanInteract = Kingdoms.getLang().getString("Guis_Regulator_WhoCanInteract_Title");
		if(whoCanInteract.length() > 32){
			whoCanInteract = whoCanInteract.substring(0, 32);
		}
		if(!e.getInventory().getTitle().equals(whoCanInteract)) return;
		
		e.setCancelled(true);
		if(!GUIManagement.allowedActions.contains(e.getAction())) return;
		
		ItemStack clickedItem = e.getCurrentItem();
		if(clickedItem.getItemMeta() == null) return;
		if(clickedItem.getItemMeta().getDisplayName() == null) return;
		if(clickedItem.getItemMeta().getLore() == null) return;
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
		Regulator regulator = regulators.get(kp.getUuid());
		
			String playerName = clickedItem.getItemMeta().getDisplayName();
			OfflinePlayer offp = Bukkit.getOfflinePlayer(playerName);
			if(offp != null)
			if(clickedItem.getData().getData() == DyeColor.RED.getWoolData()){
				if(!regulator.getWhoCanInteract().contains(offp.getUniqueId()))
					regulator.getWhoCanInteract().add(offp.getUniqueId());
			}else{
				regulator.getWhoCanInteract().remove(offp.getUniqueId());
			}
		regulators.put(kp.getUuid(), regulator);
		
		openRegulatorWhoCanInteractMenu(kp, regulators.get(kp.getUuid()));
	}

	

	private ItemStack getByStatusCanInteract(Regulator regulator, OfflineKingdomPlayer okp){
		if(regulator.getWhoCanInteract().contains(okp.getUuid())){
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.GREEN.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_Regulator_CanInteract_Title"
					));
		}else{
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.RED.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_Regulator_CannotInteract_Title"
					));
		}
	}
	
	private ItemStack makeButton(Material mat, byte id, String title, String... lore){
		ItemStack IS = new ItemStack(mat, 1, id);
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(title);
		ArrayList<String> l = new ArrayList<String>();
		for(String lang : lore) l.add(lang);
		IM.setLore(l);
		IS.setItemMeta(IM);
		return IS;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
