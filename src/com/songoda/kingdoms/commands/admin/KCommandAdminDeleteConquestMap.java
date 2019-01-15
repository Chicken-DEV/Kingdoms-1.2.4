package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;


public class KCommandAdminDeleteConquestMap extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin deleteconquestmap [name]" + ChatColor.GRAY+" - "
					+ChatColor.WHITE+"[name] for map name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_DeleteConquestMap", lang);
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
		ConsoleCommandSender user = Bukkit.getConsoleSender();
		String mapName = args.poll();
		//0 target kname
		if(Kingdoms.getManagers().getConquestManager() == null){
			user.sendMessage("Either Conquests is not enabled in config, or you don't have worldedit.");
			return;
		}
		if(Kingdoms.getManagers().getConquestManager().deleteConquestMap(mapName)){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_DeleteConquestMap_Success"));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				user.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
			}
		}else{
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_DeleteConquestMap_Failure"));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				user.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
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
		if(Kingdoms.getManagers().getConquestManager().deleteConquestMap(mapName)){
			p.sendMessage(Kingdoms.getLang().getString("Command_Admin_DeleteConquestMap_Success", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				p.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
			}
		}else{
			p.sendMessage(Kingdoms.getLang().getString("Command_Admin_DeleteConquestMap_Success", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			for(String name:Kingdoms.getManagers().getConquestManager().maps.keySet()){
				p.sendMessage(ChatColor.AQUA + " - " + capitalize(name));
			}
		}
	
	
	}
	
	private String capitalize(String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
