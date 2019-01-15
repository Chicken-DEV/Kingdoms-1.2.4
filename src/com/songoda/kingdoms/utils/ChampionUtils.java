package com.songoda.kingdoms.utils;

import org.bukkit.entity.Monster;

public class ChampionUtils {
	
	public static void setKnockbackAttribute(Monster champion, double amt){
		champion.setVelocity(champion.getLocation().getDirection().multiply(-amt));
	}

}
