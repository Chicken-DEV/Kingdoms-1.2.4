package com.songoda.kingdoms.commands.user;

import java.util.HashMap;
import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Allegiance;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomAllegianceChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandAlly extends KCommandBase {
	//REQUEST | Target
	private static HashMap<Kingdom,Kingdom> allyRequests = new HashMap<>();
	@Override
	public int getArgsAmount() {
		return 2;
	}

	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.ally")){
			return true;
		}
		
		return false;
	}

	@Override
	public String[] getUsage() {
		String str[] = new String[2];
		
		str[0] = Kingdoms.getLang().getString("Command_Help_Ally");
		str[1] = Kingdoms.getLang().getString("Command_Help_Neutral");
		
		return str;
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
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_No_Kingdom_Error", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getAlly())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getAlly().name()));
			return;
		}
		
		String sel = args.poll();
		String targetName = args.poll();
		
		Kingdom target = GameManagement.getKingdomManager().getOrLoadKingdom(targetName);
		if(target == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Doesnt_Exist_Error", kp.getLang()).replaceAll("%kingdom%", targetName));
			return;
		}
		if(target.equals(kingdom)){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Cannot_Ally_Yourself", kp.getLang()));
			return;
		}
		
		switch(sel){
		case "add":
			
			if(kingdom.getAlliesList().contains(target.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Already_Ally_Error", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()));
				return;
			}
			
			if(kingdom.getEnemiesList().contains(target.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Is_Enemy_Error", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()));
				return;
			}

			if (allyRequests.containsValue(kingdom)){
				allyRequests.remove(target);
				target.getAlliesList().add(kingdom.getKingdomUuid());
				kingdom.getAlliesList().add(target.getKingdomUuid());
				Allegiance oldAllegiance = Allegiance.NEUTRAL;
				if(kingdom.isEnemyWith(target)) oldAllegiance = Allegiance.ENEMY;
				KingdomAllegianceChangeEvent kace = new KingdomAllegianceChangeEvent(kingdom, target, oldAllegiance, Allegiance.ALLY);
				Bukkit.getPluginManager().callEvent(kace);
				target.sendAnnouncement(null, Kingdoms.getLang().getString("Command_Get_Ally", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()), true);
				kingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Command_Get_Ally", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()), true);
				if(GameManagement.getKingdomManager().isOnline(kingdom.getKingdomName())){
					kingdom.getOnlineAllies().add(target);
				}
			}else {
				allyRequests.put(kingdom,target);
				kingdom.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Ally_Pending", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()), true);
				target.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Ally_Ask", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()), true);
			}

			
			break;
		case "break":
			if(!kingdom.getAlliesList().contains(target.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Not_Ally_Error", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()));
				return;
			}

			KingdomAllegianceChangeEvent kace = new KingdomAllegianceChangeEvent(kingdom, target, Allegiance.ALLY, Allegiance.NEUTRAL);
			Bukkit.getPluginManager().callEvent(kace);
			kingdom.getAlliesList().remove(target.getKingdomUuid());
			kingdom.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Ally_Break", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()), false);
			target.sendAnnouncement(null, Kingdoms.getLang().getString("Command_Ally_Get_Break", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()), true);
			
			if(GameManagement.getKingdomManager().isOnline(kingdom.getKingdomName())){
				kingdom.getOnlineAllies().remove(target);
			}
			break;
		default:
			user.sendMessage(getUsage());
			break;
		}
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Ally_Description", lang);
	}
}
