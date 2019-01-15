package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.gui.GUIManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandMap extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"/k map"
		};
	}

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.map")){
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
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(user);
		if(args.poll() == null){
			
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(kp.getLoc());
			if(land.getOwnerUUID() != null){
				if(kp.getKingdomName() != null &&
						land.getOwnerUUID().equals(kp.getKingdomUuid())){
					if(land.getStructure() != null &&
							land.getStructure().getType() == StructureType.RADAR){
						GUIManagement.getMapManager().displayMap(user, true);
						return;
					}
				}
			}
			
			GUIManagement.getMapManager().displayMap(user, false);
		}else{
			if(!kp.isKMapOn()){
				kp.setKMapOn(true);
				kp.sendMessage(Kingdoms.getLang().getString("Command_Map_AutoMapOn", kp.getLang()));
			}else{
				kp.setKMapOn(false);
				kp.sendMessage(Kingdoms.getLang().getString("Command_Map_AutoMapOff", kp.getLang()));
			}
		}
	}

	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Map", lang);
	}
	
}
