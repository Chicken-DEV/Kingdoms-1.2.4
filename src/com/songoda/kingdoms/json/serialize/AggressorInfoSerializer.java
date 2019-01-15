package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.AggressorInfo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import com.songoda.kingdoms.database.Serializer;

public class AggressorInfoSerializer implements Serializer<AggressorInfo> {

	@Override
	public JsonElement serialize(AggressorInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("health", obj.getHealth());
		json.addProperty("damage", obj.getDamage());
		json.addProperty("speed", obj.getSpeed());
		json.addProperty("damagecap", obj.getDamagecap());
		json.addProperty("antiknockback", obj.getAntiknockback());
		
		return json;
	}

	@Override
	public AggressorInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		AggressorInfo info = new AggressorInfo();
		
		info.setHealth(json.get("health").getAsInt());
		info.setDamage(json.get("damage").getAsInt());
		info.setSpeed(json.get("speed").getAsInt());
		info.setDamagecap(json.get("damagecap").getAsInt());
		info.setAntiknockback(json.get("antiknockback").getAsInt());
		
		return info;
	}

}
