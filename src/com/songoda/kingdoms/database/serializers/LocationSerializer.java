package com.songoda.kingdoms.database.serializers;

import com.google.gson.*;
import com.songoda.kingdoms.database.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public JsonElement serialize(Location arg0, Type arg1, JsonSerializationContext arg2) {
        JsonObject json = new JsonObject();

        // return empty if world does not exists
        if (arg0.getWorld() == null) {
            return json;
        }

        json.addProperty("world", arg0.getWorld().getName());
        json.addProperty("x", arg0.getX());
        json.addProperty("y", arg0.getY());
        json.addProperty("z", arg0.getZ());
        json.addProperty("pitch", arg0.getPitch());
        json.addProperty("yaw", arg0.getYaw());

        return json;
    }

    @Override
    public Location deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        JsonObject json = (JsonObject) arg0;

        JsonElement worldElem = json.get("world");
        if (worldElem == null)
            return null;

        String worldName = worldElem.getAsString();
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            return null;

        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        float pitch = json.get("pitch") == null ? 0.0F : json.get("pitch").getAsFloat();
        float yaw = json.get("yaw") == null ? 0.0F : json.get("yaw").getAsFloat();

        return new Location(world, x, y, z, pitch, yaw);
    }

}