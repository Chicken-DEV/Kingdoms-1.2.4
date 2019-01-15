package com.songoda.kingdoms.manager.game;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.database.Database;
import com.songoda.kingdoms.database.DatabaseTransferTask;
import com.songoda.kingdoms.database.DatabaseTransferTask.TransferPair;
import com.songoda.kingdoms.database.MySqlDatabase;
import com.songoda.kingdoms.database.SQLiteDatabase;
import com.songoda.kingdoms.events.KingdomPlayerLoginEvent;
import com.songoda.kingdoms.events.KingdomPlayerLogoffEvent;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.external.ExternalManager;

import com.google.gson.JsonSyntaxException;

public class PlayerManager extends Manager implements Listener{
	//public static final File PLAYERFOLDER = new File(Kingdoms.getInstance().getDataFolder(), Kingdoms.config.playerTable);
	protected static Map<UUID, OfflineKingdomPlayer> userList = new ConcurrentHashMap<UUID, OfflineKingdomPlayer>();
	private static Database<OfflineKingdomPlayer> db;
	//2016-08-11
	//private static Database<OfflineKingdomPlayer> dbOffp;

	private final Thread autoSaveThread;

	PlayerManager(final Plugin plugin) {
		super(plugin);

		if(Config.getConfig().getBoolean("MySql.Enabled")){
			try {
				if(Config.getConfig().getBoolean("DO-NOT-TOUCH.grabPlayerFromFileDB")){
					Config.getConfig().set("DO-NOT-TOUCH.grabPlayerFromFileDB",false);
					List<TransferPair> pairs = new ArrayList<TransferPair>();
					pairs.add(new TransferPair(createFileDB(),createMysqlDB()));
					new Thread(new DatabaseTransferTask(Kingdoms.getInstance(), pairs)).start();
				}
				db = createMysqlDB();
				//2016-08-11
				//dbOffp = createMysqlDB();
				Kingdoms.logInfo("Mysql Connection Success!");
				Kingdoms.logInfo("Using " + Config.getConfig().getString("MySql.DBAddr") + " with user " + Config.getConfig().getString("MySql.DBUser"));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
					| NamingException e) {
				Kingdoms.logInfo("Mysql Connection Failed!");
				Kingdoms.logInfo("Using " + Config.getConfig().getString("MySql.DBAddr") + " with user " + Config.getConfig().getString("MySql.DBUser"));
				Kingdoms.logInfo(e.getMessage());
				Config.getConfig().set("DO-NOT-TOUCH.grabPlayerFromFileDB",true);
			} finally {
				if(db == null){
					db = createFileDB();
					Kingdoms.logInfo("Using file database for Player data");
				}

				userList.clear();
				for(Player player : Bukkit.getOnlinePlayers()){
					onJoin(new PlayerJoinEvent(player, null));
				}

				autoSaveThread = new Thread(new AutoSaveTask());
				//2016-08-11
				autoSaveThread.setPriority(Thread.MIN_PRIORITY);
				if(Config.getConfig().getBoolean("Plugin.enable-autosave"))
					autoSaveThread.start();
			}
		}else{
			db = createFileDB();
			Kingdoms.logInfo("Using file database for Player data");

			userList.clear();
			for(Player player : Bukkit.getOnlinePlayers()){
				

		        UUID uuid = player.getUniqueId();

		        KingdomPlayer kp = loadKingdomPlayer(uuid);

		        // kp cannot be null but just in case of bug
		        if (kp != null){
		            userList.put(uuid, kp);

		            if (!Config.getConfig().getBoolean("markers-on-by-default")) {
		                kp.setMarkDisplaying(false);
		            }
		        }else{
		            throw new RuntimeException("preload failed for "+player.getName());
		        }
		    
				
				onJoin(new PlayerJoinEvent(player, null));
			}

			autoSaveThread = new Thread(new AutoSaveTask());
			//2016-08-11
			autoSaveThread.setPriority(Thread.MIN_PRIORITY);
			if(Config.getConfig().getBoolean("Plugin.enable-autosave"))
				autoSaveThread.start();
		}

		//new Thread(new PreLoadTask()).start();

	}

