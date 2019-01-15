package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandDonate extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public String[] getUsage() {
		return Kingdoms.getLang().getString("Command_Usage_Donate").split(" \\| ");
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.donate")){
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
		//args[0] kingdomtodonate
		//args[1] amount
		String kingdomtodonate = args.poll();
		String stramt = args.poll();
		
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
		try{
			int amount = Integer.parseInt(stramt);
			if(kingdom.getResourcepoints() >= amount && amount > 0){
				Kingdom receiver = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomtodonate);
				if(receiver != null){
					receiver.setResourcepoints(receiver.getResourcepoints() + amount);
					kingdom.setResourcepoints(kingdom.getResourcepoints() - amount);
					kp.sendMessage(Kingdoms.getLang().getString("Command_Donate_Success").replaceAll("%amount%", stramt).replaceAll("%kingdom%", kingdomtodonate));
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Command_Donate_Kingdom_Non_Existant", kp.getLang()));
				}
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", stramt));
			}
		}catch(NumberFormatException e){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Donate_Must_Be_Number", kp.getLang()));
		}
		
	}
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Donate", lang);
	}

}
