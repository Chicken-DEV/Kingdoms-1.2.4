package com.songoda.kingdoms.commands.user;

import java.util.Queue;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.external.ExternalManager;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandDeposit extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 1;
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				Kingdoms.getLang().getString("Command_Usage_Deposit")
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
		if(sender.hasPermission("kingdoms.deposit")){
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
		//k deposit [amount]
		//amount = args.poll
		Kingdoms.logDebug("" + Config.getConfig().getBoolean("economy.enabled"));
		//Kingdoms.logDebug("" + (GameManagement.getApiManager().getEcon() != null));
		
		
		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		Player p = kp.getPlayer();

		Kingdoms plugin = Kingdoms.getInstance();
		if(ExternalManager.getVaultManager() != null && Config.getConfig().getBoolean("economy.enabled")){
			
				double amt = 0;
				try{
					amt = Double.parseDouble(args.poll());
				}catch(NumberFormatException e){
					kp.sendMessage(ChatColor.RED + "Usage: /k deposit [amount]");
				}
				
				if(kp.getKingdom() != null){
					if(ExternalManager.getBalance(p) >= amt){
						tradeMoneyForRp(kp, amt);
					}else{
						kp.sendMessage(Kingdoms.getLang().getString("Misc_Economy_Insufficient_Money", kp.getLang()));
					}
				}else{
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
				}
				
			
		}else{
			p.sendMessage(Kingdoms.getLang().getString("Misc_Enonomy_Not_Enabled", kp.getLang()));
		}
	
		
	}
	@Override
	public String getDescription(String lang) {
		return Kingdoms.getLang().getString("Command_Help_Deposit", lang).replace("%moneyforonerp%", Config.getConfig().getInt("economy.money-needed-for-one-rp") + "");
	}
	
    public void tradeMoneyForRp(KingdomPlayer kp, double amount){
    	Player p = kp.getPlayer();
    	Kingdoms plugin = Kingdoms.getInstance();
    	if(amount >= Config.getConfig().getInt("economy.money-needed-for-one-rp")){
    		
    		int rp = (int) (amount/Config.getConfig().getInt("economy.money-needed-for-one-rp"));
    		double leftover = amount - (rp*Config.getConfig().getInt("economy.money-needed-for-one-rp"));
    		kp.sendMessage(Kingdoms.getLang().getString("Misc_Economy_Trade_Successful", kp.getLang()).replaceAll("%traded%", "" + rp).replaceAll("%amount%", "" + (amount-leftover)));
    		ExternalManager.withdrawPlayer(p, amount);
    		if(leftover > 0){
    			p.sendMessage(Kingdoms.getLang().getString("Misc_Economy_Trade_Leftover_Return", kp.getLang()).replaceAll("%leftover%", "" + (leftover)));
    			ExternalManager.depositPlayer(p, leftover);
    		}
    		kp.getKingdom().setResourcepoints(kp.getKingdom().getResourcepoints() + rp);
			kp.setDonatedAmt(kp.getDonatedAmt() + rp);
			kp.setLastDonatedAmt(rp);
    		
    	}else{
    		kp.sendMessage(Kingdoms.getLang().getString("Misc_Economy_Amount_Insufficient_For_Trade", kp.getLang())
    				.replaceAll("$[UnkownVar]", ""+Config.getConfig().getInt("economy.money-needed-for-one-rp"))
    				.replaceAll("%moneyforonerp%", ""+Config.getConfig().getInt("economy.money-needed-for-one-rp")));
    	}
    }

}
