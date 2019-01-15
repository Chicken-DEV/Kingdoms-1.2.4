package com.songoda.kingdoms.constants.land;

import java.util.ArrayList;
import java.util.HashMap;

import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class WarpPadManager{
	
	private static HashMap<OfflineKingdom, ArrayList<Land>> structurePads = new HashMap<OfflineKingdom, ArrayList<Land>>();

	public static void load(){
		Kingdoms.getManagers();
		GameManagement.getLandManager();
		for(SimpleChunkLocation loc:Kingdoms.getManagers().getLandManager().getAllLandLoc()){
			Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(loc);
			checkLoad(land);
			
		}
//		Kingdoms.getManagers();
//		GameManagement.getKingdomManager();
//		for(OfflineKingdom kingdom:KingdomManager.kingdomNameList.values()){
//			Kingdoms.getManagers();
//			Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName());
//			if(k.getNexus_loc().getChunk() == null) continue;
//			if(k.getNexus_loc() != null){
//				Kingdoms.getManagers();
//				addLand(kingdom, GameManagement.getLandManager().getOrLoadLand(new SimpleChunkLocation(k.getNexus_loc().getChunk())));
//			}
//		}
	}
	
	public static void checkLoad(Land land){
		Kingdoms.getManagers();
		GameManagement.getLandManager();
			if(land.getStructure() != null){
				if(land.getStructure().getType() == StructureType.OUTPOST||
						land.getStructure().getType() == StructureType.NEXUS||
						land.getStructure().getType() == StructureType.WARPPAD){
					if(land.getOwnerUUID() != null){
						Kingdoms.getManagers();
						addLand(GameManagement.getKingdomManager().getOfflineKingdom(land.getOwnerUUID()), land);
					}
				}
			}
	}
	
	public static void addLand(OfflineKingdom k, Land land){
		ArrayList<Land> lands = structurePads.get(k);
		if(lands == null){
			lands = new ArrayList<Land>();
		}
		
		if(!lands.contains(land))lands.add(land);
		structurePads.put(k, lands);
	}
	
	
	public static void removeLand(OfflineKingdom k, Land land){
		if(!structurePads.containsKey(k)) return;
		if(structurePads.get(k).contains(land)){
			structurePads.get(k).remove(land);
		}
	}
	
	public static ArrayList<Land> getOutposts(OfflineKingdom k){
		return structurePads.get(k);
	}
	
	
	
	
	

}
