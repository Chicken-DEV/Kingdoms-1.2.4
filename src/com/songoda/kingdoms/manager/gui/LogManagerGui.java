package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Lang;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class LogManagerGui extends Manager {

	protected LogManagerGui(Plugin plugin) {
		super(plugin);

	}


	@EventHandler
	public void onInvMove(InventoryClickEvent event){
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) event.getWhoClicked());
		if(event.getInventory().getName() != null){
			if(event.getInventory().getName().equals(Kingdoms.getLang().getString("Guis_Log_Title",kp.getLang()))){
				event.setCancelled(true);
				if(event.getCurrentItem() == null) return;
				if(event.getCurrentItem().getItemMeta() == null) return;
				if(event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
				if(!event.getCurrentItem().getItemMeta().getDisplayName().equals(Kingdoms.getLang().getString("Guis_Log_DeleteAll"))) return;
				kp.getKingdom().clearInvasionLog();
				openMenu(kp);
			}
		}
	}

	public void openMenu(KingdomPlayer kp) {

		if (kp.getKingdom() == null)
			return;
		Kingdom kingdom = kp.getKingdom();
		ArrayList<ItemStack> invasions = new ArrayList<ItemStack>();
		ArrayList<ItemStack> invasionsFinal = new ArrayList<ItemStack>();
		Set<Entry<String, String>> entries = kingdom.getInvasionLog().entrySet();
		for(Entry<String,String> entry:entries){
			String date = entry.getKey();
			String s = entry.getValue();
			
			invasions.add(getButtonForLog(date, s, kingdom));
		}
		
//		for(int i = invasions.size()-1;i>=0;i--){
//			invasionsFinal.add(invasions.get(i));
//		}
		invasions.add(makeButton(Materials.WHITE_STAINED_GLASS_PANE.parseMaterial(), DyeColor.RED,Kingdoms.getLang().getString("Guis_Log_DeleteAll", kp.getLang())));
		new ScrollerInventory(invasions, Kingdoms.getLang().getString("Guis_Log_Title",kp.getLang()), kp.getPlayer());
	}
	
	private ItemStack getButtonForLog(String date, String s, Kingdom kingdom){
		String[] split = s.split(",");
		DyeColor color = DyeColor.LIME;
		String victory = Kingdoms.getLang().getString("Guis_Log_Victory");
		if(split[2].equals("false")){
			victory = Kingdoms.getLang().getString("Guis_Log_Defeat");
			color = DyeColor.RED;
		}
		ChatColor defender = ChatColor.GREEN;
		ChatColor invader = ChatColor.RED;
		if(!split[0].equals(kingdom.getKingdomName())){
			defender = ChatColor.RED;
		    invader = ChatColor.GREEN;
		}

	    String stringDate = date;
		ItemStack item = makeButton(Materials.WHITE_WOOL.parseMaterial(), color, ChatColor.YELLOW + stringDate,
				victory,
				defender + Kingdoms.getLang().getString("Guis_Log_Defending") + split[0],
				invader + Kingdoms.getLang().getString("Guis_Log_Invader") + split[1],
				Kingdoms.getLang().getString("Guis_Log_Land") + split[3]+","+split[4]+","+split[5]);
				
		return item;
	}

	private ItemStack makeButton(Material mat, DyeColor color, String title,
			String... lore) {
		ItemStack IS = new ItemStack(mat, 1, color.getWoolData());
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(title);
		ArrayList<String> l = new ArrayList<String>();
		for (String lores : lore)
			l.add(lores);
		IM.setLore(l);
		IS.setItemMeta(IM);
		return IS;
	}

	@Override
	public void onDisable() {

	}

}
