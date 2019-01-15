package com.songoda.kingdoms.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.songoda.kingdoms.constants.land.WarpPadManager;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.gui.GUIManagement;
import com.songoda.kingdoms.tps.Lag;
import com.songoda.kingdoms.utils.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.GsonBuilder;

public class Kingdoms extends JavaPlugin{
	//TurretManager
	private static GsonBuilder gsonBuilder;
	public static String dataConstant = "%%__USER__%%";
	private static Kingdoms instance;
	private static String PLUGIN_NAME;
	private static boolean isDisabling = false;
	private static Lang lang;
	private static CommandExecutor cmdExe;
	private static GameManagement managers;
	private static GUIManagement guiManagement;
	public boolean finishedEnabling = false;
	public static final Map<String, World> worlds = new HashMap<String, World>();
	public static final Map<World, String> worldNames = new HashMap<World, String>();

	public static void logDebug(String str) {
		if(Config.getConfig().getBoolean("Plugin.Debug")) {
			Bukkit.getLogger().info(PLUGIN_NAME + " " + str);
		}
	}

	public static void logColor(String s) {
		if (Config.getConfig().getBoolean("Plugin.Monitor-mode")){
			Bukkit.getLogger().warning(PLUGIN_NAME+" "+s);
		}
	}

	//	public static HashMap<ItemStack, Integer> specialcaseitems = new HashMap<ItemStack, Integer>();
//	public static ArrayList<ItemStack> blacklistitems = new ArrayList<ItemStack>();
//	public static HashMap<ItemStack, Integer> whitelistitems = new HashMap<ItemStack, Integer>();
//
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		cmdExe.onCommand(sender, command, label, args);

		return true;
	}

	@Override
	public synchronized void onDisable() {
		//2016-05-18
		for(Manager manager : Manager.getModules()){
			if(manager == null){
				continue;
			}
			isDisabling = true;
			manager.onDisable();
		}
	}

	public static boolean isDisabling(){
		return isDisabling;
	}

	@Override
	public void onEnable() {
		instance = this;
		new Config(this);
		lang = new Lang(this);
		PLUGIN_NAME = this.getDescription().getFullName();

		//2017-04-27
		for(World world : Bukkit.getWorlds()){
			worlds.put(world.getName(), world);
			worldNames.put(world, world.getName());
		}
		Bukkit.getPluginManager().registerEvents(new Listener(){
            @EventHandler
            public void onWorldLoad(WorldLoadEvent e) {
                World world = e.getWorld();

                World previous = worlds.put(world.getName(), world);
                if(previous != null)
                    worldNames.remove(previous);

                worldNames.put(world, world.getName());
            }
            @EventHandler
            public void onWorldUnload(WorldLoadEvent e) {
                World world = e.getWorld();

                World previous = worlds.remove(world.getName());
                if(previous != null)
                    worldNames.remove(previous);
            }
		}, this);

		if(databaseErrorCheck.contains(dataConstant)){
			return;
		}
		PluginManager pm = getServer().getPluginManager();

		cmdExe = new CommandExecutor(this);

		managers = new GameManagement(this);
		guiManagement = new GUIManagement(this);
		//pm.registerEvents(new ScrollerInventoryManager(this), this);
		for(Manager manager : Manager.getModules()){
			if(manager == null){
				continue;
			}
//			if(manager instanceof GeneralAPIManager){
//				return;
//			}
			try {
				pm.registerEvents(manager, this);
				Kingdoms.logInfo(manager.getClass().getSimpleName() + " loaded");
			} catch (Exception e) {
			} catch (Error e){
			}
		}
		WarpPadManager.load();
		while(!postLoadEventQueue.isEmpty()){
			Event e = postLoadEventQueue.poll();
			if(e == null) continue;

			getServer().getPluginManager().callEvent(e);
		}
		///////////////////////////////////////////////////////////////////////////////////////////
		//loadTradeItems();
		//and so on

		///////////////////////////////////////////////////////////////////////////////////////////
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
		
//		 getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
//	            public void run() {
//	                checkUpdate();
//	            }
//	        });
//		
		
		super.onEnable();
		finishedEnabling = true;
	}
	
	/**
	 * it just doesn't work at all right now
	 */
	@Deprecated
	private void checkUpdate() {
        Kingdoms.logInfo("Checking for updates");
        final Updater updater = new Updater(this, false);
        final Updater.UpdateResult result = updater.getResult();
        switch (result) {
            case FAIL_SPIGOT: {
            	Kingdoms.logInfo(ChatColor.RED + "The updater could not contact spigot.");
                break;
            }
            case NO_UPDATE: {
            	Kingdoms.logInfo(ChatColor.GREEN + "Kingdoms is up to date.");
                break;
            }
            case UPDATE_AVAILABLE: {
               availableVersion = updater.getVersion();
               Kingdoms.logInfo(ChatColor.AQUA + "============================================");
               Kingdoms.logInfo(ChatColor.AQUA + "");
               Kingdoms.logInfo(ChatColor.AQUA + "");
               Kingdoms.logInfo(ChatColor.AQUA + "An update is available:");
               Kingdoms.logInfo(ChatColor.AQUA + "Kingdoms Version " + updater.getVersion());
               Kingdoms.logInfo(ChatColor.AQUA + "");
               Kingdoms.logInfo(ChatColor.AQUA + "");
               Kingdoms.logInfo(ChatColor.AQUA + "============================================");
               updateAvailable = true;
                break;
            }
            default: {
            	//Kingdoms.logInfo(result.toString());
                break;
            }
        }
    }
	public boolean updateAvailable = false;
	public String availableVersion;
	final String uid = "a uid";

	public String getUid() {
		return uid;
	}

	public static Kingdoms getInstance() {

		return instance;
	}

	public static void logInfo(String str){
		Bukkit.getLogger().info(PLUGIN_NAME+" "+str);
	}

	public static Lang getLang() {
		return lang;
	}

	private ArrayList<String> databaseErrorCheck = new ArrayList<String>(){{
		add("fooooooooooooo");
	}};

	public static CommandExecutor getCmdExe() {
		return cmdExe;
	}

	public static GameManagement getManagers() {
		return managers;
	}

	public static GUIManagement getGuiManagement() {
		return guiManagement;
	}

	private static Queue<Event> postLoadEventQueue = new LinkedList<Event>();
	public static void queuePostLoadEvent(Event e){
		postLoadEventQueue.add(e);
	}

	public void reload(){
	    Lang reloading = new Lang(this);
	    lang = reloading;
		reloadConfig();
		new Config(this);
		guiManagement.getNexusGUIManager().init();

	}
}
