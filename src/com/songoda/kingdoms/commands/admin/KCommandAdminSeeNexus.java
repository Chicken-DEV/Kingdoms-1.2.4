package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminSeeNexus extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin seenexus [kingdom]" + ChatColor.GRAY+" - "
					+ChatColor.WHITE+"[kingdom] for kingdom name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_SeeNexus", lang);
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.seenexus")){
			return true;
		}
		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender user = Bukkit.getConsoleSender();
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player p, Queue<String> args) {
		String targetKName = args.poll();
		//0 target kname
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);
		if(targetKName == null){
			for(String usage:getUsage()){
				kp.sendMessage(usage);
			}
			return;
		}
		
		Kingdom kingdom = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(targetKName);
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_SeeNexus_Kingdom_Not_Found", kp.getLang()));
			return;
		}
		Kingdoms.getGuiManagement().getNexusGUIManager().openKingdomChest(kp, kingdom);
	}

}
