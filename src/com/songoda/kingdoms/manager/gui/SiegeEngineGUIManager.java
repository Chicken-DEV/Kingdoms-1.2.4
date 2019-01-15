package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SiegeEngine;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import com.songoda.kingdoms.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class SiegeEngineGUIManager extends Manager implements Listener {

	protected SiegeEngineGUIManager(Plugin plugin) {
		super(plugin);
	}
	public void openMenu(KingdomPlayer kp, Land engineLand) {
		Kingdom kingdom = kp.getKingdom();
		SiegeEngine engine = (SiegeEngine) engineLand.getStructure();
		InteractiveGUI gui = new InteractiveGUI(Kingdoms.getLang().getString("Structures_SiegeEngine", kp.getLang()), 27);
		SimpleChunkLocation sc = engine.getLoc().toSimpleChunk();
		for(int x = -1;x<= 1;x++){
			for(int z = -1;z<= 1;z++){
				//No diagonal Firing.
				if((x != 0) && z != 0) continue;
				ItemStack item;
				if(x == 0 && z == 0){
					item = new ItemStack(Materials.GLASS_PANE.parseMaterial());
					ItemMeta meta = item.getItemMeta();
					ArrayList<String> lore = new ArrayList<>();
					lore.add(Kingdoms.getLang().getString("Structures_SiegeEngine", kp.getLang()));
					meta.setDisplayName(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Title", kp.getLang())
							.replaceAll("%x%",""+sc.getX())
							.replaceAll("%z%",""+sc.getZ())
							.replaceAll("%tag%",""+ChatColor.GREEN + kingdom.getKingdomName()));
					lore.add(ChatColor.AQUA + "\\N/");
					lore.add(ChatColor.AQUA + "W+E");
					lore.add(ChatColor.AQUA + "/S\\");
					meta.setLore(LoreOrganizer.organize(lore));
					item.setItemMeta(meta);
				}else{
					SimpleChunkLocation chunk = new SimpleChunkLocation(sc.getWorld(),sc.getX()+x,sc.getZ()+z);
					Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(chunk);
					Kingdom kowner = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
					String kname = land.getOwner();
					if(kname == null){
						kname = Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang());
					}
					item = new ItemStack(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial());
					ItemMeta meta = item.getItemMeta();
					ArrayList<String> lore = new ArrayList<>();
					meta.setDisplayName(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Title", kp.getLang())
							.replaceAll("%x%",""+(sc.getX()+x))
							.replaceAll("%z%",""+(sc.getZ()+z))
							.replaceAll("%tag%",""+ChatColor.RED + kname));
					if(!engine.isReadyToFire()){
						lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Reloading", kp.getLang()));
						lore.add(ChatColor.RED + TimeUtils.parseTimeMinutes(engine.getConcBlastCD()));
					}else if(land.getOwnerUUID() == null){
						lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Invalid_Target", kp.getLang()));
					}else if(land.getOwnerUUID().equals(kingdom.getKingdomUuid())||
							kingdom.getAlliesList().contains(land.getOwnerUUID())){
						lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Your_Land", kp.getLang()));
					}else if(kowner.isShieldUp()){
						lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Invade_Shield", kp.getLang()));
					}else{
						int cost = Config.getConfig().getInt("siege.fire.cost");
						if(kowner.isWithinNexusShieldRange(chunk))
							lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Shield", kp.getLang())
								.replaceAll("%value%", ""+kowner.getShieldValue())
								.replaceAll("%max%", ""+kowner.getShieldMax()));
						
						//Allowed to Fire.
						lore.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Land_Click_To_Fire", kp.getLang()));
						lore.add(Kingdoms.getLang().getString("Guis_Cost_Text", kp.getLang())
								.replaceAll("%cost%",""+cost));
						gui.setAction((1+x)+(9*(z+1)), new Runnable(){
							@Override
							public void run(){
								kp.getPlayer().closeInventory();
								Kingdoms.getManagers().getSiegeEngineManager().fireSiegeEngine(
										engine, 
										land,
										kingdom,
										kowner);
							}
						});
					}
					
					meta.setLore(LoreOrganizer.organize(lore));
					item.setItemMeta(meta);
				}
				gui.getInventory().setItem((1+x)+(9*(z+1)), item);
			}
			gui.openInventory(kp.getPlayer());
		}
		
		
		ItemStack r = new ItemStack(Material.HAY_BLOCK);
		ItemMeta rm = r.getItemMeta();
		rm.setDisplayName(Kingdoms.getLang().getString("Guis_ResourcePoints_Title", kp.getLang()));
		ArrayList rl = new ArrayList();
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Desc", kp.getLang()));
		rl.add(Kingdoms.getLang().getString("Guis_ResourcePoints_Count", kp.getLang()).replaceAll("%amount%", ""+kingdom.getResourcepoints()));
		rm.setLore(LoreOrganizer.organize(rl));
		r.setItemMeta(rm);

		gui.getInventory().setItem(8, r);

	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
