package com.songoda.kingdoms.constants.kingdom;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class TurretUpgradeInfo{
	boolean simplifiedModel = false;
	boolean flurry = false;
	boolean concentratedBlast = false;
	boolean virulentPlague = false;
	boolean improvedHeal = false;
	boolean voodoo = false;
	boolean finalService = false;
	boolean hellstorm = false;
	boolean unrelentingGaze = false;
	
	public TurretUpgradeInfo() {}

	public boolean isSimplifiedModel() {
		return simplifiedModel;
	}

	public boolean isFlurry() {
		return flurry;
	}

	public boolean isConcentratedBlast() {
		return concentratedBlast;
	}

	public boolean isVirulentPlague() {
		return virulentPlague;
	}

	public boolean isImprovedHeal() {
		return improvedHeal;
	}

	public boolean isVoodoo() {
		return voodoo;
	}

	public boolean isFinalService() {
		return finalService;
	}

	public boolean isHellstorm() {
		return hellstorm;
	}

	public boolean isUnrelentingGaze() {
		return unrelentingGaze;
	}

	public void setSimplifiedModel(boolean simplifiedModel) {
		this.simplifiedModel = simplifiedModel;
	}

	public void setFlurry(boolean flurry) {
		this.flurry = flurry;
	}

	public void setConcentratedBlast(boolean concentratedBlast) {
		this.concentratedBlast = concentratedBlast;
	}

	public void setVirulentPlague(boolean virulentPlague) {
		this.virulentPlague = virulentPlague;
	}

	public void setImprovedHeal(boolean improvedHeal) {
		this.improvedHeal = improvedHeal;
	}

	public void setVoodoo(boolean voodoo) {
		this.voodoo = voodoo;
	}

	public void setFinalService(boolean finalService) {
		this.finalService = finalService;
	}

	public void setHellstorm(boolean hellstorm) {
		this.hellstorm = hellstorm;
	}

	public void setUnrelentingGaze(boolean unrelentingGaze) {
		this.unrelentingGaze = unrelentingGaze;
	}
	
	

	
	

}
