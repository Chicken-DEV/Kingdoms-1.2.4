package com.songoda.kingdoms.commands.user;

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

public class KCommandEnemy extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
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
		if(sender.hasPermission("kingdoms.enemy")){
			return true;
		}
		
		return false;
	}

	@Override
	public String[] getUsage() {
		String str[] = new String[2];
		
		str[0] = Kingdoms.getLang().getString("Command_Help_Enemy");
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
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getAlly())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getAlly().toString()));
			return;
		}
		
		String sel = args.poll();
		String targetName = args.poll();
		
		Kingdom target = GameManagement.getKingdomManager().getOrLoadKingdom(targetName);
		if(target == null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Doesnt_Exist_Error", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()));
			return;
		}
		if(target.equals(kingdom)){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Enemy_Cannot_Enemy_Yourself", kp.getLang()));
			return;
		}
		
		switch(sel){
		case "add":
			if(kingdom.isEnemyWith(target)){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Enemy_Kingdom_Already_Enemy", kp.getLang()));
				return;
			}
			Allegiance oldAllegiance = Allegiance.NEUTRAL;
			if(kingdom.isAllianceWith(target)) oldAllegiance = Allegiance.ALLY;
			KingdomAllegianceChangeEvent kace = new KingdomAllegianceChangeEvent(kingdom, target, oldAllegiance, Allegiance.ENEMY);
			Bukkit.getPluginManager().callEvent(kace);
			kingdom.enemyKingdom(target);
			target.enemyKingdom(kingdom);
			target.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Enemy_Success", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()), false);
			kingdom.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Enemy_Success", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()), false);

			break;
		case "break":
			if(!kingdom.getEnemiesList().contains(target.getKingdomName())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Enemy_Kingdom_Not_Enemy", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()));
				return;
			}

			kace = new KingdomAllegianceChangeEvent(kingdom, target, Allegiance.ENEMY, Allegiance.NEUTRAL);
			Bukkit.getPluginManager().callEvent(kace);
			kingdom.getEnemiesList().remove(target.getKingdomName());
			kingdom.sendAnnouncement(kp, Kingdoms.getLang().getString("Command_Enemy_Break_Success", kp.getLang()).replaceAll("%kingdom%", target.getKingdomName()), false);
			
			if(GameManagement.getKingdomManager().isOnline(target.getKingdomName())){
				kingdom.getOnlineEnemies().remove(target);
			}
			break;
		default:
			user.sendMessage(getUsage());
			break;
		}
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Enemy_Description", lang);
	}

}
