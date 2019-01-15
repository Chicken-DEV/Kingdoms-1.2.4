package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminGeneral extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin general [name] [kingdom]"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"[name] for player name; [kingdom] for kingdom name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_General", lang);
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.mod")){
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
			user.sendMessage("player name or kingdom name cannot be null.");
			return;
		}
		
		OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
		if(op == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found"));
			return;
		}
		
		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if(okp == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found"));
			return;
		}
		
		if(okp.getKingdomName() == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_In_A_Kingdom"));
			return;
		}
		
		OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(targetKName);
		if(ok == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Kingdom_Not_Found"));
			return;
		}
		
		if(okp.isOnline()){
			//online player
			KingdomPlayer kp = okp.getKingdomPlayer();
			Kingdom kingdom = ok.getKingdom();
			
			//check if has kingdom
			if(kp.getKingdom() == null){
				user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member"));
				return;
			}
			
			//check if in target kingdom
			if(!kp.getKingdom().equals(kingdom)){
				user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member"));
				return;
			}
			//set player rank as king
			kp.setRank(Rank.GENERALS);
		}else{
			//offline
			
			//check if has kingdom
			if(okp.getKingdomName() == null){
				user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member"));
				return;
			}
			
			//check if in the target kingdom
			if(!okp.getKingdomName().equals(ok.getKingdomName())){
				user.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member"));
				return;
			}
			
			//if target kingdom is online
			okp.setRank(Rank.GENERALS);
			
		}
		
		user.sendMessage(okp.getName()+" is now ["+okp.getRank()+"]");
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

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		
		if (targetName == null || targetKName == null) {
			kp.sendMessage("player name or kingdom name cannot be null.");
			return;
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
		if (op == null) {
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found", kp.getLang()));
			return;
		}

		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if (okp == null) {
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_Found", kp.getLang()));
			return;
		}

		if (okp.getKingdomName() == null) {
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_In_A_Kingdom", kp.getLang()));
			return;
		}

		OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(targetKName);
		if (ok == null) {
			kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Kingdom_Not_Found", kp.getLang()));
			return;
		}

		Kingdoms.logDebug("okp.isOnline() "+okp.isOnline());
		if (okp.isOnline()) {
			// online player
			KingdomPlayer kpp = okp.getKingdomPlayer();
			Kingdom kingdom = ok.getKingdom();

			// check if has kingdom
			if (kpp.getKingdom() == null) {
				kpp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member", kpp.getLang()));
				return;
			}

			// check if in target kingdom
			if (!kpp.getKingdom().equals(kingdom)) {
				kpp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member", kpp.getLang()));
				return;
			}

			// set player rank as mod
			kp.setRank(Rank.GENERALS);
		} else {
			// offline player

			// check if has kingdom
			if (okp.getKingdomName() == null) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member", kp.getLang()));
				return;
			}

			// check if in the target kingdom
			if (!okp.getKingdomName().equals(ok.getKingdomName())) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Mod_Member_Not_A_Member", kp.getLang()));
				return;
			}

			okp.setRank(Rank.GENERALS);
		}
		
		kp.sendMessage(okp.getName()+" is now ["+okp.getRank()+"]");
	}

}
