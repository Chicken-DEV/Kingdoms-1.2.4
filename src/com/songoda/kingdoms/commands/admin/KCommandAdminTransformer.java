package com.songoda.kingdoms.commands.admin;

import java.util.Queue;

import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommandAdminTransformer extends KCommandBase {

	
	
	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin")){
			return true;
		}
		if(sender.hasPermission("kingdoms.admin.transform")){
			return true;
		}
		return false;
	}
	
	@Override
	public void executeCommandConsole(Queue<String> args) {
/*		new BukkitRunnable() {
			@Override
			public void run() {
				DatabaseManagement.getTransformer().getTransformerOYTS().performTransaction();
			}
		}.runTaskLater(Kingdoms.getInstance(), 5 * 20L);
		Kingdoms.logInfo("transaction will start in 5 seconds.");
		Kingdoms.logInfo("plugin will be disabled while transferring data.");
*/
	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getUsage() {
		
		return null;
	}

	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String getDescription(String lang) {
		// TODO Auto-generated method stub
		return null;
	}



}
