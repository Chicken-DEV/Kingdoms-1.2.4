package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommand;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminHelp extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.help")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		args.poll();
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(ChatColor.GRAY + "======== " + ChatColor.GOLD + "Kingdoms+ Admin Commands" + ChatColor.GRAY + " ========");
		for(String s:Kingdoms.getCmdExe().getAdminCommands().keySet()){
			KCommand c = Kingdoms.getCmdExe().getAdminCommands().get(s);
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/k admin " + s + " - " + ChatColor.WHITE + c.getDescription(null));
		}
	}
	
	private String getColoredString(String command, String desc){
		return ChatColor.GOLD+command+ChatColor.WHITE+" - "+ChatColor.WHITE+desc;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		args.poll();
		user.sendMessage(ChatColor.GRAY + "======== " + ChatColor.GOLD + "Kingdoms+ Admin Commands" + ChatColor.GRAY + " ========");
		for(String s:Kingdoms.getCmdExe().getAdminCommands().keySet()){
			KCommand c = Kingdoms.getCmdExe().getAdminCommands().get(s);
			user.sendMessage(ChatColor.LIGHT_PURPLE + "/k admin " + s + " - " + ChatColor.WHITE + c.getDescription(Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
		}
	}

	@Override
	public String[] getUsage() {
		
		return null;
	}

	@Override
	public int getArgsAmount() {
		return -1;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_Help", lang);
	}



}
