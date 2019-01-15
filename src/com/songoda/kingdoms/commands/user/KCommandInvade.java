package com.songoda.kingdoms.commands.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;


import com.songoda.kingdoms.constants.kingdom.BotKingdom;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.events.KingdomInvadeEvent;
import com.songoda.kingdoms.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class KCommandInvade extends KCommandBase {

	@Override
	public int getArgsAmount() {
		return 0;
	}

	@Override
	public String[] getUsage() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean canExecute(CommandSender sender){
		if(sender.isOp()){
			return true;
		}
		if(sender.hasPermission("kingdoms.player")){
			return true;
		}
		if(sender.hasPermission("kingdoms.invade")){
			return true;
		}

		return false;
	}

	@Override
	public void executeCommandConsole(Queue<String> args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeCommandOP(Player op, Queue<String> args) {
		executeCommandUser(op, args);
	}

	@Override
	public void executeCommandUser(Player user, Queue<String> args) {

		final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
		final Land land = GameManagement.getLandManager().getOrLoadLand(kp.getLoc());
		UUID owner = land.getOwnerUUID();
		Kingdom target;
		target = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		Kingdom kingdom = kp.getKingdom();
			if (kingdom == null) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
				return;
			}
			if (kingdom.isNeutral()) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Neutral_Cannot_Invade", kp.getLang()));
				return;
			}
			if (!kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getInvade())) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Rank_Too_Low", kp.getLang()).replaceAll("%rank%", kingdom.getPermissionsInfo().getInvade().toString()));
				return;
			}

			if (!Kingdoms.getManagers().getMasswarManager().isMassWarOn() &&
					Config.getConfig().getBoolean("canOnlyInvadeDuringMasswar")) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Can_Only_Invade_During_Mass_War", kp.getLang()));
				return;
			}
			if (owner == null) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_No_Occupant_Error", kp.getLang()));
				return;
			}
			if (owner.equals(kingdom.getKingdomUuid())) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Land_Ownded_Error", kp.getLang()));
				return;
			}
			if (target == null) return;
			if (target.getOnlineMembers().size() < Config.getConfig().getInt("minimum-members-online-to-be-invaded") &&
					target.getMembersList().size() >= Config.getConfig().getInt("minimum-members-online-to-be-invaded") &&
					Config.getConfig().getInt("minimum-members-online-to-be-invaded") > 0) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Kingdom_Owner_Not_Enough_Online", kp.getLang()).replaceAll("%number%", "" + Config.getConfig().getInt("minimum-members-online-to-be-invaded")));
				return;
			}
			final SimpleLocation loc = new SimpleLocation(kp.getPlayer().getLocation());
			if (target.isShieldUp()) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Cannot_Invade_Shielded", kp.getLang()).replaceAll("%time%", "" +  TimeUtils.parseTimeMillis(target.getTimeLeft(OfflineKingdom.SHIELD))));
				return;
			}
			if(!(target instanceof BotKingdom)){
				if (Kingdoms.getManagers().getKingdomManager().getOfflineKingdom(owner).isNeutral()) {
					kp.sendMessage(Kingdoms.getLang().getString("Misc_Neutral_Cannot_Be_Invaded", kp.getLang()));
					return;
				}
			}
			if ((kingdom.getLand() >= (Config.getConfig().getInt("land-per-member") * kingdom.getMembersList().size() + kingdom.getExtraLandClaims()))
					&& !Config.getConfig().getStringList("infinite-claim-worlds").contains(loc.getWorld())) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Members_Needed", kp.getLang()).replaceAll("%amount%", "" + (Config.getConfig().getInt("land-per-member") * kingdom.getMembersList().size())).replaceAll("%members%", "" + kingdom.getMembersList().size()));
				return;
			}

			if (Config.getConfig().getInt("maximum-land-claims") > 0 && (kingdom.getLand() >=Config.getConfig().getInt("maximum-land-claims"))
					&& !Config.getConfig().getStringList("infinite-claim-worlds").contains(loc.getWorld())) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Max_Land_Reached", kp.getLang()));
				return;
			}

			SimpleChunkLocation chunk = loc.toSimpleChunk();
			if (!GameManagement.getStructureManager().isInvadeable(kp, chunk)) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Powercell_Present_Error", kp.getLang()));
				return;
			}
			
			if(Kingdoms.getManagers().getChampionManager().isChunkInvaded(chunk)){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Already_Invading_Error", kp.getLang()));
				return;
			}

			if(kp.getFightZone() != null){
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Sender_Already_Invading_Error", kp.getLang()));
				return;
			}

			int cost = Config.getConfig().getInt("invade-cost");
			if (GameManagement.getMasswarManager().isMassWarOn()) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_MassWar_Boost", kp.getLang()));
			} else if (kingdom.getResourcepoints() < cost) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", "" + Config.getConfig().getInt("invade-cost")));
				return;
			}
			if (kingdom.isAllianceWith(target) && target.isAllianceWith(kingdom)) {
				kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Cannot_Ally", kp.getLang()));
				return;
			}
			KingdomInvadeEvent event = new KingdomInvadeEvent(target, kp, chunk);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if(event.isCancelled()) return;
			
			if (kingdom.isShieldUp() && !Cooldown.isInCooldown(kp.getUuid(), "invadeshield")) {
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Shield_Up_Invade_Warning", kp.getLang()).replaceAll("%time%", "" + TimeUtils.parseTimeMillis(kingdom.getTimeLeft(OfflineKingdom.SHIELD))));
				Cooldown cd = new Cooldown(kp.getUuid(), "invadeshield", 5);
				cd.start();
				return;
			}
			if (kingdom.isShieldUp()) {
				kingdom.removeShield();
			}
			
			kingdom.setResourcepoints(kingdom.getResourcepoints() - cost);
			
			kp.sendMessage(Kingdoms.getLang().getString("Command_Invade_Commence", kp.getLang()));
			kingdom.setHasInvaded(true);
			kingdom.setNeutral(false);
			//Kingdoms.getLang().addString(land.getLoc().toString());
			target.sendAnnouncement(null, Kingdoms.getLang().getString("Command_Invade_Warning", kp.getLang()), true);

			GameManagement.getChampionManager().startFight(loc, kp);
			if (!kp.getPlayer().isOp()) kp.getPlayer().setAllowFlight(false);
		
	}

	@Override
	public String getDescription(String language) {
		return Kingdoms.getLang().getString("Command_Help_Invade", language);
	}

	private static class Cooldown {

	    private static Map<String, Cooldown> cooldowns = new HashMap<String, Cooldown>();
	    private long start;
	    private final int timeInSeconds;
	    private final UUID id;
	    private final String cooldownName;

	    public Cooldown(UUID id, String cooldownName, int timeInSeconds){
	        this.id = id;
	        this.cooldownName = cooldownName;
	        this.timeInSeconds = timeInSeconds;
	    }

	    public static boolean isInCooldown(UUID id, String cooldownName){
	        if(getTimeLeft(id, cooldownName)>=1){
	            return true;
	        } else {
	            stop(id, cooldownName);
	            return false;
	        }
	    }

	    private static void stop(UUID id, String cooldownName){
	        Cooldown.cooldowns.remove(id+cooldownName);
	    }

	    private static Cooldown getCooldown(UUID id, String cooldownName){
	        return cooldowns.get(id.toString()+cooldownName);
	    }

	    public static int getTimeLeft(UUID id, String cooldownName){
	        Cooldown cooldown = getCooldown(id, cooldownName);
	        int f = -1;
	        if(cooldown!=null){
	            long now = System.currentTimeMillis();
	            long cooldownTime = cooldown.start;
	            int totalTime = cooldown.timeInSeconds;
	            int r = (int) (now - cooldownTime) / 1000;
	            f = (r - totalTime) * (-1);




	        }


	        return f;
	    }

	    public void start(){
	        this.start = System.currentTimeMillis();
	        cooldowns.put(this.id.toString()+this.cooldownName, this);
	    }

	}

}
