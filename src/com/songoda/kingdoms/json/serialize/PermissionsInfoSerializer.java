package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.PermissionsInfo;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class PermissionsInfoSerializer implements Serializer<PermissionsInfo> {

	@Override
	public JsonElement serialize(PermissionsInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("nexus",obj.getNexus().getRank());
		json.addProperty("claim",obj.getClaim().getRank());
		json.addProperty("unclaim",obj.getUnclaim().getRank());
		json.addProperty("invade",obj.getInvade().getRank());
		json.addProperty("ally",obj.getAlly().getRank());
		json.addProperty("turret",obj.getTurret().getRank());
		json.addProperty("sethome",obj.getSethome().getRank());
		json.addProperty("chest",obj.getChest().getRank());
		json.addProperty("openallchest",obj.getOpenallchest().getRank());
		json.addProperty("invite",obj.getInvite().getRank());
		json.addProperty("broad",obj.getBroad().getRank());
		json.addProperty("structures",obj.getStructures().getRank());
		json.addProperty("nexusbuild",obj.getBuildInNexus().getRank());
		json.addProperty("overrideregulator",obj.getOverrideRegulator().getRank());
		json.addProperty("buyxpbottles",obj.getBuyXpBottles().getRank());
		json.addProperty("usekhome",obj.getUseKHome().getRank());
		json.addProperty("rpconvert",obj.getRPConvert().getRank());
		
		return json;
	}

	@Override
	public PermissionsInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		PermissionsInfo info = new PermissionsInfo();
		
		info.setNexus(Rank.fromValue(json.get("nexus").getAsInt()));
		info.setClaim(Rank.fromValue(json.get("claim").getAsInt()));
		info.setUnclaim(Rank.fromValue(json.get("unclaim").getAsInt()));
		info.setInvade(Rank.fromValue(json.get("invade").getAsInt()));
		info.setAlly(Rank.fromValue(json.get("ally").getAsInt()));
		info.setTurret(Rank.fromValue(json.get("turret").getAsInt()));
		info.setSethome(Rank.fromValue(json.get("sethome").getAsInt()));
		info.setChest(Rank.fromValue(json.get("chest").getAsInt()));
		info.setOpenallchest(Rank.fromValue(json.get("openallchest").getAsInt()));
		info.setInvite(Rank.fromValue(json.get("invite").getAsInt()));
		info.setBroad(Rank.fromValue(json.get("broad").getAsInt()));
		if(json.get("structures") != null){
			info.setStructures(Rank.fromValue(json.get("structures").getAsInt()));
		}else{
			info.setStructures(Rank.MODS);
			json.addProperty("structures",info.getStructures().getRank());
		}
		if(json.get("nexusbuild") != null){
			info.setBuildInNexus(Rank.fromValue(json.get("nexusbuild").getAsInt()));
		}else{
			info.setBuildInNexus(Rank.ALL);
			json.addProperty("nexusbuild",info.getBuildInNexus().getRank());
		}
		if(json.get("overrideregulator") != null){
			info.setOverrideRegulator(Rank.fromValue(json.get("overrideregulator").getAsInt()));
		}else{
			info.setOverrideRegulator(Rank.KING);
			json.addProperty("overrideregulator",info.getOverrideRegulator().getRank());
		}
		if(json.get("buyxpbottles") != null){
			info.setBuyXpBottles(Rank.fromValue(json.get("buyxpbottles").getAsInt()));
		}else{
			info.setBuyXpBottles(Rank.KING);
			json.addProperty("buyxpbottles",info.getBuyXpBottles().getRank());
		}

		if(json.get("usekhome") != null){
			info.setUseKHome(Rank.fromValue(json.get("usekhome").getAsInt()));
		}else{
			info.setBuyXpBottles(Rank.KING);
			json.addProperty("usekhome",info.getUseKHome().getRank());
		}
		if(json.get("rpconvert") != null){
			info.setRPConvert(Rank.fromValue(json.get("rpconvert").getAsInt()));
		}else{
			info.setBuyXpBottles(Rank.KING);
			json.addProperty("rpconvert",info.getRPConvert().getRank());
		}
		return info;
	}

}
