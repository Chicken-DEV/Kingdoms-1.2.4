package com.songoda.kingdoms.manager.game;

import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.KingdomChest;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.events.KingdomPlayerLostEvent;
import com.songoda.kingdoms.events.KingdomPlayerWonEvent;
import com.songoda.kingdoms.events.LandUnclaimEvent;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;

public class FightManager extends Manager implements Listener{

	protected FightManager(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onLandUnclaim(LandUnclaimEvent event) {
		SimpleChunkLocation chunk = event.getLand().getLoc();
		
		ChampionManager cm = Kingdoms.getManagers().getChampionManager();
		if(cm.isChunkInvaded(chunk)){
			int championID = cm.getInvadingChunks().get(chunk);
			Kingdom defending = cm.entityOwners.get(championID);
			KingdomPlayer challenger = cm.targets.get(championID);
			cm.stopFight(challenger);
			Bukkit.getPluginManager().callEvent(new KingdomPlayerWonEvent(challenger, defending, chunk));
			return;
		}
		
	}
	
	@EventHandler
	public void onChallengerWin(KingdomPlayerWonEvent e){
		KingdomPlayer challenger = e.getChallenger();
		
		Kingdom attacker = challenger.getKingdom();
		Kingdom defender = e.getLostKingdom();
		SimpleChunkLocation chunk = e.getChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		attacker.addInvasionLog(defender, challenger, true, land);
		defender.addInvasionLog(defender, challenger, false, land);
		if(land == null){
			Kingdoms.logInfo("land was null!");
			return;
		}
		
		attacker.sendAnnouncement(null, Kingdoms.getLang().getString("Misc_Invasion_Victory").replaceAll("%chunk%", chunk.toString()).replaceAll("%kingdom%", defender.getKingdomName()), true);
		
		//nexus chunk
		//rp of lost kingdom will be added to winner kingdom
		if(land.getStructure() != null){
			if(land.getStructure().getType() == StructureType.NEXUS){
				GameManagement.getNexusManager().breakNexus(land);
				
				int temp = defender.getResourcepoints();
				defender.setResourcepoints(0);
				attacker.setResourcepoints(attacker.getResourcepoints() + temp);
				//Bukkit.getPluginManager().callEvent(new KingdomResourcePointChangeEvent(attacker, temp));
				if(defender.getKingdomChest().getUsing() != null){
					defender.getKingdomChest().getUsing().getPlayer().closeInventory();
				}
				List<ItemStack> ISs = defender.getKingdomChest().getInv();
				if(ISs != null){
					for(ItemStack IS : ISs){
						challenger.getPlayer().getWorld().dropItemNaturally(challenger.getPlayer().getLocation(), IS);
					}
				}
				KingdomChest chest = new KingdomChest();
				chest.setInv(new ArrayList<ItemStack>());
				defender.setKingdomChest(chest);
				attacker.sendAnnouncement(null, Kingdoms.getLang().getString("Misc_Invasion_Nexus_Victory").replaceAll("%rp%", temp + "").replaceAll("%kingdom%", defender.getKingdomName()), true);
			}else{
				GameManagement.getStructureManager().breakStructure(land);
			}
		}
		
		//give land to the winner if won
		land.setOwnerUUID(attacker.getKingdomUuid());

		//Kingdoms.logLandCheck("LAND UNCLAIM DEBUG: LAND INVADED, OWNER CHANGED TO " + attacker.getKingdomName() + " AT " + chunk.toString());
		GameManagement.getVisualManager().visualizeLand(challenger, chunk);
		
	}
	
	@EventHandler
	public void onChallengerLose(KingdomPlayerLostEvent e){
		KingdomPlayer challenger = e.getChallenger();
		SimpleChunkLocation chunk = e.getLoc();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		
		Kingdom attacker = challenger.getKingdom();
		Kingdom defender = e.getDefender();
		attacker.addInvasionLog(defender, challenger, false, land);
		defender.addInvasionLog(defender, challenger, true, land);
		
		
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
