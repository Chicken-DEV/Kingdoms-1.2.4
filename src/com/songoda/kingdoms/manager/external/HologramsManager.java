package com.songoda.kingdoms.manager.external;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.songoda.kingdoms.events.StructureBreakEvent;
import com.songoda.kingdoms.events.StructurePlaceEvent;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class HologramsManager extends Manager {
	protected HologramsManager(Plugin plugin) {
		super(plugin);

		if (Kingdoms.getInstance().getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            Kingdoms.logInfo("Holograms Hooked!");
        }
	
	}
	
	@EventHandler
	public void placeEvent(StructurePlaceEvent event){
		String loc = event.getLand().getLoc().toString().replaceAll(" ", "_");
		String structure = event.getStructureType().toString();
		Hologram holo = createHologram(loc+"-"+structure+"-kingdomsholo", event.getLocation().clone().add(0,1,0));
		holo.appendTextLine(event.getStructureType().getTitle());
	}
	@EventHandler
	public void placeEvent(StructureBreakEvent event){
		String loc = event.getLand().getLoc().toString().replaceAll(" ", "_");
		String structure = event.getStructureType().toString();
		Hologram holo = createHologram(loc+"-"+structure+"-kingdomsholo", event.getLocation().clone().add(0,1,0));
		holo.appendTextLine(event.getStructureType().getTitle());
	}
	
	public Hologram createHologram(String id, Location location) {
	    Hologram hologram = HologramsAPI.createHologram(Kingdoms.getInstance(), location);
	    return hologram;
	}
	
	
	public void addTextLine(Hologram hologram, String text) {
	    hologram.appendTextLine(text);
	}
	
	public void deleteHologram(Hologram hologram) {
	    hologram.delete();
	}

	@Override
	public void onDisable() {
	    for (Hologram h : HologramsAPI.getHolograms(Kingdoms.getInstance())) {
	        h.delete();
        }
	}


}
