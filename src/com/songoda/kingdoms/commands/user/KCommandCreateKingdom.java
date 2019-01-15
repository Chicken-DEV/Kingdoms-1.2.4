package com.songoda.kingdoms.commands.user;

import java.util.ArrayList;
import java.util.Queue;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.external.ExternalManager;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.utils.EnglishChecker;
import com.songoda.kingdoms.utils.RegexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.utils.InventoryUtil;

public class KCommandCreateKingdom extends KCommandBase {
	//check kingdom exist -> Kingdom name vaildation -> kingdom name dup check -> 
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeCommandOP(Player op,Queue<String> args) {
		executeCommandUser(op,args);
	}
	
	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.create")){
			return true;
		}
		
		return false;
	}

	@Override
	public void executeCommandUser(Player user,Queue<String> args) {
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		Kingdom kingdom = kp.getKingdom();
		
		if(kp.isTemp()){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_KP_Not_Loaded", kp.getLang()));
			return;
		}
		
		if(kingdom != null){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Already_In_Kingdom_Error", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()));
			return;
		}
		
		final String kingdomName = args.poll();
		if(!EnglishChecker.isEnglish(kingdomName) && !Config.getConfig().getBoolean("Plugin.allowSpecialCharactersInNamingKingdoms")){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Invalid_Name_Error", kp.getLang()));
			return;
		}
		if(kingdomName.equalsIgnoreCase("safezone")||kingdomName.equalsIgnoreCase("warzone")){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Banned_Name", kp.getLang()));
			return;
		}
		
		if(kingdomName.length() > 12){
			kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Name_Too_Long_Error", kp.getLang()));
			return;
		}

		if (RegexUtil.checkForMatch(Kingdoms.getInstance().getConfig().getStringList("disallowed-kingdom-names"), kingdomName)) {
            kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Name_Blacklisted", kp.getLang()));
            return;
        }

//		if(Kingdoms.config.blackListedNames.size() > 0)
//		for(String s:Kingdoms.config.blackListedNames){
//			if(s.toLowerCase().equals(kingdomName.toLowerCase())){
//				kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Name_Blacklisted", kp.getLang()));
//				return;
//			}
//		}

		if(ExternalManager.getVaultManager() != null && Config.getConfig().getBoolean("economy.enabled") && Config.getConfig().getInt("economy.kingdom-create-cost") > 0){
			if(ExternalManager.getBalance(user) < Config.getConfig().getInt("economy.kingdom-create-cost")){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Need_More_Money", kp.getLang()).replaceAll("%cost%", ""+ Config.getConfig().getInt("economy.kingdom-create-cost")));
				return;
			}
		}
		ArrayList<String> needed = new ArrayList<String>();
		if(Config.getConfig().getBoolean("kingdom-create-need-item-cost")){
			for(String s:Config.getConfig().getStringList("kingdom-create-item-cost")){
				String[] split = s.split(",");
				
				Material mat = Material.valueOf(split[0]);
				if(mat == null){
					Kingdoms.logInfo("Specified material, " + mat + " is not a recognised material! Please use Spigot material names");
					continue;
				}
				if(!InventoryUtil.hasEnough(kp.getPlayer(), mat, Integer.parseInt(split[1]))){
					needed.add(s);
				}
				
			}
			
			if(needed.size() > 0){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Need_More_Items", kp.getLang()));
				for(String s:needed){
					kp.sendMessage(s);
				}
				return;
			}
		}
		
		new BukkitRunnable(){
			@Override
			public void run() {
				if(GameManagement.getKingdomManager().hasKingdom(kingdomName)){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Name_Exists", kp.getLang()));
					return;
				}
				
				Bukkit.getScheduler().runTask(Kingdoms.getInstance(), new Runnable(){
					@Override
					public void run(){
						if(Config.getConfig().getBoolean("kingdom-create-need-item-cost"))
						for(String s:Config.getConfig().getStringList("kingdom-create-item-cost")){
							String[] split = s.split(",");
							
							Material mat = Material.valueOf(split[0]);
							InventoryUtil.removeMaterial(kp.getPlayer(), mat, Integer.parseInt(split[1]));
							
						}
						
						if(ExternalManager.getVaultManager() != null 
								&& Config.getConfig().getBoolean("economy.enabled")
								&& Config.getConfig().getInt("economy.kingdom-create-cost") > 0){
							ExternalManager.withdrawPlayer(user, Config.getConfig().getInt("economy.kingdom-create-cost"));
							
						}
					}
				});
				
				if(GameManagement.getKingdomManager().createNewKingdom(kingdomName, kp)){
					kp.sendMessage(Kingdoms.getLang().getString("Command_Create_Success", kp.getLang()));
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Previous_Request_Processing", kp.getLang()));
				}
			}
		}.runTaskAsynchronously(Kingdoms.getInstance());
	}


	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Usage_Create")
		};
	}

	@Override
	public int getArgsAmount() {
		return 1;
	}
	
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Create", lang);
	}


}
