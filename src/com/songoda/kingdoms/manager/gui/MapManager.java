package com.songoda.kingdoms.manager.gui;

import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.game.LandManager;
import com.songoda.kingdoms.utils.AsciiCompass;

public class MapManager extends Manager implements Listener{

	protected MapManager(Plugin plugin) {
		super(plugin);
	}

	public void displayMap(Player p, boolean revealStructures) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {

				String com1 = "";
		        String com2 = "";
		        String com3 = "";
		        String[] row = {"", "", "", "", "", "", "", ""};

		        ArrayList<String> compass = AsciiCompass.getAsciiCompass(AsciiCompass.getCardinalDirection(p), ChatColor.YELLOW, ChatColor.AQUA + "");
		       
		        com1 = compass.get(0);
		        com2 = compass.get(1);
		        com3 = compass.get(2);

		//com1 = "\\W/";
		//com2 = "N+S";
		//com3 = "/E\\";
		String cck = ChatColor.AQUA + "Unoccupied";
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
		
		SimpleChunkLocation chunk = new SimpleChunkLocation(p.getLocation().getChunk());
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		
		if (land.getOwner() != null) {
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());

			if(land.getOwnerUUID().equals(kp.getKingdomUuid())){
				cck = ChatColor.GREEN + kingdom.getKingdomName();
	/*			cck = ChatColor.LIGHT_PURPLE + "â˜¢";powercore
				cck = ChatColor.LIGHT_PURPLE + "Ç¾";outpost*/
			}else if(kingdom.isAllianceWith(kp.getKingdom())){
				cck = ChatColor.LIGHT_PURPLE + kingdom.getKingdomName();
				
			}else if(kingdom.isEnemyWith(kp.getKingdom())){
				cck = ChatColor.RED + kingdom.getKingdomName();
			}else{
				cck = ChatColor.GRAY + kingdom.getKingdomName();
			}
		}
		//p.sendMessage(ChatColor.AQUA + "============[" + cck + ChatColor.AQUA + "]============");
		//p.sendMessage(ChatColor.GREEN
		//		+ Kingdoms.getLang().getString("Map_Your_Kingdom) + "                 " + ChatColor.GRAY
		//		+ Kingdoms.getLang().getString("Map_Unidentified));
		//p.sendMessage(ChatColor.RED + Kingdoms.getLang().getString("Map_Enemies)
		//		+ "   " + ChatColor.AQUA + Kingdoms.getLang().getString("Map_Unoccupied));
		//p.sendMessage(ChatColor.LIGHT_PURPLE + Kingdoms.getLang().getString("Map_Allies)
		//				+ "      " + ChatColor.WHITE + Kingdoms.getLang().getString("Map_You));
		// North: -Z
		// South: +Z
		// East: +X
		// West: -X
		int orix = chunk.getX();
		int oriz = chunk.getZ();
		for (int xc = 0; xc < 8; xc++) {
			int x = xc - 4;
			for (int zc = 0; zc <= 24; zc++) {
				int z = zc - 12;
				SimpleChunkLocation schunk = new SimpleChunkLocation(chunk.getWorld(), orix + x, oriz + z);
				String schunkcolor = mapIdentifyChunk(schunk, kp, revealStructures);
				if (x == 0 && z == 0) {
					schunkcolor = ChatColor.WHITE + "▣";
				}

				row[xc] += schunkcolor;
				if (xc == 0 && zc == 24) {
					row[xc] = row[xc] + ChatColor.LIGHT_PURPLE + "   ===========Key============";
				}
				if (xc == 1 && zc == 24) {
					row[xc] = row[xc] + ChatColor.LIGHT_PURPLE + "   ▣ = "
							+ Kingdoms.getLang().getString("Map_You", kp.getLang()) + "                 □ = "
							+ Kingdoms.getLang().getString("Map_NexusChunk", kp.getLang());
				}
				if (xc == 2 && zc == 24) {
					row[xc] = row[xc] + ChatColor.LIGHT_PURPLE + "   ▥ = "
							+ Kingdoms.getLang().getString("Map_Powercell", kp.getLang()) + " ▤ = "
							+ Kingdoms.getLang().getString("Map_Outpost", kp.getLang());
				}
				
				if (xc == 3 && zc == 24) {
					row[xc] = row[xc] + "   " + ChatColor.WHITE + com1;
				}
				if (xc == 4 && zc == 24) {
					row[xc] = row[xc] + "   " + ChatColor.WHITE + com2 + ChatColor.AQUA + "      [" + cck + ChatColor.AQUA + "]";
				}
				if (xc == 5 && zc == 24) {
					row[xc] = row[xc] + "   " + ChatColor.WHITE + com3;
				}
				
				if(xc == 6 && zc == 24){
					row[xc] = row[xc] + "   " + ChatColor.GREEN
							+ Kingdoms.getLang().getString("Map_Your_Kingdom", kp.getLang()) + "   "
							+ ChatColor.WHITE + Kingdoms.getLang().getString("Map_You", kp.getLang());
				}
				if(xc == 7 && zc == 24){
					row[xc] = row[xc] + "   " + ChatColor.RED + Kingdoms.getLang().getString("Map_Enemies", kp.getLang())
							+ "   " + ChatColor.AQUA + Kingdoms.getLang().getString("Map_Unoccupied", kp.getLang());
				}
				if(xc == 8 && zc == 24){
					row[xc] = row[xc] + "   " + ChatColor.LIGHT_PURPLE + Kingdoms.getLang().getString("Map_Allies", kp.getLang())
							+ "   " + ChatColor.GRAY + Kingdoms.getLang().getString("Map_Unidentified", kp.getLang());
				}

			}
			p.sendMessage(row[xc]);
		}

