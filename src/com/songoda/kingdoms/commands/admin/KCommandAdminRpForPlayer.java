package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomResourcePointChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminRpForPlayer extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.rpforplayer")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//args[0] is player
		//args[1] is amount
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		String offlineplayername = args.poll();
		String stringNumber = args.poll();
		final OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(offlineplayername);
		if(offlineplayer == null){
			return;
		}
		final OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(offlineplayer);
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
		try{
			int amount = Integer.parseInt(stringNumber);
			if(kingdom != null){
				kingdom.setResourcepoints(kingdom.getResourcepoints() + amount);
				if(amount >= 0){
					sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Added_Success").replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Deducted_Success").replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}else{
				sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rpforplayer_Player_Has_No_Kingdom"));
			}
			
		}catch(NumberFormatException e){
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Not_Number"));
		}
		
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		//args[0] is player
		//args[1] is amount
		String offlineplayername = args.poll();
		String stringNumber = args.poll();
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(offlineplayername);
		if(offlineplayer == null){
			return;
		}
		final OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(offlineplayer);
		if(okp == null){
			return;
		}
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
		try{
			int amount = Integer.parseInt(stringNumber);
			if(kingdom != null){
				kingdom.setResourcepoints(kingdom.getResourcepoints() + amount);
				Bukkit.getPluginManager().callEvent(new KingdomResourcePointChangeEvent(kingdom));
				if(amount >= 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Added_Success", kp.getLang()).replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rp_Deducted_Success", kp.getLang()).replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Rpforplayer_Player_Has_No_Kingdom", kp.getLang()));
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
		return 2;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_RpforPlayer", lang);
	}



}
