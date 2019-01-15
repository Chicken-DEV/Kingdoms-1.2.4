package com.songoda.kingdoms.manager.game;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.land.WarpPadManager;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.database.Database;
import com.songoda.kingdoms.database.DatabaseTransferTask;
import com.songoda.kingdoms.database.DatabaseTransferTask.TransferPair;
import com.songoda.kingdoms.database.MySqlDatabase;
import com.songoda.kingdoms.database.SQLiteDatabase;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.gui.GUIManagement;
import com.songoda.kingdoms.events.LandClaimEvent;
import com.songoda.kingdoms.events.LandLoadEvent;
import com.songoda.kingdoms.events.LandUnclaimEvent;
import com.songoda.kingdoms.events.PlayerChangeChunkEvent;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.external.ExternalManager;

//2016-07-02 -- plain File accessing database is now changed to abstract Database form
public class LandManager extends Manager implements Listener{
	protected static Map<SimpleChunkLocation, Land> landList = new ConcurrentHashMap<SimpleChunkLocation, Land>();

	private static Database<Land> db; //2016-07-02 -- abstract Database

	private final Thread autoSaveThread;

	LandManager(Plugin plugin) throws IOException {
		super(plugin);

		if(Config.getConfig().getBoolean("MySql.Enabled")){
			try {
				if(Config.getConfig().getBoolean("DO-NOT-TOUCH.grabLandFromFileDB")){
					Config.getConfig().set("DO-NOT-TOUCH.grabLandFromFileDB",false);
					List<TransferPair> pairs = new ArrayList<TransferPair>();
					pairs.add(new TransferPair(createFileDB(),createMysqlDB()));
					new Thread(new DatabaseTransferTask(Kingdoms.getInstance(), pairs)).start();
				}
				db = createMysqlDB();
				Kingdoms.logInfo("Mysql Connection Success!");
				Kingdoms.logInfo("Using "+Config.getConfig().getString("MySql.DBAddr")+" with user "+ Config.getConfig().getString("MySql.DBUser"));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
					| NamingException e) {
				Kingdoms.logInfo("Mysql Connection Failed!");
				Kingdoms.logInfo("Using "+Config.getConfig().getString("MySql.DBAddr")+" with user "+Config.getConfig().getString("MySql.DBUser"));
				Kingdoms.logInfo(e.getMessage());
				Config.getConfig().set("DO-NOT-TOUCH.grabLandFromFileDB",true);
			} finally {
				if(db == null){
					db = createFileDB();
					Kingdoms.logInfo("Using file database for Land data");
				}

				landList.clear();
				//added
				initLands();

				//2016-08-11

				autoSaveThread = new Thread(new autoSaveTask());
				autoSaveThread.setPriority(Thread.MIN_PRIORITY);
				if(Config.getConfig().getBoolean("Plugin.enable-autosave"))
					autoSaveThread.start();
			}
		}else{
			db = createFileDB();
			Kingdoms.logInfo("Using file database for Land data");
			landList.clear();
			//added
			initLands();

			//2016-08-11
			autoSaveThread = new Thread(new autoSaveTask());
			autoSaveThread.setPriority(Thread.MIN_PRIORITY);
			if(Config.getConfig().getBoolean("Plugin.enable-autosave"))
				autoSaveThread.start();
		}

		if(Config.getConfig().getBoolean("tax.enabled")) {
			int time = 0;
			String interval = Config.getConfig().getString("tax.interval");
			Pattern regex = Pattern.compile("([0-9]+)(minute(s|)|m|hour(s|)|h|day(s|)|d|week(s|)|w)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
			Matcher regexMatcher = regex.matcher(interval);
			while (regexMatcher.find()) {
				interval = regexMatcher.group(1);

				switch (regexMatcher.group(2).toLowerCase().charAt(0)) {
					case 'm':
						time = time + Integer.parseInt(interval)*60;
						break;
					case 'h':
						time = time + Integer.parseInt(interval)*60*60;
						break;
					case 'd':
						time = time + Integer.parseInt(interval)*60*60*24;
						break;
					case 'w':
						time = time + Integer.parseInt(interval)*60*60*24*7;
						break;
					default:
						break;
				}
			}
			Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new LandLogisticsTask(this), 20 * time, 20 * time);
		}
	}

	public SQLiteDatabase<Land> createFileDB(){
		return new SQLiteDatabase<>(plugin.getDataFolder(),"db.db",Config.getConfig().getString("MySql.land-table-name"), Land.class);
	}

