package com.songoda.kingdoms.main;

import com.songoda.kingdoms.utils.HashUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Lang{
    private YamlConfiguration verifyData = new YamlConfiguration();
    private HashMap<YamlConfiguration,File> langfiles = new HashMap<>();
    private HashMap<String, YamlConfiguration> langkeys = new HashMap<>();

    public Lang(Plugin plugin) {
        try {
            verifyData.load(new InputStreamReader(plugin.getResource("lang_eng.yml")));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        File langdir = new File(plugin.getDataFolder().getAbsolutePath()+ File.separator+"lang");
        if(!langdir.exists()) langdir.mkdirs();
        if (langdir.list().length==0){
            try {
                FileUtils.copyInputStreamToFile(plugin.getResource("lang_eng.yml"),new File(langdir+File.separator+ "lang_eng.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File file: langdir.listFiles()) {
            YamlConfiguration yml = new YamlConfiguration();
            try {
                yml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            if(verify(yml)){
                langfiles.put(yml,file);
                langkeys.put(yml.getString("langkey"),yml);
            }else{
                plugin.getLogger().warning("[Kingdoms] lang file "+file.getName()+" is incorrect");
                fixFile(yml,file);
            }
        }
    }

    public boolean isRegistered(String lang) {
        return langkeys.containsKey(lang);
    }

    private boolean verify(YamlConfiguration yml){
        Set<String> orikeys = verifyData.getKeys(true);
        Set<String> verkeys = yml.getKeys(true);
        return HashUtils.setEqualsIgnoreOrder(orikeys,verkeys);
    }
    private void fixFile(YamlConfiguration yml,File file){
        Set<String> orikeys = verifyData.getKeys(true);
        Set<String> verkeys = yml.getKeys(true);
        orikeys.removeAll(verkeys);
        for (String key: orikeys) {
            String value = verifyData.getString(key);
            yml.set(key,value);
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        langfiles.put(yml,file);
        langkeys.put(yml.getString("langkey"),yml);
    }

    /**
     * Get string from the default lang file
     * @param key
     * @return String
     */
    public String getString(String key){
        return getString(key,Config.getConfig().getString("Plugin.Lang"));
    }

    /**
     * Can return null of key is incorrect
     * @param key string key
     * @param lang lang key
     * @return String
     */
    public String getString(String key, String lang){
        if (lang == null) return getString(key);
        lang = lang.toLowerCase();
        return ChatColor.translateAlternateColorCodes('&',langkeys.get(lang).getString(key));
    }

    public HashMap<String, YamlConfiguration> getLangs() {
        return langkeys;
    }
}
