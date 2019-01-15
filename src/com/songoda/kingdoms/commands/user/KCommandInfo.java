package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandInfo extends KCommandBase {

	@Override
	public int getArgsAmount() {
		// TODO Auto-generated method stub
		return 1;
	}

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.info")){
			return true;
		}
		
		return false;
	}
	
	@Override
	public String[] getUsage() {
		String str[] = new String[11];

		str[0] = ChatColor.GRAY + "========" 
				+ ChatColor.GOLD + " Kingdoms Info "
				+ ChatColor.GRAY + "========";
		str[1] = ChatColor.LIGHT_PURPLE + "Page 1: " + Kingdoms.getLang().getString("Command_Info_Page1Title");
		str[2] = ChatColor.LIGHT_PURPLE + "Page 2: " + Kingdoms.getLang().getString("Command_Info_Page2Title");
		str[3] = ChatColor.LIGHT_PURPLE + "Page 3: " + Kingdoms.getLang().getString("Command_Info_Page3Title");
		str[4] = ChatColor.LIGHT_PURPLE + "Page 4: " + Kingdoms.getLang().getString("Command_Info_Page4Title");
		str[5] = ChatColor.LIGHT_PURPLE + "Page 5: " + Kingdoms.getLang().getString("Command_Info_Page5Title");
		str[6] = ChatColor.LIGHT_PURPLE + "Page 6: " + Kingdoms.getLang().getString("Command_Info_Page6Title");
		str[7] = ChatColor.LIGHT_PURPLE + "Page 7: " + Kingdoms.getLang().getString("Command_Info_Page7Title");
		str[8] = ChatColor.LIGHT_PURPLE + "Page 8: " + Kingdoms.getLang().getString("Command_Info_Page8Title");
		if(Kingdoms.getManagers().getConquestManager() != null){
			str[9] = ChatColor.LIGHT_PURPLE + "Page 9: " + Kingdoms.getLang().getString("Command_Info_Page9Title");
			str[10] = ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_NextPageText")
					.replace("%nextpagenum%", "2");
		}else{
			str[9] = ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_NextPageText")
				.replace("%nextpagenum%", "2");
		}
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
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
//		Kingdom kingdom = kp.getKingdom();
//		if (kingdom == null) {
//			kp.sendMessage("You are not in any Kingdom yet!");
//			return;
//		}

		String str = args.poll();
		int page = 0;
		try {
			page = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			kp.sendMessage(str + " is not a valid number.");
		}

		if (page == 1) {

		} else if (page == 2) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "2")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(Kingdoms.getLang().getString("Command_Info_Page2Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page2Content1", kp.getLang()));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "3"));

		} else if (page == 3) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "3")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(Kingdoms.getLang().getString("Command_Info_Page3Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page3Content1", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page3Content2", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page3Content3", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page3Content4", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_PURPLE + Kingdoms.getLang().getString("Command_Info_Page3Content5", kp.getLang()).replace("%claimcost%", String.valueOf(Config.getConfig().getInt("claim-cost"))));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "4"));

		} else if (page == 4) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "4")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(Kingdoms.getLang().getString("Command_Info_Page4Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page4Content1", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page4Content2", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page4Content3", kp.getLang()).replace("%itemrp%",String.valueOf(Config.getConfig().getInt("items-needed-for-one-resource-point"))));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "5"));

		} else if (page == 5) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "5")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_Page5Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page5Content1", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page5Content2", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page5Content3", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page5Content4", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_PURPLE + Kingdoms.getLang().getString("Command_Info_Page5Content5", kp.getLang()).replace("%invadecost%",String.valueOf(Config.getConfig().getInt("invade-cost"))));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "6"));

		} else if (page == 6) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "6")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_Page6Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page6Content1", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page6Content2", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page6Content3", kp.getLang()));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "7"));

		} else if (page == 7) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "7")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_Page7Title", kp.getLang()));
//			kp.sendMessage(
//					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page7Content01", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page7Content2", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page7Content3", kp.getLang()));
			kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_NextPageText", kp.getLang())
					.replace("%nextpagenum%", "8"));

		} else if (page == 8) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "8")
					+ ChatColor.GRAY + " ========");
			kp.sendMessage(ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_Page8Title", kp.getLang()));
			kp.sendMessage(
					ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page8Content1", kp.getLang()));
			kp.sendMessage(
					ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page8Content2", kp.getLang()));

		} else if (page == 9) {
			kp.sendMessage(ChatColor.GRAY + "======== " + Kingdoms.getLang().getString("Command_Info_PageTextHeader", kp.getLang()).replace("%currpagenum%", "9")
			+ ChatColor.GRAY + " ========");
	kp.sendMessage(ChatColor.BLUE + Kingdoms.getLang().getString("Command_Info_Page9Title", kp.getLang()));
	kp.sendMessage(
			ChatColor.GRAY + Kingdoms.getLang().getString("Command_Info_Page9Content1", kp.getLang()));
	kp.sendMessage(
			ChatColor.DARK_GRAY + Kingdoms.getLang().getString("Command_Info_Page9Content2", kp.getLang()));
	kp.sendMessage(ChatColor.GOLD + Kingdoms.getLang().getString("Command_Info_EndingText", kp.getLang()));

		}

	}

	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Info", lang);
	}
	
}
