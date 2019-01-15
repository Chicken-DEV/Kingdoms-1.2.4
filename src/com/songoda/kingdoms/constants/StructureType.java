package com.songoda.kingdoms.constants;

import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.StructureManager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum StructureType {
	OUTPOST(Kingdoms.getLang().getString("Structures_Outpost"),
			Kingdoms.getLang().getString("Structures_Outpost_Desc"),
			StructureManager.getOutpostDisk(),
			Material.HAY_BLOCK),
	POWERCELL(Kingdoms.getLang().getString("Structures_Powercell"),
			Kingdoms.getLang().getString("Structures_Powercell_Desc"),
			StructureManager.getPowerCellDisk(),
			Materials.REDSTONE_TORCH.parseMaterial()),
	NEXUS(Kingdoms.getLang().getString("Structures_Nexus"),
			Kingdoms.getLang().getString("Structures_Nexus_Desc"),
			StructureManager.getOutpostDisk(),
			Material.BEACON),
	EXTRACTOR(Kingdoms.getLang().getString("Structures_Extractor"),
			Kingdoms.getLang().getString("Structures_Extractor_Desc"),
			StructureManager.getExtractorDisk(),
			Material.EMERALD_BLOCK),
	WARPPAD(Kingdoms.getLang().getString("Structures_WarpPad"),
			Kingdoms.getLang().getString("Structures_WarpPad_Desc"),
			StructureManager.getWarpPadDisk(),
			Material.SEA_LANTERN),
//	LAB(Kingdoms.getLang().getString("Structures_Outpost,
//			Kingdoms.getLang().getString("Structures_Outpost_Desc,
//			StructureManager.getOutpostDisk()),
//	CRYSTAL(Kingdoms.getLang().getString("Structures_Outpost,
//			Kingdoms.getLang().getString("Structures_Outpost_Desc,
//			StructureManager.getOutpostDisk()),
	RADAR(Kingdoms.getLang().getString("Structures_Radar"),
			Kingdoms.getLang().getString("Structures_Radar_Desc"),
			StructureManager.getRadarDisk(),
			Materials.WHITE_STAINED_GLASS.parseMaterial()),
	ARSENAL(Kingdoms.getLang().getString("Structures_Arsenal"),
			Kingdoms.getLang().getString("Structures_Arsenal_Desc"),
			StructureManager.getArsenalDisk(),
			Material.FURNACE),
//	OBSCURER(Kingdoms.getLang().getString("Structures_Outpost,
//			Kingdoms.getLang().getString("Structures_Outpost_Desc,
//			StructureManager.getOutpostDisk()),
	REGULATOR(Kingdoms.getLang().getString("Structures_Regulator"),
			Kingdoms.getLang().getString("Structures_Regulator_Desc"),
			StructureManager.getRegulatorDisk(),
			Material.REDSTONE_BLOCK),
	SIEGEENGINE(Kingdoms.getLang().getString("Structures_SiegeEngine"),
			Kingdoms.getLang().getString("Structures_SiegeEngine_Desc"),
			StructureManager.getSiegeEngineDisk(),
			Material.DISPENSER),
	SHIELDBATTERY(Kingdoms.getLang().getString("Structures_ShieldBattery"),
			Kingdoms.getLang().getString("Structures_ShieldBattery_Desc"),
			StructureManager.getShieldBatteryDisk(),
			Material.PRISMARINE);
	
	private ItemStack disk;
	private String title;
	private String desc;
	private Material material;
	private StructureType(String title, String desc, ItemStack disk, Material material){
		this.disk = disk;
		this.title = title;
		this.desc = desc;
		this.material = material;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public ItemStack getDisk() {
		return disk;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getMetaData(){
		return this.toString().toLowerCase();
	}
	
	public boolean isEnabled(){
		switch(this){
			case EXTRACTOR:
				return Config.getConfig().getBoolean("enable.structure.extractor");
			case NEXUS:
				return true;
			case OUTPOST:
				return Config.getConfig().getBoolean("enable.structure.outpost");
			case POWERCELL:
				return Config.getConfig().getBoolean("enable.structure.powercell");
			case RADAR:
				return Config.getConfig().getBoolean("enable.structure.radar");
			case REGULATOR:
				return Config.getConfig().getBoolean("enable.structure.regulator");
			case SIEGEENGINE:
				return Config.getConfig().getBoolean("enable.structure.siegeengine");
			case ARSENAL:
				return Config.getConfig().getBoolean("enable.structure.arsenal");
			case WARPPAD:
				return Config.getConfig().getBoolean("enable.structure.warppad");
			case SHIELDBATTERY:
				return Config.getConfig().getBoolean("enable.structure.shieldbattery");
		default:
			break;
		}
		return false;
	}

	public int getCost(){
		switch(this){
			case EXTRACTOR:
				return Config.getConfig().getInt("cost.structures.extractor");
			case NEXUS:
				return 0;
			case OUTPOST:
				return Config.getConfig().getInt("cost.structures.outpost");
			case POWERCELL:
				return Config.getConfig().getInt("cost.structures.powercell");
			case RADAR:
				return Config.getConfig().getInt("cost.structures.radar");
			case REGULATOR:
				return Config.getConfig().getInt("cost.structures.regulator");
			case SIEGEENGINE:
				return Config.getConfig().getInt("cost.structures.siegeengine");
			case ARSENAL:
				return Config.getConfig().getInt("cost.structures.arsenal");
			case WARPPAD:
				return Config.getConfig().getInt("cost.structures.warppad");
			case SHIELDBATTERY:
				return Config.getConfig().getInt("cost.structures.shieldbattery");
		}
		return 0;
	}
	
}
