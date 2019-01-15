package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.task.DelayedDeclineOfferTask;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandInvite extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
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
		if(sender.hasPermission("kingdoms.invite")){
			return true;
		}
		
		return false;
	}
	
	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		KingdomPlayer offerer = GameManagement.getPlayerManager().getSession(user);
		if(offerer.getKingdom() == null){
			offerer.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", offerer.getLang()));
			return;
		}
		
		Kingdom kingdom = offerer.getKingdom();
		if(!offerer.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getInvite())){
			offerer.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", offerer.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getInvite().toString()));
			return;
		}
		
		if(kingdom.getMembersList().size() == kingdom.getMaxMember()){
			offerer.sendMessage(Kingdoms.getLang().getString("Misc_Kingdom_Too_Many_Members", offerer.getLang()));
			return;
		}
		
		String targetName = args.poll();
		Player player = Bukkit.getPlayer(targetName);
		if(player == null){
			offerer.sendMessage(Kingdoms.getLang().getString("Command_Invite_Player_Not_Online_Error", offerer.getLang()));
			return;
		}

		KingdomPlayer target = GameManagement.getPlayerManager().getSession(player);
		
		if(target.isTemp()){
			offerer.sendMessage(Kingdoms.getLang().getString("Command_Invite_Player_Is_Loading", offerer.getLang()));
			return;
		}
		
		if(target.getKingdom() != null){
			offerer.sendMessage(Kingdoms.getLang().getString("Command_Invite_Player_Is_In_Another_Kingdom_Error", offerer.getLang()));
			return;
		}
		
		if(target.getInvited() != null){
			offerer.sendMessage(Kingdoms.getLang().getString("Command_Invite_Player_Has_Offer_Error", offerer.getLang()));
			return;
		}
		
		target.setInvited(kingdom);
		offerer.sendMessage(Kingdoms.getLang().getString("Command_Invite_Success", offerer.getLang()).replaceAll("%player%", targetName));
		Bukkit.getScheduler().runTaskLater(Kingdoms.getInstance(), new DelayedDeclineOfferTask(target), Config.getConfig().getInt("invite_expire_delay") * 20L);
		target.sendMessage(ChatColor.GOLD + "===========================================");
		target.sendMessage(Kingdoms.getLang().getString("Command_Invite_Text", offerer.getLang()).replaceAll("%inviter%", user.getName()).replaceAll("%kingdom%", kingdom.getKingdomName()));
		target.sendMessage(Kingdoms.getLang().getString("Command_Invite_Text2", offerer.getLang()));
		target.sendMessage(Kingdoms.getLang().getString("Command_Invite_Text3", offerer.getLang()));
		//Kingdoms.getLang().addInteger(Kingdoms.config.invite_expire_delay);
		target.sendMessage(Kingdoms.getLang().getString("Command_Invite_Text4", offerer.getLang()).replace("%time%",String.valueOf(Config.getConfig().getInt("invite_expire_delay"))));
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Invite", lang);
	}

}
