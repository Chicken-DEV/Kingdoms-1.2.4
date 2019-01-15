package com.songoda.kingdoms.commands.user;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandTop extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(ChatColor.GRAY+"======== " + Kingdoms.getLang().getString("Command_Top_Header") +ChatColor.GRAY+ " ========");
		int rank = 1;
		for(Map.Entry<String, Integer> entry  : GameManagement.getTopManager().getTopList().entrySet()){
			sender.sendMessage(ChatColor.GREEN + "" + rank + ". " + GameManagement.getKingdomManager().getOrLoadKingdom(entry.getKey()).getKingdomName() + ChatColor.GRAY + " - "
					+ ChatColor.GOLD + entry.getValue());
			
			rank++;
		}
	}

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.top")){
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
		user.sendMessage(ChatColor.GRAY+"======== " + Kingdoms.getLang().getString("Command_Top_Header", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()) +ChatColor.GRAY+ " ========");
		int rank = 1;
		for(Map.Entry<String, Integer> entry  : GameManagement.getTopManager().getTopList().entrySet()){
			Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(UUID.fromString(entry.getKey()));
			if (k==null)continue;
			user.sendMessage(ChatColor.GREEN + "" + rank + ". " + ((Kingdom) k).getKingdomName() + ChatColor.GRAY + " - "
					+ ChatColor.GOLD + entry.getValue());
			
			rank++;
		}
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_List", lang);
	}

}
