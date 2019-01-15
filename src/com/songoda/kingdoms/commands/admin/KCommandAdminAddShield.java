package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminAddShield extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.addshield")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final CommandSender sender = Bukkit.getConsoleSender();
		//args[0] is kingdom
		String kingdomName = args.poll();
		String shieldAmtString = args.poll();
		int shieldAmt = -1;
		try{
			shieldAmt = Integer.parseInt(shieldAmtString);
		}catch(NumberFormatException e){
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_AddShield_InvalidInput"));
			return;
		}
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
		if(kingdom != null){
			kingdom.removeShield();
			kingdom.giveShield(shieldAmt);
		}else{
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_Doesnt_Exist"));
		}
		
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		//args[0] is kingdom
		String kingdomName = args.poll();
		String shieldAmtString = args.poll();
		int shieldAmt = -1;
		try{
			shieldAmt = Integer.parseInt(shieldAmtString);
		}catch(NumberFormatException e){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_AddShield_InvalidInput", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
			return;
		}
		
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		//final CommandSender kp = Bukkit.getConsoleSender();
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
		if(kingdom != null){
			kingdom.removeShield();
			kingdom.giveShield(shieldAmt);
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_AddShield_Success", kp.getLang()));
		}else{
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_Doesnt_Exist", kp.getLang()));
		}
	}

	@Override
	public String[] getUsage() {
		return new String[]{"/k admin addshield [kingdom] [duration minutes]"};
	}

	@Override
	public int getArgsAmount() {
		return 2;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_AddShield", lang);
	}



}