	public MySqlDatabase<Land> createMysqlDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, NamingException{
		return new MySqlDatabase<>(Config.getConfig().getString("MySql.DBAddr"),Config.getConfig().getString("MySql.DBName"),Config.getConfig().getString("MySql.land-table-name"),Config.getConfig().getString("MySql.DBUser"),Config.getConfig().getString("MySql.DBPassword"),Land.class);
	}

	public TransferPair<Land> getTransferPair(Database<Land> from){
		return new TransferPair<Land>(from, db);
	}

	private synchronized Land databaseLoad(String name, Land land){
		return db.load(name, land);
	}


	private HashMap<SimpleChunkLocation, Land> toBeLoaded = new HashMap<SimpleChunkLocation, Land>();
	///////////////////////////////////////////////////////////////////////////////////////////////
	//added
	private void initLands() throws IOException{
		//2016-12-15
		Set<String> keys = db.getKeys();
		if (keys.contains("LandData") && keys.size() == 1) {
			//OldUnusedLandManager oldManager = new OldUnusedLandManager(plugin, this);
			plugin.logInfo("ERROR: Using old land data! Please revert to an older version to convert old data to new!");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		for(String landName:keys){
			if(landName.equals("LandData")) continue;
			if(landName.endsWith("_temp")){
				continue;
			}
			//2016-08-11
			Kingdoms.logColor("Loading land: " + landName);
			try{
				SimpleChunkLocation chunk = SimpleChunkLocation.chunkStrToLoc(landName);
				Land land = databaseLoad(landName, null);
				if(Bukkit.getWorld(chunk.getWorld()) == null){

					if(!toBeLoaded.containsKey(chunk))toBeLoaded.put(chunk, land);

					continue;
				}
				//the land has owner but the owner kingdom doesn't exist
				if(land.getOwnerUUID() != null){
					Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
					if (kingdom == null) {
						Kingdoms.logInfo("Land data [" + landName + "] is corrupted! ignoring...");
						Kingdoms.logInfo("The land owner is [" + land.getOwner() + "] but no such kingdom with the name exists");
						//continue;
					}

				}

				if(!landList.containsKey(chunk))landList.put(chunk, land);
				Kingdoms.queuePostLoadEvent(new LandLoadEvent(land));
			}catch(Exception e){
				Kingdoms.logInfo("Land data ["+landName+"] is corrupted! ignoring...");
				if(Config.getConfig().getBoolean("Plugin.Debug")) e.printStackTrace();;
			}
		}
		db.save("LandData", null);
		Kingdoms.logInfo("Total of ["+getAllLandLoc().size()+"] lands are initialized");
	}

	private class autoSaveTask implements Runnable{

		@Override
		public void run() {
			//2016-12-15
			while(plugin.isEnabled() && !Thread.interrupted()){
				try {
					Thread.sleep(5 * 60 * 1000L);
				} catch (InterruptedException e) {
					Kingdoms.logInfo("Land auto save is interrupted.");

					//2016-08-22
					return;
				}

				try {
					int i = saveAll();
					Kingdoms.logDebug("Saved [" + i + "] lands");
				} catch (InterruptedException e) {
					Kingdoms.logInfo("Land auto save is interrupted.");
					return;
				}
			}
		}

	}

	//2016-05-18
	private synchronized int saveAll() throws InterruptedException{
		int i = 0;
		Kingdoms.logDebug("Beginning Land Save");
		//2016-12-15
		Set<String> saved = new HashSet<String>();
		String landName = null;
		Land land = null;
		//synchronized (landList) {
		Collection<SimpleChunkLocation>  locs =  getAllLandLoc();
		for (SimpleChunkLocation loc:locs) {
			landName = loc.toString();
			if(saved.contains(landName)) continue;
			Kingdoms.logColor("Saving land: " + landName);
//				if(saved.contains(landName)){
//					continue;
//				}
			land = getOrLoadLand(loc);
			if (land.getOwnerUUID() == null && land.getTurrets().size() <= 0 && land.getStructure() == null) {
				db.save(landName, null);
				saved.add(landName);
				i++;
				continue;
			}
			if (land.getOwnerUUID() != null && Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerUUID()) == null) {
				db.save(landName, null);
				saved.add(landName);
				i++;
				continue;
			}
			//Kingdoms.logDebug("Saving land: " + landName);
			try{
				db.save(landName, land);
				i++;
				saved.add(landName);
			}catch(Exception e){

				Bukkit.getLogger().severe("[Kingdoms] Failed autosave for a piece of land!");
			}
		}
		//}
		Kingdoms.logDebug("Finished.");
		return i;
	}

