package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandMarkers extends KCommandBase {

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
		if(sender.hasPermission("kingdoms.markers")){
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
		
		if(kp.isMarkDisplaying()){
			kp.setMarkDisplaying(false);
			
			kp.sendMessage(Kingdoms.getLang().getString("Command_Markers_Disabled", kp.getLang()));
		}else{
			kp.setMarkDisplaying(true);
			
			kp.sendMessage(Kingdoms.getLang().getString("Command_Markers_Enabled", kp.getLang()));
		}
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Markers", lang);
	}

}
