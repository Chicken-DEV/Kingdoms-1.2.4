package com.songoda.kingdoms.constants.conquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.ConquestManager;
import com.songoda.kingdoms.manager.game.GameManagement;

public class ConquestLand {
	
	public String map;
	
	public int x;
	public int y;
	private HashMap<Integer, Integer> turrets = new HashMap<Integer, Integer>(){{
		
		put(1, 0);
		put(2, 0);
		put(3, 0);
		put(4, 0);
		
	}};
	private int walllevel = 0;
    private int spawnerlevel = 0;
	private UUID owner = null;
	private int supplylevel = Config.getConfig().getInt("conquest.upkeep.max-supply-for-one-land");
	public boolean isUnderSiege = false;
	
	public String getDataID(){
		return map + "," + x + "," + y;
	}
	public ConquestLand(String id){
		String[] split = id.replaceAll("_tmp", "").split(",");
		this.map = split[0];
		
		this.x = Integer.parseInt(split[1]);
		this.y = Integer.parseInt(split[2]);
	}
	
	public ConquestLand(ConquestMap conquest, int x, int y){
		this.map = conquest.name;
		this.x = x;
		this.y = y;
	}
	
	public int getUpKeepAmount(){
		int upkeep = 0;
		
		for(int level:turrets.values()){
			if(level > 0)upkeep += Config.getConfig().getInt("conquest.upkeep.per-turret");
		}
		if(walllevel > 0) upkeep += Config.getConfig().getInt("conquest.upkeep.wall");
		if(spawnerlevel > 0) upkeep += Config.getConfig().getInt("conquest.upkeep.spawner");
		
		return upkeep;
	}
	
	public static ConquestLand getLandAt(ConquestMap map, int x, int y){
		
		for(ConquestLand land:map.lands){
			if(land.x == x && land.y == y){
				return land;
			}
		}
		return null;
	}
	
	public Integer getTurretLevelAtSlot(int slot){
		if(turrets.containsKey(slot)){
			return turrets.get(slot);
		}else return null;
	}
	public void setTurretLevelAtSlot(int slot, int level){
		turrets.put(slot, level);
	}
	
	public void sellTurret(int slot){
		turrets.put(slot, 0);
	}

	public int getWalllevel() {
		return walllevel;
	}

	public int getSpawnerlevel() {
		return spawnerlevel;
	}

	public void setWalllevel(int walllevel) {
		this.walllevel = walllevel;
	}

	public void setSpawnerlevel(int spawnerlevel) {
		this.spawnerlevel = spawnerlevel;
	}

	public UUID getOwner() {
		return owner;
	}
	public String getOwnerName(){
		if(getOwner()!=null) {
			return GameManagement.getKingdomManager().getOrLoadKingdom(owner).getKingdomName();
		}
		return null;
	}
	public void setOwner(UUID owner) {
		this.owner = owner;
	}
	
	public boolean isCapital(){
		if(x == 0 && y == 0)return true;
		if(x == 5 && y == 0)return true;
		if(x == 0 && y == 5)return true;
		if(x == 5 && y == 5)return true;
		return false;
	}

	public int getSupplylevel() {
		return supplylevel;
	}

	public void setSupplylevel(int supplylevel) {
		this.supplylevel = supplylevel;
		if(this.supplylevel < 0) this.supplylevel = 0;
	}
	
	public boolean anyCloseBy(Kingdom k){
		for(int x = -1;x <= 1; x++){
				for(int z = -1;z <= 1; z++){
					if(x == 0 && z == 0)continue;
					int newx = this.x-x;
					int newz = this.y-z;
					if(newx > 5 || newx < 0|| newz > 5|| newz < 0) continue;
					
					ConquestLand land = getLandAt(ConquestManager.maps.get(map), newx, newz);
					if(land.getOwner() != null){
						if(land.getOwner().equals(k.getKingdomUuid()))return true;
					}
				}
			}
		
		return false;
	}
	
	public boolean canBeAttackedBy(OfflineKingdom kingdom){
		for(ConquestLand land:getSurrounding()){
			if(land.getOwner() == null)continue;
			if(!land.isEncircled() && land.getOwner().equals(kingdom.getKingdomUuid()))return true;
		}
		return false;
	}
	
