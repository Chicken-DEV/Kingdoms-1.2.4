package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import com.songoda.kingdoms.constants.StructureType;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.database.Serializer;
import com.songoda.kingdoms.constants.land.Extractor;
import com.songoda.kingdoms.constants.land.Regulator;
import com.songoda.kingdoms.constants.land.SiegeEngine;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.land.Structure;
import com.songoda.kingdoms.main.Kingdoms;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class StructureSerializer implements Serializer<Structure> {

	@Override
	public JsonElement serialize(Structure obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.add("type", context.serialize(obj.getType()));
		json.add("loc", context.serialize(obj.getLoc()));
		if(obj.getType() == StructureType.EXTRACTOR){
			json.add("time", context.serialize(((Extractor) obj).getTimeToNextCollection()));
		}
		if(obj.getType() == StructureType.SIEGEENGINE){
			json.add("siegecd", context.serialize(((SiegeEngine) obj).fireCooldown));
		}
		if(obj.getType() == StructureType.REGULATOR){
			json.add("whoCanBuild", context.serialize(((Regulator) obj).getWhoCanBuild()));
			json.add("whoCanInteract", context.serialize(((Regulator) obj).getWhoCanInteract()));
			json.add("allowMonsterSpawning", context.serialize(((Regulator) obj).isAllowMonsterSpawning()));
			json.add("allowAnimalSpawning", context.serialize(((Regulator) obj).isAllowAnimalSpawning()));
		}
		return json;
	}

	@Override
	public Structure deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		StructureType t = context.deserialize(json.get("type"), StructureType.class);
		SimpleLocation loc = context.deserialize(json.get("loc"), SimpleLocation.class);
		if(t == StructureType.EXTRACTOR){
			long time = 0;
			if(json.get("time") != null){
				time = context.deserialize(json.get("time"), Long.class);
			}else{
				time = System.currentTimeMillis();
			}
			return new Extractor(loc, t, time);
		}
		if(t == StructureType.SIEGEENGINE){
			long time = 0;
			if(json.get("siegecd") != null){
				time = context.deserialize(json.get("siegecd"), Long.class);
			}else{
				time = System.currentTimeMillis();
			}
			return new SiegeEngine(loc, t, time);
		}
		if(t == StructureType.REGULATOR){
			ArrayList<UUID>  whoCanBuild = new ArrayList<UUID>();
			ArrayList<UUID>  whoCanInteract = new ArrayList<UUID>();
			boolean allowMonsterSpawning = false;
			boolean allowAnimalSpawning = false;
			if(json.get("whoCanBuild") != null){
				ArrayList<String> list = context.deserialize(json.get("whoCanBuild"), ArrayList.class);
				for(String s:list){
					if(!whoCanBuild.contains(UUID.fromString(s)))whoCanBuild.add(UUID.fromString(s));
				}
			}
			if(json.get("whoCanInteract") != null){
				ArrayList<String> list = context.deserialize(json.get("whoCanInteract"), ArrayList.class);
				for(String s:list){
					if(!whoCanInteract.contains(UUID.fromString(s)))whoCanInteract.add(UUID.fromString(s));
				}
			}
			if(json.get("allowMonsterSpawning") != null){
				allowMonsterSpawning = context.deserialize(json.get("allowMonsterSpawning"), Boolean.class);
				if(!Config.getConfig().getBoolean("regulator.allow-toggling-monster-spawn")) allowMonsterSpawning = true;
			}
			if(json.get("allowAnimalSpawning") != null){
				allowAnimalSpawning = context.deserialize(json.get("allowAnimalSpawning"), Boolean.class);
				if(!Config.getConfig().getBoolean("regulator.allow-toggling-animal-spawn")) allowAnimalSpawning = true;
			}
			return new Regulator(loc, t, whoCanBuild, 
					whoCanInteract,
					allowMonsterSpawning,
					allowAnimalSpawning);
		}
		return new Structure(loc, t);
	}

}
