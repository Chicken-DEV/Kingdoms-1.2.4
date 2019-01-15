package com.songoda.kingdoms.commands.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.NamingException;

import com.songoda.kingdoms.database.DatabaseTransferTask;
import com.songoda.kingdoms.database.DatabaseTransferTask.TransferPair;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminImport extends KCommandBase {
	Set<String> dbTypes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER){{
		add("mysql");
		add("file");
	}};
	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"db types : "+dbTypes,
				"USAGE:",
				"If you type [/k admin import file], Data will be copied from your file database to your sql database.",
				"If you type [/k admin import sql], Data will be copied from your sql database to your file database."
		};
	}

    @Override
	public String getDescription(String lang) {
		return "/k admin import <from>";
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		String fromName = args.poll();
		if(!dbTypes.contains(fromName)){
			Kingdoms.logInfo("Invalid db type -- "+fromName);
			return;
		}
		Bukkit.getConsoleSender().sendMessage("Stopping autosave...");
		Kingdoms.getManagers().getLandManager().stopAutoSave();
		Kingdoms.getManagers().getPlayerManager().stopAutoSave();
		Kingdoms.getManagers().getKingdomManager().stopAutoSave();
		
		
		List<TransferPair> pairs = new ArrayList<TransferPair>();
		if(fromName.equalsIgnoreCase("mysql")){
			try {
				pairs.add(GameManagement.getPlayerManager().getTransferPair(GameManagement.getPlayerManager().createMysqlDB()));
				pairs.add(GameManagement.getKingdomManager().getTransferPair(GameManagement.getKingdomManager().createMysqlDB()));
				pairs.add(GameManagement.getLandManager().getTransferPair(GameManagement.getLandManager().createMysqlDB()));
				Bukkit.getConsoleSender().sendMessage("[Kingdoms] Importing data from sql to flatfile. In the meantime, auto saving will be stopped.");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
					| NamingException e) {
				e.printStackTrace();
			}
		}else{
			pairs.add(GameManagement.getPlayerManager().getTransferPair(GameManagement.getPlayerManager().createFileDB()));
			pairs.add(GameManagement.getKingdomManager().getTransferPair(GameManagement.getKingdomManager().createFileDB()));
			pairs.add(GameManagement.getLandManager().getTransferPair(GameManagement.getLandManager().createFileDB()));
			Bukkit.getConsoleSender().sendMessage("[Kingdoms] Importing data from flatfile to sql. In the meantime, auto saving will be stopped.");
		}
		
		
		
		new Thread(new DatabaseTransferTask(Kingdoms.getInstance(), pairs)).start();
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		op.sendMessage(ChatColor.RED + "Use from console.");
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		user.sendMessage(ChatColor.RED + "Use from console.");
	}

}
