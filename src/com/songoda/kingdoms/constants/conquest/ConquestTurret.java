package com.songoda.kingdoms.constants.conquest;

import java.util.ArrayList;

import com.songoda.kingdoms.main.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.songoda.kingdoms.main.Kingdoms;

public class ConquestTurret {
	
	int level;
	private int tickPast = 0;
	final Location location;
	ActiveConquestBattle battle;
	
	public ConquestTurret(ActiveConquestBattle battle, Location loc, int level){
		this.battle = battle;
		this.location = loc;
		this.level = level;
		if(battle.land.getSupplylevel() < Config.getConfig().getInt("conquest.upkeep.max-supply-for-one-land")/2){
			this.level = level/2;
		}
	}
	
	public void tick(){
		if(level == 0) return;
		if(battle.land.getSupplylevel() == 0){
			return;
		}
		int delay = 20-level;
		if(delay < 5){
			delay = 5;
		}
		boolean crit = level > 10;
		boolean fire = level > 5;
		if(tickPast < delay){
		   		tickPast++;
			return;
		}
		ArrayList<Player> entities = battle.getInvaders();
		Bukkit.getScheduler().runTaskAsynchronously(Kingdoms.getInstance(), new BukkitRunnable() {
	       @Override
	       public void run()
	       {
    	   boolean hasShot = false;
	    	   for(Player p: entities){
		   			if(p.getLocation().distance(location) <= 20){
		   				Bukkit.getScheduler().runTask(Kingdoms.getInstance(), new BukkitRunnable() {
		   					@Override
		   					public void run()
		   					{
		   					    shootArrow(p.getLocation().add(0, 1, 0), location.clone().add(0.5,0.5,0.5), crit, fire);
				   				tickPast = 0;
		   					    return;
		   					}
		   				});
		   			}
	   			}
	       }
		});
		
	}
	
	public static void shootArrow(Location target, Location origin, boolean crit, boolean fire){	
	    Vector to = target.clone().add(0.0D, 0.75D, 0.0D).toVector();
	    Location fromLoc = origin.clone().add(0.0D, 1.0D, 0.0D);
	    Vector from = fromLoc.toVector();
	    Vector direction = to.subtract(from);
	    direction.normalize();
	   
	    Arrow arrow = origin.getWorld().spawnArrow(fromLoc, direction, 1.5F, 10);//speed,spread
	    arrow.setMetadata("CONQUESTARROW", new FixedMetadataValue(Kingdoms.getInstance(), "yes!"));
	    if(crit) arrow.setCritical(crit);
	    if(fire) arrow.setFireTicks(Integer.MAX_VALUE);
	    //TurretUtil.shotArrows.add(new ShotArrow(arrow));
	}

}
