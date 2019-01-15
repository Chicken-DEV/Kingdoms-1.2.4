package com.songoda.kingdoms.constants;

import com.songoda.kingdoms.main.Kingdoms;
import org.bukkit.ChatColor;

public enum Rank {
	KING(0), MODS(2), GENERALS(1), ALL(9999);
	
	private int rank;
	private Rank(int rank){
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}

	public static Rank fromValue(int rank){
		for(Rank r : Rank.values()){
			if(r.rank == rank) return r;
		}
		
		return ALL;
	}
	
	public boolean isHigherOrEqualTo(Rank target){
		if(this.rank <= target.rank) return true;
		
		return false;
	}
	
	public boolean isHigherThan(Rank target){
		if(this.rank < target.rank) return true;
		
		return false;
	}
	
	public static ChatColor colorByRank(Rank rank){
		switch(rank){
		case ALL:
			return ChatColor.GREEN;
		case MODS:
			return ChatColor.BLUE;
		case GENERALS:
			return ChatColor.GOLD;
		case KING:
			return ChatColor.RED;
			default:
				return ChatColor.WHITE;
		}
	}
	
	public static String getFancyMarkByRank(Rank rank){
		switch(rank){
		case ALL:
			return Kingdoms.getLang().getString("Rank_All");
		case MODS:
			return Kingdoms.getLang().getString("Rank_Mods");
		case GENERALS:
			return Kingdoms.getLang().getString("Rank_Generals");
		case KING:
			return Kingdoms.getLang().getString("Rank_King");
			default:
				return "?";
		}
	}
	
	public String getFancyMark(){
		switch(this){
		case ALL:
			return Kingdoms.getLang().getString("Rank_All");
		case MODS:
			return Kingdoms.getLang().getString("Rank_Mods");
		case GENERALS:
			return Kingdoms.getLang().getString("Rank_Generals");
		case KING:
			return Kingdoms.getLang().getString("Rank_King");
			default:
				return "?";
		}
	}
	
	public ChatColor getColor(){
		switch(this){
		case ALL:
			return ChatColor.GREEN;
		case MODS:
			return ChatColor.BLUE;
		case GENERALS:
			return ChatColor.GOLD;
		case KING:
			return ChatColor.RED;
			default:
				return ChatColor.WHITE;
		}
	}
	
	@Override
	public String toString(){
		switch(this){
		case ALL:
			return Kingdoms.getLang().getString("Rank_All_Name");
		case MODS:
			return Kingdoms.getLang().getString("Rank_Mods_Name");
		case GENERALS:
			return Kingdoms.getLang().getString("Rank_Generals_Name");
		case KING:
			return Kingdoms.getLang().getString("Rank_King_Name");
		default:
			return Kingdoms.getLang().getString("Rank_All_Name");
		}
	}
	
	
	public static Rank getFromString(String key){
		if(key.equalsIgnoreCase(Kingdoms.getLang().getString("Rank_All_Name"))){
			return Rank.ALL;
		}else if(key.equalsIgnoreCase(Kingdoms.getLang().getString("Rank_Mods_Name"))){
			return Rank.MODS;
		}else if(key.equalsIgnoreCase(Kingdoms.getLang().getString("Rank_Generals_Name"))){
			return Rank.GENERALS;
		}else if(key.equalsIgnoreCase(Kingdoms.getLang().getString("Rank_King_Name"))){
			return Rank.KING;
		}else return Rank.ALL;
		
	}
}
