package com.songoda.kingdoms.json.serialize;

import java.lang.reflect.Type;

import com.songoda.kingdoms.constants.kingdom.TurretUpgradeInfo;
import com.songoda.kingdoms.database.Serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class TurretUpgradeInfoSerializer implements Serializer<TurretUpgradeInfo>{

	@Override
	public JsonElement serialize(TurretUpgradeInfo obj, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
//		boolean simplifiedModel = false;
//		boolean flurry = false;
//		boolean concentratedBlast = false;
//		boolean virulentPlague = false;
//		boolean improvedHeal = false;
//		boolean voodoo = false;
//		boolean finalService = false;
//		boolean hellstorm = false;
//		boolean unrelentingGaze = false;
		
		json.addProperty("simplifiedModel",obj.isSimplifiedModel());
		json.addProperty("flurry",obj.isFlurry());
		json.addProperty("concentratedBlast",obj.isConcentratedBlast());
		json.addProperty("virulentPlague",obj.isVirulentPlague());
		json.addProperty("improvedHeal",obj.isImprovedHeal());
		json.addProperty("voodoo",obj.isVoodoo());
		json.addProperty("finalService",obj.isFinalService());
		json.addProperty("hellstorm",obj.isHellstorm());
		json.addProperty("unrelentingGaze",obj.isUnrelentingGaze());
		
		return json;
	}

	@Override
	public TurretUpgradeInfo deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject json = (JsonObject) obj;
		TurretUpgradeInfo info = new TurretUpgradeInfo();
		
		info.setSimplifiedModel(json.get("simplifiedModel").getAsBoolean());
		info.setFlurry(json.get("flurry").getAsBoolean());
		info.setConcentratedBlast(json.get("concentratedBlast").getAsBoolean());
		info.setVirulentPlague(json.get("virulentPlague").getAsBoolean());
		info.setImprovedHeal(json.get("improvedHeal").getAsBoolean());
		info.setVoodoo(json.get("voodoo").getAsBoolean());
		info.setFinalService(json.get("finalService").getAsBoolean());
		info.setHellstorm(json.get("hellstorm").getAsBoolean());
		info.setUnrelentingGaze(json.get("unrelentingGaze").getAsBoolean());
		
		return info;
	}

}
