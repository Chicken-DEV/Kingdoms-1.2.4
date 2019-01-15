package com.songoda.kingdoms.manager.external;

import java.util.ArrayList;
import java.util.List;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.LandManager;
import com.songoda.kingdoms.events.PlayerChangeChunkEvent;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import com.songoda.kingdoms.main.Kingdoms;

public class DynmapManager extends Manager implements Listener{
	public DynmapAPI dynmap;
	
	public DynmapManager(Plugin plugin){
		super(plugin);

		this.dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
			
		MarkerSet set = this.dynmap.getMarkerAPI().getMarkerSet("kingdoms");
	    if(set != null){
	    	set.deleteMarkerSet();
	    }
		
		new BukkitRunnable(){
			@Override
			public void run() {
			    for(SimpleChunkLocation chunk : GameManagement.getLandManager().getAllLandLoc()){
			    	updateClaimMarker(chunk);
			    }
			}
		}.runTaskLater(plugin, 20L);
	}
	
	
	
	private String getChunkName(SimpleChunkLocation chunk){
		return chunk.getWorld()+"@"+chunk.getX()+","+chunk.getZ();
	}
	
	private static double[] toPrimitive(List<Double> array) {
		if (array == null) {
			return null;
		} else if (array.size() == 0) {
			return new double[] {};
		}
		final double[] result = new double[array.size()];
		for (int i = 0; i < array.size(); i++) {
			result[i] = array.get(i).doubleValue();
		}
		return result;
	}
	
	private static final String base = "<div>"
			+ "This land is owned by: <span style=\"font-weight:bold;color:black\">%kingdomName% </span><br>"
			+ "<span style=\"font-weight:italic;color:red\">%king% </span><br>"
			+ "<span style=\"font-weight:italic;color:red\">%membercount% </span><br>"
			+ "<span style=\"font-weight:italic;color:red\">%resourcepoints% </span><br>"
			+ "<span style=\"font-weight:bold;color:black\">Members: </span><br>"
			+ "</div>";
	private String getKingdomDesc(OfflineKingdom offKingdom){
		String desc = base;
	
		desc = desc.replace("%kingdomName%", offKingdom.getKingdomName());
		desc = desc.replace("%king%", "King: " + offKingdom.getKingName());
		desc = desc.replace("%membercount%", "Number of Members: " + offKingdom.getMembersList().size() + "");
		desc = desc.replace("%resourcepoints%", "ResourcePoints: " + offKingdom.getResourcepoints() + "");
		
		if(offKingdom.isOnline()){
			Kingdom kingdom = offKingdom.getKingdom();
			for(KingdomPlayer kp : kingdom.getOnlineMembers()){
				try {
					if (kp == null) continue;
					if (kp.getPlayer() == null) continue;
					desc += "<span style=\"font-weight:italic;color:black\">" + kp.getPlayer().getName() + "</span><br>";
				} catch (NullPointerException e) {
					e.printStackTrace();
					continue;
				}
			}
		}

		return desc;
	}
	
	private String getMarkerName(Chunk c){
		return chunkToString(c);
	}
	
	private String chunkToString(Chunk c){
		return c.getWorld().getName()+"@"+c.getX()+","+c.getZ();
	}
	
	/**
	 * update the mark at Chunk (thread safe)
	 * @param chunk
	 */
	public void updateClaimMarker(SimpleChunkLocation chunk){
		if(chunk == null) return;
		
		new Thread(new DynmapUpdateTask(chunk)).start();
	}
	
	/**
	 * remove mark at Chunk
	 * @param c
	 */
	public void removeClaimMarker(Chunk c) {

		if(c == null) return;
		
		MarkerAPI marker = dynmap.getMarkerAPI();
		MarkerSet set;
		AreaMarker amarker;

		if ((set = marker.getMarkerSet("kingdoms")) == null) {
			return;
		}

		if ((amarker = set.findAreaMarker(getMarkerName(c))) != null) {
			amarker.deleteMarker();
		}
	}
	
