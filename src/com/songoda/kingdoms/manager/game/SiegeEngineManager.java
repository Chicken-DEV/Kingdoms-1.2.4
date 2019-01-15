package com.songoda.kingdoms.manager.game;

import java.util.HashMap;
import java.util.Map.Entry;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SiegeEngine;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Sounds;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class SiegeEngineManager extends Manager {

	protected SiegeEngineManager(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	public void fireSiegeEngine(SiegeEngine e, Land sctarget, Kingdom firingKingdom, Kingdom targetKingdom){
		e.resetFireCooldown();
		boolean isTargetShielded = false;
		if(targetKingdom.isWithinNexusShieldRange(sctarget.getLoc())){
			if(targetKingdom.getShieldValue() > 0){
				isTargetShielded = true;
			}
		}
		boolean messageSent = false;
		Chunk target = sctarget.getLoc().toChunk();
		for(Entry<Integer,Integer> crucial:crucialPoints.entrySet()){
			int x = crucial.getKey();
			int z = crucial.getValue();
			Location boom = target.getBlock(x,0,z).getLocation();
			int y = target.getWorld().getHighestBlockYAt(boom);
			if(isTargetShielded){
				y += 10;
			}
			boom = target.getBlock(x,y,z).getLocation();
			if(isTargetShielded){
//				target.getWorld().playEffect(boom, Effect.MAGIC_CRIT, 5);
//				target.getWorld().playEffect(boom, Effect.EXPLOSION, 5);
//				target.getWorld().playEffect(boom, Effect.WATERDRIP, 5);
				target.getWorld().playSound(boom, Sounds.EXPLODE.bukkitSound(), 3.0f, 1.0f);
				if(!messageSent){
					targetKingdom.setShieldValue(targetKingdom.getShieldValue()- Config.getConfig().getInt("siege.fire.shield-damage"));
					targetKingdom.sendAnnouncement(null, 
							Kingdoms.getLang().getString("Siege_Warning_Shielded")
							.replaceAll("%value%",""+targetKingdom.getShieldValue())
							.replaceAll("%max%",""+targetKingdom.getShieldMax())
							.replaceAll("%kingdom%",""+firingKingdom.getKingdomName()),
							true);
					firingKingdom.sendAnnouncement(null, 
							Kingdoms.getLang().getString("Siege_Success_Shielded")
							.replaceAll("%value%",""+targetKingdom.getShieldValue())
							.replaceAll("%max%",""+targetKingdom.getShieldMax())
							.replaceAll("%kingdom%",""+targetKingdom.getKingdomName()),
							true);
					messageSent = true;
				}
			}else{
				target.getWorld().createExplosion(boom, (float) Config.getConfig().getDouble("siege.fire.explosion-radius"));
				if(!messageSent){
					targetKingdom.sendAnnouncement(null, 
							Kingdoms.getLang().getString("Siege_Warning")
							.replaceAll("%value%",""+targetKingdom.getShieldValue())
							.replaceAll("%max%",""+targetKingdom.getShieldMax())
							.replaceAll("%kingdom%",""+firingKingdom.getKingdomName()),
							true);

					firingKingdom.sendAnnouncement(null, 
							Kingdoms.getLang().getString("Siege_Success")
							.replaceAll("%value%",""+targetKingdom.getShieldValue())
							.replaceAll("%max%",""+targetKingdom.getShieldMax())
							.replaceAll("%kingdom%",""+targetKingdom.getKingdomName()),
							true);
					messageSent = true;
				}
			}
		}
	}

	
	private HashMap<Integer, Integer> crucialPoints = new HashMap<Integer, Integer>(){{
		//16x16
		//X  4 8 12 X
		//4  - - - 4
		//8  - - - 8
		//12 - - - 12
		//X  4 8 12
		
		put(4,4);
		put(4,8);
		put(4,12);
		put(8,4);
		put(8,8);
		put(8,12);
		put(12,4);
		put(12,8);
		put(12,12);
		
	}};
	
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
