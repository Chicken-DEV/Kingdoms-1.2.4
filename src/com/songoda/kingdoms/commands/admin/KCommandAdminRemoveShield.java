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

public class KCommandAdminRemoveShield extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.removeshield")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		//args[0] is kingdom
		String kingdomName = args.poll();
		//final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final CommandSender kp = Bukkit.getConsoleSender();
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
			if(kingdom != null){
				//Bukkit.getPluginManager().callEvent(new KingdomResourcePointChangeEvent(kingdom));
				if(kingdom.isShieldUp()){
					kingdom.removeShield();
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Success"));
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_No_Shield"));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_Doesnt_Exist"));
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
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		//final CommandSender kp = Bukkit.getConsoleSender();
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
			if(kingdom != null){
				//Bukkit.getPluginManager().callEvent(new KingdomResourcePointChangeEvent(kingdom));
				if(kingdom.isShieldUp()){
					kingdom.removeShield();
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Success", kp.getLang()));
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_No_Shield", kp.getLang()));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_RemoveShield_Kingdom_Doesnt_Exist", kp.getLang()));
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
		return Kingdoms.getLang().getString("Command_Help_Admin_RemoveShield", lang);
	}



}
