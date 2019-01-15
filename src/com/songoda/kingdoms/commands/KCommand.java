package com.songoda.kingdoms.commands;

import java.util.Queue;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface KCommand {
	public void execute(CommandSender sender, Queue<String> args);
	/**
	 * returns number of args. Return -1 if there are more than 2 options
	 * @return
	 */
	public int getArgsAmount();
	
	/**
	 * detailed usage of command
	 * @return
	 */
	public String[] getUsage();
	
	/**
	 * brief description of command
	 * @return
	 */
	public String getDescription(String language);
	
	/**
	 * Checks if the user has the permission to use the command
	 * @return
	 */
	public boolean canExecute(CommandSender sender);
}
