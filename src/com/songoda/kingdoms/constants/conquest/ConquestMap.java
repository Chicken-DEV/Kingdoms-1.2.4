package com.songoda.kingdoms.constants.conquest;

import java.util.ArrayList;

public class ConquestMap {
	
	public String name;
	public ArrayList<ConquestLand> lands = new ArrayList<ConquestLand>();
	
	public ConquestMap(String name){
		this.name = name;
		for(int x = 0; x<=5; x++){
			for(int y = 0; y<=5; y++){
				lands.add(new ConquestLand(this, x, y));
			}
		}
	}
	
	public ConquestMap(String name, ArrayList<ConquestLand> lands){
		this.name = name;
		this.lands = lands;
	}

	public static ConquestLand getLandAt(ConquestMap map, int x, int y){
		
		for(ConquestLand land:map.lands){
			if(land.x == x && land.y == y){
				return land;
			}
		}
		return null;
	}
	
}
