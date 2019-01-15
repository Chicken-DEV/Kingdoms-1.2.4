package com.songoda.kingdoms.constants.player;

import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import org.bukkit.entity.Entity;

public interface Challenger {
	public Entity getChampionPlayerFightingWith();
	public void setChampionPlayerFightingWith(Entity champion);
	public SimpleChunkLocation getFightZone();
	public void setInvadingChunk(SimpleChunkLocation loc);
}
