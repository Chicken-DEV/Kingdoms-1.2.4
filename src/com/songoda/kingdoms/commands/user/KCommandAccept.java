package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAccept extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.accept")){
			return true;
		}
		
		return false;
	}

	@Override
	public String[] getUsage() {
		return Kingdoms.getLang().getString("Command_Usage_Accept").split(" \\| ");
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
		if(kp.getInvited() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Accept_No_Invite_Error", kp.getLang()));
			return;
		}
		
		if(kp.isTemp()){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_KP_Not_Loaded", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getInvited();
		if(kingdom.getMembersList().size() + 1 > kingdom.getMaxMember()){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Accept_Kingdom_Full_Error", kp.getLang()));
			return;
		}
		
		kp.setInvited(null);
		kp.setRank(Rank.ALL);
		kp.setKingdom(kingdom);

		kp.sendMessage(Kingdoms.getLang().getString("Command_Accept_Success", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()));
		if(kingdom.getKingdomLore() != null) kp.sendMessage(kingdom.getKingdomLore());
		Bukkit.getPluginManager().callEvent(new KingdomMemberJoinEvent(kp, kingdom));
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Accept", lang);
	}
	
}