	public boolean isEncircled(){
//		if(getOwner() == null) return false;
//		if(isCapital()) return false;
//		if(isNotSurroundedByOwnedLand()) return true;
//
//		for (ConquestLand land : getSurrounding()) {
//		    if (checkSurrounding(land)) return true;
//        }
//
//        return true;
		
		for(ConquestLand land:getSurrounding()){
			if(land.getOwner() == null) continue;
			if(!land.getOwner().equals(getOwner())) continue;
			if(land.isCapital() && land.getOwner() != null){
				if(land.getOwner().equals(this.getOwner()))return false;
			}

			for(ConquestLand subland:land.getSurrounding()){
				if(subland.getOwner() == null) continue;
				if(!subland.getOwner().equals(getOwner())) continue;
				if(subland.isCapital() && subland.getOwner() != null){
					if(subland.getOwner().equals(this.getOwner()))return false;
				}
				for(ConquestLand subsubland:subland.getSurrounding()){
					if(subsubland.getOwner() == null) continue;
					if(!subsubland.getOwner().equals(getOwner())) continue;
					if(subsubland.isCapital() && subsubland.getOwner() != null){
						if(subsubland.getOwner().equals(this.getOwner()))return false;
					}

					for(ConquestLand subsubsubland:subsubland.getSurrounding()){
						if(subsubsubland.getOwner() == null) continue;
						if(!subsubsubland.getOwner().equals(getOwner())) continue;
						if(subsubsubland.isCapital() && subsubsubland.getOwner() != null){
							if(subsubsubland.getOwner().equals(this.getOwner()))return false;
						}

						for(ConquestLand subsubsubsubland:subsubsubland.getSurrounding()){
							if(subsubsubsubland.getOwner() == null) continue;
							if(!subsubsubsubland.getOwner().equals(getOwner())) continue;
							if(subsubsubsubland.isCapital() && subsubsubsubland.getOwner() != null){
								if(subsubsubsubland.getOwner().equals(this.getOwner()))return false;
							}

							for(ConquestLand subsubsubsubsubland:subsubsubsubland.getSurrounding()){
								if(subsubsubsubsubland.getOwner() == null) continue;
								if(!subsubsubsubsubland.getOwner().equals(getOwner())) continue;
								if(subsubsubsubsubland.isCapital() && subsubsubsubsubland.getOwner() != null){
									if(subsubsubsubsubland.getOwner().equals(this.getOwner()))return false;
								}
							}
						}
					}
				}
			}

		}


		return true;
	}
	
	public ArrayList<ConquestLand> getSurrounding(){
		ArrayList<ConquestLand> list = new ArrayList<ConquestLand>();
		
		for(int x = -1;x <= 1; x++){
			for(int z = -1;z <= 1; z++){
				int newx = this.x-x;
				int newz = this.y-z;
				if(newx > 5 || newx < 0|| newz > 5|| newz < 0) continue;
				if(newx == this.x && newz == this.y) continue;
				//Kingdoms.logDebug(newx + "," + newz);
				ConquestLand land = getLandAt(ConquestManager.maps.get(map), newx, newz);
				list.add(land);
			}
		}
		return list;
	}
	
	public boolean isNotSurroundedByOwnedLand(){
		if(this.isCapital()) return false;
		if(owner == null) return false;
		//Kingdoms.logDebug("======" + getDataID() + "=======");
		if(owner != null){
			for(int x = -1;x <= 1; x++){
				for(int z = -1;z <= 1; z++){
					int newx = this.x-x;
					int newz = this.y-z;
					if(newx > 5 || newx < 0|| newz > 5|| newz < 0) continue;
					if(newx == this.x && newz == this.y) continue;
					//Kingdoms.logDebug(newx + "," + newz);
					ConquestLand land = getLandAt(ConquestManager.maps.get(map), newx, newz);
					if(land.getOwner() != null){
						if(land.getOwner().equals(this.getOwner()))return false;
					}
				}
			}
		}
		return true;
	}

}
