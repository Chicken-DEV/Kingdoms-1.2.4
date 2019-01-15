package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.LandManager;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandClaimLand extends KCommandBase {
	//check if in kingdom -> check rank
	@Override
	public int getArgsAmount() {
		return -1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Usage_Claim"),
				Kingdoms.getLang().getString("Command_Usage_ClaimAuto")};
	}

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.claim")){
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
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		LandManager lm = GameManagement.getLandManager();
		lm.attemptNormalLandClaim(kp);
		
		if(args.size() > 0 && args.poll().equalsIgnoreCase("auto")){
			kp.setKAutoClaimOn(!kp.isKAutoClaimOn());
			if(kp.isKAutoClaimOn()){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Auto_On", kp.getLang()));
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Auto_Off", kp.getLang()));
			}
		}
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Claim", lang);
	}
	
	
	

}