	public SQLiteDatabase<OfflineKingdomPlayer> createFileDB(){
		return new SQLiteDatabase<>(plugin.getDataFolder(),"db.db", Config.getConfig().getString("MySql.player-table-name"), KingdomPlayer.class);
	}

	public MySqlDatabase<OfflineKingdomPlayer> createMysqlDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, NamingException{
		return new MySqlDatabase<>(Config.getConfig().getString("MySql.DBAddr"),Config.getConfig().getString("MySql.DBName"),Config.getConfig().getString("MySql.player-table-name"),Config.getConfig().getString("MySql.DBUser"),Config.getConfig().getString("MySql.DBPassword"),KingdomPlayer.class);
	}

	public TransferPair<OfflineKingdomPlayer> getTransferPair(Database<OfflineKingdomPlayer> from){
		return new TransferPair<OfflineKingdomPlayer>(from, db);
	}

	private class AutoSaveTask implements Runnable{

		@Override
		public void run() {
			while(plugin.isEnabled()){
				try {
					Thread.sleep(5 * 1000L);
				} catch (InterruptedException e) {
					Kingdoms.logInfo("Player auto save is interrupted.");

					//2016-08-22
					return;
				}

				saveAll();
			}
		}

//		private void perform(){
//			userList.forEach((k,v) -> {
//				db.saveData(k.toString(), v);
//				if(!(v instanceof KingdomPlayer)) {
//					Kingdoms.logDebug(k+" is saved and removed.");
//					Kingdoms.logDebug(v.getKingdomName());
//					Kingdoms.logDebug(""+v.getRank());
//					userList.remove(k);
//				}
//			});
//		}
	}

	// 2016-05-18
	private synchronized void saveAll() {
		for (Entry<UUID, OfflineKingdomPlayer> entry : userList.entrySet()) {

			UUID id = entry.getKey();
			OfflineKingdomPlayer okp = entry.getValue();
			Kingdoms.logColor("Saving player" + okp.getName());
			try {
				db.save(id.toString(), okp);
			} catch (Exception e) {
				Bukkit.getLogger().severe("[Kingdoms] Failed autosave!");
			}
			//2017-05-09 -- no point to remove this. Wasting resources. This will be replaced anyway when the player become online.
/*			if (!(okp instanceof KingdomPlayer)) {
				Kingdoms.logDebug(id + " is saved and removed.");
				Kingdoms.logDebug(okp.getKingdomName());
				Kingdoms.logDebug("" + okp.getRank());
				userList.remove(id);
			}*/
		}
	}

	//2017-05-09
	public OfflineKingdomPlayer getOfflineKingdomPlayer(UUID uuid){
		return getOrLoadKingdomPlayer(uuid);
	}

	//2017-05-09
	public OfflineKingdomPlayer getOfflineKingdomPlayer(OfflinePlayer p){
		return getOrLoadKingdomPlayer(p.getUniqueId());
	}
	
	private synchronized Object databaseLoad(String name, OfflineKingdomPlayer obj){
		return db.load(name, obj);
	}

	//2017-05-09
	/**
	 *
	 * @param uuid
	 * @return
	 */
    private OfflineKingdomPlayer getOrLoadKingdomPlayer(UUID uuid){
		if(uuid == null) return null;
		Kingdoms.logColor("Getting session of offline player, " + uuid.toString());

		if(userList.containsKey(uuid)){
			if(userList.get(uuid) instanceof KingdomPlayer&&
					((KingdomPlayer) userList.get(uuid)).isTemp()){
				
			}else{
				return getUserFromList(uuid);
			}
		}

		//this is the only point where main thread will be locked for db access.
		//We might can make it more elegant, but lets leave it this way for a while
		//Though, it's not to worry about as we will no longer delete offline info from userlist.
		//Check out saveAll() method if you are in doubt.
		OfflineKingdomPlayer okp = db.load(uuid.toString(), null);
		if(okp != null){
			userList.put(uuid, okp);
		}
		return okp;
	}

	public boolean isOnline(UUID uuid){
		if(!userList.containsKey(uuid))
			return false;

		KingdomPlayer kp = getUserFromList(uuid).getKingdomPlayer();
		if(kp.getPlayer() == null)
			return false;

		return getUserFromList(uuid).getKingdomPlayer().getPlayer().isOnline();
	}

