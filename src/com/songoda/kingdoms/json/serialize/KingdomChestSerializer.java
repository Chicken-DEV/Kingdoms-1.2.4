package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.KingdomChest;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class KingdomChestSerializer implements Serializer<KingdomChest> {

	@Override
	public JsonElement serialize(KingdomChest obj, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(KingdomChest.serializeChest(obj));
	}

	@Override
	public KingdomChest deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		String ser = obj.getAsString();
		return KingdomChest.deserializeChest(ser);
	}

}
