package com.songoda.kingdoms.manager.game;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.Turret;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.events.PlayerChangeChunkEvent;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.manager.external.ExternalManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerMovementManager extends Manager implements Listener {

  PlayerMovementManager(Plugin plugin){
	super(plugin);
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent e){
	new BukkitRunnable() {
	  @Override
	  public void run(){
		if(e.getPlayer() == null || !e.getPlayer().isOnline() || !e.getPlayer().isValid()) return;
		PlayerChangeChunkEvent pcce = new PlayerChangeChunkEvent(e.getPlayer(), null,
			e.getPlayer().getLocation().getChunk());

		plugin.getServer().getPluginManager().callEvent(pcce);
	  }
	}.runTaskLater(plugin, 5L);
  }

  @EventHandler
  public void onTp(final PlayerTeleportEvent e){
	if(e.getFrom().getChunk().equals(e.getTo().getChunk())){
	  return;
	}
	if(e.getPlayer() == null || !e.getPlayer().isOnline() || !e.getPlayer().isValid()) return;
	PlayerChangeChunkEvent pcce = new PlayerChangeChunkEvent(e.getPlayer(), null,
		e.getTo().getChunk());

	plugin.getServer().getPluginManager().callEvent(pcce);
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event){
	if(!Config.getConfig().getStringList("enabled-worlds").contains(event.getPlayer().getWorld().getName())){
	  return;
	}
	if(!event.getPlayer().isOnline()) return;

	Player p = event.getPlayer();
	if(ExternalManager.isCitizen(p.getPlayer())) return;

	Chunk chunk = event.getTo().getChunk();
	int centerX = chunk.getX();
	int centerZ = chunk.getZ();
	for(int x = -1; x <= 1; x++){
	  for(int z = -1; z <= 1; z++){
		Chunk iter = p.getWorld().getChunkAt(centerX + x, centerZ + z);
		Land land = LandManager.landList.get(new SimpleChunkLocation(iter));
		if(land != null){
		  List<Turret> turrets = land.getTurrets();
		  for(Turret turret : turrets){
			//attempt to get the closest player to the turret
			Location turretLocation = turret.getLoc().getLocation();
			Player closest = null;
			double closestDistance = 0.0;
			for(Player player : getNearbyPlayers(turretLocation, turret.getType().getRange())){
			  double distance = player.getLocation().distance(turretLocation);
			  if(distance > closestDistance){
				closest = player;
				closestDistance = distance;
			  }
			}
			//try to fire each turret. No need for a PRE check for cooldown as fire checks that by itself
			if(closest != null){
			  turret.fire(closest);
			}
		  }
		}
	  }
	}
	if(event.getTo().getChunk() != event.getFrom().getChunk()){

		    /*
		        Check if player is in a fightzone
		     */
	  if(GameManagement.getPlayerManager().getSession(event.getPlayer()).getFightZone() != null
		  && Config.getConfig().getBoolean("can-not-leave-chunk-if-invading")){
		event.getPlayer().teleport(event.getPlayer().getLocation().add(event.getFrom().toVector().subtract(event.getTo().toVector()).normalize().multiply(2)));
		event.getPlayer().setFallDistance(0);
		event.setCancelled(true);
		event.getPlayer().sendMessage(Kingdoms.getLang().getString("Invade_LeavingChunk_Denied"));
		return;
	  }

	  PlayerChangeChunkEvent pcce = new PlayerChangeChunkEvent(p, event.getFrom().getChunk(),
		  event.getTo().getChunk());
	  Bukkit.getServer().getPluginManager().callEvent(pcce);
	}

  }

  private static final int SEC = 20;

  @EventHandler
  public void onChunkChange(final PlayerChangeChunkEvent e){
	if(ExternalManager.isCitizen(e.getPlayer())) return;
	if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
	  return;
	}
	final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
	if(kp == null) return;
	if(kp.getPlayer() == null) return;
	SimpleChunkLocation locTo = new SimpleChunkLocation(e.getToChunk());

	final Land landTo = GameManagement.getLandManager().getOrLoadLand(locTo);
//		kp.setLoc(locTo);

	if(ExternalManager.isInRegion(kp.getPlayer().getLocation())){
	  return;
	}

	if(e.getFromChunk() != null){
	  SimpleChunkLocation locFrom = new SimpleChunkLocation(e.getFromChunk());
	  Land landFrom = GameManagement.getLandManager().getOrLoadLand(locFrom);

	  UUID fromOwner = landFrom.getOwnerUUID();
	  UUID toOwner = landTo.getOwnerUUID();
	  if(fromOwner == null && toOwner == null){
		return;
	  }
	  else if(fromOwner != null && toOwner != null){
		if(fromOwner.equals(toOwner)) return;
	  }
	}

	new Thread(new Runnable() {
	  @Override
	  public void run(){
		if(landTo.getOwnerUUID() == null){
		  ExternalManager.sendActionBar(e.getPlayer(), ChatColor.DARK_GREEN + "" + ChatColor.BOLD + Kingdoms.getLang().getString("Map_Unoccupied", GameManagement.getPlayerManager().getSession(e.getPlayer()).getLang()));
		  if(Config.getConfig().getBoolean("showLandEnterMessage"))
			kp.sendMessage(ChatColor.DARK_GREEN + Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang()));

		  return;
		}

		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(landTo.getOwnerUUID());

		ChatColor color;
		if(kp.getKingdom() == null){
		  color = ChatColor.WHITE;
		}
		else if(kp.getKingdom().equals(kingdom)){
		  color = ChatColor.GREEN;
		}
		else if(kp.getKingdom().isAllianceWith(kingdom)){
		  color = ChatColor.YELLOW;
		}
		else if(kp.getKingdom().isEnemyWith(kingdom)){
		  color = ChatColor.RED;
		}
		else{
		  color = ChatColor.WHITE;
		}

		String lore = "";
		String titleLore = "";
		if(kingdom.isNeutral())
		  lore += ChatColor.GREEN + " (" + Kingdoms.getLang().getString("Misc_Neutral", kp.getLang()) + ")";
		if(kingdom != null && kingdom.getKingdomLore() != null){
		  lore += ChatColor.WHITE + " - " + color + kingdom.getKingdomLore();
		  titleLore = kingdom.getKingdomLore();
		}

		ExternalManager.sendTitleBar(e.getPlayer(), color + landTo.getOwner(), titleLore);


		ExternalManager.sendActionBar(e.getPlayer(), ChatColor.BOLD + "" + color + ChatColor.BOLD + landTo.getOwner());

		if(Config.getConfig().getBoolean("showLandEnterMessage"))
		  kp.sendMessage(color + landTo.getOwner() + lore);
	  }
	}).start();

  }

  private List<Player> getNearbyPlayers(Location loc, double distance){
	double distanceSquared = distance * distance;
	List<Player> list = new ArrayList<>();
	for(Player p : Bukkit.getOnlinePlayers()){
	  if(!p.getWorld().equals(loc.getWorld())){
		continue;
	  }
	  if(p.getLocation().distanceSquared(loc) < distanceSquared){
		list.add(p);
	  }
	}
	return list;
  }


  @Override
  public void onDisable(){

  }

}