		p.sendMessage(ChatColor.AQUA + "=======================================");
			}
		});
	}

	private String mapIdentifyChunk(SimpleChunkLocation chunk, KingdomPlayer kp, boolean revealStructures) {
		String cck = ChatColor.AQUA+"▩";
		
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land == null) return cck;
		
		if(land.getOwnerUUID() == null) return cck;
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		
		if(kingdom == null){
			if(land.getOwnerUUID() == null){
				return cck;
			}
			else{
				return cck;
			}
		}
		
		if(kp.getKingdom() == null){//player has no kingdom so other kingdom are all gray
			cck = ChatColor.GRAY + "▩";
		}else if(kp.getKingdom().equals(kingdom)){//own kingdom
			cck = ChatColor.GREEN + "▩";//â™œ
			if(land.getStructure() != null){
				if (land.getStructure().getType() == StructureType.NEXUS) {
					cck = ChatColor.GREEN + "□";
				}else if(land.getStructure().getType() == StructureType.OUTPOST){
					cck = ChatColor.GREEN + "▤";
				}else if(land.getStructure().getType() == StructureType.POWERCELL){
					cck = ChatColor.GREEN + "▥";
				}
			}
		}else if(kp.getKingdom().isAllianceWith(kingdom)){//ally
			cck = ChatColor.LIGHT_PURPLE + "▩";
			if(land.getStructure() != null && revealStructures){
				if (land.getStructure().getType() == StructureType.NEXUS) {
					cck = ChatColor.LIGHT_PURPLE + "□";
				}else if(land.getStructure().getType() == StructureType.OUTPOST){
					cck = ChatColor.LIGHT_PURPLE + "▤";
				}else if(land.getStructure().getType() == StructureType.POWERCELL){
					cck = ChatColor.LIGHT_PURPLE + "▥";
				}
			}
		}else if(kp.getKingdom().isEnemyWith(kingdom)){//enemy
			cck = ChatColor.RED + "▩";
			if(land.getStructure() != null && revealStructures){
				if (land.getStructure().getType() == StructureType.NEXUS) {
					cck = ChatColor.RED + "□";
				}else if(land.getStructure().getType() == StructureType.OUTPOST){
					cck = ChatColor.RED + "▤";
				}else if(land.getStructure().getType() == StructureType.POWERCELL){
					cck = ChatColor.RED + "▥";
				}
			}
		}else{//neither ally nor enemy
			cck = ChatColor.GRAY + "▩";
			if(land.getStructure() != null && revealStructures){
				if (land.getStructure().getType() == StructureType.NEXUS) {
					cck = ChatColor.GRAY + "□";
				}else if(land.getStructure().getType() == StructureType.OUTPOST){
					cck = ChatColor.GRAY + "▤";
				}else if(land.getStructure().getType() == StructureType.POWERCELL){
					cck = ChatColor.GRAY + "▥";
				}
			}
		}

		return cck;
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

}
