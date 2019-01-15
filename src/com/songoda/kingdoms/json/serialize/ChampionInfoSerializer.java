package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.ChampionInfo;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class ChampionInfoSerializer implements Serializer<ChampionInfo> {

	@Override
	public JsonElement serialize(ChampionInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("health", obj.getHealth());
		json.addProperty("damage", obj.getDamage());
		json.addProperty("specials", obj.getSpecials());
		json.addProperty("speed", obj.getSpeed());
		json.addProperty("thor", obj.getThor());
		json.addProperty("resist", obj.getResist());
		json.addProperty("tier", obj.getTier());
		json.addProperty("grab", obj.getGrab());
		json.addProperty("summon", obj.getSummon());
		json.addProperty("damagecap", obj.getDamagecap());
		json.addProperty("plow", obj.getPlow());
		json.addProperty("strength", obj.getStrength());
		json.addProperty("armor", obj.getArmor());
		json.addProperty("reinforcements", obj.getReinforcements());
		json.addProperty("mimic", obj.getMimic());
		json.addProperty("weapon", obj.getWeapon());
		json.addProperty("drag", obj.getDrag());
		json.addProperty("mock", obj.getMock());
		json.addProperty("duel", obj.getDuel());
		json.addProperty("focus", obj.getFocus());
		json.addProperty("aqua", obj.getAqua());
		json.addProperty("determination", obj.getDetermination());
		
		return json;
	}

	@Override
	public ChampionInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		ChampionInfo info = new ChampionInfo();
		
		info.setHealth(json.get("health").getAsInt());
		info.setDamage(json.get("damage").getAsInt());
		info.setSpecials(json.get("specials").getAsInt());
		info.setSpeed(json.get("speed").getAsInt());
		info.setThor(json.get("thor").getAsInt());
		info.setResist(json.get("resist").getAsInt());
		info.setTier(json.get("tier").getAsInt());
		info.setGrab(json.get("grab").getAsInt());
		info.setSummon(json.get("summon").getAsInt());
		info.setDamagecap(json.get("damagecap").getAsInt());
		info.setPlow(json.get("plow").getAsInt());
		info.setStrength(json.get("strength").getAsInt());
		info.setArmor(json.get("armor").getAsInt());
		info.setReinforcements(json.get("reinforcements").getAsInt());
		info.setMimic(json.get("mimic").getAsInt());
		info.setWeapon(json.get("weapon").getAsInt());
		info.setDrag(json.get("drag").getAsInt());
		info.setMock(json.get("mock").getAsInt());
		info.setDuel(json.get("duel").getAsInt());
		info.setFocus(json.get("focus").getAsInt());
		info.setAqua(json.get("aqua").getAsInt());
		if(json.get("determination") != null){
			info.setDetermination(json.get("determination").getAsInt());
		}
		return info;
	}

}
