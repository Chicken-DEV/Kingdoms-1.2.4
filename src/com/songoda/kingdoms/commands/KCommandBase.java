package com.songoda.kingdoms.commands;

import java.util.Queue;

import com.songoda.kingdoms.main.Kingdoms;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class KCommandBase implements KCommand{
	@Override
	public void execute(CommandSender sender, Queue<String> args) {
		if(sender == null || sender instanceof ConsoleCommandSender){
			Kingdoms.logDebug("console command");
			executeCommandConsole(args);
		}else{
			Player player = (Player) sender;
			if(player.isOp()){
				Kingdoms.logDebug("op command");
				executeCommandOP(player, args);
			}else{
				Kingdoms.logDebug("user command");
				executeCommandUser(player, args);
			}
		}

	}
	
	public abstract void executeCommandConsole(Queue<String> args);
	public abstract void executeCommandOP(Player op,Queue<String> args);
	public abstract void executeCommandUser(Player user,Queue<String> args);
}
