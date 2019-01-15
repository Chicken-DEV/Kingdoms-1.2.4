package com.songoda.kingdoms.commands.user;

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
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandKing extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 1;
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
		if(sender.hasPermission("kingdoms.king")){
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
		
		String targetName = args.poll();
		OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
		if(targetPlayer == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found", kp.getLang()));
			return;
		}
		
		OfflineKingdomPlayer target = GameManagement.getPlayerManager().getOfflineKingdomPlayer(targetPlayer.getUniqueId());
		if(target == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found", kp.getLang()));
			return;
		}
		
		if(!target.getKingdomName().equals(kp.getKingdom().getKingdomName())){
			kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found", kp.getLang()));
			return;
		}
		
		if(target.getRank() == Rank.KING){
			kp.sendMessage(Kingdoms.getLang().getString("Command_King_Already_A_King", kp.getLang()));
			return;
		}
		
		//change committee data
		kp.setRank(Rank.ALL);
		
		//target data
		if(target.isOnline()){// if online
			KingdomPlayer otarget = (KingdomPlayer) target;
			if(otarget.getKingdom() == null){
				kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", kp.getLang()));
				return;
			}
			
			if(!otarget.getKingdom().equals(kingdom)){
				kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", kp.getLang()));
				return;
			}
			
			otarget.setRank(Rank.KING);
		}else{// if offline
			if(target.getKingdomName() == null){
				kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", kp.getLang()));
				return;
			}
			
			if(!target.getKingdomName().equals(kingdom.getKingdomName())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", kp.getLang()));
				return;
			}

			target.setRank(Rank.KING);
		}
		
		//kingdom data
		kingdom.setKing(targetPlayer.getUniqueId());
		
		kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Command_King_Success_Broadcast", kp.getLang()).replaceAll("%player%", targetPlayer.getName()), true);
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_King", lang);
	}

}
