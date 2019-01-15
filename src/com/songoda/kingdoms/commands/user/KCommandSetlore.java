package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.utils.EnglishChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandSetlore extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return -1;
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
		if(sender.hasPermission("kingdoms.setlore")){
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
		if(kp.getKingdom() == null){
			
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(!kp.getRank().isHigherOrEqualTo(Rank.KING)){
			
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", "King"));
			return;
		}
		
		String lore = "";
		for(String str : args) lore += str+" ";
		
		if(lore.length() > 30){
			
			kp.sendMessage(Kingdoms.getLang().getString("Command_Setlore_Lore_Too_Long", kp.getLang()));
			return;
		}

		if(lore.contains("$")||lore.contains("%")){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Setlore_Invalid_Word", kp.getLang()));
			return;
		}
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Setlore_Success", kp.getLang()).replaceAll("%lore%", lore));
		kingdom.setKingdomLore(lore);
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Setlore", lang);
	}

}
