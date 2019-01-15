package com.songoda.kingdoms.commands.admin;

import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminAllShield extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.allshield")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final CommandSender sender = Bukkit.getConsoleSender();
		String shieldAmtString = args.poll();
		int shieldAmt = -1;
		try{
			shieldAmt = Integer.parseInt(shieldAmtString);
		}catch(NumberFormatException e){
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_AddShield_InvalidInput"));
			return;
		}
		int i = 0;
		for(UUID s:Kingdoms.getManagers().getKingdomManager().getKingdomList().keySet()){
			i++;
			Kingdom k = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(s);
			k.removeShield();
			k.giveShield(shieldAmt);
		}
		
		sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_AllShield_Success") + " [" + i + "]");
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		

		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		String shieldAmtString = args.poll();
		int shieldAmt = -1;
		try{
			shieldAmt = Integer.parseInt(shieldAmtString);
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_AddShield_InvalidInput", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
			return;
		}
		int i = 0;
		for(UUID s:Kingdoms.getManagers().getKingdomManager().getKingdomList().keySet()){
			i++;
			Kingdom k = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(s);
			k.removeShield();
			k.giveShield(shieldAmt);
		}
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_AllShield_Success", kp.getLang()) + " [" + i + "]");
	
	
	}

	@Override
	public String[] getUsage() {
		
		return new String[]{"/k admin addshield [kingdom] [duration minutes]"};
	}

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_AddShield", lang);
	}



}
