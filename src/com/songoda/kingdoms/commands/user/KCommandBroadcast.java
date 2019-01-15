package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandBroadcast extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Usage_Broadcast")
		};
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.broadcast")){
			return true;
		}
		if(sender.hasPermission("kingdoms.bc")){
			return true;
		}
		if(sender.hasPermission("kingdoms.broad")){
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
		Kingdom kingdom = kp.getKingdom();
		if(args.size() < 1){
			kp.sendMessage("/k broad" +" - "
					+ (getDescription(kp.getLang())));
			return;
		}
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBroad())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBroad().toString()));
			return;
		}
		
		String message = "";
		
		while(!args.isEmpty()){
			message += args.poll() + " ";
		}
		
		kingdom.sendAnnouncement(kp, message, false);
	}
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Broadcast", lang);
	}

}
