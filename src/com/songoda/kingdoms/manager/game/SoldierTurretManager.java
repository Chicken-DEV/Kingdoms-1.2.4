package com.songoda.kingdoms.manager.game;

import java.util.HashMap;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.songoda.kingdoms.main.Kingdoms;

public class SoldierTurretManager extends Manager {
	
	public HashMap<Location, Soldier> soldiers = new HashMap<>();

	protected SoldierTurretManager(Plugin plugin) {
		super(plugin);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	
	public void turretSpawnSoldier(Kingdom owner, Location loc, Location origin, int damage, Player target){
		if(soldiers.containsKey(loc)){
			Soldier soldier = soldiers.get(loc);
			soldier.zombie.remove();
		}
		spawnSoldier(owner, loc, origin, damage, target);
	}
	
	@EventHandler
	public void onFinalService(EntityDeathEvent e){
		if(e.getEntity().hasMetadata("finalservice")){
			if(e.getEntity().getKiller() != null){
				e.getEntity().getKiller().damage(10.0,e.getEntity());
			}
		}
	}
	
	public Zombie spawnSoldier(Kingdom owner, Location loc, Location origin, int damage, Player target){
		Zombie soldier = loc.getWorld().spawn(loc, Zombie.class);
        soldier.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
        soldier.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 2));
        soldier.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
        soldier.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        soldier.setBaby(false);
        soldier.getEquipment().setItemInHandDropChance(0.0f);
        soldier.getEquipment().setHelmetDropChance(0.0f);
        soldier.setTarget(target);
        soldier.setCustomName(GuardsManager.GUARDNAME);
        soldier.setCustomNameVisible(true);
        soldier.setMetadata("kingdom+" + owner.getKingdomName(), new FixedMetadataValue(Kingdoms.getInstance(), ""));
        if (damage > -1)
            soldier.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999,
                   damage));
        if(owner.getTurretUpgrades().isFinalService())
        	soldier.setMetadata("finalservice", new FixedMetadataValue(Kingdoms.getInstance(), ""));
        GuardsManager.guardTargets.put(soldier, target);
        soldiers.put(loc, new Soldier(soldier, loc, target));
		return soldier;
	}
	
	public class Soldier {
		private final Zombie zombie;
		private final Location spawnLoc;
		private final LivingEntity target;
		public Soldier(Zombie zombie, Location spawnLoc, LivingEntity target) {
			super();
			this.zombie = zombie;
			this.spawnLoc = spawnLoc;
			this.target = target;
		}
		public Zombie getZombie() {
			return zombie;
		}
		public Location getSpawnLoc() {
			return spawnLoc;
		}
		public LivingEntity getTarget() {
			return target;
		}
		
	}

}
