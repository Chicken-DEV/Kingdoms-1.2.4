package com.songoda.kingdoms.manager.external;

import com.bekvon.bukkit.residence.selection.WorldGuardUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import com.songoda.kingdoms.main.Kingdoms;

public class ExternalManager extends Manager {

	private static PlaceholderAPIManager placeholderAPIManager;
	private static WorldGuardManager worldGuardManager;
	private static ResidenceManager residenceManager;
	private static ScoreboardManager scoreboardManager;
	private static PlaceholderAPIManager placeholderManager;
	private static MVdWPlaceholderManager mvdwplaceholderManager;
	private static TitleAPIManager titleAPIManager;
	private static ActionbarAPIManager actionBarAPIManager;
	private static MVdWPlaceholderManager mvdwPlaceholderManager;
	private static DynmapManager dynmapManager;
	private static VaultManager vaultManager;
	private static CitizensManager citizensManager;
	private static GriefPreventionManager griefPreventionManager;
	private static VehiclesManager vehiclesManager;
	private static CannonsManager cannonsManager;
	//private static GuardianBeamManager guardianBeamManager;
	
	public ExternalManager(Plugin plugin) {
		super(plugin);
		checkSoftDepends(plugin);
	}
	
	private void checkSoftDepends(Plugin plugin){
		PluginManager pm = Bukkit.getPluginManager();
		if(worldGuardManager == null
				&& Config.getConfig().getBoolean("useWorldguardSupport")
				&& pm.getPlugin("WorldGuard") != null){
			if (!pm.getPlugin("WorldGuard").getDescription().getVersion().startsWith("7")) {
				worldGuardManager = new WorldGuardManager(plugin);
			}
		}
		if(griefPreventionManager == null
				&& Config.getConfig().getBoolean("useGriefPreventionSupport")
				&& pm.getPlugin("GriefPrevention") != null){
			griefPreventionManager = new GriefPreventionManager(plugin);
		}

		if(residenceManager == null 
				&& Config.getConfig().getBoolean("useResidenceSupport")
				&& pm.getPlugin("Residence") != null){
			residenceManager = new ResidenceManager(plugin);
		}
		if(titleAPIManager == null
				&& Config.getConfig().getBoolean("useTitleAPISupport")
				&& pm.getPlugin("TitleAPI") != null){
			titleAPIManager = new TitleAPIManager(plugin);
		}
		if(actionBarAPIManager == null
				&& Config.getConfig().getBoolean("useActionBarAPISupport")
				&& pm.getPlugin("ActionBarAPI") != null){
			actionBarAPIManager = new ActionbarAPIManager(plugin);
		}
		if(scoreboardManager == null
				&& Config.getConfig().getBoolean("useScoreboardStatsSupport")
				&& pm.getPlugin("ScoreboardStats") != null){
			scoreboardManager = new ScoreboardManager(plugin);
		}
		if(placeholderAPIManager == null
				&& Config.getConfig().getBoolean("usePlaceHolderAPISupport")
				&& pm.getPlugin("PlaceholderAPI") != null){
			placeholderAPIManager = new PlaceholderAPIManager(plugin);
		}
		if(mvdwPlaceholderManager == null
				&& Config.getConfig().getBoolean("useMVdWPlaceHolderAPISupport")
				&& pm.getPlugin("MVdWPlaceholderAPI") != null){
			mvdwPlaceholderManager = new MVdWPlaceholderManager(plugin);
		}
		if(dynmapManager == null 
				&& Config.getConfig().getBoolean("useDynmapSupport")
				&& pm.getPlugin("dynmap") != null){
			dynmapManager = new DynmapManager(plugin);
		}
		if(vaultManager == null 
				&& pm.getPlugin("Vault") != null){
			vaultManager = new VaultManager(plugin);
		}
		if(citizensManager == null
				&& pm.getPlugin("Citizens") != null){
			citizensManager = new CitizensManager(plugin);
		}
		if(vehiclesManager == null
				&& pm.getPlugin("Vehicles") != null){
			vehiclesManager = new VehiclesManager(plugin);
		}
		if(cannonsManager == null 
				&& pm.getPlugin("Cannons") != null){
			cannonsManager = new CannonsManager(plugin);
		}
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event){
		checkSoftDepends(plugin);
	}

	public static boolean isCitizen(Entity e){
		if(citizensManager != null) return citizensManager.isCitizen(e);
		return false;
	}


	public static boolean isInRegion(Location loc){
		if(worldGuardManager != null){
			if(worldGuardManager.isInRegion(loc)){
				return true;
			}
		}
		if(residenceManager != null){
			if(residenceManager.isInRegion(loc)){
				return true;
			}
		}
		if(griefPreventionManager != null){
			if(griefPreventionManager.isInRegion(loc)){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean canBuild(Player p, Location loc){
		if(worldGuardManager != null){
			return worldGuardManager.canBuild(p, loc);
		}
		if(residenceManager != null){
			return residenceManager.canBuild(p, loc);
		}
		if(griefPreventionManager != null){
			return griefPreventionManager.canBuild(p, loc);
		}
		return true;
	}
	public static boolean canBuild(Player p, Block b){
		Location loc = b.getLocation();
		if(worldGuardManager != null){
			return worldGuardManager.canBuild(p, loc);
		}
		if(residenceManager != null){
			return residenceManager.canBuild(p, loc);
		}
		if(griefPreventionManager != null){
			return griefPreventionManager.canBuild(p, loc);
		}
		return true;
	}
	
	//public static GuardianBeamManager getBeamManager(){
	//	return guardianBeamManager;
	//}
	
	public static double getBalance(OfflinePlayer p){
		if(vaultManager != null)
		return VaultManager.getBalance(p);
		
		return 0;
	}
	
	public static void depositPlayer(OfflinePlayer p, double amt){
		if(vaultManager != null) vaultManager.deposit(p, amt);
	}
	public static void withdrawPlayer(OfflinePlayer p, double amt){
		if(vaultManager != null) vaultManager.withdraw(p, amt);
	}

	public static void sendTitleBar(Player p, String title, String lore){
		if(titleAPIManager != null) titleAPIManager.sendTitle(p, title, lore);
	}
	public static boolean sendActionBar(Player p, String message){
		if(actionBarAPIManager != null){
			actionBarAPIManager.sendActionBar(p, message);
			return true;
		}
		return false;
	}
	
	public static ScoreboardManager getScoreboardManager(){
		return scoreboardManager;
	}
	
	public static VaultManager getVaultManager(){
		return vaultManager;
	}
	
	public static DynmapManager getDynmapManager(){
		return dynmapManager;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	public static boolean cannotClaimInRegion(Location loc){
		if(worldGuardManager != null){
			if(worldGuardManager.cannotClaimInRegion(loc)){
				return true;
			}
		}
		if(residenceManager != null){
			if(residenceManager.isInRegion(loc)){
				return true;
			}
		}
		if(griefPreventionManager != null){
			if(griefPreventionManager.isInRegion(loc)){
				return true;
			}
		}
		
		return false;
	}

}
