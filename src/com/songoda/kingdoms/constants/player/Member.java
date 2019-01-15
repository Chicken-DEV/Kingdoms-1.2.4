package com.songoda.kingdoms.constants.player;

import com.songoda.kingdoms.constants.kingdom.Kingdom;

public interface Member {
	public Kingdom getInvited();
	public void setInvited(Kingdom kingdom);
}
