package com.songoda.kingdoms.constants.land;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;

public class Land{
	private SimpleChunkLocation loc;
	private UUID owner = null;
	private Long claimTime;
	private Structure structure = null;
	private List<Turret> turrets = new ArrayList<Turret>();
	private List<KChestSign> signs = new ArrayList<KChestSign>();
	private String name;
	private Land(){
		//super(null,null);
		turrets = new ArrayList<Turret>();
		signs = new ArrayList<KChestSign>();
	}
	
	public Land(SimpleChunkLocation loc){
		//super(LandManager.LANDFOLDER, loc.toString());
		this.loc = loc;
		//save();
		if(this.name == null) name = loc.toString();
	}

	public SimpleChunkLocation getLoc() {
		return loc;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		if(name == null){
			 name = loc.toString();
		}
		return name;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
		//save();
	}
	
	public ArrayList<Land> getSurrounding(){
		ArrayList<Land> lands = new ArrayList<Land>();
		for(int xi = -1; xi <= 1; xi++){
			for(int zi = -1; zi <= 1; zi++){
				if(xi == 0 && zi == 0) continue;
				SimpleChunkLocation nloc = new SimpleChunkLocation(loc.getWorld(),loc.getX() + xi,loc.getZ() +zi);
				lands.add(Kingdoms.getInstance().getManagers().getLandManager().getOrLoadLand(nloc));
			}
		}
		
		return lands;
	}
	
	public Turret getTurret(SimpleLocation loc){
		//2016-08-11
		if(turrets == null)
			return null;
		
		for(Turret turret : turrets){
			if(turret.getLoc().equals(loc)) return turret;
		}
		
		return null;
	}

	public void addTurret(Turret turret) {
		//2016-08-11
		if(turrets == null)
			turrets = new ArrayList<Turret>();
		
		if(turrets.contains(turret)) return;
		
		turrets.add(turret);
		//save();
	}
	
	public boolean hasTurret(Turret turret){
		//2016-08-11
		if(turrets == null)
			turrets = new ArrayList<Turret>();
		
		return turrets.contains(turret);
	}
	
	public List<Turret> getTurrets(){
		return turrets;
	}
/**
 * 
 * @return Stringname of owning kingdom. Returns null if no owner
 * @deprecated use getOwnerUUID() instead
 */
	public String getOwner() {
		if (owner!=null) {
			return GameManagement.getKingdomManager().getOrLoadKingdom(owner).getKingdomName();
		}
		return null;
	}

	public void setOwner(String owner) {
		this.owner = GameManagement.getKingdomManager().getOrLoadKingdom(owner).getKingdomUuid();
		//save();
	}
	public UUID getOwnerUUID(){
		return owner;
	}
	public void setOwnerUUID(UUID uuid){
		this.owner = uuid;
	}

	public Long getClaimTime() {
		return claimTime;
	}

	public void setClaimTime(Long claimTime) {
		this.claimTime = claimTime;
		//if(owner != null || turrets.size() != 0) save();
	}
	
/*	public void autoSave(){
		if(owner == null && turrets.size() == 0) delete();
		else save();
	}*/

	public void addChestSign(KChestSign sign){
		//2016-08-11
		if(signs == null)
			signs = new ArrayList<KChestSign>();
		
		if(signs.contains(sign)) signs.remove(sign);
		
		signs.add(sign);
	}
	
	public KChestSign getChestSign(SimpleLocation loc) {
		if(loc == null) return null;
		
		//2016-08-11
		if(signs == null)
			return null;
		
		for(KChestSign sign : signs){
			if(sign.getLoc().equals(loc)) return sign;
		}
		return null;
	}
	
	public void removeChestSign(SimpleLocation loc){
		if(loc == null) return;
		
		//2016-08-11
		if(signs == null)
			return;
		
		for(Iterator<KChestSign> iter = signs.iterator();iter.hasNext();){
			KChestSign sign = iter.next();
			if(sign.getLoc().equals(loc)) {
				iter.remove();
				return;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loc == null) ? 0 : loc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Land other = (Land) obj;
		if (loc == null) {
			if (other.loc != null)
				return false;
		} else if (!loc.equals(other.loc))
			return false;
		return true;
	}
}
