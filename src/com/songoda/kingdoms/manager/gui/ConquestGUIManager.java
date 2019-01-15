package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;

import com.songoda.kingdoms.constants.conquest.ConquestLand;
import com.songoda.kingdoms.constants.conquest.ConquestMap;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.ConquestManager;
import com.songoda.kingdoms.manager.game.GameManagement;

public class ConquestGUIManager extends Manager implements Listener{

	protected ConquestGUIManager(Plugin plugin) {
		super(plugin);
	}
	

	@EventHandler
	public void onConquestButtonClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player p = (Player) event.getWhoClicked();
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null) return;
		if(event.getInventory().getName() == null)return;
		if(!event.getInventory().getName().equals(Kingdoms.getLang().getString("Guis_Nexus_Conquests_Title")))
			return;
		event.setCancelled(true);
		if(event.getCurrentItem() == null) return;
		if(event.getCurrentItem().getItemMeta() == null) return;
		if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		if(event.getCurrentItem().getItemMeta().getLore() == null) return;
		if(!event.getCurrentItem().getItemMeta().getLore().contains(Kingdoms.getLang().getString("Guis_Conquests_ButtonLore",kp.getLang()))) return;
		Kingdoms.getGuiManagement().getConquestMapGUIManager().openMenu(kp, ConquestManager.maps.get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase())));
	}
	
	public void openMenu(KingdomPlayer kp) {
		
			Kingdom kingdom = kp.getKingdom();

			final ArrayList<ConquestMap> usedMaps = new ArrayList<ConquestMap>();
			final ArrayList<ConquestMap> canJoinMaps = new ArrayList<ConquestMap>();
			final ArrayList<ConquestMap> finishedMaps = new ArrayList<ConquestMap>();
			
			ArrayList<ItemStack> conquests = new ArrayList<ItemStack>();
			
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
				@Override
				public void run() {
					for(ConquestMap map:ConquestManager.maps.values()){
						boolean fullConquered = true;
						for(ConquestLand land:map.lands){
							if(land.isCapital() && land.getOwner() == null){
								if(!canJoinMaps.contains(map)) canJoinMaps.add(map);
							}
							if(land.getOwner() == null){
								fullConquered = false;
								continue;
							}
							if(land.getOwner().equals(kingdom.getKingdomName())){
								usedMaps.add(map);
							}else{
								fullConquered = false;
							}
						}
						if(fullConquered) finishedMaps.add(map);
						
						if(!usedMaps.contains(map)&&
								!finishedMaps.contains(map)&&
								!canJoinMaps.contains(map)){
							boolean isEnemyFullOwned = true;
							String oldowner = "";
							for(ConquestLand land:map.lands){
								if(land.getOwner() == null){
									isEnemyFullOwned = false;
									break;
								}
								if(oldowner.equals("")) oldowner = land.getOwnerName();
								if(!oldowner.equals(land.getOwner())){
									isEnemyFullOwned = false;
									break;
								}
							}
							
							if(isEnemyFullOwned)canJoinMaps.add(map);
							
						}
						
					}
					
					Bukkit.getScheduler().runTask(plugin, new Runnable(){

						@Override
						public void run() {
							for(ConquestMap map:ConquestManager.maps.values()){
								if(finishedMaps.contains(map)){
									int income = 0;
									ChatColor color = ChatColor.RED;
									for(ConquestLand land:map.lands){
										if(land.isCapital()){
											income += Config.getConfig().getInt("conquestCapitalReward") - land.getUpKeepAmount();
										}else{
											income += Config.getConfig().getInt("conquestLandReward") - land.getUpKeepAmount();
										}
									}
									if(income > 0) color = ChatColor.GREEN;
									String incomeText = color + Kingdoms.getLang().getString("Guis_Conquests_Map_Income_Yield").replaceAll("%amount%", ""+income);
									
									conquests.add(makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.GREEN, ChatColor.GREEN + capitalize(map.name), Kingdoms.getLang().getString("Guis_Conquests_ButtonLore"),
											Kingdoms.getLang().getString("Guis_Conquests_Kingdom_Conquered_Map"), incomeText));
								}else if(usedMaps.contains(map)){
									int income = 0;
									ChatColor color = ChatColor.RED;
									for(ConquestLand land:map.lands){
										if(land.getOwner() == null) continue;
										if(!land.getOwner().equals(kingdom.getKingdomName())) continue;
										if(land.isCapital()){
											income += Config.getConfig().getInt("conquestCapitalReward") - land.getUpKeepAmount();
										}else{
											income += Config.getConfig().getInt("conquestLandReward") - land.getUpKeepAmount();
										}
									}
									if(income > 0) color = ChatColor.GREEN;
									String incomeText = color + Kingdoms.getLang().getString("Guis_Conquests_Map_Income_Yield").replaceAll("%amount%", ""+income);
									
									conquests.add(makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.RED, ChatColor.RED + capitalize(map.name), Kingdoms.getLang().getString("Guis_Conquests_ButtonLore"),
											Kingdoms.getLang().getString("Guis_Conquests_Kingdom_Is_In_Map"), incomeText));
								}else if(canJoinMaps.contains(map)){
									conquests.add(makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.YELLOW, ChatColor.GREEN + capitalize(map.name), Kingdoms.getLang().getString("Guis_Conquests_ButtonLore"),
											Kingdoms.getLang().getString("Guis_Conquests_Kingdom_Can_Join_Map")));
								}else{
									conquests.add(makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.GRAY, ChatColor.DARK_GRAY + capitalize(map.name), Kingdoms.getLang().getString("Guis_Conquests_ButtonLore"),
											Kingdoms.getLang().getString("Guis_Conquests_Kingdom_Is_Not_In_Full_Map")));
								}
							}
							
							ItemStack backbtn = new ItemStack(Material.REDSTONE_BLOCK);
							ItemMeta backbtnmeta = backbtn.getItemMeta();
							backbtnmeta.setDisplayName(ChatColor.RED + Kingdoms.getLang().getString("Guis_Back_Btn"));
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(ChatColor.RED + "" + ChatColor.YELLOW + "" + ChatColor.GREEN);
							backbtnmeta.setLore(lore);
							backbtn.setItemMeta(backbtnmeta);

							new ScrollerInventory(conquests, Kingdoms.getLang().getString("Guis_Nexus_Conquests_Title"), kp.getPlayer());
						
						}
						
					});
					
					
					
					
				}
			});

			
	}
	
	private ItemStack makeButton(Material mat, DyeColor color, String title,
			String btnType, String... lore) {
		ItemStack IS = new ItemStack(mat, 1, color.getWoolData());
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(title);
		ArrayList<String> l = new ArrayList<String>();
		for (String lores : lore)
			l.add(lores);
		l.add(btnType);
		IM.setLore(LoreOrganizer.organize(l));
		IS.setItemMeta(IM);
		return IS;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	
	private String capitalize(String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
