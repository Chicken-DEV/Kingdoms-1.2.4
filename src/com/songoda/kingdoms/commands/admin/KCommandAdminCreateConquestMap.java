package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;


public class KCommandAdminCreateConquestMap extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin createconquestmap [name]" + ChatColor.GRAY+" - "
					+org.bukkit.ChatColor.WHITE+"[name] for map name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_CreateConquestMap", lang);
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.createconquestmap")){
			return true;
		}
		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		String mapName = args.poll();
		//0 target kname
		if(Kingdoms.getManagers().getConquestManager() == null){
			sender.sendMessage("Either Conquests is not enabled in config, or you don't have worldedit.");
			return;
		}
		if(Kingdoms.getManagers().getConquestManager().createNewConquestMap(mapName)){
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_CreateConquestMap_Success"));
		}else{
			sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_CreateConquestMap_Failure"));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				sender.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
			}
		}
	
	
	
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player p, Queue<String> args) {
		String mapName = args.poll();
		//0 target kname
		if(Kingdoms.getManagers().getConquestManager() == null){
			p.sendMessage("Either Conquests is not enabled in config, or you don't have worldedit.");
			return;
		}
		if(Kingdoms.getManagers().getConquestManager().createNewConquestMap(mapName)){
			p.sendMessage(Kingdoms.getLang().getString("Command_Admin_CreateConquestMap_Success", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
		}else{
			p.sendMessage(Kingdoms.getLang().getString("Command_Admin_CreateConquestMap_Failure", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				p.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
			}
		}
	
	
	}
	
	private String capitalize(String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
