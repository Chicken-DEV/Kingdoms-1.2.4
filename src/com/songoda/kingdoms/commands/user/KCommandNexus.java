package com.songoda.kingdoms.commands.user;

import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandNexus extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.nexus")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getNexus())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getNexus().toString()));
			return;
		}
		
		Land land = GameManagement.getLandManager().getOrLoadLand(kp.getLoc());
		UUID owner = land.getOwnerUUID();
		if(owner == null || !owner.equals(kp.getKingdom().getKingdomUuid())){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Nexus_Land_Not_Owned_Error", kp.getLang()));
			return;
		}
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Nexus_Setting_Activated", kp.getLang()));
		GameManagement.getNexusManager().startNexusSet(kp);
	}

	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Nexus", lang);
	}
	
}
