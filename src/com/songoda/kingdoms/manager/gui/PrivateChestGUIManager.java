package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.KChestSign;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class PrivateChestGUIManager extends Manager implements Listener{

	private static final String TITLE = ChatColor.AQUA+"Manage the users of this chest";

	protected PrivateChestGUIManager(Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onClickButton(InventoryClickEvent e){
		if(e.getInventory() == null) return;
		if(e.getInventory().getTitle() == null) return;
		if(!e.getInventory().getTitle().equals(TITLE)) return;
		
		e.setCancelled(true);
		if(!GUIManagement.allowedActions.contains(e.getAction())) return;
		
		ItemStack clickedItem = e.getCurrentItem();
		if(clickedItem.getItemMeta() == null) return;
		if(clickedItem.getItemMeta().getDisplayName() == null) return;
		if(clickedItem.getItemMeta().getLore() == null) return;
		
		if(!(e.getWhoClicked() instanceof Player)) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getWhoClicked());
		
		if(kp.getModifyingSign() == null) return;
		KChestSign sign = kp.getModifyingSign();
		
		if (clickedItem.getItemMeta().getLore()
				.contains(Kingdoms.getLang().getString("Guis_privateSignGUI_PrivateSign", kp.getLang()))) {

			String playerName = clickedItem.getItemMeta().getDisplayName();
			OfflinePlayer offp = Bukkit.getOfflinePlayer(playerName);
			if(offp == null){
				
			}else if(clickedItem.getData().getData() == DyeColor.RED.getWoolData()){
				if(!sign.getOwners().contains(offp.getUniqueId()))
					sign.getOwners().add(offp.getUniqueId());
			}else{
				sign.getOwners().remove(offp.getUniqueId());
			}
		}
		
		openMenu(kp);
	}
/*	
	@EventHandler
	public void onCloseSignModifier(InventoryCloseEvent e){
		if(e.getInventory().getTitle() == null) return;
		if(!e.getInventory().getTitle().equals(TITLE)) return;
		if(!(e.getPlayer() instanceof Player)) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) e.getPlayer());
		kp.setModifyingSign(null);
	}*/
	
	/**
	 * Need to set modifyingsign first ex) KingdomPlayer.setModifyingSign(KChestSign)
	 * @param kp
	 */
	public void openMenu(KingdomPlayer kp) {
		if(kp.getModifyingSign() == null) return;
		
		if(kp.getKingdom() == null) return;
		Kingdom kingdom = kp.getKingdom();

		ArrayList<ItemStack> members = new ArrayList<ItemStack>();
		for(UUID uuid : kingdom.getMembersList()){
			if(uuid.equals(kp.getUuid())) continue;
			
			OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(uuid);
			if(okp == null) continue;
			
			members.add(getByStatus(kp.getModifyingSign(), okp));
		}

		new ScrollerInventory(members, TITLE, kp.getPlayer());
	}
		
	private ItemStack getByStatus(KChestSign sign, OfflineKingdomPlayer okp){
		if(sign.getOwners().contains(okp.getUuid())){
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.GREEN.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_privateSignGUI_PrivateSign"),
					Kingdoms.getLang().getString("Guis_privateSignGUI_canOpenChest"
					));
		}else{
			return makeButton(Materials.WHITE_WOOL.parseMaterial(), DyeColor.RED.getWoolData(),
					okp.getName(),
					Kingdoms.getLang().getString("Guis_privateSignGUI_PrivateSign"),
					Kingdoms.getLang().getString("Guis_privateSignGUI_cannotOpenChest"
                    ));
		}
	}
	
	private ItemStack makeButton(Material mat, byte id, String title, String btnType, String... lore){
		ItemStack IS = new ItemStack(mat, 1, id);
		ItemMeta IM = IS.getItemMeta();
		IM.setDisplayName(title);
		ArrayList<String> l = new ArrayList<String>();
		for(String lang : lore) l.add(lang);
		l.add(btnType);
		IM.setLore(l);
		IS.setItemMeta(IM);
		return IS;
	}
	
	@Override
	public void onDisable() {
		
	}

}