	//2017-05-09 -- We now load player on prelogin process. In this way, we don't have to lock main thread while loading data from db.
	//              PlayerJoinEvent will not be called unless the AsyncPlayerPreLoginEvent is finished.
    @EventHandler(priority = EventPriority.LOW)
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
    	if(!Config.getConfig().getBoolean("Plugin.initiatePlayerLoadBeforePlayerJoin")) return;
        UUID uuid = e.getUniqueId();

        KingdomPlayer kp = loadKingdomPlayer(uuid);

        // kp cannot be null but just in case of bug
        if (kp != null){
            userList.put(uuid, kp);
            Kingdoms.logInfo("Loaded info for " + kp.getName());
            if (!Config.getConfig().getBoolean("markers-on-by-default")) {
                kp.setMarkDisplaying(false);
            }
        }else{
        	Kingdoms.logInfo("Failed to load info for " + e.getName());
            throw new RuntimeException("preload failed for "+e.getName());
        }
    }
    
    public void asyncLoadPlayer(Player player){
        UUID uuid = player.getUniqueId();
    	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
    		
    		public void run(){
    			KingdomPlayer kp = loadKingdomPlayer(uuid);

    	        // kp cannot be null but just in case of bug
    	        if (kp != null){
    	            userList.put(uuid, kp);
    	            Kingdoms.logInfo("[ERROR]: Player data for " + kp.getName() + " wasn't loaded on start! Attempting to load now!");
    	            if (!Config.getConfig().getBoolean("markers-on-by-default")) {
    	                kp.setMarkDisplaying(false);
    	            }
    	           
    	        }
    		}
    		
    	});
    }
    public void asyncLoadPlayer(UUID uuid){
    	Player p = Bukkit.getPlayer(uuid);
    	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
    		
    		public void run(){
    			KingdomPlayer kp = loadKingdomPlayer(uuid);

    	        // kp cannot be null but just in case of bug
    	        if (kp != null){
    	            userList.put(uuid, kp);
    	            Kingdoms.logInfo("[ERROR]: Player data for " + kp.getName() + " wasn't loaded on start! Attempting to load now!");
    	            if (!Config.getConfig().getBoolean("markers-on-by-default")) {
    	                kp.setMarkDisplaying(false);
    	            }
    	        }
    	        Kingdoms.logDebug("Loaded player data for " + kp.getName() + " asyncLoadPlayer(uuid)");
 	           
    		}
    		
    	});
    }
    
    //2017-05-09 -- We need to set most fresh Player object into the KingdomPlayer we've created in prelogin step.
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        KingdomPlayer kp = getSession(uuid);
        if(kp == null || kp.isTemp()){
        	//Kingdoms.logInfo("Info for " + player.getName() + " isn't loaded. Attempting to load");
            //throw new RuntimeException("Something went wrong. Player info is not loaded for "+player.getName());
        	asyncLoadPlayer(player);
        
        
        }
        plugin.getServer().getPluginManager().callEvent(new KingdomPlayerLoginEvent(kp));
    }
