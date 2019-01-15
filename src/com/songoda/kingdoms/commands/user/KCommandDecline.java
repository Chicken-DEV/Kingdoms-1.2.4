package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandDecline extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getUsage() {
		return Kingdoms.getLang().getString("Command_Usage_Decline").split(" \\| ");
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
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Decline_Success", kp.getLang()).replaceAll("%kingdom%", kp.getInvited().getKingdomName()));
		kp.setInvited(null);
	}

	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Decline", lang);
	}
}
