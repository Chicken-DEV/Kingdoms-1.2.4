package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomMemberLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandLeave extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.leave")){
			return true;
		}
		
		return false;
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
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		Kingdom k = kp.getKingdom();
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		if(kp.getRank() == Rank.KING){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Leave_King_Error", kp.getLang()));
			return;
		}
		kp.getKingdom().sendAnnouncement(null, Kingdoms.getLang().getString("Command_Leave_Broadcast", kp.getLang()).replaceAll("%player%", kp.getPlayer().getName()), true);
		kp.sendMessage(Kingdoms.getLang().getString("Command_Leave_Success", kp.getLang()).replaceAll("%kingdom%", kp.getKingdom().getKingdomName()));
		kp.setKingdom(null);
		kp.setRank(Rank.ALL);
		
		Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(kp, k.getKingdomName()));
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Leave", lang);
	}

}