//
//    @EventHandler(priority = EventPriority.NORMAL)
//    public void latePlayerJoin(final PlayerJoinEvent event) {
//        final Player player = event.getPlayer();
//        if(player.hasPermission("kingdoms.admin") && plugin.updateAvailable) {
//            Bukkit.getScheduler().runTaskLater(plugin, new Runnable () {
//                public void run() {
//                    player.sendMessage(ChatColor.GREEN + "A new version of Kingdoms is Out!");
//                    player.sendMessage(ChatColor.GREEN + "Version "+ plugin.availableVersion + ", current version running is version " + plugin.getDescription().getVersion());
//                }
//            }, 20L);
//        }
//    }
//    
    //2017-05-09
	/**
	 * load kingdom player. <b> Has db access; call this method only from separate thread. </b>
	 * @param uuid target
	 * @return existing data or new data
	 */
	private KingdomPlayer loadKingdomPlayer(UUID uuid){
		Kingdoms.logColor("Loading player info for " + uuid);
		KingdomPlayer kp = null;

		//here we delete offline info and save it if necessary
		if (userList.containsKey(uuid)) {
			if(userList.get(uuid) instanceof KingdomPlayer&&
					((KingdomPlayer) userList.get(uuid)).isTemp()){
				userList.remove(uuid);
			}else{
				db.save(uuid.toString(), userList.remove(uuid));
			}
		}

		try {
			kp = (KingdomPlayer) databaseLoad(uuid.toString(), null);
		} catch (IllegalStateException e) {
			Kingdoms.logInfo("[ERROR]: The file, " +uuid.toString() + " under Players is corrupted.");
			return new KingdomPlayer(Bukkit.getPlayer(uuid.toString()));
		} catch (JsonSyntaxException e) {
			Kingdoms.logInfo("[ERROR]: The file, " + uuid.toString() + " under Players is corrupted.");
			return new KingdomPlayer(Bukkit.getPlayer(uuid.toString()));
		}

		if(kp == null)
		    kp = new KingdomPlayer(uuid);
		return kp;
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		if(!Config.getConfig().getBoolean("respawn-in-kingdom-home")) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(event.getPlayer().getWorld().getName())){
			KingdomPlayer kp = getSession(event.getPlayer());
			if(kp.getKingdom() != null){
				Kingdom kingdom = kp.getKingdom();
				if(kingdom.getHome_loc() != null){
					event.setRespawnLocation(kingdom.getHome_loc());
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if(!userList.containsKey(uuid)) return;
		OfflineKingdomPlayer okp = userList.remove(uuid);

		if(okp instanceof KingdomPlayer)
			plugin.getServer().getPluginManager().callEvent(new KingdomPlayerLogoffEvent((KingdomPlayer) okp));

		//2017-05-09 -- Yes. We do not want to lock main thread while saving this data.
		new Thread(new Runnable(){
            @Override
            public void run() {
                db.save(player.getUniqueId().toString(), okp);
            }
		}).start();
	}

	//2017-05-09 -- just use the overloaded method
	/**
	 * get current session of player.
	 * @param player
	 * @return KingdomPlayer if player is online; null if (player == null), offline, or not exist
	 */
	public KingdomPlayer getSession(Player player){
		if(player == null){
			return null;
		}
		Kingdoms.logColor("Getting session of player, " + player.getName());

		if(ExternalManager.isCitizen(player)) return null;

		return getSession(player.getUniqueId());
	}

	//2017-05-09 -- we only required to check if the session is alive. Session is alive means that the player is now online(KingdomPlayer exists).
	/**
	 * get current session of player.
	 * @param uuid
	 * @return KingdomPlayer if player is online; null if (player == null), offline, or not exist
	 */
	public KingdomPlayer getSession(UUID uuid){
		if(uuid == null) return null;

		Kingdoms.logColor("Getting session of player uuid, " + uuid.toString());
        if(getUserFromList(uuid) instanceof KingdomPlayer){
            return (KingdomPlayer) getUserFromList(uuid);
        }else{
        	asyncLoadPlayer(uuid);
            return  (KingdomPlayer) getUserFromList(uuid);
        }
	}
	
	


	private OfflineKingdomPlayer getUserFromList(UUID uuid) {
		if(userList.get(uuid) == null){
			KingdomPlayer temp = new KingdomPlayer(uuid);
			temp.setTemp(true);
			return temp;
			
		}else{
			return userList.get(uuid);
		}
	}

	public void stopAutoSave(){
		autoSaveThread.interrupt();
		// 2016-08-22
		try {
			autoSaveThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public synchronized void onDisable() {
		//2016-08-11
		stopAutoSave();
		Kingdoms.logInfo("Saving ["+userList.size()+"] loaded players...");
		try{
			saveAll();
			Kingdoms.logInfo("Done!");
		}catch(Exception e){
			Kingdoms.logInfo("SQL connection failed! Saving to file DB");
			db = createFileDB();
			saveAll();
			Config.getConfig().set("DO-NOT-TOUCH.grabPlayerFromFileDB",true);
		}
		userList.clear();

	}

	///////////////////////////////////////////////////////////////////

}
