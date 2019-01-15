package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.songoda.kingdoms.constants.land.KChestSign;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;

public class KChestSignSerializer implements Serializer<KChestSign> {

	@Override
	public JsonElement serialize(KChestSign obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add("loc", context.serialize(obj.getLoc()));
		json.add("owner", context.serialize(obj.getOwner()));
		json.add("owners", context.serialize(obj.getOwners()));
		return json;
	}

	private static Type listType = new TypeToken<ArrayList<UUID>>(){}.getType();
	@Override
	public KChestSign deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;

		SimpleLocation loc = context.deserialize(json.get("loc"), SimpleLocation.class);
		UUID owner = context.deserialize(json.get("owner"), UUID.class);
		List<UUID> owners = context.deserialize(json.get("owners"), listType);
		return new KChestSign(loc, owner, owners);
	}

}
