package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class RankSerializer implements Serializer<Rank> {

	@Override
	public JsonElement serialize(Rank obj, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(obj.getRank());
	}

	@Override
	public Rank deserialize(JsonElement obj, Type type, JsonDeserializationContext context) throws JsonParseException {
		Rank rank = null;
		try{
			rank = Rank.fromValue(obj.getAsInt());
		}catch(Exception e){
			rank = Rank.valueOf(obj.getAsString());
		}
		return rank;
	}

}
