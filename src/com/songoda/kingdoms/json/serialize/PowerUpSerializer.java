package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.PowerUp;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class PowerUpSerializer implements Serializer<PowerUp> {

	@Override
	public JsonElement serialize(PowerUp obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("arrow", obj.getArrowboost());
		json.addProperty("dmgboost", obj.getDmgboost());
		json.addProperty("dmgreduction", obj.getDmgreduction());
		json.addProperty("doubleloot", obj.getDoublelootchance());
		json.addProperty("regen", obj.getRegenboost());
		
		return json;
	}

	@Override
	public PowerUp deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject json = (JsonObject) obj;
		PowerUp pu = new PowerUp();
		
		pu.setArrowboost(json.get("arrow").getAsInt());
		pu.setDmgboost(json.get("dmgboost").getAsInt());
		pu.setDmgreduction(json.get("dmgreduction").getAsInt());
		pu.setDoublelootchance(json.get("doubleloot").getAsInt());
		pu.setRegenboost(json.get("regen").getAsInt());
		
		return pu;
	}

}
