package com.songoda.kingdoms.constants.land;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KChestSign {
	private SimpleLocation loc;
	private UUID owner;
	private List<UUID> owners;
	private KChestSign(){
		owners = new ArrayList<UUID>();
	}
	/**
	 * 
	 * @param loc location of wall sign
	 */
	public KChestSign(SimpleLocation loc, UUID owner){
		this.loc = loc;
		this.owner = owner;
		owners = new ArrayList<UUID>();
	}
	public KChestSign(SimpleLocation loc, UUID owner,  List<UUID> owners){
		this.loc = loc;
		this.owner = owner;
		this.owners = owners;
	}
	
	public UUID getOwner() {
		return owner;
	}
	public SimpleLocation getLoc() {
		return loc;
	}
	public List<UUID> getOwners() {
		return owners;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
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
		KChestSign other = (KChestSign) obj;
		if (loc == null) {
			if (other.loc != null)
				return false;
		} else if (!loc.equals(other.loc))
			return false;
		return true;
	}
	
}
