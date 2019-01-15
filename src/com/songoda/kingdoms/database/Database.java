package com.songoda.kingdoms.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.songoda.kingdoms.database.serializers.ItemStackArraySerializer;
import com.songoda.kingdoms.database.serializers.ItemStackSerializer;
import com.songoda.kingdoms.database.serializers.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

public abstract class Database<T> {
    public static GsonBuilder builder = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT,Modifier.STATIC)
            .enableComplexMapKeySerialization().serializeNulls().registerTypeAdapter(Location.class,new LocationSerializer()).registerTypeAdapter(ItemStack.class,new ItemStackSerializer()).registerTypeAdapter(ItemStack[].class, new ItemStackArraySerializer());
    public static void registerSerializer(Class<?> clazz, Object object){
        synchronized (builder){
            builder.registerTypeAdapter(clazz, object);
        }
    }
    public abstract T load(String key, T def);
    public abstract void save(String key, T value);
    public abstract boolean has(String key);
    public abstract void clear();
    public abstract Set<String> getKeys();
    private Gson gson = null;
    public String serialize(Object obj, Type clazz){
        if (gson==null){
            gson = builder.create();
        }
        return gson.toJson(obj,clazz);
    }
    public Object deserialize(String ser, Type clazz) throws JsonSyntaxException {
        if (gson == null)
            gson = builder.create();

        return gson.fromJson(ser, clazz);
    }
}
