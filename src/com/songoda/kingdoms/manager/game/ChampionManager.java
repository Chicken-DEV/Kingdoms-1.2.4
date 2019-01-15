package com.songoda.kingdoms.manager.game;

import com.songoda.kingdoms.api.events.*;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.constants.kingdom.ChampionInfo;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.events.KingdomPlayerLostEvent;
import com.songoda.kingdoms.events.KingdomPlayerWonEvent;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.manager.external.ExternalManager;
import com.songoda.kingdoms.utils.ChampionUtils;
import com.songoda.kingdoms.utils.Materials;
import com.songoda.kingdoms.utils.ProbabilityTool;
import com.songoda.kingdoms.utils.TurretUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChampionManager extends Manager implements Listener {
  public static Map<Integer, Kingdom> entityOwners = new ConcurrentHashMap<Integer, Kingdom>();
  public static Map<Integer, KingdomPlayer> targets = new ConcurrentHashMap<Integer, KingdomPlayer>();
  public static HashMap<Integer, Integer> determination = new HashMap<Integer, Integer>();
  public static Map<SimpleChunkLocation, Integer> invadingChunks = new ConcurrentHashMap<SimpleChunkLocation, Integer>();

  protected ChampionManager(Plugin plugin){
	super(plugin);

  }

  /**
   * get Kingdom who owns the champion
   *
   * @param entityID entityID of champion (Entity.getEntityID();)
   * @return owner Kingdom; null if nobody owns this champion (which is not good)
   */
  public Kingdom getChampionOwner(int entityID){
	return entityOwners.get(entityID);
  }

  //public static final String CHAMPNAME = "Champion";
  public static final HashMap<UUID, Location> defenders = new HashMap<UUID, Location>();

  /**
   * start the fight between champion and challenger
   *
   * @param loc        location of champion to be spawned
   * @param challenger KingdomPlayer who challenges the champion
   * @return Entity instance of champion.
   */
  public Entity startFight(SimpleLocation loc, final KingdomPlayer challenger){
	SimpleChunkLocation chunk = loc.toSimpleChunk();
	Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	if(land.getOwnerUUID() == null){
	  Kingdoms.logInfo("Could not spawn champion at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("No kingdom owns this land.");
	  return null;
	}

	final Kingdom defending = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());


	if(defending == null){
	  Kingdoms.logInfo("Could not spawn champion at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("The owner of this land is [" + land.getOwner() + "] but there is no data.");
	  Kingdoms.logInfo("This seems to be a plugin malfunction and needed to be reported.");
	  return null;
	}

	for(KingdomPlayer kp : defending.getOnlineMembers()){
	  defenders.put(kp.getUuid(), loc.toLocation());
	}

	challenger.getPlayer().setGameMode(GameMode.SURVIVAL);

	Location bukkitLoc = loc.toLocation();
	ChampionInfo info = defending.getChampionInfo();

	final Zombie champion = (Zombie) bukkitLoc.getWorld().spawnEntity(bukkitLoc, EntityType.ZOMBIE);
	startChampionCountdown(champion);
	String CHAMPNAME = ChatColor.RED + defending.getKingdomName() + "'s Champion";
	champion.setCustomName(CHAMPNAME);
	champion.setCustomNameVisible(true);
	challenger.setChampionPlayerFightingWith(champion);
	challenger.setInvadingChunk(chunk);

	entityOwners.put(champion.getEntityId(), defending);
	targets.put(champion.getEntityId(), challenger);
	invadingChunks.put(chunk, champion.getEntityId());
	// set target as challenger, set baby false, set health as in champ info (max 2048)
	champion.setTarget(challenger.getPlayer());
	champion.setBaby(false);
	champion.setMaxHealth(info.getHealth() > Config.getConfig().getInt("max.champion.health") ? Config.getConfig().getInt("max.champion.health") : info.getHealth());
	champion.setHealth(info.getHealth() > Config.getConfig().getInt("max.champion.health") ? Config.getConfig().getInt("max.champion.health") : info.getHealth());

	// 200 bonus health for nexus defence
	if(land.getStructure() != null){
	  if(land.getStructure().getType() == StructureType.NEXUS){
		SimpleLocation nexusLoc = land.getStructure().getLoc();
		SimpleChunkLocation nexusChunk = nexusLoc.toSimpleChunk();

		if(chunk.equals(nexusChunk)){
		  champion.setMaxHealth(info.getHealth() + 200 > Config.getConfig().getInt("max.champion.health") ? Config.getConfig().getInt("max.champion.health") : info.getHealth() + 200);
		  champion.setHealth(info.getHealth() + 200 > Config.getConfig().getInt("max.champion.health") ? Config.getConfig().getInt("max.champion.health") : info.getHealth() + 200);
		}

		//champ.reinforce nexusguard will spawn if nexus defence
		if(chunk.equals(nexusChunk) && defending.getMisupgradeInfo().isNexusguard()){
		  callReinforcement(loc, challenger, 50);
		}
	  }

	  if(land.getStructure().getType() == StructureType.POWERCELL){
		if(defending.getMisupgradeInfo().isPsioniccore() && Config.getConfig().getBoolean("enable.psioniccore")){

		  champion.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000, 1));

		}
	  }
	}
	//org.bukkit.attribute.Attribute
	try{
	  Method m = champion.getClass().getMethod("getAttribute", Class.forName("org.bukkit.attribute.Attribute"));
	  double kb = info.getResist() / 100f;
	  ChampionUtils.setKnockbackAttribute(champion, kb);
	}catch(NoSuchMethodException | SecurityException | ClassNotFoundException e){

	}

	//set helmet as pumpkin new ItemStack(Material.PUMPKIN)
	champion.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));

	//set diamond chestplace, add enchant PROTECTION_ENVIRONMENTAL champ.armor
	ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
	if(info.getArmor() > 0){
	  armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, info.getArmor() - 1);
	}
	champion.getEquipment().setChestplate(armor);

	//champ.mimic copy challenger's item to champ
	if(info.getMimic() > 0){
	  ItemStack IS;

	  IS = challenger.getPlayer().getInventory().getHelmet();
	  if(IS != null)
		champion.getEquipment().setHelmet(IS);
			
/*			IS = challenger.getPlayer().getInventory().getChestplate();
			if(IS != null)
				champion.getEquipment().setChestplate(IS);*/

	  IS = challenger.getPlayer().getInventory().getBoots();
	  if(IS != null)
		champion.getEquipment().setBoots(IS);

	  IS = challenger.getPlayer().getInventory().getLeggings();
	  if(IS != null)
		champion.getEquipment().setLeggings(IS);
	}
	if(info.getAqua() > 0 && Enchantment.getByName("DEPTH_STRIDER") != null){
	  ItemStack boots = champion.getEquipment().getBoots();
	  if(boots == null){
		champion.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
		boots = champion.getEquipment().getBoots();
	  }
	  if(boots.getType() == Material.AIR){
		champion.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
		boots = champion.getEquipment().getBoots();
	  }
	  boots.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	  boots.addUnsafeEnchantment(Enchantment.getByName("DEPTH_STRIDER"), 10);
	}
	//set weap, chapm.weap 0 null 1 wood 2 stone 3 iron 4 diamond 4>lv diamond + ench(lv-4)
	int weapon = info.getWeapon();
	if(weapon == 0){
	  champion.getEquipment().setItemInHand(null);
	}
	else if(weapon == 1){
	  champion.getEquipment().setItemInHand(new ItemStack(Materials.WOODEN_SWORD.parseMaterial()));
	}
	else if(weapon == 2){
	  champion.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
	}
	else if(weapon == 3){
	  champion.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
	}
	else if(weapon == 4){
	  champion.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
	}
	else if(weapon > 4){
	  ItemStack diasword = new ItemStack(Material.DIAMOND_SWORD);
	  diasword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, weapon - 4);

	  champion.getEquipment().setItemInHand(diasword);
	}

