package com.songoda.kingdoms.manager.external;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.events.KingdomMemberJoinEvent;
import com.songoda.kingdoms.events.KingdomMemberLeaveEvent;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import com.github.games647.scoreboardstats.variables.ReplaceEvent;

public class ScoreboardManager extends Manager implements Listener{
	com.github.games647.scoreboardstats.ScoreboardStats scoreboardStats;
	protected ScoreboardManager(Plugin plugin) {
		super(plugin);
		scoreboardStats = (com.github.games647.scoreboardstats.ScoreboardStats) plugin.getServer().getPluginManager().getPlugin("ScoreboardStats");
		Method method = getMethod();
		if(method == null){
			Kingdoms.logInfo("Couldn't initialize ScoreboardStats.");
			Kingdoms.logInfo("This feature will be disabled.");
			return;
		}
		
		if(!isNewVersion){
			try {
				method.invoke(scoreboardStats.getReplaceManager(),
						new com.github.games647.scoreboardstats.variables.VariableReplacer() {
							@Override
							public void onReplace(Player p, String var, ReplaceEvent e) {
								KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
								
								if (kp.getKingdom() == null)
									return;

								Kingdom kingdom = kp.getKingdom();
								switch (var) {
								case "rp":
									e.setScore(kingdom.getResourcepoints());
									break;
								case "membercount":
									e.setScore(kingdom.getMembersList().size());
									break;
								case "memberonlinecount":
									e.setScore(kingdom.getOnlineMembers().size());
									break;
								case "land":
									e.setScore(kingdom.getLand());
									break;
								}

							}
				}, plugin, new String[]{"rp", "membercount", "memberonlinecount", "land"});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			try {
				method.invoke(scoreboardStats.getReplaceManager(),
						new com.github.games647.scoreboardstats.variables.VariableReplaceAdapter(plugin, "rp",
								"membercount", "memberonlinecount", "land") {
							@Override
							public void onReplace(Player p, String var, ReplaceEvent e) {
								KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
								if(kp.getKingdom() == null) return;
								
								Kingdom kingdom = kp.getKingdom();
								switch (var) {
								case "rp":
									e.setScore(kingdom.getResourcepoints());
									break;
								case "membercount":
									e.setScore(kingdom.getMembersList().size());
									break;
								case "memberonlinecount":
									e.setScore(kingdom.getOnlineMembers().size());
									break;
								case "land":
									e.setScore(kingdom.getLand());
									break;
								}
							}
						});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isNewVersion = false;
	private Method getMethod(){
		Class<?> clazz = scoreboardStats.getReplaceManager().getClass();
		Method method = null;
		
		try {
			method = clazz.getMethod("register",
					com.github.games647.scoreboardstats.variables.VariableReplacer.class,
					Plugin.class,
					String[].class);
			method.setAccessible(true);
		} catch (NoSuchMethodException e) {
			isNewVersion = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			if(isNewVersion){
				method = clazz.getMethod("register",
						com.github.games647.scoreboardstats.variables.VariableReplaceAdapter.class);
				method.setAccessible(true);
			}
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return method;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerJoin(PlayerJoinEvent event) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(event.getPlayer());
		if(kp == null){
			return;
		}
		if (kp.getKingdom() == null)
			return;

		Kingdom kingdom = kp.getKingdom();

		scoreboardStats.getReplaceManager().updateScore(event.getPlayer(), 
				"rp", kingdom.getResourcepoints());
		scoreboardStats.getReplaceManager().updateScore(event.getPlayer(),
				"membercount", kingdom.getMembersList().size());
		scoreboardStats.getReplaceManager().updateScore(event.getPlayer(),
				"memberonlinecount", kingdom.getOnlineMembers().size());
		scoreboardStats.getReplaceManager().updateScore(event.getPlayer(),
				"land", kingdom.getLand());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onKingdomMemberJoin(KingdomMemberJoinEvent e) {

		OfflineKingdomPlayer okp = e.getKp();
		if(!(okp instanceof KingdomPlayer)) return;
		
		KingdomPlayer kp = okp.getKingdomPlayer();
		if (kp.getKingdom() == null)
			return;

		Kingdom kingdom = e.getKingdom();

		scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(), 
				"rp", kingdom.getResourcepoints());
		scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
				"membercount", kingdom.getMembersList().size());
		scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
				"memberonlinecount", kingdom.getOnlineMembers().size());
		scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
				"land",	kingdom.getLand());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onKingdomMemberQuit(KingdomMemberLeaveEvent e) {
		if(e.getKp() instanceof KingdomPlayer){
			KingdomPlayer kp = (KingdomPlayer) e.getKp();
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"rp", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"membercount", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"memberonlinecount", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"land", 0);
		}
	}
	
	public void updateScoreboard(KingdomPlayer kp){
		if(kp.getKingdom() == null){
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"rp", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"membercount", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"memberonlinecount", 0);
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"land", 0);
		}else{
			Kingdom kingdom = kp.getKingdom();
			
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(), 
					"rp", kingdom.getResourcepoints());
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"membercount", kingdom.getMembersList().size());
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"memberonlinecount", kingdom.getOnlineMembers().size());
			scoreboardStats.getReplaceManager().updateScore(kp.getPlayer(),
					"land", kingdom.getLand());
		}

	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