	/**
	 * get all existing land data.
	 * <b>Modifying the set also modifies the landList!</b>
	 * @return list of all land locations.
	 */
	public Set<SimpleChunkLocation> getAllLandLoc() {
/*		Set<SimpleChunkLocation> list = new HashSet<SimpleChunkLocation>();
		
		if(LANDFOLDER.listFiles() == null) return list;
		for(File file : LANDFOLDER.listFiles()) list.add(SimpleChunkLocation.chunkStrToLoc(file.getName()));
		*/
		return Collections.unmodifiableMap(landList).keySet();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * load land if exist; create if not exist
	 * @param loc location of land.
	 * @return always an land object; null if loc is null
	 */
	public Land getOrLoadLand(SimpleChunkLocation loc) {
		//2016-08-11
		if(loc == null)
			return null;

		Kingdoms.logColor("Fetching info for land: " + loc.toString());

		//2016-08-11
		Land land = landList.get(loc);
		//new land so create empty one
		if(land == null){
			//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: CREATING NEW LAND OBJECT AT: " + loc.toString());
			land = new Land(loc);
			if(!landList.containsKey(loc))landList.put(loc, land);
		}

		return land;
	}

	/*	private Land loadLand(SimpleChunkLocation loc){
            try {
                Land land = (Land) Data.createInstance(Land.class, new File(LANDFOLDER, loc.toString()));
                if(land != null)plugin.getServer().getPluginManager().callEvent(new LandLoadEvent(land));
                return land;
            } catch (IOException e) {
                return null;
            }
        }
    */

	/**
	 * claim a new land. This does not check if chunk is already occupied.
	 * @param chunk chunk location
	 * @param kingdom owner
	 */
	public void claimLand(SimpleChunkLocation chunk, Kingdom kingdom){
		Land land = getOrLoadLand(chunk);
		LandClaimEvent lce = new LandClaimEvent(land, kingdom);
		Bukkit.getPluginManager().callEvent(lce);

		if(lce.isCancelled()){
			return;
		}

		land.setClaimTime(new Date().getTime());
		land.setOwnerUUID(kingdom.getKingdomUuid());
		//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: OWNER CHANGED TO: " + kingdom.getKingdomName() + " AT " + chunk.toString());
		if(GameManagement.getDynmapManager() != null) GameManagement.getDynmapManager().updateClaimMarker(chunk);
	}

	/**
	 * unclaim the land. This does not check if chunk is occupied
	 * @param chunk chunk to unclaim
	 * @param kingdom kingdom who is unclaiming
	 */
	public void unclaimLand(SimpleChunkLocation chunk, Kingdom kingdom){
		final Land land = getOrLoadLand(chunk);
		if(land == null)
			return;

		LandUnclaimEvent lce = new LandUnclaimEvent(land, kingdom);
		Bukkit.getPluginManager().callEvent(lce);

		if(lce.isCancelled()){
			return;
		}
		land.setClaimTime(0L);
		land.setOwnerUUID(null);
		db.save(land.getName(),null);
		//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: OWNER REMOVED AT " + chunk.toString());
		if(land.getStructure() != null){
			new BukkitRunnable(){
				@Override
				public void run() {
					GameManagement.getStructureManager().breakStructure(land);
				}
			}.runTask(plugin);
		}

		if(GameManagement.getDynmapManager() != null) GameManagement.getDynmapManager().updateClaimMarker(chunk);
	}

	/**
	 * Unclaims ALL existing land in database
	 * Use at own risk.
	 */
	public void unclaimAllExistingLand() {
		for (OfflineKingdom kingdom : GameManagement.getKingdomManager().getKingdomList().values()) {
			unclaimAllLand(GameManagement.getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName()));
		}
	}


