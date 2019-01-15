package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.MisupgradeInfo;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class MisupgradeInfoSerializer implements Serializer<MisupgradeInfo>{

	@Override
	public JsonElement serialize(MisupgradeInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		json.addProperty("antitrample",obj.isAntitrample());
		json.addProperty("anticreeper",obj.isAnticreeper());
		json.addProperty("nexusguard",obj.isNexusguard());
		json.addProperty("glory",obj.isGlory());
		json.addProperty("bombshards",obj.isBombshards());
		json.addProperty("psioniccore",obj.isPsioniccore());
		json.addProperty("enabledantitrample",obj.isEnabledantitrample());
		json.addProperty("enabledanticreeper",obj.isEnabledanticreeper());
		json.addProperty("enablednexusguard",obj.isEnablednexusguard());
		json.addProperty("enabledglory",obj.isEnabledglory());
		json.addProperty("enabledbombshards",obj.isEnabledbombshards());
		json.addProperty("enabledpsioniccore",obj.isEnabledpsioniccore());
		
		return json;
	}

	@Override
	public MisupgradeInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		MisupgradeInfo info = new MisupgradeInfo();
		
		info.setAntitrample(json.get("antitrample").getAsBoolean());
		info.setAnticreeper(json.get("anticreeper").getAsBoolean());
		info.setNexusguard(json.get("nexusguard").getAsBoolean());
		info.setGlory(json.get("glory").getAsBoolean());
		info.setBombshards(json.get("bombshards").getAsBoolean());
		info.setPsioniccore(json.get("psioniccore").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnabledantitrample(json.get("enabledantitrample").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnabledanticreeper(json.get("enabledanticreeper").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnablednexusguard(json.get("enablednexusguard").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnabledglory(json.get("enabledglory").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnabledbombshards(json.get("enabledbombshards").getAsBoolean());
		if(json.get("enabledantitrample") != null)info.setEnabledpsioniccore(json.get("enabledpsioniccore").getAsBoolean());
		
		return info;
	}

}
