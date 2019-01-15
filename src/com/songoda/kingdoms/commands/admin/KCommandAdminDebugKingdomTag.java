package com.songoda.kingdoms.commands.admin;

import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAdminDebugKingdomTag extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.debugkingdomtag")){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public void executeCommandConsole(Queue<String> arguments) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		for(UUID s:Kingdoms.getManagers().getKingdomManager().getKingdomList().keySet()){
			Kingdom k = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(s);
			Iterator<UUID> it = k.getMembersList().iterator();
			while(it.hasNext()){
				UUID id = it.next();
				OfflineKingdomPlayer okp = Kingdoms.getManagers().getPlayerManager().getOfflineKingdomPlayer(id);
				if(okp.getKingdomName() == null){
					it.remove();
					if(okp.isOnline()){
						k.getOnlineMembers().remove(okp);
					}
					continue;
				}
				if(!okp.getKingdomName().equals(k.getKingdomName())){
					it.remove();
					if(okp.isOnline()){
						//noinspection SuspiciousMethodCalls
						k.getOnlineMembers().remove(okp);
					}
					continue;
				}
				
			}
		}
		sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_DebugKingdomTag_Success"));
		
		
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
		
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public void executeCommandUser(Player user, Queue<String> arguments) {
		for(UUID s:Kingdoms.getManagers().getKingdomManager().getKingdomList().keySet()){
			Kingdom k = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(s);
			Iterator<UUID> it = k.getMembersList().iterator();
			while(it.hasNext()){
				UUID id = it.next();
				OfflineKingdomPlayer okp = Kingdoms.getManagers().getPlayerManager().getOfflineKingdomPlayer(id);
				if(okp.getKingdomName() == null){
					it.remove();
					if(okp.isOnline()){
						k.getOnlineMembers().remove(okp);
					}
					continue;
				}
				if(!okp.getKingdomName().equals(k.getKingdomName())){
					it.remove();
					if(okp.isOnline()){
						k.getOnlineMembers().remove(okp);
					}
					continue;
				}
				if(okp.getRank().equals(Rank.KING)){
					if(!k.getKing().equals(okp.getUuid())){
						okp.setRank(Rank.ALL);
					}
					continue;
				}else{
					if(k.getKing().equals(okp.getUuid())){
						okp.setRank(Rank.KING);
					}
				}
				
			}
		}
		user.sendMessage(Kingdoms.getLang().getString("Command_Admin_DebugKingdomTag_Success", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
	}

	@Override
	public String[] getUsage() {
		
		return new String[]{
				"/k admin debugkingdomtag"
		};
	}

	@Override
	public int getArgsAmount() {
		return -1;
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Admin_DebugKingdomTag", lang);
	}



}