	private boolean isLoading = false;
	private class DynmapUpdateTask implements Runnable{
		private SimpleChunkLocation chunk;
		
		public DynmapUpdateTask(SimpleChunkLocation chunk) {
			super();
			this.chunk = chunk;
		}

		@Override
		public void run() {
			try {
				while(isLoading){
					if(count == 10){
						//Kingdoms.logDebug("abort dynmap update (10 secs passed)");
						return;
					}
					
					count++;
					//Kingdoms.logDebug("dynmap update pending");
					Thread.sleep(1L);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			
			isLoading = true;
			update();
			isLoading = false;
		}
		
		private int count = 0;
		public void update(){
			
			MarkerAPI marker = dynmap.getMarkerAPI();
			MarkerSet set;
			AreaMarker amarker;

			if ((set = marker.getMarkerSet("kingdoms")) == null) {
				set = marker.createMarkerSet("kingdoms", "kingdoms", null, true);
			}

			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if (land == null || land.getOwnerUUID() == null) {
				if ((amarker = set.findAreaMarker(getChunkName(chunk))) != null) {
					amarker.deleteMarker();
				}
				return;
			}

			OfflineKingdom kingdom = GameManagement.getKingdomManager().getOfflineKingdom(land.getOwnerUUID());

			List<Double> arrX = new ArrayList<Double>();
			List<Double> arrZ = new ArrayList<Double>();

			arrX.add(((chunk.getX() << 4) | 0) - 0.5);
			arrZ.add(((chunk.getZ() << 4) | 0) - 0.5);

			arrX.add(((chunk.getX() << 4) | 0) - 0.5);
			arrZ.add(((chunk.getZ() << 4) | 15) + 0.5);

			arrX.add(((chunk.getX() << 4) | 15) + 0.5);
			arrZ.add(((chunk.getZ() << 4) | 15) + 0.5);

			arrX.add(((chunk.getX() << 4) | 15) + 0.5);
			arrZ.add(((chunk.getZ() << 4) | 0) - 0.5);

			if ((amarker = set.findAreaMarker(getChunkName(chunk))) == null) {
				amarker = set.createAreaMarker(getChunkName(chunk), land.getOwner(), false, chunk.getWorld(),
						toPrimitive(arrX), toPrimitive(arrZ), true);
			}

				amarker.setLineStyle(0, 0, kingdom.getDynmapColor());
				amarker.setFillStyle(0.4, kingdom.getDynmapColor());
				amarker.setDescription(" " + getKingdomDesc(kingdom));
				amarker.setCornerLocations(toPrimitive(arrX), toPrimitive(arrZ));
			}

		}
	///////////////////////////////////////////////////////////////////////////////
/*	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent e){
		//Bukkit.getLogger().info("chunkLoaded therefore added to queue");
		
	}*/
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkChange(PlayerChangeChunkEvent e){
		
		int radius = 1;
		
		Chunk center = e.getToChunk();
		for(int x = -radius;x <= radius; x++){
			for(int z = -radius; z<=radius; z++){
				SimpleChunkLocation loc = new SimpleChunkLocation(center.getWorld().getName(),
						center.getX() + x, center.getZ() + z);
				new Thread(new DynmapUpdateTask(loc)).start();
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		Chunk center = e.getChunk();
		SimpleChunkLocation loc = new SimpleChunkLocation(center.getWorld().getName(),
				center.getX(), center.getZ());
		new Thread(new DynmapUpdateTask(loc)).start();
	}
	
	/**
	 * add update task to queue for Chunk
	 * @param chunk
	 * @deprecated updateClaimMarker is indeed thread safe
	 */
	public void addMarkerUpdateQueue(SimpleChunkLocation chunk){
		
		new Thread(new DynmapUpdateTask(chunk)).start();
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
}
