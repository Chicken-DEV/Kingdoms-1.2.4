package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminPacifistToggle extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.pacifisttoggle")){
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
		OfflineKingdom kingdom = GameManagement.getKingdomManager().getOfflineKingdom(kingdomName);
		if(kingdom != null){

			if(kingdom.isNeutral()){
				kingdom.setNeutral(false);
				sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_PacifistToggle_False").replaceAll("%kingdom%", kingdomName));
			}else{
				kingdom.setNeutral(true);
				sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_PacifistToggle_True").replaceAll("%kingdom%", kingdomName));
			}
		
		}
		
		
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> arguments) {
		//args[0] is the kingdom
		String kingdomName = arguments.poll();
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		OfflineKingdom kingdom = GameManagement.getKingdomManager().getOfflineKingdom(kingdomName);
		if(kingdom != null){

			if(!Config.getConfig().getBoolean("allow-pacifist")){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_PacifistToggle_Warning", kp.getLang()));
			}
			if(kingdom.isNeutral()){
				kingdom.setNeutral(false);
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_PacifistToggle_False", kp.getLang()).replaceAll("%kingdom%", kingdomName));
			}else{
				kingdom.setNeutral(true);
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_PacifistToggle_True", kp.getLang()).replaceAll("%kingdom%", kingdomName));
			}
		
		}else{
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Disband_KingdomNoutFound", kp.getLang()));
		}
		
		
	}

	@Override
	public String[] getUsage() {
		
		return  null;
	}

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_PacifistToggle", lang);
	}



}
