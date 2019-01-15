package com.songoda.kingdoms.manager.game;

import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.ArsenalItem;
import com.songoda.kingdoms.main.Kingdoms;

public class KingdomArsenalItemManager extends Manager implements Listener{
	protected KingdomArsenalItemManager(Plugin plugin) {
		super(plugin);
	}

	@Override
	public void onDisable() {
		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAttack(PlayerInteractEvent event) {
		if(!Config.getConfig().getStringList("enabled-worlds").contains(event.getPlayer().getWorld().getName())) return;
		if(event.getAction() != Action.RIGHT_CLICK_AIR &&
				event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getPlayer().getItemInHand() == null) return;
		Player p = event.getPlayer();
		if(p.getItemInHand().getItemMeta() == null) return;
		if(p.getItemInHand().getItemMeta().getLore() == null) return;
		if(p.getItemInHand().getItemMeta().getLore().size() == 0) return;
		ArsenalItem type = null;
		for(ArsenalItem item:ArsenalItem.values()){

			if(p.getItemInHand().getItemMeta().getLore().contains(item.getUnique())){
				type = item;
				break;
			}
		}
		if(type == null) return;
		event.setCancelled(true);
		switch(type){
		case SIEGE_ROCKET:
			fireSiegeRocket(p);
			break;
		case TURRET_BREAKER:
			destroyFence(event);
			break;
		
		}
		
	}
	
	public void destroyFence(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getClickedBlock() == null){
			p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Wrong_Usage", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			return;
		}
		KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(p);
		Land land = Kingdoms.getManagers().getLandManager().getOrLoadLand(new SimpleChunkLocation(event.getClickedBlock().getChunk()));
		if(land.getOwnerUUID() == null){
			p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Wrong_Usage", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			return;
		}
		if(kp.getKingdom() == null){
			p.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			return;
		}
		if(land.getOwnerUUID().equals(kp.getKingdomUuid())){
			p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Wrong_Usage", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			return;
		}
		if(event.getClickedBlock().getType() == Materials.SKELETON_SKULL.parseMaterial()){
			if(event.getClickedBlock().getRelative(0,-1,0).getType() == Materials.OAK_FENCE.parseMaterial()){
				event.getClickedBlock().getRelative(0,-1,0).setType(Material.AIR);
				if(p.getItemInHand().getAmount() > 0){
					p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
				}else{
					p.setItemInHand(null);
				}
				p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Success", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
				return;
			}
		}else if(event.getClickedBlock().getType().toString().endsWith("FENCE")){
			event.getClickedBlock().setType(Material.AIR);
			if(p.getItemInHand().getAmount() > 0){
				p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
			}else{
				p.setItemInHand(null);
			}
			p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Success", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
			return;
		}else{
			p.sendMessage(Kingdoms.getLang().getString("Arsenal_Item_Turret_Breaker_Wrong_Usage", Kingdoms.getManagers().getPlayerManager().getSession(p).getLang()));
		}
	}
	
	private static final String ROCKET_META = "SIEGE_ROCKET";
	
	public void fireSiegeRocket(Player p){
		if(p.getItemInHand().getAmount() > 0){
			p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
		}else{
			p.setItemInHand(null);
		}
		Location loc = p.getEyeLocation().toVector().add(p.getLocation().getDirection().multiply(2)).toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());
		LargeFireball rocket = p.getWorld().spawn(loc, LargeFireball.class);
		rocket.setVelocity(p.getEyeLocation().getDirection().multiply(0.5));
		rocket.setMetadata(ROCKET_META, new FixedMetadataValue(plugin, p.getUniqueId()));
		rocket.setShooter(p);
	}
	
	@EventHandler
	public void rocketDestruction(EntityExplodeEvent event){
		if(event.getEntity() instanceof LargeFireball){
			LargeFireball rocket = (LargeFireball) event.getEntity();
			if(rocket.hasMetadata(ROCKET_META)){
				event.setCancelled(true);
				rocket.getWorld().createExplosion(rocket.getLocation().getX(),
						rocket.getLocation().getY(),
						rocket.getLocation().getZ(),
						(float) Config.getConfig().getDouble("siege.fire.explosion-radius"), false, false);
				rocket.remove();
				
			}
		}
	}

}
