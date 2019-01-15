package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandDemote extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 1;
	}

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.decline")){
			return true;
		}
		
		return false;
	}
	
	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Demote_Usage1")
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Demote_Description", lang);
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		//check if king
		//check if setting king as mod
		//check if player in in the kingdom
		//check if player is not mod/general
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		if(kp.getKingdom() == null){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		Kingdom kingdom = kp.getKingdom();
		
		if(!kp.getRank().isHigherOrEqualTo(Rank.KING)){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", Rank.KING.toString()));
			return;
		}
		
		String targetName = args.poll();
		OfflinePlayer targetP = Bukkit.getOfflinePlayer(targetName);
		if(targetP == null){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found", kp.getLang()));
			return;
		}
		
		OfflineKingdomPlayer target = GameManagement.getPlayerManager().getOfflineKingdomPlayer(targetP);
		if(target == null){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found", kp.getLang()));
			return;
		}
		if(target.getKingdomName() == null || !target.getKingdomName().equals(kingdom.getKingdomName())){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Player_Not_In", kp.getLang()));
			return;
		}
		
		if(target.getRank() == Rank.ALL|| target.getRank() == Rank.KING){
			Kingdoms.getInstance();
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Mod", kp.getLang()));
			return;
		}
		
		Kingdoms.getInstance();
		kp.sendMessage(ChatColor.GREEN + Kingdoms.getLang().getString("Command_Demote_Broadcast", kp.getLang()).replaceAll("%player%", target.getName()));
		kp.getKingdom().sendAnnouncement(null, Kingdoms.getLang().getString("Command_Demote_Broadcast", kp.getLang()).replaceAll("%player%", target.getName()), true);
		target.setRank(Rank.ALL);
	}

}
