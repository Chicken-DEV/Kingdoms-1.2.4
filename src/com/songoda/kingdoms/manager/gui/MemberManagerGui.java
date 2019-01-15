package com.songoda.kingdoms.manager.gui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class MemberManagerGui extends Manager {

	protected MemberManagerGui(Plugin plugin) {
		super(plugin);
		TITLE = Kingdoms.getLang().getString("Guis_Member_Manager_Title");
	}

	private final String TITLE;
	
	@EventHandler
	public void onInvMove(InventoryClickEvent event){
		if(event.getInventory().getName() != null){
			if(event.getInventory().getName().equals(TITLE)){
				event.setCancelled(true);
			}
		}
	}

	public void openMenu(KingdomPlayer kp) {

		if (kp.getKingdom() == null)
			return;
		Kingdom kingdom = kp.getKingdom();

		ArrayList<ItemStack> members = new ArrayList<ItemStack>();
		for (UUID uuid : kingdom.getMembersList()) {

			OfflineKingdomPlayer okp = GameManagement.getPlayerManager()
					.getOfflineKingdomPlayer(uuid);
			if (okp == null)
				continue;

			members.add(getByInfo(okp));
		}

		new ScrollerInventory(members, TITLE, kp.getPlayer());
	}

	public ItemStack getByInfo(OfflineKingdomPlayer player) {
		DyeColor color = null;

		if(player.getLastTimeDonated() != null){	
			Calendar cal = Calendar.getInstance();
			cal.setTime(player.getLastTimeDonated());
			cal.add(Calendar.DAY_OF_YEAR, +5);
			Date future = cal.getTime();
	
			if (new Date().after(future)) {
				color = DyeColor.RED;
			}else{
				color = DyeColor.LIME;
				if(player.getLastDonatedAmt() < 100){
					color = DyeColor.YELLOW;
				}
			}
			
		}else{
			color = DyeColor.RED;
		}
		String rank = ChatColor.GRAY + "[" + ChatColor.YELLOW
				+ player.getRank().toString() + ChatColor.GRAY + "]";
		String lastTime = Kingdoms.getLang()
				.getString("Guis_Member_NeverDonated");
		if(player.getLastTimeDonated() != null){
			lastTime = DateFormat.getDateInstance(DateFormat.MEDIUM).format(player.getLastTimeDonated());
		}
		ItemStack member = makeButton(
				Materials.WHITE_WOOL.parseMaterial(),
				color,
				rank + " " + ChatColor.AQUA + player.getName(),
				Kingdoms.getLang().getString("Guis_Member_Lore"),
				Kingdoms.getLang()
						.getString("Guis_Member_LastTimeDonated")
						.replaceAll("%lasttime%",lastTime),
				Kingdoms.getLang()
						.getString("Guis_Member_LastTimeDonatedAmt")
						.replaceAll("%amt%", player.getLastDonatedAmt() + ""),
				Kingdoms.getLang()
						.getString("Guis_Member_TotalDonated")
						.replaceAll("%amt%", "" + player.getDonatedAmt()));

		return member;
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
		IM.setLore(l);
		IS.setItemMeta(IM);
		return IS;
	}

	@Override
	public void onDisable() {

	}

}