	private ArrayList<String> unclaiming = new ArrayList<String>();
	/**
	 * unclaim all lands belong to kingdom
	 * @param kingdom owner
	 * @return number of lands unclaimed
	 */
	public int unclaimAllLand(Kingdom kingdom){
		int count = 0;
		if(!unclaiming.contains(kingdom.getKingdomName())){
			unclaiming.add(kingdom.getKingdomName());
		}else{
			return -1;
		}
		for(SimpleChunkLocation chunk : getAllLandLoc()){
			final Land land = getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) {
				continue;
			}
			if(!land.getOwnerUUID().equals(kingdom.getKingdomUuid())) {
				continue;
			}
			LandClaimEvent lce = new LandClaimEvent(land, kingdom);
			Bukkit.getPluginManager().callEvent(lce);

			if(lce.isCancelled()){
				continue;
			}
			land.setClaimTime(0L);
			land.setOwnerUUID(null);
			//db.save(land.getName(),null);
			//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: UNCLAIM ALL CALLED FOR " + chunk.toString());
			if(land.getStructure() != null){
				new BukkitRunnable(){
					@Override
					public void run() {
						GameManagement.getStructureManager().breakStructure(land);

					}
				}.runTask(plugin);
			}

			count++;
			if(GameManagement.getDynmapManager() != null) GameManagement.getDynmapManager().addMarkerUpdateQueue(chunk);
		}
		unclaiming.remove(kingdom.getKingdomName());
		return count;
	}
	
	public boolean isConnectedToNexus(Land land){
		if(land.getOwnerUUID() == null) return false;
		Kingdom kingdom = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		if(kingdom == null) return false;
		if(kingdom.getNexus_loc() == null) return false;
		Land nexus = getOrLoadLand(new SimpleChunkLocation(kingdom.getNexus_loc().getChunk()));
		return getAllConnectingLand(nexus).contains(land);
	}

	/**
	 * unclaim all lands not connected to the kingdom, and with no structures.
	 * @param kingdom owner
	 * @return number of lands unclaimed
	 */
	public int unclaimDisconnectedLand(Kingdom kingdom){
		int count = 0;
		if(!unclaiming.contains(kingdom.getKingdomName())){
			unclaiming.add(kingdom.getKingdomName());
		}else{
			return -1;
		}
		Collection<Land> structureLands = new ArrayList();
		for(SimpleChunkLocation chunk : getAllLandLoc()){
			final Land land = getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			if(!land.getOwnerUUID().equals(kingdom.getKingdomUuid())) continue;
			if(land.getStructure() == null) continue;
			structureLands.add(land);
		}

		ArrayList<Land> connected = new ArrayList();
		for(Land land: structureLands){
			connected.addAll(getAllConnectingLand(land));
		}


		for(SimpleChunkLocation chunk : getAllLandLoc()){
			final Land land = getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			if(!land.getOwnerUUID().equals(kingdom.getKingdomUuid())) continue;
			if(connected.contains(land)) continue;
			LandUnclaimEvent lce = new LandUnclaimEvent(land, kingdom);
			Bukkit.getPluginManager().callEvent(lce);

			if(lce.isCancelled()){
				continue;
			}
			land.setClaimTime(0L);
			land.setOwnerUUID(null);
			db.save(land.getName(),null);
			//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: UNCLAIM DISCONNECTED CALLED AT " + chunk.toString());
			count++;
		}

		unclaiming.remove(kingdom.getKingdomName());
		return count;
	}

