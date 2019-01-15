package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class SimpleChunkLocationSerializer implements Serializer<SimpleChunkLocation> {

	@Override
	public JsonElement serialize(SimpleChunkLocation obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("world", obj.getWorld());
		json.addProperty("x", obj.getX());
		json.addProperty("z", obj.getZ());
		
		return json;
	}

	@Override
	public SimpleChunkLocation deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		
		return new SimpleChunkLocation(json.get("world").getAsString(), json.get("x").getAsInt(), json.get("z").getAsInt());
	}

}