//		if(info.getResist() > 0){
//			champion.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(info.getResist());;
//		}

	//chap.drag teleport challenger to champ
	int drag = info.getDrag();
	if(drag > 0){
	  dragTasks.put(champion.getEntityId(),
		  Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
			  new DragTask(challenger.getPlayer(), champion), 1L, 40L));
	}

	//champ.plow
	int plow = info.getPlow();
	if(plow > 0){
	  plowTasks.put(champion.getEntityId(),
		  Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
			  new PlowTask(challenger.getPlayer(), champion), 1L, 5L));
	}

	//champ.aqua champ teleport to challenger in water
	//int aqua = info.get (?)

	//champ.thor struct lightning and damage manually, 6 dmg, 8dmg all around 7,7,7 area if not ally
	int thor = info.getThor();
	if(thor > 0){
	  thorTasks.put(champion.getEntityId(),
		  Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
			  new ThorTask(challenger.getPlayer(), champion, defending), 1L, (long) (Config.getConfig().getDouble("champion-specs.thor-delay") * 20L)));
	}

	//set drop chance to 0
	champion.getEquipment().setBootsDropChance(0.0F);
	champion.getEquipment().setChestplateDropChance(0.0F);
	champion.getEquipment().setHelmetDropChance(0.0F);
	champion.getEquipment().setItemInHandDropChance(0.0F);
	champion.getEquipment().setLeggingsDropChance(0.0F);

	//champ.speed SPEED potion effect, infinite time,
	int speed = info.getSpeed();
	if(speed > 0){
	  champion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, speed - 1));
	}
	return champion;
  }

  /**
   * Spawn custom champion.
   *
   * @param loc         location of champion to be spawned
   * @param challenger  KingdomPlayer who challenges the champion
   * @param championMob the type of mob the champion is. (MUST EXTEND MONSTER)
   * @return Entity instance of champion.
   */
  public Monster spawnCustomChampion(SimpleLocation loc, final KingdomPlayer challenger, EntityType championMob){
	SimpleChunkLocation chunk = loc.toSimpleChunk();
	Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	if(land.getOwnerUUID() == null){
	  Kingdoms.logInfo("Could not spawn champion at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("No kingdom owns this land.");
	  return null;
	}

	final Kingdom defending = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());


	if(defending == null){
	  Kingdoms.logInfo("Could not spawn champion at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("The owner of this land is [" + land.getOwner() + "] but there is no data.");
	  Kingdoms.logInfo("This seems to be a plugin malfunction and needed to be reported.");
	  return null;
	}

	for(KingdomPlayer kp : defending.getOnlineMembers()){
	  defenders.put(kp.getUuid(), loc.toLocation());
	}

	challenger.getPlayer().setGameMode(GameMode.SURVIVAL);

	Location bukkitLoc = loc.toLocation();

	final Monster champion = (Monster) bukkitLoc.getWorld().spawnEntity(bukkitLoc, championMob);
	startChampionCountdown(champion);
	challenger.setChampionPlayerFightingWith(champion);
	challenger.setInvadingChunk(chunk);

	entityOwners.put(champion.getEntityId(), defending);
	targets.put(champion.getEntityId(), challenger);
	invadingChunks.put(chunk, champion.getEntityId());
	// set target as challenger
	champion.setTarget(challenger.getPlayer());
	return champion;
  }

  private void startChampionCountdown(Monster champion){
	try{
	  Method invulnerable = Monster.class.getMethod("setInvulnerable");
	  Method AI = Monster.class.getMethod("setAI");
	  invulnerable.invoke(champion, true);
	  AI.invoke(champion, false);
	  new BukkitRunnable() {
		@Override
		public void run(){
		  try{
			invulnerable.invoke(champion, false);
			AI.invoke(champion, true);
		  }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			e.printStackTrace();
		  }
		}
	  }.runTaskLater(plugin, 40L);

	}catch(NoSuchMethodException e){
	  champion.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255));
	  champion.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 255));
	}catch(SecurityException |
		IllegalAccessException |
		IllegalArgumentException |
		InvocationTargetException e){
	  e.printStackTrace();
	}
  }


  private class DragTask implements Runnable {
	Player p;
	Entity champion;
	KingdomPlayer kp;

	public DragTask(Player p, Entity champion){
	  super();
	  this.p = p;
	  this.champion = champion;
	  this.kp = GameManagement.getPlayerManager().getSession(p);
	}

	@Override
	public void run(){
	  if(p == null || champion == null) return;

	  if((!p.isDead()) && (!champion.isDead()) && (champion.isValid()) && (p.isOnline())){
		ChampionDragEvent dragEvent = new ChampionDragEvent(champion, kp);
		Bukkit.getPluginManager().callEvent(dragEvent);
		if(!dragEvent.isCancelled() && p.getLocation().distance(champion.getLocation()) > dragEvent.getDragRange()){
		  p.teleport(champion.getLocation());
		  p.sendMessage(Kingdoms.getLang().getString("Champion_Drag", kp.getLang()));
		}
	  }
	}
  }

  private class ThorTask implements Runnable {
	Player p;
	Entity champion;
	Kingdom kingdom;

	public ThorTask(Player p, Entity champion, Kingdom kingdom){
	  super();
	  this.p = p;
	  this.champion = champion;
	  this.kingdom = kingdom;
	}

	@Override
	public void run(){
	  if(p == null || champion == null) return;

	  if((!p.isDead()) && (!champion.isDead()) && (champion.isValid()) && (p.isOnline())){
		sendLightning(p, p.getLocation());
		p.damage(kingdom.getChampionInfo().getThor(), champion);

		p.sendMessage(Kingdoms.getLang().getString("Champion_Thor", GameManagement.getPlayerManager().getSession(p).getLang()));
	  }

	  for(Entity e : champion.getNearbyEntities(7, 7, 7)){
		if(e instanceof Player && !e.getUniqueId().equals(p.getUniqueId())){
		  KingdomPlayer kpNear = GameManagement.getPlayerManager().getSession(e.getUniqueId());
		  if(kpNear.getKingdom() == null || (!kpNear.getKingdom().equals(kingdom)
			  && !kpNear.getKingdom().isAllianceWith(kingdom))){
			ChampionThorEvent thorEvent = new ChampionThorEvent(champion, GameManagement.getPlayerManager().getSession(p));
			Bukkit.getPluginManager().callEvent(thorEvent);
			if(thorEvent.isCancelled()){
			  return;
			}
			//p.getWorld().strikeLightningEffect(p.getLocation());
			sendLightning(p, p.getLocation());
			p.damage(thorEvent.getDmg(), champion);

			p.sendMessage(Kingdoms.getLang().getString("Champion_Thor", GameManagement.getPlayerManager().getSession(p).getLang()));
		  }
		}
	  }
	}

  }

  private class PlowTask implements Runnable {
	Player p;
	Entity champion;

	public PlowTask(Player p, Entity champion){
	  super();
	  this.p = p;
	  this.champion = champion;
	}

	@Override
	public void run(){
	  if((!p.isDead()) && (!champion.isDead()) && (champion.isValid()) && (p.isOnline())){
		int radius = 1;

		for(int x = -radius; x <= radius; x++){
		  for(int y = -radius; y <= radius; y++){
			for(int z = -radius; z <= radius; z++){
			  Block block = champion.getLocation().getBlock().getRelative(x, y, z);
			  Material type = block.getType();
			  if(type == Materials.COBWEB.parseMaterial() || type == Material.LAVA){
				ChampionPlowEvent plowEvent = new ChampionPlowEvent(champion, block);
				if(!plowEvent.isCancelled()){
				  champion.getLocation().getBlock().setType(Material.AIR);
				}
			  }
			}
		  }
		}
	  }

	}

  }

  public boolean isChunkInvaded(SimpleChunkLocation loc){
	return invadingChunks.containsKey(loc);
  }

  private void callReinforcement(SimpleLocation loc, final KingdomPlayer target, int duration){
	Location bukkitLoc = loc.toLocation();
	SimpleChunkLocation chunk = loc.toSimpleChunk();

	Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	if(land.getOwnerUUID() == null){
	  Kingdoms.logInfo("Could not call reinforcement at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("No kingdom owns this land.");
	  return;
	}

	final Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
	if(kingdom == null){
	  Kingdoms.logInfo("Could not call reinforcement at [" + chunk.toString() + "].");
	  Kingdoms.logInfo("The owner of this land is [" + land.getOwner() + "] but there is no data.");
	  Kingdoms.logInfo("This seems to be a plugin malfunction and needed to be reported.");
	  return;
	}

	GameManagement.getGuardsManager().spawnNexusGuard(bukkitLoc, kingdom, target);
  }

  /**
   * stops the fight between champion on challenger
   *
   * @param kp the challenger
   */
  public void stopFight(KingdomPlayer kp){
	// challenger quit, champion dead
	Entity champion = kp.getChampionPlayerFightingWith();
	if(champion == null){
	  Kingdoms.logInfo("could not stop the fight.");
	  Kingdoms.logInfo("[" + kp.getPlayer().getName() + "] was fighting with nobody.");
	  return;
	}

	Integer dragTask = dragTasks.remove(champion.getEntityId());
	Integer thorTask = thorTasks.remove(champion.getEntityId());
	Integer plowTask = plowTasks.remove(champion.getEntityId());

	if(dragTask != null) Bukkit.getScheduler().cancelTask(dragTask);
	if(thorTask != null) Bukkit.getScheduler().cancelTask(thorTask);
	if(plowTask != null) Bukkit.getScheduler().cancelTask(plowTask);

	entityOwners.remove(champion.getEntityId());
	targets.remove(champion.getEntityId());
	invadingChunks.remove(kp.getFightZone());
	champion.remove();

	kp.setChampionPlayerFightingWith(null);
	kp.setInvadingChunk(null);
  }

  /**
   * Listener; do not touch
   *
   * @param event
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChallengerQuit(PlayerQuitEvent event){
	KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
	if(kp == null){
	  Kingdoms.logInfo("kp was null!");
	  return;
	}

	Entity champion = kp.getChampionPlayerFightingWith();
	if(champion == null) return;

	SimpleChunkLocation chunk = kp.getFightZone();
	Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	if(land.getOwnerUUID() == null){
	  Kingdoms.logInfo("[" + kp.getPlayer().getName() + "] was quit during fight.");
	  Kingdoms.logInfo("But the fightzone has no owner.");
	  stopFight(kp);
	  return;
	}

	stopFight(kp);

	Kingdom defending = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());

	plugin.getServer().getPluginManager().callEvent(new KingdomPlayerLostEvent(kp, defending, chunk));
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onChallengerDeath(PlayerDeathEvent e){
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getEntity());
	if(kp == null) return;
	if(kp.getChampionPlayerFightingWith() == null) return;


	Land land = GameManagement.getLandManager().getOrLoadLand(kp.getFightZone());
	Kingdom defender = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());

	plugin.getServer().getPluginManager().callEvent(new KingdomPlayerLostEvent(kp, defender, kp.getFightZone()));

	stopFight(kp);
  }

  public boolean isChampion(Entity e){
	return entityOwners.containsKey(e.getEntityId());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void championHp(EntityDamageEvent e){
	World bukkitWorld = e.getEntity().getWorld();
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;

	if(!(e.getEntity() instanceof Damageable)) return;
	Damageable champion = ((Damageable) e.getEntity());
	Kingdom kingdom = entityOwners.get(e.getEntity().getEntityId());

	String name = ChatColor.RED + kingdom.getKingdomName() + "'s Champion "
		+ ChatColor.GRAY + "[" +
		ChatColor.GREEN + ((int) (champion.getHealth() - e.getFinalDamage())) +
		ChatColor.AQUA + "/" +
		ChatColor.GREEN + champion.getMaxHealth() +
		ChatColor.GRAY + "]";

	if(kingdom.getChampionInfo().getDetermination() > 0){
	  name += ChatColor.GRAY + "[" + ChatColor.RED
		  + determination.get(e.getEntity().getEntityId())
		  + ChatColor.AQUA + "/"
		  + ChatColor.RED
		  + kingdom.getChampionInfo().getDetermination() + ChatColor.GRAY + "]";
	}

	e.getEntity().setCustomName(name);
	e.getEntity().setCustomNameVisible(true);
  }

  public static Map<SimpleChunkLocation, Integer> getInvadingChunks(){
	return invadingChunks;
  }

  public static void setInvadingChunks(
	  Map<SimpleChunkLocation, Integer> invadingChunks){
	ChampionManager.invadingChunks = invadingChunks;
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChampionVoidDamage(EntityDamageEvent e){
	if(!Config.getConfig().getBoolean("champion-specs.invader-lose-on-champion-void-damage")) return;
	if(e.getCause() != DamageCause.VOID) return;
	World bukkitWorld = e.getEntity().getWorld();
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;
	KingdomPlayer challenger = targets.get(e.getEntity().getEntityId());
	if(challenger == null){
	  Kingdoms.logInfo("Fatal error! challenger was null!");
	  return;
	}

	if(challenger.getKingdom() == null) return;

	Land land = GameManagement.getLandManager().getOrLoadLand(challenger.getFightZone());
	Kingdom defender = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());

	plugin.getServer().getPluginManager().callEvent(new KingdomPlayerLostEvent(challenger, defender, challenger.getFightZone()));

	stopFight(challenger);
	challenger.sendMessage(Kingdoms.getLang().getString("Champion_Void_Death", challenger.getLang()));
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChampionDrown(EntityTransformEvent e){
	if(e.getTransformReason() != EntityTransformEvent.TransformReason.DROWNED) return;
	if(!isChampion(e.getEntity())) return;
	
	e.setCancelled(true);
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChampionDamage(EntityDamageByEntityEvent e){
	World bukkitWorld = e.getEntity().getWorld();
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;

	if(!(e.getDamager() instanceof Player)) return;

	Player damager = (Player) e.getDamager();
	if(damager == null) return;

	KingdomPlayer challenger = GameManagement.getPlayerManager().getSession(damager);
	if(challenger == null){
	  Kingdoms.logInfo("Fatal error! challenger was null!");
	  return;
	}

	if(challenger.getKingdom() == null) return;

	Kingdom attacking = challenger.getKingdom();


	if(attacking.equals(entityOwners.get(e.getEntity().getEntityId()))){
	  Kingdoms.logInfo("check kingdom");
	  challenger.sendMessage(Kingdoms.getLang().getString("Champion_Own_Kingdom", challenger.getLang()));
	  e.setDamage(0.0D);
	  return;
	}

	ChampionByPlayerDamageEvent damageEvent = new ChampionByPlayerDamageEvent(e.getEntity(), challenger, e.getDamage());
	if(damageEvent.isCancelled()){
	  e.setCancelled(true);
	  return;
	}
	else{
	  e.setDamage(damageEvent.getDamage());
	}
  }

  @EventHandler
  public void onChampionEnterVehicle(VehicleEnterEvent e){
	if(!entityOwners.containsKey(e.getEntered().getEntityId())) return;

	e.setCancelled(true);

  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChampionDetermination(EntityDamageByEntityEvent e){
	World bukkitWorld = e.getEntity().getWorld();
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;

	if(!(e.getDamager() instanceof Player)) return;

	Player damager = (Player) e.getDamager();
	if(damager == null) return;

	KingdomPlayer challenger = GameManagement.getPlayerManager().getSession(damager);
	if(challenger == null){
	  Kingdoms.logInfo("Fatal error! challenger was null!");
	  return;
	}


	ChampionInfo info = entityOwners.get(e.getEntity().getEntityId()).getChampionInfo();
	if(info.getDetermination() > 0){
	  //Does this determination map ever get cleaned???? If not this could be a memory leak?
	  //Also this appears to ignore damage to an extent? I do 14 dmg and boss has 1 determination it does nothing???
	  //Also why only rip determination when hit by another entity?
	  if(!determination.containsKey(e.getEntity().getEntityId())){
		determination.put(e.getEntity().getEntityId(), info.getDetermination());
	  }
	  if(determination.get(e.getEntity().getEntityId()) > 0){
		ChampionDeterminationDamageEvent determinationDamageEvent =
			new ChampionDeterminationDamageEvent(e.getEntity(), e.getDamage(), determination.get(e.getEntity().getEntityId()), challenger);
		Bukkit.getPluginManager().callEvent(determinationDamageEvent);
		if(determinationDamageEvent.isCancelled()){
		  return;
		}
		e.setDamage(determinationDamageEvent.getDamage());
		int newd = (int) (determination.get(e.getEntity().getEntityId()) - e.getDamage());
		e.setDamage(0.0);
		//TODO possible fix
		/*
		if(newd < 0){
			e.setDamage(-1 * newD);
		}
		else{
			e.setDamage(0)
		}
		 */
		if(newd < 0) newd = 0;
		determination.put(e.getEntity().getEntityId(), newd);
	  }
	}
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChampionDamageByTurretArrow(EntityDamageByEntityEvent e){
	World bukkitWorld = e.getEntity().getWorld();
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;
	Kingdom champKingdom = entityOwners.get(e.getEntity().getEntityId());

	if(!(e.getDamager() instanceof Arrow)) return;

	Arrow a = (Arrow) e.getDamager();

	if(a.getMetadata(TurretUtil.META_SHOOTER) == null) return;
	if(a.getMetadata(TurretUtil.META_SHOOTER).size() < 1) return;

	String shooterKingdom = a.getMetadata(TurretUtil.META_SHOOTER).get(0).asString();
	if(shooterKingdom == null) return;

	Kingdom shootKingdom = GameManagement.getKingdomManager().getOrLoadKingdom(shooterKingdom);
	if(shootKingdom == null) return;

	if(shootKingdom.equals(champKingdom)){
	  e.setDamage(0.0D);
	  e.setCancelled(true);
	  return;
	}

	ChampionDamageEvent damageEvent = new ChampionDamageEvent(e.getEntity(), e.getDamage(), ChampionDamageEvent.ChampionDamageCause.TURRET);
	Bukkit.getPluginManager().callEvent(damageEvent);
	if(!damageEvent.isCancelled()){
	  e.setDamage(damageEvent.getDamage());
	}
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChampionDamageByPotion(PotionSplashEvent e){

	World bukkitWorld = e.getEntity().getWorld();
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	for(Iterator<LivingEntity> iter = e.getAffectedEntities().iterator(); iter.hasNext(); ){
	  Entity entity = iter.next();
	  if(!entityOwners.containsKey(entity.getEntityId())) continue;
	  Kingdom champKingdom = entityOwners.get(entity.getEntityId());

	  if(!(e.getPotion().getShooter() instanceof Player)) continue;
	  KingdomPlayer shooter = GameManagement.getPlayerManager().getSession((Player) e.getPotion().getShooter());

	  if(champKingdom.equals(shooter.getKingdom())
		  || champKingdom.isAllianceWith(shooter.getKingdom())
		  || shooter.getPlayer().getGameMode() != GameMode.SURVIVAL)
		iter.remove();
	}
  }

  /**
   * Listener (do not touch)
   *
   * @param event
   */
  @EventHandler
  public void onChampionDeath(EntityDeathEvent event){
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(event.getEntity())) return;
	World bukkitWorld = event.getEntity().getWorld();
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	if(!entityOwners.containsKey(event.getEntity().getEntityId())) return;

	Player killer = event.getEntity().getKiller();
	if(killer == null) return;

	KingdomPlayer challenger = targets.get(event.getEntity().getEntityId());
	if(challenger == null){
	  Kingdoms.logInfo("Fatal error! challenger was null!");
	  return;
	}

	if(challenger.getKingdom() == null) return;

	if(event.getEntity() != challenger.getChampionPlayerFightingWith()) return;

	SimpleChunkLocation chunk = challenger.getFightZone().clone();
	Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
	if(land.getOwnerUUID() == null){
	  Kingdoms.logInfo("Error! champion of [" + chunk.toString() + "] is dead.");
	  Kingdoms.logInfo("But no kingdom owns this land.");
	  stopFight(challenger);
	  return;
	}

	Kingdom defending = entityOwners.get(event.getEntity().getEntityId());

	stopFight(challenger);
	event.getDrops().clear();
	plugin.getServer().getPluginManager().callEvent(new KingdomPlayerWonEvent(challenger, defending, chunk));
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onChallengerDeathWhileInvade(PlayerDeathEvent e){
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	World bukkitWorld = e.getEntity().getWorld();

	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;

	KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getEntity());
	if(kp == null) return;
	if(kp.getChampionPlayerFightingWith() == null) return;


	stopFight(kp);
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommandWhileFight(PlayerCommandPreprocessEvent e){
	KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
	if(kp == null) return;//GameManagement.getPlayerManager().preloadKingdomPlayer(e.getPlayer());
	if(kp.getChampionPlayerFightingWith() == null) return;
	if(e.getMessage().equalsIgnoreCase("/k surrender") ||
		e.getMessage().equalsIgnoreCase("/kingdoms surrender") ||
		e.getMessage().equalsIgnoreCase("/kingdom surrender") ||
		e.getMessage().equalsIgnoreCase("/k ff") ||
		e.getMessage().equalsIgnoreCase("/kingdoms ff") ||
		e.getMessage().equalsIgnoreCase("/kingdom ff")){
	  if(kp.getPlayer().hasPermission("kingdoms.surrender") ||
		  kp.getPlayer().hasPermission("kingdoms.player"))
		return;
	}
	kp.sendMessage(Kingdoms.getLang().getString("Champion_Command_Block", kp.getLang()));
	e.setCancelled(true);
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onTargetChange(EntityTargetLivingEntityEvent e){
	World bukkitWorld = e.getEntity().getWorld();
	if(!Config.getConfig().getStringList("enabled-worlds").contains(bukkitWorld.getName())) return;
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getTarget())) return;
	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;
	Kingdom kingdom = entityOwners.get(e.getEntity().getEntityId());
	KingdomPlayer challenger = targets.get(e.getEntity().getEntityId());
	ChampionTargetChangeEvent championTargetChangeEvent = new ChampionTargetChangeEvent(e.getEntity(), e.getTarget());
	if(e.getTarget() instanceof Player){
	  Player targetP = (Player) e.getTarget();
	  KingdomPlayer target = GameManagement.getPlayerManager().getSession(targetP);
	  if(target.getKingdom() == null) return; // don't change if has no kingdom

	  if(kingdom.equals(target.getKingdom())){//Why this???????? -> || kingdom.equals(target.getKingdom())){
		e.setTarget(challenger.getPlayer());// change target if ally or own kingdom member
	  }
	  //TODO check that this is right lmao
	  else{
	    Bukkit.getPluginManager().callEvent(championTargetChangeEvent);
	    if(championTargetChangeEvent.isCancelled()){
	      e.setTarget(challenger.getPlayer());
		}
	  }
	}
  }


  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onKnockBack(EntityDamageByEntityEvent e){

	if(e.getCause() != DamageCause.ENTITY_ATTACK //only arrow and projectile
		|| e.getCause() != DamageCause.PROJECTILE) return;

	if(e.getEntity().getType() != EntityType.ZOMBIE) return; //check if zombie

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return; //check if champion

	Kingdom kingdom = entityOwners.get(e.getEntity().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();

	int resist = info.getResist();
	if(!(resist > 0)) return;

	if(ProbabilityTool.testProbability100(resist)){
	  ChampionIgnoreKnockbackEvent ignoreKnockbackEvent = new ChampionIgnoreKnockbackEvent(e.getEntity());
	  Bukkit.getPluginManager().callEvent(ignoreKnockbackEvent);
	  if(ignoreKnockbackEvent.isCancelled()){
	    return;
	  }
	  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> e.getEntity().setVelocity(new Vector())
		  , 1L);

	}

  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onPlaceInMockRange(BlockPlaceEvent e){
	KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
	if(kp.getKingdom() == null) return; //check if has kingdom

	Entity entity = kp.getChampionPlayerFightingWith();
	if(entity == null) return;//check if fighting

	Kingdom defender = entityOwners.get(entity.getEntityId());
	ChampionInfo info = defender.getChampionInfo();
	int mock = info.getMock();

	if(!(mock > 0)) return;
	ChampionPreMockEvent preMockEvent = new ChampionPreMockEvent(entity, mock);
	Bukkit.getPluginManager().callEvent(preMockEvent);
	if(preMockEvent.isCancelled()){
	  return;
	}
	mock = preMockEvent.getMockRange();
	Location champLoc = entity.getLocation();
	int champX = champLoc.getBlockX();
	int champZ = champLoc.getBlockZ();

	int placingX = e.getBlock().getX();
	int placingZ = e.getBlock().getZ();

	if(e.getBlock().getLocation().distanceSquared(champLoc) > mock * mock) return;
	ChampionMockEvent mockEvent = new ChampionMockEvent(entity);
	Bukkit.getPluginManager().callEvent(mockEvent);
	e.setCancelled(!mockEvent.isCancelled());
	kp.sendMessage(Kingdoms.getLang().getString("Champion_Mock", kp.getLang()).replaceAll("%mock%", mock + ""));
	return;

  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onDeathDuelChampDamageToNonInvader(EntityDamageByEntityEvent e){
	if(e.getDamager().getType() != EntityType.ZOMBIE) return; //damager is not zombie
	if(!(e.getEntity() instanceof Player)) return; //damaged is not player

	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;
	if(!entityOwners.containsKey(e.getDamager().getEntityId())) return; //damager not champion
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getDamager())) return;
	KingdomPlayer damaged = GameManagement.getPlayerManager().getSession((Player) e.getEntity());
	//if(damaged.getKingdom() == null)//not in kingdom

	if(damaged.getChampionPlayerFightingWith() != null) return; //it's invader

	Kingdom kingdom = entityOwners.get(e.getDamager().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();
	int duel = info.getDuel();
	if(!(duel > 0)) return;// duel is not on

	e.setDamage(e.getDamage() * 2);//double to non-invader
	//damaged.sendMessage(ChatColor.RED+"Death duel rage!!!");
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onDeathDuelNonInvaderDamageToChamp(EntityDamageByEntityEvent e){
	if(!(e.getDamager() instanceof Player)) return; //damager is not player
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getDamager())) return;
	if(e.getEntity().getType() != EntityType.ZOMBIE) return; //damaged is not zombie

	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;//damaged is not champion

	KingdomPlayer damager = GameManagement.getPlayerManager().getSession((Player) e.getDamager());
	//if(damager.getKingdom() == null);//not in kingdom

	if(damager.getChampionPlayerFightingWith() != null) return; //it's invader

	Kingdom kingdom = entityOwners.get(e.getEntity().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();
	int duel = info.getDuel();
	if(!(duel > 0)) return;// duel is not on

	e.setDamage(e.getDamage() / 2);//double to non-invader
	damager.sendMessage(Kingdoms.getLang().getString("Champion_DeathDuel", damager.getLang()));
  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChampDamageWhileDamageCapOn(EntityDamageByEntityEvent e){
	if(e.getEntity().getType() != EntityType.ZOMBIE) return; //damaged is not zombie
	if(!entityOwners.containsKey(e.getEntity().getEntityId())) return;//damaged is not champion

	Kingdom kingdom = entityOwners.get(e.getEntity().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();
	int damageCap = info.getDamagecap();
	if(!(damageCap > 0)) return;// damageCap is not on

	if(e.getDamage() > 15.0D){
	  ChampionDamageCapEvent damageCapEvent = new ChampionDamageCapEvent(e.getEntity(), e.getDamager(), damageCap, e.getDamage());
	  Bukkit.getPluginManager().callEvent(damageCapEvent);
	  if(!damageCapEvent.isCancelled()){
		e.setDamage(damageCapEvent.getDamageCap());
	  }
	}

  }

  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onFocus(EntityDamageByEntityEvent e){
	if(e.getDamager().getType() != EntityType.ZOMBIE) return; //damager is not zombie
	if(!(e.getEntity() instanceof Player)) return; //damaged is not player
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;

	if(!entityOwners.containsKey(e.getDamager().getEntityId())) return; //damager not champion
	Player p = (Player) e.getEntity();
	Kingdom kingdom = entityOwners.get(e.getDamager().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();
	int focus = info.getFocus();
	if(focus <= 0) return;// focus is not on
	Collection<PotionEffect> effects = p.getActivePotionEffects();
	if(effects.size() > 0){
	  ChampionFocusEvent focusEvent = new ChampionFocusEvent(e.getDamager(), GameManagement.getPlayerManager().getSession(p));
	  Bukkit.getPluginManager().callEvent(focusEvent);
	  if(focusEvent.isCancelled()){
	    return;
	  }
	  for(PotionEffect effect : effects){
		PotionEffect pe = new PotionEffect(effect.getType(), effect.getDuration() - 1, effect.getAmplifier());
		p.removePotionEffect(effect.getType());
		p.addPotionEffect(pe);
	  }
	}
  }


  /**
   * Listener (do not touch)
   *
   * @param e
   */
  @EventHandler
  public void onDamageWhileStrengthUp(EntityDamageByEntityEvent e){
	if(e.getDamager().getType() != EntityType.ZOMBIE) return; //damager is not zombie
	if(!(e.getEntity() instanceof Player)) return; //damaged is not player
	GameManagement.getApiManager();
	if(ExternalManager.isCitizen(e.getEntity())) return;

	if(!entityOwners.containsKey(e.getDamager().getEntityId())) return; //damager not champion

	Kingdom kingdom = entityOwners.get(e.getDamager().getEntityId());
	ChampionInfo info = kingdom.getChampionInfo();
	int strength = info.getStrength();
	if(!(strength > 0)) return;// strength is not on

	if(ProbabilityTool.testProbability100(strength)){
	  ChampionStrengthEvent strengthEvent = new ChampionStrengthEvent(e.getDamager(), GameManagement.getPlayerManager().getSession(e.getEntity().getUniqueId()));
	  Bukkit.getPluginManager().callEvent(strengthEvent);
	  if(strengthEvent.isCancelled()){
	    return;
	  }
	  e.getEntity().setVelocity(new Vector(0, 1.5, 0));
	}
  }

  /**
   * Listener (do not touch)
   * @param e
   */

  /**
   * get Entity instance from its id
   *
   * @param world world
   * @param id    entityID
   * @return Entity if found; null if not found
   */
  public static Entity getEntityByEntityID(World world, int id){
	Iterator<Entity> iter = world.getEntities().iterator();
	for(; iter.hasNext(); ){
	  Entity e = iter.next();
	  if(e.getEntityId() == id) return e;
	}

	return null;
  }

  private static final Map<Integer, Integer> dragTasks = new ConcurrentHashMap<Integer, Integer>();
  private static final Map<Integer, Integer> thorTasks = new ConcurrentHashMap<Integer, Integer>();
  private static final Map<Integer, Integer> plowTasks = new ConcurrentHashMap<Integer, Integer>();

  @Override
  public void onDisable(){
	for(Map.Entry<Integer, KingdomPlayer> entry : targets.entrySet()){
	  stopFight(entry.getValue());
	}

	targets.clear();
  }

  public static void sendLightning(Player p, Location l){
	Class<?> light = getNMSClass("EntityLightning");
	try{
	  Constructor<?> constu =
		  light
			  .getConstructor(getNMSClass("World"),
				  double.class, double.class,
				  double.class, boolean.class, boolean.class);
	  Object wh = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
	  Object lighobj = constu.newInstance(wh, l.getX(), l.getY(), l.getZ(), false, false);

	  Object obj =
		  getNMSClass("PacketPlayOutSpawnEntityWeather")
			  .getConstructor(getNMSClass("Entity")).newInstance(lighobj);


	  try{
		sendPacket(p, obj);
		p.playSound(p.getLocation(), Sound.valueOf("AMBIENCE_THUNDER"), 100, 1);
	  }catch(IllegalArgumentException e){
		try{
		  sendPacket(p, obj);
		  p.playSound(p.getLocation(), Sound.valueOf("ENTITY_LIGHTNING_THUNDER"), 100, 1);
		}catch(IllegalArgumentException ex){
		  p.getWorld().strikeLightningEffect(p.getLocation());
		}
	  }
//        } catch (NoSuchMethodException | SecurityException |
//                IllegalAccessException | IllegalArgumentException |
//                InvocationTargetException | InstantiationException e) {
	}catch(Exception e){
	  e.printStackTrace();
	}
  }

  public static Class<?> getNMSClass(String name){
	String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	try{
	  return Class.forName("net.minecraft.server." + version + "." + name);
	}catch(ClassNotFoundException e){
	  e.printStackTrace();
	  return null;
	}
  }

  public static void sendPacket(Player player, Object packet){
	try{
	  Object handle = player.getClass().getMethod("getHandle").invoke(player);
	  Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
	  playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
		  .invoke(playerConnection, packet);
	}catch(Exception e){
	  e.printStackTrace();
	}
  }
}
