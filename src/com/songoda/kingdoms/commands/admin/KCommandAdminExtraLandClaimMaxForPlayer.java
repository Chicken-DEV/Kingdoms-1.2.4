package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminExtraLandClaimMaxForPlayer extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.extralandclaimmax")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {

		//args[0] is player
		//args[1] is amount
		String playerName = args.poll();
		String stringNumber = args.poll();
		//final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final CommandSender kp = Bukkit.getConsoleSender();
		OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
		if(op == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaimsForPlayer_Player_Doesnt_Exist"));
			return;
		}
		OfflineKingdomPlayer okp = Kingdoms.getManagers().getPlayerManager().getOfflineKingdomPlayer(op);
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
		try{
			int amount = Integer.parseInt(stringNumber);
			if(kingdom != null){
				kingdom.setExtraLandClaims(kingdom.getExtraLandClaims() + amount);
				if(amount >= 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Added_Success").replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Deducted_Success").replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaimsForPlayer_Player_No_Kingdom"));
			}
			
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Not_Number"));
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
		String playerName = args.poll();
		String stringNumber = args.poll();
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		//final CommandSender kp = Bukkit.getConsoleSender();
		OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
		if(op == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaimsForPlayer_Player_Doesnt_Exist", kp.getLang()));
			return;
		}
		OfflineKingdomPlayer okp = Kingdoms.getManagers().getPlayerManager().getOfflineKingdomPlayer(op);
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
		try{
			int amount = Integer.parseInt(stringNumber);
			if(kingdom != null){
				kingdom.setExtraLandClaims(kingdom.getExtraLandClaims() + amount);
				if(amount >= 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Added_Success", kp.getLang()).replaceAll("%amount%", "" + amount).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}else if(amount < 0){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Deducted_Success", kp.getLang()).replaceAll("%amount%", "" + (amount*-1)).replaceAll("%kingdom%", kingdom.getKingdomName()));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaimsForPlayer_Player_No_Kingdom", kp.getLang()));
			}
			
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_ExtraLandClaims_Not_Number", kp.getLang()));
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
		return Kingdoms.getLang().getString("Command_Help_Admin_ExtraLandClaimsForPlayer", lang);
	}



}
