package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.manager.game.DataZipper;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminBackup extends KCommandBase {
	

	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.backup")){
			return true;
		}
		return false;
	}
	

	@Override
	public void executeCommandConsole(Queue<String> args) {
		
		DataZipper.AppZip.main();
		
	}
	
	private String getColoredString(String command, String desc){
		return ChatColor.GOLD+command+ChatColor.WHITE+" - "+ChatColor.WHITE+desc;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		op.sendMessage("Backing up...");
		executeCommandUser(op, args);
		op.sendMessage("Backup finished.");
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		
		executeCommandConsole(args);
		
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
		return Kingdoms.getLang().getString("Command_Help_Admin_Backup", lang);
	}

}
