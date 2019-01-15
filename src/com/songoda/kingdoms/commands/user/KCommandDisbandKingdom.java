package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandDisbandKingdom extends KCommandBase {
	//chat if in kingdom -> rank check (king only) -> 
	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String[] getUsage() {
		return new String[]{"/k disband"};
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
		if(sender.hasPermission("kingdoms.disband")){
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
		
		if(kp.getRank() != Rank.KING){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%","king"));
			return;
		}
		String kingdomname = kingdom.getKingdomName();
		
		if(kp.isConfirmed("disband")){
			if(GameManagement.getKingdomManager().deleteKingdom(kingdom.getKingdomName(), kp)){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Disband_Success", kp.getLang()).replaceAll("%kingdom%", kingdomname));
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Previous_Request_Processing", kp.getLang()));
			}
		}else{
			kp.setConfirmed("disband");
			kp.sendMessage(Kingdoms.getLang().getString("Command_Disband_DoNotMove", kp.getLang()));
			kp.sendMessage(Kingdoms.getLang().getString("Command_Disband_TypeAgain", kp.getLang()));
		}

	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Disband", lang);
	}

}
