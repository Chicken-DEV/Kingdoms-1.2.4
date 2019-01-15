package com.songoda.kingdoms.manager.gui;


import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.plugin.Plugin;

public class GUIManagement extends Manager {
	private static NexusGUIManager nexusGUIManager;
	private static PermissionsGUIManager permissionsGUIManager;
	private static MisUpGUIManager misGUIManager;
	private static ChampionGUIManager champGUIManager;
	private static MapManager mapManager;
	private static StructureGUIManager structureGUIManager;
	private static TurretGUIManager turretGUIManager;
	private static PrivateChestGUIManager pchestGUImanager;
	private static ScrollerInventoryManager scrollerManager;
	private static MemberManagerGui memberManager;
	private static ConquestGUIManager conquestGUIManager;
	private static ConquestMapGUIManager conquestMapGUIManager;
	private static LogManagerGui logManager;
	private static TurretUpgradeGUIManager turretUpgradeGUIManager;
	private static RegulatorGUIManager regulatorGUIManager;
	private static OutpostGUIManager outpostGUIManager;
	private static ExtractorGUIManager extractorGUIManager;
	private static WarppadGUIManager warppadGUIManager;
	private static InteractiveGUIManager interactiveGUIManager;
	private static ArsenalGUIManager arsenalGUIManager;
	private static SiegeEngineGUIManager siegeEngineGUIManager;
	static List<InventoryAction> allowedActions = new ArrayList<InventoryAction>(){{
		add(InventoryAction.PICKUP_ALL);
		add(InventoryAction.PICKUP_ONE);
		add(InventoryAction.PICKUP_HALF);
	}};
	
	public GUIManagement(Plugin plugin){
		super(plugin);
		
		nexusGUIManager = new NexusGUIManager(plugin);
		permissionsGUIManager = new PermissionsGUIManager(plugin);
		misGUIManager = new MisUpGUIManager(plugin);
		champGUIManager = new ChampionGUIManager(plugin);
		mapManager = new MapManager(plugin);
		interactiveGUIManager = new InteractiveGUIManager(plugin);
		structureGUIManager = new StructureGUIManager(plugin);
		turretGUIManager = new TurretGUIManager(plugin);
		pchestGUImanager = new PrivateChestGUIManager(plugin);
		scrollerManager = new ScrollerInventoryManager(plugin);
		memberManager = new MemberManagerGui(plugin);
		conquestGUIManager = new ConquestGUIManager(plugin);
		conquestMapGUIManager = new ConquestMapGUIManager(plugin);
		logManager = new LogManagerGui(plugin);
		turretUpgradeGUIManager = new TurretUpgradeGUIManager(plugin);
		regulatorGUIManager = new RegulatorGUIManager(plugin);
		outpostGUIManager = new OutpostGUIManager(plugin);
		extractorGUIManager = new ExtractorGUIManager(plugin);
		warppadGUIManager = new WarppadGUIManager(plugin);
		arsenalGUIManager = new ArsenalGUIManager(plugin);
		siegeEngineGUIManager = new SiegeEngineGUIManager(plugin);
/*		
		
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(nexusGUIManager, plugin);
		pm.registerEvents(permissionsGUIManager, plugin);
		pm.registerEvents(misGUIManager, plugin);
		pm.registerEvents(champGUIManager, plugin);
		pm.registerEvents(mapManager, plugin);
		pm.registerEvents(structureGUIManager, plugin);
		pm.registerEvents(turretGUIManager, plugin);
		pm.registerEvents(powerupGUIManager, plugin);
		pm.registerEvents(scoreboardManager, plugin);*/
	}
	
	public static OutpostGUIManager getOutpostGUIManager(){
		return outpostGUIManager;
	}

	public static PrivateChestGUIManager getPchestGUImanager() {
		return pchestGUImanager;
	}


	public static NexusGUIManager getNexusGUIManager() {
		return nexusGUIManager;
	}

	public static RegulatorGUIManager getRegulatorGUIManager() {
		return regulatorGUIManager;
	}

	public static PermissionsGUIManager getPermissionsGUIManager() {
		return permissionsGUIManager;
	}

	public static MisUpGUIManager getMisGUIManager() {
		return misGUIManager;
	}

	public static ChampionGUIManager getChampGUIManager() {
		return champGUIManager;
	}

	public static ArsenalGUIManager getArsenalGUIManager(){
		return arsenalGUIManager;
	}
	
	public static MapManager getMapManager() {
		return mapManager;
	}

	public static StructureGUIManager getStructureGUIManager() {
		return structureGUIManager;
	}

	public static TurretGUIManager getTurretGUIManager() {
		return turretGUIManager;
	}


	public static MemberManagerGui getMemberManager() {
		return memberManager;
	}
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public static ConquestGUIManager getConquestGUIManager() {
		return conquestGUIManager;
	}

	public static ConquestMapGUIManager getConquestMapGUIManager() {
		return conquestMapGUIManager;
	}

	public static LogManagerGui getLogManager() {
		return logManager;
	}

	public static TurretUpgradeGUIManager getTurretUpgradeGUIManager() {
		return turretUpgradeGUIManager;
	}

	public static ExtractorGUIManager getExtractorGUIManager() {
		return extractorGUIManager;
	}

	public static WarppadGUIManager getWarppadGUIManager() {
		return warppadGUIManager;
	}
	public static SiegeEngineGUIManager getSiegeEngineGUIManager() {
		return siegeEngineGUIManager;
	}
	
}
