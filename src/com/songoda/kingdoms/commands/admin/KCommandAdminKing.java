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

public class KCommandAdminKing extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				ChatColor.GOLD+"/k admin king [name] [kingdom]"+ChatColor.GRAY+" - "
						+ChatColor.WHITE+"[name] for player name; [kingdom] for kingdom name"
		};
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_King", lang);
	}

	@Override
	public boolean canExecute(CommandSender sender) {
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.king")){
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
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found"));
			return;
		}
		
		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if(okp == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found"));
			return;
		}
		
		if(okp.getKingdomName() == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_In_A_Kingdom"));
			return;
		}
		
		OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(targetKName);
		if(ok == null){
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Kingdom_Not_Found"));
			return;
		}
		
		if(okp.isOnline()){
			//online player
			KingdomPlayer kp = okp.getKingdomPlayer();
			Kingdom kingdom = ok.getKingdom();
			
			//check if has kingdom
			if(kp.getKingdom() == null){
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member"));
				return;
			}
			
			//check if in target kingdom
			if(!kp.getKingdom().equals(kingdom)){
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member"));
				return;
			}
			
			//set previous king as ALL
			OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager().getOfflineKingdomPlayer(kingdom.getKing());
			if(previousKing.isOnline()){
				previousKing.getKingdomPlayer().setRank(Rank.ALL);
			}else{
				previousKing.setRank(Rank.ALL);
			}
			
			//set kingdom king as target player
			kingdom.setKing(kp.getUuid());
			
			//set player rank as king
			kp.setRank(Rank.KING);
		}else{
			//offline
			
			//check if has kingdom
			if(okp.getKingdomName() == null){
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member"));
				return;
			}
			
			//check if in the target kingdom
			if(!okp.getKingdomName().equals(ok.getKingdomName())){
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member"));
				return;
			}
			
			//if target kingdom is online
			if(ok.isOnline()){
				Kingdom kingdom = ok.getKingdom();
				
				//set previous king as All
				OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager().getOfflineKingdomPlayer(kingdom.getKing());
				if(previousKing.isOnline()){
					previousKing.getKingdomPlayer().setRank(Rank.ALL);
				}else{
					previousKing.setRank(Rank.ALL);
				}
				
				//set kingdom king
				kingdom.setKing(okp.getUuid());
				okp.setRank(Rank.KING);
			}else{//if target kingdom is offline
				OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager().getOfflineKingdomPlayer(ok.getKing());
				
				//set previous king as All
				if(previousKing.isOnline()){
					previousKing.getKingdomPlayer().setRank(Rank.ALL);
				}else{
					previousKing.setRank(Rank.ALL);
				}
				
				//set kingdom king
				ok.setKing(okp.getUuid());
				okp.setRank(Rank.KING);
			}
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

		KingdomPlayer user = GameManagement.getPlayerManager().getSession(p);
		
		if (targetName == null || targetKName == null) {
			user.sendMessage("player name or kingdom name cannot be null.");
			return;
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
		if (op == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found", user.getLang()));
			return;
		}

		OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
		if (okp == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_Found", user.getLang()));
			return;
		}

		if (okp.getKingdomName() == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_In_A_Kingdom", user.getLang()));
			return;
		}

		OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(targetKName);
		if (ok == null) {
			user.sendMessage(Kingdoms.getLang().getString("Command_King_Kingdom_Not_Found", user.getLang()));
			return;
		}

		Kingdoms.logDebug("okp.isOnline() "+okp.isOnline());
		if (okp.isOnline()) {
			// online player
			KingdomPlayer kp = okp.getKingdomPlayer();
			Kingdom kingdom = ok.getKingdom();

			// check if has kingdom
			if (kp.getKingdom() == null) {
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", user.getLang()));
				return;
			}

			// check if in target kingdom
			if (!kp.getKingdom().equals(kingdom)) {
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", user.getLang()));
				return;
			}

			// set previous king as ALL
			OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager()
					.getOfflineKingdomPlayer(kingdom.getKing());
			if (previousKing.isOnline()) {
				previousKing.getKingdomPlayer().setRank(Rank.ALL);
			} else {
				previousKing.setRank(Rank.ALL);
			}

			// set kingdom king as target player
			kingdom.setKing(kp.getUuid());

			// set player rank as king
			kp.setRank(Rank.KING);
		} else {
			// offline player

			// check if has kingdom
			if (okp.getKingdomName() == null) {
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", user.getLang()));
				return;
			}

			// check if in the target kingdom
			if (!okp.getKingdomName().equals(ok.getKingdomName())) {
				user.sendMessage(Kingdoms.getLang().getString("Command_King_Member_Not_A_Member", user.getLang()));
				return;
			}

			// if target kingdom is online
			if (ok.isOnline()) {
				Kingdom kingdom = ok.getKingdom();

				// set previous king as All
				if(!ok.getKing().equals(okp.getUuid())){
					OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager()
							.getOfflineKingdomPlayer(kingdom.getKing());
					if (previousKing.isOnline()) {
						previousKing.getKingdomPlayer().setRank(Rank.ALL);
					} else {
						previousKing.setRank(Rank.ALL);
					}
				}

				// set kingdom king
				kingdom.setKing(okp.getUuid());
			} else {// if target kingdom is offline
				if(!ok.getKing().equals(okp.getUuid())){
					OfflineKingdomPlayer previousKing = GameManagement.getPlayerManager()
							.getOfflineKingdomPlayer(ok.getKing());
					// set previous king as All
					if (previousKing.isOnline()) {
						previousKing.getKingdomPlayer().setRank(Rank.ALL);
					} else {
						previousKing.setRank(Rank.ALL);
					}
				}

				// set kingdom king
				ok.setKing(okp.getUuid());
			}
			
			okp.setRank(Rank.KING);
		}
		
		user.sendMessage(okp.getName()+" is now ["+okp.getRank()+"]");
	}

}
