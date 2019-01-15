package com.songoda.kingdoms.constants.player;

public interface Confirmable {
	public boolean resetAllConfirmation();
	public boolean isConfirmed(String key);
	public void setConfirmed(String key);
}