//	private void getAllConnectingLand(Land center, ArrayList<Land> checked){
//        if(!checked.contains(center)) {
//            checked.add(center);
//        }else return;
//        ArrayList<Land> connected = getConnectingLand(center, checked);
//        if(connected.isEmpty()) return;
//        for(Land conn:connected) {
//            checked.add(conn); //error line
//            Kingdoms.logDebug("Checking Claim:" + conn.getLoc().toString());
//            getAllConnectingLand(conn, checked);
//        }
//    }

	public ArrayList<Land> getAllConnectingLand(Land center) {
		ArrayList<Land> connected = new ArrayList<>();
		ArrayList<Land> checked = new ArrayList<>();
		if(!connected.contains(center))connected.add(center);
		ArrayList<Land> outwards = getOutwardLands(getConnectingLand(center, checked), checked);
		boolean newAdded = true;
		while(newAdded) {
			newAdded = false;
			ArrayList<Land> newOutwards = new ArrayList<>();
			for(Land land:outwards) {
				Kingdoms.logDebug("Checking Claim:" + land.getLoc().toString());
				if(checked.contains(land)) continue;
				checked.add(land);
				if(land.getOwnerUUID() == null) continue;
				if(!land.getOwnerUUID().equals(center.getOwnerUUID())) continue;
				connected.add(land);
				newOutwards.add(land);
				newAdded = true;
			}
			outwards = getOutwardLands(newOutwards, checked);
		}
		return connected;
	}

	public ArrayList<Land> getOutwardLands(ArrayList<Land> surroundings, ArrayList<Land> checked) {
		ArrayList<Land> connected = new ArrayList<>();
		for(Land land:surroundings) {
			for(Land furtherSurroundings:land.getSurrounding()) {
				if(surroundings.contains(furtherSurroundings))
					continue;
				if(checked.contains(furtherSurroundings))
					continue;
				if(connected.contains(furtherSurroundings))
					continue;
				connected.add(furtherSurroundings);
			}
		}
		return connected;
	}

	public ArrayList<Land> getConnectingLand(Land center, ArrayList<Land> checked){
		ArrayList<Land> connected = new ArrayList<>();
		for(Land land:center.getSurrounding()) {
			if(checked.contains(land)) continue;
			if(land.getOwnerUUID() == null) continue;
			if(!land.getOwnerUUID().equals(center.getOwnerUUID())) continue;
			connected.add(land);

		}
		return connected;
	}

	private class LandLogisticsTask implements Runnable {
		LandManager manager;
		public LandLogisticsTask(LandManager manager) {
			super();
			this.manager = manager;
		}

		@Override
		public void run() {
			Kingdoms.logDebug("LandLogistics Loop Initiate");
			Bukkit.getServer().broadcastMessage(Kingdoms.getLang().getString("Misc_TaxTake").replace("%taxamount%",String.valueOf(Config.getConfig().getInt("tax.amount"))));
			Map<SimpleChunkLocation, Land> lands = Collections.unmodifiableMap(landList);
			for(Land land:lands.values()){
				if(land.getOwnerUUID() != null){
					Kingdom kingdom = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
					if(kingdom == null) return;
					if(Config.getConfig().getInt("tax.amount") < 0 && kingdom.getResourcepoints() == 0 && Config.getConfig().getBoolean("tax.disbandondue")){
						Bukkit.getServer().broadcastMessage(Kingdoms.getLang().getString("Misc_TaxDisband").replaceAll("%kingdom%", kingdom.getKingdomName()));
						GameManagement.getKingdomManager().deleteKingdom(kingdom.getKingdomName());
						return;
					}
					kingdom.setResourcepoints(kingdom.getResourcepoints() + Config.getConfig().getInt("tax.amount"));
				}
			}
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event){
		ArrayList<Entry<SimpleChunkLocation, Land>> toBeRemoved = new ArrayList<Entry<SimpleChunkLocation, Land>>();
		for(Entry<SimpleChunkLocation, Land> set:toBeLoaded.entrySet()){
			Land land = set.getValue();
			SimpleChunkLocation chunk = set.getKey();
			//the land has owner but the owner kingdom doesn't exist
			if(land.getOwnerUUID() != null){
				Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
				if (kingdom == null) {
					Kingdoms.logInfo("Land data [" + chunk.toString() + "] is corrupted! ignoring...");
					Kingdoms.logInfo("The land owner is [" + land.getOwner() + "] but no such kingdom with the name exists");
					//continue;
				}

			}


			if(!landList.containsKey(chunk))landList.put(chunk, land);
			Bukkit.getPluginManager().callEvent(new LandLoadEvent(land));
			toBeRemoved.add(set);
		}
		for(Entry<SimpleChunkLocation, Land> set:toBeRemoved){
			WarpPadManager.checkLoad(set.getValue());
			toBeLoaded.remove(set.getKey());
		}


	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkChange(PlayerChangeChunkEvent e){
		if(ExternalManager.isCitizen(e.getPlayer())){
			return;
		}
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.isKMapOn()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Kingdoms.getInstance(), new Runnable() {
				public void run() {
					GUIManagement.getMapManager().displayMap(e.getPlayer(), false);
				}
			}, 1L);
		}
		
		if(kp.isKAutoClaimOn()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Kingdoms.getInstance(), new Runnable() {
				public void run() {
					attemptNormalLandClaim(kp);
				}
			}, 1L);
		}
	}
	
	public void attemptNormalLandClaim(KingdomPlayer kp){
		if(!Config.getConfig().getStringList("enabled-worlds").contains(kp.getPlayer().getWorld().getName())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Invalid_World", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = kp.getKingdom();
		if(kingdom == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		if(!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getClaim())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getClaim().toString()));
			return;
		}
		
		if (ExternalManager.cannotClaimInRegion(kp.getPlayer().getLocation())) {
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Worldguard_Claim_Off_Limits", kp.getLang()));
			return;
		}
		
		Land land = GameManagement.getLandManager().getOrLoadLand(kp.getLoc());
		if(land.getOwnerUUID() != null){
			if(land.getOwnerUUID().equals(kingdom.getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Land_Owned_Error", kp.getLang()));
				return;
			}
			
			kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Land_Occupied_Error", kp.getLang()).replaceAll("%kingdom%", land.getOwner()));
			kp.sendMessage("You may conquer this land by invading. (/k invade)");
			return;
		}
		
		//land amount < land-per-member * kingdomMemberNumb
		//land amount < maximum-land-claims , maximum-land-claims > 0
		if((kingdom.getLand() >= (Config.getConfig().getInt("land-per-member")*kingdom.getMembersList().size() + kingdom.getExtraLandClaims()))){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Members_Needed", kp.getLang()).replaceAll("%amount%", Config.getConfig().getInt("land-per-member")*kingdom.getMembersList().size() + "").replaceAll("%members%", kingdom.getMembersList().size() + ""));
			return;
		}
		
		if(Config.getConfig().getInt("maximum-land-claims") > 0 && (kingdom.getLand() >= Config.getConfig().getInt("maximum-land-claims"))){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Land_Reached", kp.getLang()));
			return;
		}
		
		if(kingdom.getLand() <= 0){//check if first land
			kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_FirstTime1", kp.getLang()).replaceAll("%kingdom%", kingdom.getKingdomName()));
			kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_FirstTime2", kp.getLang()));
			
			if(kingdom.getHome_loc() == null){
				kingdom.setHome_loc(kp.getPlayer().getLocation());
				kp.sendMessage(Kingdoms.getLang().getString("Command_Sethome_Success", kp.getLang()).replaceAll("%coords%", new SimpleLocation(kingdom.getHome_loc()).toString()));
			}
		
		}else{
			if(Config.getConfig().getBoolean("land-must-be-connected")){
				boolean conn = false;
				Chunk main = kp.getPlayer().getLocation().getChunk();
				World w = kp.getPlayer().getWorld();
				for(int x = -1; x <= 1; x++){
					for(int z = -1; z <= 1; z++){
						if(x == 0 && z == 0) continue;
						Chunk c = w.getChunkAt(main.getX() + x, main.getZ() + z);
						Land adj = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(c));
						if(adj.getOwnerUUID() != null){
							if(adj.getOwnerUUID().equals(kingdom.getKingdomUuid())){
								conn = true;
								break;
							}
						}
					}
				}
				if(!conn){
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Must_Be_Connected", kp.getLang()));
					return;
				}
			}
			
			int cost = Config.getConfig().getInt("claim-cost");
			if(!kp.isAdminMode() && kingdom.getResourcepoints() < cost){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + cost));
				return;
			}
			if(!kp.isAdminMode()){
				
				kingdom.setResourcepoints(kingdom.getResourcepoints() - Config.getConfig().getInt("claim-cost"));
				kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Success", kp.getLang()).replaceAll("%cost%", "" + Config.getConfig().getInt("claim-cost")));
			}else{
				kp.sendMessage(Kingdoms.getLang().getString("Command_Claim_Op", kp.getLang()));
			}
		}

		GameManagement.getLandManager().claimLand(kp.getLoc(), kingdom);
		GameManagement.getVisualManager().visualizeLand(kp, land.getLoc());
	}
	
	private ArrayList<String> forbidden = new ArrayList<String>(){{
		add(Material.DROPPER.toString());
		add(Material.DISPENSER.toString());
		add(Material.HOPPER.toString());
		add(Material.TRAPPED_CHEST.toString());
		add(Material.CHEST.toString());
		add(Material.FURNACE.toString());
		add("DOOR");
		add(Material.LEVER.toString());
		add(Materials.OAK_BUTTON.parseMaterial().toString());
		add(Material.STONE_BUTTON.toString());
		add(Material.ANVIL.toString());
		add(Materials.CRAFTING_TABLE.parseMaterial().toString());
		add(Materials.ENCHANTING_TABLE.parseMaterial().toString());
		add(Materials.FURNACE.parseMaterial().toString());
		add("SHULKER_BOX");
		add(Material.DROPPER.toString());
	}};
	
	private boolean isInForbiddenList(Material mat){
		if(forbidden.contains(mat.toString())) return true;
		for(String s:forbidden){
			if(mat.toString().endsWith(s)){
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteractOnOtherKingdom(PlayerInteractEvent e){
		if(e.isCancelled()) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if(e.getClickedBlock() == null) return;

		if(Config.getConfig().getBoolean("can-open-storage-blocks-in-other-kingdom-land")) return;
		
		if(e.getPlayer().isSneaking()){
			if(!isInForbiddenList(e.getClickedBlock().getType())){
				if(e.getPlayer().getItemInHand() == null) return;
				if(e.getPlayer().getItemInHand().getType() != Material.ARMOR_STAND
						&& e.getPlayer().getItemInHand().getType() != Material.ITEM_FRAME){
					return;
				}
			}
		}
		
		if(e.getPlayer().getItemInHand() != null){
			if(!isInForbiddenList(e.getClickedBlock().getType())){
				for(String mat:Config.getConfig().getStringList("allowedToUseInOtherKingdomLand")){
					if(mat.toLowerCase().equals(e.getPlayer().getItemInHand().getType().toString().toLowerCase())){
						return;
					}
				}
			}
		}

		Location bukkitLoc = e.getClickedBlock().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

			e.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();
			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

				e.setCancelled(true);
				return;
			}
			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}
		}
	}

	@EventHandler
	public void onInteractAtEntity(PlayerInteractAtEntityEvent e){
		SimpleLocation loc = new SimpleLocation(e.getRightClicked().getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

			e.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();

			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

				e.setCancelled(true);
				return;
			}

			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}

		}
	}

	@EventHandler
	public void onSpecialLandExplode(EntityExplodeEvent e){
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block == null || block.getType() == Material.AIR) continue;

			SimpleLocation loc = new SimpleLocation(block.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();

			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;

		}


	}

	@EventHandler
	public void onBucketEmptyOnUnoccupiedLand(PlayerBucketEmptyEvent event) {
		if(!Config.getConfig().getStringList("enabled-worlds").contains(event.getBlockClicked().getWorld().getName())) return;
		if(!Config.getConfig().getStringList("worlds-with-no-building-in-unoccupied-land").contains(event.getBlockClicked().getWorld().getName())) return;Location bukkitLoc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
		if(land.getOwnerUUID() == null && !kp.isAdminMode()){
			event.setCancelled(true);
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Build_In_Unoccupied_Land", kp.getLang()));
		}
	}

	@EventHandler
	public void onBucketFillOnUnoccupiedLand(PlayerBucketFillEvent event) {
		if(!Config.getConfig().getStringList("enabled-worlds").contains(event.getBlockClicked().getWorld().getName())) return;
		if(!Config.getConfig().getStringList("worlds-with-no-building-in-unoccupied-land").contains(event.getBlockClicked().getWorld().getName())) return;
		Location bukkitLoc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
		if(land.getOwnerUUID() == null && !kp.isAdminMode()){
			event.setCancelled(true);
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Build_In_Unoccupied_Land", kp.getLang()));
		}
	}


	@EventHandler
	public void onBreakBlockUnoccupied(BlockBreakEvent e){
		if(e.isCancelled()) return;
		if(Config.getConfig().getStringList("enabled-worlds").contains(e.getBlock().getWorld().getName())) return;
		if(e.getBlock() == null) return;
		if(!Config.getConfig().getStringList("worlds-with-no-building-in-unoccupied-land").contains(e.getBlock().getWorld().getName())) return;
		if(Kingdoms.getManagers().getConquestManager() != null && e.getBlock().getWorld().equals(ConquestManager.world)) return;
		Location bukkitLoc = e.getBlock().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(land.getOwnerUUID() == null && !kp.isAdminMode()){
			e.setCancelled(true);
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Build_In_Unoccupied_Land", kp.getLang()));
		}
	}

	@EventHandler
	public void onBuildBlockUnoccupied(BlockPlaceEvent e){

		if(e.isCancelled()) return;
		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getBlock().getWorld().getName())) return;
		if(e.getBlock() == null) return;
		if(!Config.getConfig().getStringList("worlds-with-no-building-in-unoccupied-land").contains(e.getBlock().getWorld().getName())) return;
		if(Kingdoms.getManagers().getConquestManager() != null && e.getBlock().getWorld().equals(ConquestManager.world)) return;
		Location bukkitLoc = e.getBlock().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(land.getOwnerUUID() == null && !kp.isAdminMode()){
			e.setCancelled(true);
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Build_In_Unoccupied_Land", kp.getLang()));
		}
	}


	@EventHandler
	public void onBreakArmorStandOrFrame(EntityDamageByEntityEvent e){
		if(e.isCancelled()) return;

		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getEntity().getWorld().getName())) return;
		if(e.getEntity().getType() != EntityType.ARMOR_STAND && e.getEntity().getType() != EntityType.ITEM_FRAME) return;
		if(!(e.getDamager() instanceof Player)) return;
		Location bukkitLoc = e.getEntity().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player)e.getDamager());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

			e.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();

			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

				e.setCancelled(true);
				return;
			}
			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}
		}
	}

	@EventHandler
	public void onBreakBlockOnOtherKingdom(BlockBreakEvent e){
		if(e.isCancelled()) return;

		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getBlock().getWorld().getName())) return;
		if(e.getBlock() == null) return;

		Location bukkitLoc = e.getBlock().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

			e.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();

			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

				e.setCancelled(true);
				return;
			}
			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}
		}
	}

	@EventHandler
	public void onBucketEmptyOnOtherKingdom(PlayerBucketEmptyEvent event) {
		Location bukkitLoc = event.getBlockClicked().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = this.getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
			event.setCancelled(true);
		}else{
			if(!land.getOwnerUUID().equals(kp.getKingdom().getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBucketFillOnOtherKingdom(PlayerBucketFillEvent event) {
		Location bukkitLoc = event.getBlockClicked().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = this.getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
			event.setCancelled(true);
		}else{
			if(!land.getOwnerUUID().equals(kp.getKingdom().getKingdomUuid())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlaceOnOtherKingdom(BlockPlaceEvent e) {
		if(e.getBlock() == null) return;
		Location bukkitLoc = e.getBlock().getLocation();
		SimpleLocation loc = new SimpleLocation(bukkitLoc);
		SimpleChunkLocation chunk = loc.toSimpleChunk();

		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.isAdminMode()) return;
		if(kp.getKingdom() == null){//not in kingdom
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

			e.setCancelled(true);
		}else{//in kingdom
			Kingdom kingdom = kp.getKingdom();

			if(!kingdom.getKingdomUuid().equals(land.getOwnerUUID())){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Break_In_Other_Land", kp.getLang()));

				e.setCancelled(true);
				return;
			}
			if(land.getStructure() != null &&
					land.getStructure().getType() == StructureType.NEXUS &&
					!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getBuildInNexus())){
				e.setCancelled(true);
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low_NexusBuild", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getBuildInNexus().toString()));
				return;
			}
		}
	}

	@EventHandler
	public void onFlowIntoKingdomLand(BlockFromToEvent e){
		if(!Config.getConfig().getBoolean("disableFlowIntoLand")) return;

		SimpleLocation locFrom = new SimpleLocation(e.getBlock().getLocation());
		SimpleLocation locTo = new SimpleLocation(e.getToBlock().getLocation());

		Land landFrom = GameManagement.getLandManager().getOrLoadLand(locFrom.toSimpleChunk());
		Land landTo = GameManagement.getLandManager().getOrLoadLand(locTo.toSimpleChunk());

		if(landFrom.getOwnerUUID() == null){
			if(landTo.getOwnerUUID() != null){
				e.setCancelled(true);
			}
		}else if(landFrom.getOwnerUUID().equals(landTo.getOwnerUUID())){
		}else{
			e.setCancelled(true);
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
	public void onDisable() {
		//2016-08-11
		stopAutoSave();
		Kingdoms.logInfo("Saving lands to db...");
		try{
			int i = saveAll();
			Kingdoms.logInfo("[" + i + "] lands saved!");
		}catch(Exception e){
			Kingdoms.logInfo("SQL connection failed! Saving to file DB");
			db = createFileDB();
			try {
				int i = saveAll();
				Kingdoms.logInfo("[" + i + "] lands saved offline. Files will be saved to SQL server when connection is restored in future");
			} catch (InterruptedException e1) {
			}
			Config.getConfig().set("DO-NOT-TOUCH.grabLandFromFileDB",true);
		}
		landList.clear();
	}

}
