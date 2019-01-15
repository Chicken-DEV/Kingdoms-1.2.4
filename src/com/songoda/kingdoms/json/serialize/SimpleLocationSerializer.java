package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class SimpleLocationSerializer implements Serializer<SimpleLocation> {

	@Override
	public JsonElement serialize(SimpleLocation obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("world", obj.getWorld());
		json.addProperty("x", obj.getX());
		json.addProperty("y", obj.getY());
		json.addProperty("z", obj.getZ());
		return json;
	}

	@Override
	public SimpleLocation deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		return new SimpleLocation(json.get("world").getAsString(), json.get("x").getAsInt(),
				json.get("y").getAsInt(), json.get("z").getAsInt());
	}

}
