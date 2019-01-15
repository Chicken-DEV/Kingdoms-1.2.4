package com.songoda.kingdoms.manager.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class CancelManager extends Manager implements Listener{
	public List<UUID> iswarping = new ArrayList<UUID>();
	
	protected CancelManager(Plugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWarpCancel(PlayerMoveEvent event) {
		if (iswarping.contains(event.getPlayer().getUniqueId())) {
			if ((int) event.getFrom().getX() != (int) event.getTo().getX()
					|| (int) event.getFrom().getY() != (int) event.getTo().getY()
					|| (int) event.getFrom().getZ() != (int) event.getTo().getZ()) {
				iswarping.remove(event.getPlayer().getUniqueId());
				event.getPlayer().sendMessage(Kingdoms.getLang().getString("Command_Home_Failed", Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()).getLang()));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCofirmCancel(PlayerMoveEvent event) {

		if ((int) event.getFrom().getX() != (int) event.getTo().getX()
				|| (int) event.getFrom().getY() != (int) event.getTo().getY()
				|| (int) event.getFrom().getZ() != (int) event.getTo().getZ()) {
			KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
			if(kp == null){
				//kp = GameManagement.getPlayerManager().preloadKingdomPlayer(event.getPlayer());
				return;
			}
			
			if(kp.resetAllConfirmation())
				event.getPlayer().sendMessage(Kingdoms.getLang().getString("Misc_Confirmation_Cancel", Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()).getLang()));
		}
	}
	
	@Override
	public void onDisable() {
		iswarping.clear();
	}

}
