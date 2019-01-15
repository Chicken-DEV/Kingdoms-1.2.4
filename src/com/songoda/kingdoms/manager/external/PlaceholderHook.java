package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

public class PlaceholderHook extends me.clip.placeholderapi.external.EZPlaceholderHook {

	public PlaceholderHook(Kingdoms plugin) {
		super(plugin, "kingdoms");
	}
	
		@Override
		public String onPlaceholderRequest(Player p, String identifier) {
						if(p == null) return "";
						KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);

						if (identifier.equalsIgnoreCase("haskingdom")) {
							return "" + (kp.getKingdom() != null);
						} else if (identifier.equalsIgnoreCase("isneutral") || identifier.equalsIgnoreCase("neutral")) {
							if (kp.getKingdom() == null)
								return "0";
							return kp.getKingdom().isNeutral() + "";
						} else if (identifier.equalsIgnoreCase("resourcepoints") || identifier.equalsIgnoreCase("rp")) {
							if (kp.getKingdom() == null)
								return "0";
							return kp.getKingdom().getResourcepoints() + "";
						} else if (identifier.equalsIgnoreCase("kingdom")) {
							if (kp.getKingdom() == null)
								return Kingdoms.getLang().getString("PlaceHolders_None");
							return kp.getKingdom().getKingdomName();
						} else if (identifier.equalsIgnoreCase("land")) {
							if (kp.getKingdom() == null)
								return "0";
							return String.valueOf(kp.getKingdom().getLand());
						} else if (identifier.equalsIgnoreCase("members")) {
							if (kp.getKingdom() == null)
								return "0";
							return String.valueOf(kp.getKingdom().getMembersList().size());
						} else if (identifier.equalsIgnoreCase("rank")) {
							return String.valueOf(kp.getRank().getFancyMark());
						} else if (identifier.equalsIgnoreCase("onlinemembers")) {
							if (kp.getKingdom() == null)
								return "0";
							return String.valueOf(kp.getKingdom().getOnlineMembers().size());
						} else if (identifier.equalsIgnoreCase("king")) {
							if (kp.getKingdom() == null)
								return Kingdoms.getLang().getString("PlaceHolders_None");
							if (Bukkit.getOfflinePlayer(kp.getKingdom().getKing()) == null)
								return Kingdoms.getLang().getString("PlaceHolders_None");
							return Bukkit.getOfflinePlayer(kp.getKingdom().getKing()).getName();
						}
						return null;
					
		}



}
