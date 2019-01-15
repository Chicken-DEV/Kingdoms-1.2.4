package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminReload extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.reload")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		Kingdoms.getInstance().reload();
		Bukkit.getConsoleSender().sendMessage(Kingdoms.getLang().getString("Command_Reload_Success"));
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandConsole(args);
		op.sendMessage(Kingdoms.getLang().getString("Command_Reload_Success", Kingdoms.getManagers().getPlayerManager().getSession(op).getLang()));
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		executeCommandConsole(args);
		user.sendMessage(Kingdoms.getLang().getString("Command_Reload_Success", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
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
		return Kingdoms.getLang().getString("Command_Help_Reload", lang);
	}



}
