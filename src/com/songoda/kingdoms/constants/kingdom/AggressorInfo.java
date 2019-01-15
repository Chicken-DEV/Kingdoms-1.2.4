package com.songoda.kingdoms.constants.kingdom;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class AggressorInfo{
	int health;
	int damage;
	int speed;
	int damagecap;
	int antiknockback;
	public AggressorInfo(){
		
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getDamagecap() {
		return damagecap;
	}
	public void setDamagecap(int damagecap) {
		this.damagecap = damagecap;
	}
	public int getAntiknockback() {
		return antiknockback;
	}
	public void setAntiknockback(int antiknockback) {
		this.antiknockback = antiknockback;
	}
}
