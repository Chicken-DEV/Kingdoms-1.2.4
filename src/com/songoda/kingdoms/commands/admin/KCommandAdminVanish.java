package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminVanish extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.vanish")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage("[Kingdoms]: This command can only be used ingame!");
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		kp.setVanishMode(!kp.isVanishMode());
		if(kp.isVanishMode()){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Vanish_True", kp.getLang()));
		}else kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Vanish_False", kp.getLang()));
	}

	@Override
	public String[] getUsage() {
		
		return null;
	}

	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_Vanish", lang);
	}



}
