package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.manager.Manager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.songoda.kingdoms.main.Kingdoms;

public class VaultManager extends Manager {
	private static net.milkbowl.vault.economy.Economy econ;

	protected VaultManager(Plugin plugin) {
		super(plugin);

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(rsp != null){
        	econ = rsp.getProvider();
		}
		if (econ != null) {
			Kingdoms.logInfo("Vault Hooked!");
		}else{
			Kingdoms.logInfo("Vault could not be hooked into!");
		}
	
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	
	public static double getBalance(OfflinePlayer p){
		if(econ == null) return 0;
		return econ.getBalance(p);
	}
	
	public static void withdraw(OfflinePlayer p, double amt){
		if(econ != null)
		econ.withdrawPlayer(p, amt);
	}	
	
	public static void deposit(OfflinePlayer p, double amt){
		if(econ != null)
		econ.depositPlayer(p, amt);
	}
	
	public net.milkbowl.vault.economy.Economy getEcon(){
		return econ;
	}
	

}
