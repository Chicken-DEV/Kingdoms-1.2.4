package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.land.Turret;
import com.songoda.kingdoms.constants.TurretType;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class TurretSerializer implements Serializer<Turret> {

	@Override
	public JsonElement serialize(Turret obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.add("type", context.serialize(obj.getType()));
		json.add("loc", context.serialize(obj.getLoc()));
		
		return json;
	}

	@Override
	public Turret deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		TurretType t = context.deserialize(json.get("type"), TurretType.class);
		SimpleLocation loc = context.deserialize(json.get("loc"), SimpleLocation.class);

		return new Turret(loc, t);
	}

}
