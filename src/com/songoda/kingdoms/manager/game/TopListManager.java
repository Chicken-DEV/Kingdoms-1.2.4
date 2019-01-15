package com.songoda.kingdoms.manager.game;

import java.util.LinkedHashMap;
import java.util.Map;

import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.events.KingdomDeleteEvent;
import com.songoda.kingdoms.events.KingdomResourcePointChangeEvent;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Sort;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TopListManager extends Manager implements Listener{
	private static final int TOPLIMIT = 10;
	private static Map<String, Integer> topList = new LinkedHashMap<String, Integer>();
	
	protected TopListManager(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onRPChange(KingdomResourcePointChangeEvent e){
		OfflineKingdom kingdom = e.getKingdom();
		topList.put(kingdom.getKingdomName(), kingdom.getResourcepoints());

		topList = Sort.reverse(topList, TOPLIMIT);
	}
	
	public Map<String, Integer> getTopList(){
		topList.clear();
		Map<String, Integer> list = new LinkedHashMap<String, Integer>();
		list.putAll(GameManagement.getKingdomManager().getAllByResourcePointsFromDB());
		//Sort.sort(list).forEach((k,v) -> Kingdoms.logDebug(k+":"+v));
		//Sort.reverse(list).forEach((k,v) -> Kingdoms.logDebug(k+":"+v));
		int i=0;
		for(Map.Entry<String, Integer> entry : Sort.reverse(list).entrySet()){
			if(i == TOPLIMIT) break;

			topList.put(entry.getKey(), entry.getValue());
			i++;
		}
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.putAll(topList);
		
		return map;
	}
	
	@EventHandler
	public void onKingdomsDisband(KingdomDeleteEvent e){
		topList.remove(e.getKingdom().getKingdomName());
	}
	
	@Override
	public void onDisable() {
		topList.clear();
	}

}
