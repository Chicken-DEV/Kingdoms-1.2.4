package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.BotKingdom;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomMemberJoinEvent;
import com.songoda.kingdoms.events.KingdomMemberLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminJoin extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 2;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin join [name] [kingdom]" + ChatColor.GRAY+" - "
					+ChatColor.WHITE+"[name] for target player;[kingdom] for kingdom name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("command_Help_Admin_Join", lang);
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.join")){
			return true;
		}
		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		ConsoleCommandSender user = Bukkit.getConsoleSender();
		
		//0 player name
		//1 kingdom name
		
		String targetName = args.poll();
		String targetKName = args.poll();
		
		if(targetName == null || targetKName == null){
			for(String usage:getUsage()){
				user.sendMessage(usage);
			}
			return;
		}
		
		OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
		if(op == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Member_Not_Found"));
			return;
		}
		
		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if(okp == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Member_Not_Found"));
			return;
		}
		
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(targetKName);
		if(kingdom == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Kingdom_Not_Found"));
			return;
		}
		if(kingdom instanceof BotKingdom){
			((BotKingdom) kingdom).displayInfo(user);
			return;
		}
		if(okp.isOnline()){
			KingdomPlayer kp = okp.getKingdomPlayer();
			Kingdom joining = kingdom.getKingdom();
			Kingdom previous = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
			if(previous != null){
				if(previous.isOnline()){
					Kingdom oprevious = previous.getKingdom();
					Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(okp, oprevious.getKingdomName()));
				}else{
					previous.getMembersList().remove(okp.getUuid());
				}
				if(kp.getKingdom() != null){
					Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(kp, kp.getKingdomName()));
				}
			}
		}else{
			if(okp.getKingdomName() != null){
				OfflineKingdom previous = GameManagement.getKingdomManager().getOfflineKingdom(okp.getKingdomName());
				
				if(previous.isOnline()){
					Kingdom oprevious = previous.getKingdom();
					Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(okp, oprevious.getKingdomName()));
				}else{
					previous.getMembersList().remove(okp.getUuid());
				}
			}
			
			
		}
		
		if(kingdom.isOnline()){
			Kingdom joining = kingdom.getKingdom();
			kingdom.getMembersList().add(okp.getUuid());
			okp.setKingdomName(kingdom.getKingdomName());
			okp.setRank(Rank.ALL);
			if(okp.isOnline()){
				KingdomPlayer kp = okp.getKingdomPlayer();
				kp.setKingdom(kingdom);
			}
			Bukkit.getPluginManager().callEvent(new KingdomMemberJoinEvent(okp, joining));
			
		}else{
			kingdom.getMembersList().add(okp.getUuid());
			okp.setKingdomName(kingdom.getKingdomName());
			okp.setRank(Rank.ALL);
			if(okp.isOnline()){
				KingdomPlayer kp = okp.getKingdomPlayer();
				kp.setKingdom(kingdom);
			}
		}
		
		user.sendMessage(okp.getName()+" is now in ["+okp.getKingdomName()+"]");
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player p, Queue<String> args) {
		// 0 player name
		// 1 kingdom name

		String targetName = args.poll();
		String targetKName = args.poll();

		KingdomPlayer user = GameManagement.getPlayerManager().getSession(p);
		if (targetName == null || targetKName == null) {
			user.sendMessage("player name or kingdom name cannot be null.");
			return;
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
		if (op == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Member_Not_Found", user.getLang()));
			return;
		}

		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if (okp == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Member_Not_Found", user.getLang()));
			return;
		}

		OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(targetKName);
		if (ok == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Kingdom_Not_Found", user.getLang()));
			return;
		}

		if(ok instanceof BotKingdom){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_Kingdom_Is_Bot", user.getLang()));
			return;
		}
		if(okp.getRank() == Rank.KING){
			user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Join_King_Warning", user.getLang()).replace("%oldkp%",okp.getKingdomName()));
		}
		
		Kingdoms.logDebug("okp.isOnline() "+okp.isOnline());
		if (okp.isOnline()) {
			KingdomPlayer kp = okp.getKingdomPlayer();
			Kingdom joining = ok.getKingdom();

			Kingdoms.logDebug("kp "+ kp);
			Kingdoms.logDebug("joining "+ joining);
			
			if (kp.getKingdom() != null) {
				Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(kp, kp.getKingdomName()));
			}

			kp.setRank(Rank.ALL);
			kp.setKingdom(joining);
			Bukkit.getPluginManager().callEvent(new KingdomMemberJoinEvent(kp, joining));
		} else {
			Kingdoms.logDebug("okp.getKingdomName() "+ okp.getKingdomName());
			if (okp.getKingdomName() != null) {
				OfflineKingdom previous = GameManagement.getKingdomManager().getOfflineKingdom(okp.getKingdomName());

				Kingdoms.logDebug("previous.isOnline() "+ previous.isOnline());
				if (previous.isOnline()) {
					Kingdom oprevious = previous.getKingdom();
					Bukkit.getPluginManager().callEvent(new KingdomMemberLeaveEvent(okp, oprevious.getKingdomName()));
				} else {
					previous.getMembersList().remove(okp.getUuid());
				}
			}

			Kingdoms.logDebug("ok.isOnline() "+ ok.isOnline());
			if (ok.isOnline()) {
				Kingdom joining = ok.getKingdom();
				okp.setKingdomName(joining.getKingdomName());
				
				Bukkit.getPluginManager().callEvent(new KingdomMemberJoinEvent(okp, joining));
			} else {
				okp.setKingdomName(ok.getKingdomName());
				
				ok.getMembersList().add(okp.getUuid());
			}
		}

		user.sendMessage(okp.getName() + " is now in [" + okp.getKingdomName() + "]");
	}

}
