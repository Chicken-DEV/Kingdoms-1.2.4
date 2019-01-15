package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.ArmyInfo;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class ArmyInfoSerializer implements Serializer<ArmyInfo> {

	@Override
	public JsonElement serialize(ArmyInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("endermite", obj.getEndermite());
		json.addProperty("human", obj.getHuman());
		json.addProperty("zombie", obj.getZombie());
		
		return json;
	}

	@Override
	public ArmyInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		ArmyInfo info = new ArmyInfo();
		
		info.setEndermite(json.get("endermite").getAsInt());
		info.setHuman(json.get("human").getAsInt());
		info.setZombie(json.get("zombie").getAsInt());
		
		return info;
	}

}
