package com.songoda.kingdoms.manager.gui;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.WarpPadManager;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.StructureManager;

import java.util.ArrayList;
import java.util.List;

public class WarppadGUIManager extends Manager implements Listener {

  protected WarppadGUIManager(Plugin plugin){
	super(plugin);
  }


  @EventHandler
  public void onWarpPadClickButton(InventoryClickEvent e){
	if(e.getCurrentItem() == null) return;
	if(e.getCurrentItem().getItemMeta() == null) return;
	if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

	if(!(e.getWhoClicked() instanceof Player)) return;
	KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
	if(kp.getKingdom() == null) return;

	Kingdom kingdom = kp.getKingdom();
	if(e.getInventory().getName() == null) return;
	if(e.getInventory().getName().equals(ChatColor.AQUA + "Warp Pad")){
	  e.setCancelled(true);
	  List<String> lores = e.getCurrentItem().getItemMeta().getLore();
	  String displayName = e.getCurrentItem().getItemMeta().getDisplayName();

	  if(lores != null && displayName != null){
		if(displayName.equals(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Title", kp.getLang()))){
		  OutpostGUIManager.renameLand(kp, StructureManager.selected.get(kp));
		  return;
		}
		Location loc = stringToLocation(displayName);
		if(loc != null){
		  kp.getPlayer().teleport(loc.add(0.3, 0, 0.3));
		  kp.getPlayer().closeInventory();
		}
	  }

	}
  }

  public void openMenu(KingdomPlayer kp){
	Land land = StructureManager.selected.get(kp);
	Kingdom kingdom = kp.getKingdom();

	Inventory outpostinv = Bukkit.createInventory(null, 54,
		ChatColor.AQUA + "Warp Pad");

	for(Land loopland : WarpPadManager.getOutposts(kp.getKingdom())){
	  if(loopland == null) continue;
	  if(loopland.getStructure() == null) continue;
	  if(loopland.getStructure().getLoc() == null) continue;
	  ItemStack i3 = new ItemStack(Materials.END_PORTAL_FRAME.parseMaterial());
	  ItemMeta i3m = i3.getItemMeta();
	  //Store this as a string variable
	  String locationString = locationToString(loopland.getStructure().getLoc().toLocation().add(0, 1, 0));
	  //If we need to be hiding the warp pad location then proceed to use the invisible text method
	  if(Config.getConfig().getBoolean("hidewarppadlocations")){
	    locationString = hidString(locationString);
	  }
	  i3m.setDisplayName(ChatColor.AQUA + "[" + ChatColor.YELLOW + locationString + ChatColor.AQUA + "]");
	  ArrayList i3l = new ArrayList();
	  String type = null;
	  if(loopland.getStructure().getType() == StructureType.OUTPOST){
		type = ChatColor.AQUA + "[" + ChatColor.YELLOW + Kingdoms.getLang().getString("Structures_Outpost", kp.getLang()) + ChatColor.AQUA + "]";
	  }
	  else if(loopland.getStructure().getType() == StructureType.WARPPAD){
		type = ChatColor.AQUA + "[" + ChatColor.YELLOW + Kingdoms.getLang().getString("Structures_WarpPad", kp.getLang()) + ChatColor.AQUA + "]";
	  }
	  else if(loopland.getStructure().getType() == StructureType.NEXUS){
		type = ChatColor.AQUA + "[" + ChatColor.YELLOW + Kingdoms.getLang().getString("Structures_Nexus", kp.getLang()) + ChatColor.AQUA + "]";
	  }
	  if(loopland.getStructure().getType() != StructureType.NEXUS)
		i3l.add(ChatColor.AQUA + "[" + ChatColor.YELLOW + loopland.getName() + ChatColor.AQUA + "]");
	  i3l.add(Kingdoms.getLang().getString("Guis_WarpPad_Click_To_Tp", kp.getLang()));
	  i3l.add(ChatColor.YELLOW + type);
	  if(loopland.equals(GameManagement.getLandManager().getOrLoadLand(kp.getLoc())))
		i3l.add(Kingdoms.getLang().getString("Guis_WarpPad_You_Are_Here", kp.getLang()));
	  i3m.setLore(LoreOrganizer.organize(i3l));
	  i3.setItemMeta(i3m);
	  outpostinv.addItem(i3);
	}
	ItemStack i6 = new ItemStack(Material.PAPER);
	ItemMeta i6m = i6.getItemMeta();
	i6m.setDisplayName(Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Title", kp.getLang()));
	ArrayList i6l = new ArrayList();
	i6l.add(ChatColor.GREEN + Kingdoms.getLang().getString("Guis_Outpost_Rename_Land_Desc", kp.getLang()).replaceAll("%name%", land.getName()));
	i6m.setLore(LoreOrganizer.organize(i6l));
	i6.setItemMeta(i6m);
	outpostinv.addItem(i6);


	kp.getPlayer().openInventory(outpostinv);
  }


  public static Location stringToLocation(String key){
	String keyToSplit = ChatColor.stripColor(key).replaceAll("\\[", "").replaceAll("\\]", "");
	String[] split = keyToSplit.split(" , ");
	if(split.length == 4){
	  Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), 0, 0);
	  return loc;
	}
	return null;
  }

  private static String hidString(String unhidden){
    String hidden = "";
    for(String s : unhidden.split("")){
      hidden += "&e" + s;
	}
	return ChatColor.translateAlternateColorCodes('&', hidden);
  }

  public static String locationToString(Location loc){
	if(loc == null){
	  return "none";
	}
	return loc.getWorld().getName() + " , " + (int) loc.getX() + " , " + (int) loc.getY() + " , " + (int) loc.getZ();
  }

  @Override
  public void onDisable(){
	// TODO Auto-generated method stub

  }
}
