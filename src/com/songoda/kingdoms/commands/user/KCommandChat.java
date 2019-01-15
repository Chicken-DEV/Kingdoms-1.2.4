package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.ChatChannel;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandChat extends KCommandBase {

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
		if(sender.hasPermission("kingdoms.chat")){
			return true;
		}
		
		return false;
	}
	
	@Override
	public String[] getUsage() {
		return new String[]{
			Kingdoms.getLang().getString("Command_Chat_Help_Kingdom"),
			Kingdoms.getLang().getString("Command_Chat_Help_Ally"),
			Kingdoms.getLang().getString("Command_Chat_Help_Public")
		};
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
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		String poll = args.poll();
		if(poll.equalsIgnoreCase("k")){
			kp.setChannel(ChatChannel.KINGDOM);
		}else if(poll.equalsIgnoreCase("a")){
			kp.setChannel(ChatChannel.ALLY);
		}else if(poll.equalsIgnoreCase("p")){
			kp.setChannel(ChatChannel.PUBLIC);
		}else{
			kp.setChannel(ChatChannel.PUBLIC);
		}
		
		kp.sendMessage(Kingdoms.getLang().getString("Command_Chat_Switched", kp.getLang()).replaceAll("%channel%", kp.getChannel().toString()));
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Chat", lang);
	}

}
