package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandUnclaim extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Unclaim_All_Usage"),
				Kingdoms.getLang().getString("Command_Unclaim_Disconnected_Usage"),
				Kingdoms.getLang().getString("Command_Unclaim_Usage")
		};
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.unclaim")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	private static boolean isProcessing = false;
	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		String arg = args.poll();
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getUnclaim())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getUnclaim().toString()));
			return;
		}
		
		if(arg == null){
			SimpleChunkLocation chunk = kp.getLoc();
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null || !land.getOwnerUUID().equals(kingdom.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_Not_Your_Kingdom", kp.getLang()));
				return;
			}
			if(land.getStructure() != null && land.getStructure().getType() == StructureType.NEXUS){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_Cannot_Unclaim_Nexus", kp.getLang()));
				return;
			}
			GameManagement.getLandManager().unclaimLand(chunk, kingdom);
			//Kingdoms.getLang().addInteger(5);
			//kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_Lostmight));
			kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_Success", kp.getLang()));
		}else if(arg.equalsIgnoreCase("all")){
			if(kp.isConfirmed("unclaimAll")){
				new Thread(new Runnable() {
					private int count = 0;
					
					@Override
					public void run() {
						try {
							while(isProcessing){
								if(count == 500){
									Kingdoms.logDebug("k unclaimall requested, but is lagging.");
									isProcessing = false;
									return;
								}
								
								count++;
								Thread.sleep(100L);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
						
						isProcessing = true;
						
						int lands = GameManagement.getLandManager().unclaimAllLand(kingdom);
						if(lands == -1){
							kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_Still_Processing", kp.getLang()));
							return;
						}
						//Kingdoms.getLang().addInteger(lands);
						kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_LostLandTotal", kp.getLang()).replace("%amount%",String.valueOf(lands)));
						
						isProcessing = false;
					}

				}).start();
				
			}else{
				kp.setConfirmed("unclaimAll");
				kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_DoNotMove", kp.getLang()));
				kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_TypeAgain", kp.getLang()));
				Location nexus = kp.getKingdom().getNexus_loc();
				if(nexus == null) return;
				SimpleChunkLocation loc = new SimpleChunkLocation(nexus.getChunk());
				if(Kingdoms.getManagers().getChampionManager().isChunkInvaded(loc)){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_Nexus", kp.getLang()));
				}
			
			}
		}else if(arg.equalsIgnoreCase("disconnected")){
			
			new Thread(new Runnable() {
				private int count = 0;
				
				@Override
				public void run() {
					try {
						while(isProcessing){
							if(count == 1500){
								Kingdoms.logDebug("k unclaim disconnected requested, but is lagging.");
								isProcessing = false;
								return;
							}
							
							count++;
							Thread.sleep(100L);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
					
					isProcessing = true;
					
					int lands = Kingdoms.getManagers().getLandManager().unclaimDisconnectedLand(kingdom);
					if(lands == -1){
						kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_Still_Processing", kp.getLang()));
						return;
					}
					//Kingdoms.getLang().addInteger(lands);
					kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_All_LostLandTotal", kp.getLang()).replace("%amount%",String.valueOf(lands)));
					//Kingdoms.getLang().addInteger(5*lands);
					kp.sendMessage(Kingdoms.getLang().getString("Command_Unclaim_Lostmight", kp.getLang()).replace("%amount%",String.valueOf(5*lands)));
					isProcessing = false;
				}

			}).start();
			
			
		}else{
			for(String str : getUsage()) user.sendMessage(ChatColor.GRAY+"    "+str);
		}
	}
	
	
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Unclaim", lang);
	}

}
