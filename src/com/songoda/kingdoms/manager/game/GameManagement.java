package com.songoda.kingdoms.manager.game;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.WorldEdit;
import com.songoda.kingdoms.database.Database;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import com.songoda.kingdoms.constants.kingdom.AggressorInfo;
import com.songoda.kingdoms.constants.kingdom.ArmyInfo;
import com.songoda.kingdoms.constants.kingdom.ChampionInfo;
import com.songoda.kingdoms.constants.kingdom.KingdomChest;
import com.songoda.kingdoms.constants.kingdom.MisupgradeInfo;
import com.songoda.kingdoms.constants.kingdom.PermissionsInfo;
import com.songoda.kingdoms.constants.kingdom.PowerUp;
import com.songoda.kingdoms.constants.land.KChestSign;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.land.Structure;
import com.songoda.kingdoms.constants.land.Turret;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.external.DynmapManager;
import com.songoda.kingdoms.manager.external.ExternalManager;

/**
 * Collection of all managers related to Kingdoms game
 * @author wysohn
 *
 */
public class GameManagement extends Manager {
	private static PlayerMovementManager movementManager;
	private static PlayerManager playerManager;
	private static KingdomManager kingdomManager;
	private static LandManager landManager;
	private static ChampionManager championManager;
	private static FightManager fightManager;
	private static NexusManager nexusManager;
	private static KingdomChestManager chestManager;
	private static KingdomPowerUpManager powerupManager;
	private static KingdomChatManager chatManager;
	private static TopListManager topManager;
	private static StructureManager structureManager;
	private static TurretManager turretManager;
	private static LandVisualizeManager visualManager;
	private static DynmapManager dynmapManager;
	private static CancelManager tpManager;
	private static MisupManager misManager;
	private static ArmyManager armyManager;
	private static AggressorManager aggrManager;
	private static PrivateChestManager pchestManager;
	private static MasswarManager masswarManager;
	private static DataZipper dataZipper;
	private static GuardsManager guardsManager;
	private static ConquestManager conquestManager;
	private static ActiveConquestBattleManager conquestBattleManager;
	private static SoldierTurretManager soldierTurretManager;
	private static RegulatorManager regulatorManager;
	private static KingdomArsenalItemManager kingdomArsenalItemManager;
	private static SiegeEngineManager siegeEngineManager;
	private static ExternalManager externalManager;
	//private static GeneralAPIManager apiManager;
	
