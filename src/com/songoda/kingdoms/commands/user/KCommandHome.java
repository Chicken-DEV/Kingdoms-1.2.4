package com.songoda.kingdoms.commands.user;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandHome extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.home")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom.getHome_loc() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Home_No_Home_Error", kp.getLang()));
			return;
		}
		
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getUseKHome())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getUseKHome().toString()));
			return;
		}
		
		final Location home = kingdom.getHome_loc();
		if(Config.getConfig().getBoolean("kingdom-home-stops-working-if-unclaimed")){
			if(Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(home.getChunk())).getOwnerUUID() == null){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Home_Not_Own_Land", kp.getLang()));
				return;
			}
			
			if(!Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(home.getChunk())).getOwnerUUID().equals(kingdom.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Home_Not_Own_Land", kp.getLang()));
				return;
			}
		}
		final List<UUID> iswarping = GameManagement.getTpManager().iswarping;
		iswarping.add(kp.getUuid());
		kp.sendMessage(Kingdoms.getLang().getString("Command_Home_Wait", kp.getLang()).replace("%time%",String.valueOf(Config.getConfig().getInt("khome-delay"))));

		Bukkit.getScheduler().scheduleSyncDelayedTask(Kingdoms.getInstance(), new Runnable() {
			public void run() {
				if (iswarping.contains(kp.getUuid())) {
					kp.getPlayer().teleport(home, TeleportCause.COMMAND);
					kp.sendMessage(Kingdoms.getLang().getString("Command_Home_Success", kp.getLang()));
					iswarping.remove(kp.getUuid());
				}
			}
		}, Config.getConfig().getInt("khome-delay") * 20);
		
	}
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Home", lang);
	}

}
