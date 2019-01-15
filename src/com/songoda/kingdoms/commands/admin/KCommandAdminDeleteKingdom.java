package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminDeleteKingdom extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.disband")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> arguments) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();

		//args[0] is admin
		//args[1] is forcedisband
		//args[2] is the kingdom
		String kingdomName = arguments.poll();
		Kingdoms.logDebug(kingdomName);
		Kingdoms.getManagers().getKingdomManager().debugDeleteKingdom(kingdomName);
		sender.sendMessage("File deleted.");
		
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> arguments) {
		//args[0] is the kingdom
		String kingdomName = arguments.poll();
		Kingdoms.getManagers().getKingdomManager().debugDeleteKingdom(kingdomName);
		user.sendMessage("File deleted.");
		
		
		
	}

	@Override
	public String[] getUsage() {
		
		return null;
	}

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_Disband", lang);
	}



}
