package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomMemberLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandKick extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
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
		if(sender.hasPermission("kingdoms.kick")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public synchronized void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getInvite())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getInvite().toString()));
			return;
		}
		
		String targetName = args.poll();
		if(targetName.equalsIgnoreCase(user.getName())){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Kick_Cannot_Kick_Yourself", kp.getLang()));
			return;
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
		if(player == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Kick_Member_Not_Found", kp.getLang()));
			return;
		}
		
		if(!kingdom.getMembersList().contains(player.getUniqueId())){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Kick_Member_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		OfflineKingdomPlayer target = GameManagement.getPlayerManager().getOfflineKingdomPlayer(player);
		if(target.getRank().isHigherThan(kp.getRank())){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Kick_Rank_Error", kp.getLang()));
			return;
		}
		if(target.isOnline()) target = target.getKingdomPlayer();
		kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Command_Kick_Broadcast").replaceAll("%kicker%", user.getName()).replaceAll("%victim%", target.getName()), true);
		if(target.isOnline()) target.getKingdomPlayer().sendMessage(Kingdoms.getLang().getString("Command_Kick_Message", kp.getLang()));
		Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(target, target.getKingdomName()));
	}
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Kick", lang);
	}

}
