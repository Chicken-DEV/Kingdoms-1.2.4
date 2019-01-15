package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomResourcePointChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminRpForAll extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.rpforall")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//args[0] is amount
		String stringNumber = args.poll();
		final CommandSender kp = Bukkit.getConsoleSender();
		try{
			int amount = Integer.parseInt(stringNumber);
			for(OfflineKingdom kingdom:Kingdoms.getManagers().getKingdomManager().getKingdomList().values()){
				Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName()).setResourcepoints(kingdom.getResourcepoints() + amount);
				if(amount >= 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Added_Success").replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Deducted_Success").replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}
			
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Not_Number"));
		}
		
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		//args[0] is amount
		String stringNumber = args.poll();
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		try{
			int amount = Integer.parseInt(stringNumber);
			for(OfflineKingdom kingdom:Kingdoms.getManagers().getKingdomManager().getKingdomList().values()){
				Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName()).setResourcepoints(kingdom.getResourcepoints() + amount);
				Bukkit.getPluginManager().callEvent(new KingdomResourcePointChangeEvent(kingdom));
				if(amount >= 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Added_Success", kp.getLang()).replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Deducted_Success", kp.getLang()).replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}
			
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Not_Number", kp.getLang()));
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
		return Kingdoms.getLang().getString("Command_Help_Admin_Rp_For_All", lang);
	}



}