	public GameManagement(Plugin plugin){
		super(plugin);
		PluginManager pm = plugin.getServer().getPluginManager();
		initSerializers();
		
		//core components. must be initialized first
		movementManager = new PlayerMovementManager(plugin);
		kingdomManager = new KingdomManager(plugin);
		playerManager = new PlayerManager(plugin);
		try {
			landManager = new LandManager(plugin);
		} catch (IOException e1) {
			Kingdoms.logInfo("Fatal Error! could not create landList!");
			Kingdoms.logInfo("Disabling plugin...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			e1.printStackTrace();
		}
		structureManager = new StructureManager(plugin);
		championManager = new ChampionManager(plugin);
		fightManager = new FightManager(plugin);
		nexusManager = new NexusManager(plugin);
		chestManager = new KingdomChestManager(plugin);
		powerupManager = new KingdomPowerUpManager(plugin);
		chatManager = new KingdomChatManager(plugin);
		turretManager = new TurretManager(plugin);
		soldierTurretManager = new SoldierTurretManager(plugin);
		visualManager = new LandVisualizeManager(plugin);
		misManager = new MisupManager(plugin);
		armyManager = new ArmyManager(plugin);
		aggrManager = new AggressorManager(plugin);
		pchestManager = new PrivateChestManager(plugin);
		masswarManager = new MasswarManager(plugin);
		dataZipper = new DataZipper(plugin);
		topManager = new TopListManager(plugin);
		guardsManager = new GuardsManager(plugin);
		externalManager = new ExternalManager(plugin);
		kingdomArsenalItemManager = new KingdomArsenalItemManager(plugin);
		siegeEngineManager = new SiegeEngineManager(plugin);
		
		if(Config.getConfig().getBoolean("enable.structure.regulator"))regulatorManager = new RegulatorManager(plugin);

		
		tpManager = new CancelManager(plugin);
		/*pm.registerEvents(tpManager, plugin);*/
	}
	
	private static final List<Class<?>> serClasses = new ArrayList<Class<?>>(){{
		add(AggressorInfo.class);
		add(ArmyInfo.class);
		add(ChampionInfo.class);
		add(KChestSign.class);
		add(KingdomChest.class);
		add(MisupgradeInfo.class);
		add(PermissionsInfo.class);
		add(PowerUp.class);
		//add(Rank.class);
		add(SimpleChunkLocation.class);
		add(SimpleLocation.class);
		add(Structure.class);
		add(Turret.class);
	}};
	private void initSerializers(){
		for(Class<?> clazz : serClasses)
			initSerializer(clazz);
	}
	
	private static final String pkgName = "com.songoda.kingdoms.json.serialize";
	private void initSerializer(Class<?> clazz) {
		try {
			Class<?> ser = Class.forName(pkgName + "." + clazz.getSimpleName() + "Serializer");
			Constructor<?> con = ser.getConstructor();
			Database.registerSerializer(clazz, con.newInstance());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public static CancelManager getTpManager() {
		return tpManager;
	}

	public static DynmapManager getDynmapManager() {
		return dynmapManager;
	}

	public static PlayerMovementManager getMovementManager() {
		return movementManager;
	}
	
	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static KingdomManager getKingdomManager() {
		return kingdomManager;
	}

	public static LandManager getLandManager() {
		return landManager;
	}

	public static ChampionManager getChampionManager() {
		return championManager;
	}

	public static FightManager getFightManager() {
		return fightManager;
	}

	public static NexusManager getNexusManager() {
		return nexusManager;
	}

	public static KingdomPowerUpManager getPowerupManager() {
		return powerupManager;
	}

	public static KingdomChestManager getChestManager() {
		return chestManager;
	}

//	public static GeneralAPIManager getApiManager() {
//		return apiManager;
//	}

	public static KingdomChatManager getChatManager() {
		return chatManager;
	}

	public static TopListManager getTopManager() {
		return topManager;
	}
	
	

	public static MasswarManager getMasswarManager() {
		return masswarManager;
	}
	public static LandVisualizeManager getVisualManager() {
		return visualManager;
	}

	public static TurretManager getTurretManager() {
		return turretManager;
	}

	public static StructureManager getStructureManager() {
		return structureManager;
	}
	
	public static ExternalManager getApiManager(){
		return externalManager;
	}

	public static MisupManager getMisManager() {
		return misManager;
	}

	public static ArmyManager getArmyManager() {
		return armyManager;
	}

	public static AggressorManager getAggrManager() {
		return aggrManager;
	}

	public static PrivateChestManager getPchestManager() {
		return pchestManager;
	}

	public static DataZipper getDataZipper(){
		return dataZipper;
	}
	
	public static GuardsManager getGuardsManager(){
		return guardsManager;
	}
	
	public static RegulatorManager getRegulatorManager() {
		return regulatorManager;
	}
	
	public static ConquestManager getConquestManager() {
		return conquestManager;
	}
	public static SoldierTurretManager getSoldierTurretManager() {
		return soldierTurretManager;
	}
	public static KingdomArsenalItemManager getKingdomArsenalItemManager() {
		return kingdomArsenalItemManager;
	}
	public static void setConquestManager(ConquestManager conquestManager) {
		GameManagement.conquestManager = conquestManager;
	}
	public static SiegeEngineManager getSiegeEngineManager() {
		return siegeEngineManager;
	}
	public static void setSiegeEngineManager(SiegeEngineManager siegeEngineManager) {
		GameManagement.siegeEngineManager = siegeEngineManager;
	}
	@Override
	public void onDisable() {
	}
	
	
}
