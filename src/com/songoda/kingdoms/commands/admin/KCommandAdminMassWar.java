package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminMassWar extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.masswar")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//args[0] is time
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		String strtime = args.poll();
		if(!GameManagement.getMasswarManager().isMassWarOn()){
			try{
				int time = Integer.parseInt(strtime)*60;
				GameManagement.getMasswarManager().startMassWar(time);
				sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Masswar_Success").replaceAll("%time%", "" + time/60));
				
			}catch(NumberFormatException e){
				sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Masswar_Not_Number"));
			}
		}
		
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		//args[0] is time
		String strtime = args.poll();
		if(!GameManagement.getMasswarManager().isMassWarOn()){
			try{
				int time = Integer.parseInt(strtime)*60;
				GameManagement.getMasswarManager().startMassWar(time);
				user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Masswar_Success", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()).replaceAll("%time%", "" + time/60));
				
				
			}catch(NumberFormatException e){
				user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Masswar_Not_Number", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
			}
		}
		
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
		return Kingdoms.getLang().getString("Command_Help_Admin_MassWar", lang);
	}



}
