package com.songoda.kingdoms.constants.conquest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.ConquestManager;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.songoda.kingdoms.main.Kingdoms;

public class ActiveConquestBattle {
	
	
	public Chunk middle;

	public ArrayList<Chunk> subchunks = new ArrayList<Chunk>();
	public ArrayList<Chunk> usedChunks = new ArrayList<Chunk>();
	public Kingdom invadingKingdom;
	public ArrayList<Player> players = new ArrayList<Player>();
	//public HashMap<Location, Integer> turretLocs = new HashMap<Location, Integer>();
    public HashMap<Integer, ConquestTurret> turrets = new HashMap<Integer, ConquestTurret>();
	public ConquestLand land;
	public static HashMap<UUID,Integer> gametasks = new HashMap<UUID,Integer>();
	private UUID uuid;
	public ArrayList<KingdomPlayer> invaders = new ArrayList<KingdomPlayer>();
	public Runnable gametask;
	public ArrayList<Entity> moblist = new ArrayList<Entity>();
	public ArrayList<Location> modifiedBlocks = new ArrayList<Location>();
	
	public class GameTask implements Runnable{
		ActiveConquestBattle battle;
		int spawnerDelay = 10;
		int sectick = 0;
		int siegeBreakerSpawn = 0;
		Location creeperSpawn;
		public GameTask(ActiveConquestBattle battle) {
			super();
			this.battle = battle;
			Chunk tchunk = battle.getMiddle().getWorld().getChunkAt(battle.getMiddle().getX(),battle.getMiddle().getZ()+1);
			creeperSpawn = tchunk.getBlock(8, 102, 2).getLocation();
		}
		@Override
		public void run() {
			int maxmobs = 50;
			if(turrets.size() > 0){
				for(int i:turrets.keySet()){
					ConquestTurret turret = turrets.get(i);
					if(turret.location.getBlock().getType() != Materials.SKELETON_SKULL.parseMaterial()){
						continue;
					}
					turret.tick();
				}
			}
			if(sectick < 20){
				sectick++;
			}
			if(sectick >= 20){
				sectick = 0;
				for(Iterator<Entity> iter = moblist.iterator(); iter.hasNext();){
					Entity e = iter.next();
					if(!e.isValid()||e.isDead())iter.remove();
				}
				
				if(land.getSpawnerlevel() > 0 && moblist.size() < maxmobs){
					spawnerDelay -= land.getSpawnerlevel();
					
					if(spawnerDelay <= 0){
						Kingdoms.getManagers();
						Kingdoms.getManagers();
						Entity z = GameManagement.getGuardsManager().spawnNexusGuard(middle.getBlock(8, 103, 8).getLocation(), GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner()), null);
						if(land.getSupplylevel() == 0){
							((Zombie) z).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 2));
							((Zombie) z).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 999999, 1));
						}
						if(siegeBreakerSpawn < 5){
							siegeBreakerSpawn++;
						}else{
							
							siegeBreakerSpawn = 0;
							Kingdoms.getManagers();
							Kingdoms.getManagers();
							Entity c = GameManagement.getGuardsManager().spawnSiegeBreaker(creeperSpawn, GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner()), null);
							if(land.getSupplylevel() == 0){
								((Creeper) c).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 2));
								((Creeper) c).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 999999, 3));
							}
							moblist.add(c);
						}
						
						
						moblist.add(z);
						
					}
				}
			}
			
		}
	}
	public ActiveConquestBattle(Chunk middle, ConquestLand land){
		uuid = UUID.randomUUID();
		this.middle = middle;
		this.land = land;
		int radius = 5;
		for(int x = -1*radius; x <= radius; x++){
			for(int z = -1*radius; z <= radius; z++){
				subchunks.add(ConquestManager.world.getChunkAt(x, z));
			}
		}
		
		int midx = middle.getX();
		int midz = middle.getZ();
		gametask = new GameTask(this);
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Kingdoms.getInstance(), gametask, 0, 1L);
		Kingdoms.logDebug("Put in, " + id + " under " + uuid.toString());
		gametasks.put(uuid, id);
	}

	
	
	private boolean isTerminated = false;
	public void concludeVictory(){
		if(isTerminated) return;
		isTerminated = true;
		land.setOwner(invadingKingdom.getKingdomUuid());
		invadingKingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Conquered_Land").replaceAll("%land%", capitalize(land.getDataID())), true);
		for(KingdomPlayer kp:invaders)kp.sendMessage(Kingdoms.getLang().getString("Conquests_Kingdom_Conquered_Land", kp.getLang()).replaceAll("%land%", capitalize(land.getDataID())));
		stopInvasion();
		ConquestLand land = ConquestMap.getLandAt(ConquestManager.maps.get(this.land.map), this.land.x,this.land.y);
	}
	
	public void concludeDefeat(){
		if(isTerminated) return;
		isTerminated = true;
		//land.setOwner(kingdom.getKingdomName());
		invadingKingdom.sendAnnouncement(null, Kingdoms.getLang().getString("Conquests_Kingdom_Defeat").replaceAll("%land%", capitalize(land.getDataID())), true);

		for(KingdomPlayer kp:invaders)kp.sendMessage(Kingdoms.getLang().getString("Conquests_Kingdom_Defeat", kp.getLang()).replaceAll("%land%", capitalize(land.getDataID())));
		stopInvasion();
		ConquestLand land = ConquestMap.getLandAt(ConquestManager.maps.get(this.land.map), this.land.x,this.land.y);
	}
	
	public ArrayList<Player> getInvaders(){
		ArrayList<Player> list = new ArrayList<Player>();
		for(KingdomPlayer p:invaders){
			list.add(p.getPlayer());
		}
		return list;
	}

	
	public void stopInvasion(){
		ArrayList<KingdomPlayer> invaders = new ArrayList<KingdomPlayer>();
		for(KingdomPlayer kp:this.invaders){
			invaders.add(kp);
		}
		for(KingdomPlayer p:invaders){
			ConquestManager.leaveOffensive(p);
		}
		ConquestManager.kingdomsMissions.remove(invadingKingdom);
		invaders.clear();
		for(Entity e:moblist) e.remove();
		for(Chunk c:usedChunks){
			for(int x = 0; x < 15; x++){
				for(int z = 0; z < 15; z++){
					for(int y = 100; y < 130; y++){
						c.getBlock(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		Kingdoms.logDebug("Took out, " + gametasks.get(uuid) + " from " + uuid.toString());
		Bukkit.getScheduler().cancelTask(gametasks.remove(uuid));
		ConquestLand land = ConquestMap.getLandAt(ConquestManager.maps.get(this.land.map), this.land.x,this.land.y);
		land.isUnderSiege = false;
	}
	
	public void stopInvasionServerStop(){
		ArrayList<KingdomPlayer> invaders = new ArrayList<KingdomPlayer>();
		for(KingdomPlayer kp:this.invaders){
			invaders.add(kp);
		}
		for(KingdomPlayer p:invaders){
			ConquestManager.leaveOffensive(p);
		}
		invaders.clear();
		for(Entity e:moblist) e.remove();
		for(Chunk c:usedChunks){
			for(int x = 0; x < 15; x++){
				for(int z = 0; z < 15; z++){
					for(int y = 100; y < 115; y++){
						c.getBlock(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		Bukkit.getScheduler().cancelTask(gametasks.remove(uuid));
		ConquestLand land = ConquestMap.getLandAt(ConquestManager.maps.get(this.land.map), this.land.x,this.land.y);
		land.isUnderSiege = false;
	}
	
	public Chunk getMiddle(){
		return middle;
	}

	public ArrayList<Chunk> getSubchunks() {
		return subchunks;
	}

	public ArrayList<Chunk> getUsedChunks() {
		return usedChunks;
	}
	
	private String capitalize(String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	

	
}
