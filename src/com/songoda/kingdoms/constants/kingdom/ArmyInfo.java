package com.songoda.kingdoms.constants.kingdom;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ArmyInfo {
	int zombie;
	int human;
	int endermite;
	public ArmyInfo(){
		
	}
	public int getZombie() {
		return zombie;
	}
	public void setZombie(int zombie) {
		this.zombie = zombie;
	}
	public int getHuman() {
		return human;
	}
	public void setHuman(int human) {
		this.human = human;
	}
	public int getEndermite() {
		return endermite;
	}
	public void setEndermite(int endermite) {
		this.endermite = endermite;
	}
	
}
