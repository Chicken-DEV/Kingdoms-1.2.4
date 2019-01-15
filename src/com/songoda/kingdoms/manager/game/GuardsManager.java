package com.songoda.kingdoms.manager.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.songoda.kingdoms.main.Kingdoms;

public class GuardsManager extends Manager {

	protected GuardsManager(Plugin plugin) {
		super(plugin);
		new Thread(new guardTrackWorker()).start();
	}
	
	public static final String GUARDNAME = Kingdoms.getLang().getString("Soldier_Name");
	public static final Map<Monster, Player> guardTargets = new HashMap<Monster, Player>();
	public Entity spawnNexusGuard(Location loc, Kingdom owner, KingdomPlayer target){
		Entity e = loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		Zombie zombie = (Zombie) e;
		
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
		zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		zombie.setBaby(false);
		zombie.getEquipment().setItemInHandDropChance(0.0f);
		if(target != null)zombie.setTarget(target.getPlayer());
		zombie.setCustomName(GUARDNAME);
		zombie.setCustomNameVisible(true);
		if(owner != null) zombie.setMetadata("kingdom+" +owner.getKingdomName(), new FixedMetadataValue(Kingdoms.getInstance(), ""));
		int weapon = owner.getChampionInfo().getWeapon();
		if(weapon == 0){
			zombie.getEquipment().setItemInHand(null);
		}else if(weapon == 1){
			zombie.getEquipment().setItemInHand(new ItemStack(Materials.WOODEN_SWORD.parseMaterial()));
		}else if(weapon == 2){
			zombie.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
		}else if(weapon == 3){
			zombie.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
		}else if(weapon == 4){
			zombie.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
		}else if(weapon > 4){
			ItemStack diasword = new ItemStack(Material.DIAMOND_SWORD);
			diasword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, weapon - 4);
			
			zombie.getEquipment().setItemInHand(diasword);
		}
		if(owner != null){
		int speed = owner.getChampionInfo().getSpeed();
			if(speed > 0){
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, speed - 1));
			}
		}
		if(target != null)guardTargets.put(zombie, target.getPlayer());
		return e;
	}
	
	public Entity spawnSiegeBreaker(Location loc, Kingdom owner, KingdomPlayer target){
		Entity e = loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
		Creeper creeper = (Creeper) e;
		
		creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
		if(target != null)creeper.setTarget(target.getPlayer());
		creeper.setCustomName(GUARDNAME);
		creeper.setCustomNameVisible(true);
		if(owner != null)creeper.setMetadata("kingdom+" +owner.getKingdomName(), new FixedMetadataValue(Kingdoms.getInstance(), ""));
//		int speed = owner.getChampionInfo().getSpeed();
//		if(speed > 0){
//			creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, speed - 1));
//		}
		creeper.setPowered(true);
		if(target != null)guardTargets.put(creeper, target.getPlayer());
		return e;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof LivingEntity){
			if(((LivingEntity)event.getEntity()).getCustomName() == null) return;
			if(((LivingEntity)event.getEntity()).getCustomName().equals(GUARDNAME)){
				if(event.getCause() == DamageCause.ENTITY_EXPLOSION){
					event.setCancelled(true);
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event){
		if(event.getEntity().getCustomName() != null){
			if(event.getEntity().getCustomName().equals(GUARDNAME)){
				event.setDroppedExp(0);
				event.getDrops().clear();
			}
		}
	}
	

	@EventHandler
	public void onSoldierEnterVehicle(VehicleEnterEvent event){
		if(event.getEntered() instanceof LivingEntity){
			if(((LivingEntity)event.getEntered()).getCustomName() == null) return;
			if(((LivingEntity)event.getEntered()).getCustomName().equals(GUARDNAME)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent event){
		if(guardTargets.containsKey(event.getEntity())){
			if(event.getTarget() == null) return;
			if(!event.getTarget().equals(guardTargets.get(event.getEntity()))){
				event.setCancelled(true);
				((Creature) event.getEntity()).setTarget(guardTargets.get(event.getEntity()));
			}
		}
	}
	
	public class guardTrackWorker implements Runnable{

		@Override
		public void run() {
			while(plugin.isEnabled()){
				Iterator<Map.Entry<Monster, Player>> it = guardTargets.entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<Monster, Player> entry = it.next();
			    	Monster guard = entry.getKey();
					Entity target = entry.getValue();

					if (guard.isDead() || !guard.isValid() || target.isDead() || !target.isValid()) {
						it.remove();
						guard.remove();
						Iterator<Map.Entry<Location, SoldierTurretManager.Soldier>> soldierIt = GameManagement.getSoldierTurretManager().soldiers.entrySet().iterator();
						while(soldierIt.hasNext()){
							Map.Entry<Location, SoldierTurretManager.Soldier> soldierEntry = soldierIt.next();
							SoldierTurretManager.Soldier soldier = soldierEntry.getValue();
							if(soldier.getZombie().getUniqueId().equals(guard.getUniqueId())){
								soldierIt.remove();
								break;
							}
						}
						
						continue;
					}
					guard.setTarget((LivingEntity) target);
			    }
				
				
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

}
