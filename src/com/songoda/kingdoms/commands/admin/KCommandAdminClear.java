package com.songoda.kingdoms.commands.admin;

import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminClear extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.clear")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage("[Kingdoms] Only players can execute this command!");
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		
		
		Land land = GameManagement.getLandManager().getOrLoadLand(kp.getLoc());
		UUID owner = land.getOwnerUUID();
		if(owner == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Clear_Land_Empty", kp.getLang()));
			return;
		}
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(owner);
		GameManagement.getLandManager().unclaimLand(kp.getLoc(), kingdom);

		GameManagement.getVisualManager().visualizeLand(kp, land.getLoc());
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Clear_Success", kp.getLang()));
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
		return Kingdoms.getLang().getString("Command_Help_Admin_Unclaim", lang);
	}



}
