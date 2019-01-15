package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandTradable extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.untradable")){
			return true;
		}
		if(sender.hasPermission("kingdoms.tradable")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		if (Config.getConfig().getBoolean("use-whitelist")) {
			user.sendMessage(Kingdoms.getLang().getString("Command_Tradable_Conversion_Ratio_Msg", kp.getLang())
					.replaceAll("%rpi%", "" + Config.getConfig().getInt("items-needed-for-one-resource-point")));
			user.sendMessage(Kingdoms.getLang().getString("Command_Tradable_Enabled_Items_Title", kp.getLang()));
			for (ItemStack mat : Kingdoms.getGuiManagement().getNexusGUIManager().whiteListed.keySet()) {
				String addition = mat.getType().toString();
				if (mat.getDurability() != 0)
					addition += ":" + (int) mat.getDurability();
				user.sendMessage(ChatColor.GREEN + addition + " | "
						+ Kingdoms.getLang().getString("Command_Tradable_Worth") + " "
						+ Kingdoms.getGuiManagement().getNexusGUIManager().whiteListed.get(new ItemStack(mat.getType(), 1, (byte) mat.getDurability()))
						+ " " + Kingdoms.getLang().getString("Command_Tradable_ItemsUnit", kp.getLang()));
			}
		} else {
			user.sendMessage(Kingdoms.getLang().getString("Command_Tradable_Disabled_Items_Title", kp.getLang()));
			for (ItemStack mat : Kingdoms.getGuiManagement().getNexusGUIManager().blackListed) {
				String message = ChatColor.RED + mat.getType().toString();
				if(mat.getDurability() != 0){
					message += ":" + mat.getDurability();
				}
				user.sendMessage(message);
			}
			user.sendMessage("");
			user.sendMessage(Kingdoms.getLang().getString("Command_Tradable_Special_Case_Items_Title", kp.getLang()));
			for (ItemStack mat : Kingdoms.getGuiManagement().getNexusGUIManager().specials.keySet()) {
				String addition = mat.getType().toString();
				if (mat.getDurability() != 0)
					addition += ":" + (int) mat.getDurability();
				user.sendMessage(ChatColor.GREEN + addition + " | "
						+ Kingdoms.getLang().getString("Command_Tradable_Worth", kp.getLang()) + " "
						+ Kingdoms.getGuiManagement().getNexusGUIManager().specials.get(new ItemStack(mat.getType(), 1, (byte) mat.getDurability()))
						+ " " + Kingdoms.getLang().getString("Command_Tradable_ItemsUnit", kp.getLang()));
			}
		}
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Tradable", lang);
	}
}
