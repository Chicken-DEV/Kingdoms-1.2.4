package com.songoda.kingdoms.main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    private static FileConfiguration config;
    Plugin plugin;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}

