package com.songoda.kingdoms.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ALL")
public class DatabaseTransferTask implements Runnable {
    private Plugin plugin;
    private List<TransferPair> pairs;

    public DatabaseTransferTask(Plugin plugin, List<TransferPair> pairs) {
        super();
        this.plugin = plugin;
        this.pairs = pairs;
    }

    public DatabaseTransferTask(Plugin plugin, Set<TransferPair> pairs) {
        this.plugin = plugin;
        this.pairs = new ArrayList<TransferPair>();
        this.pairs.addAll(pairs);
    }

    @Override
    public void run() {
        Bukkit.getPluginManager().disablePlugin(plugin);

        int pairi = 0;
        for (TransferPair pair : pairs) {
            Database from = pair.from;
            Database to = pair.to;
            Set<String> keys = from.getKeys();
            int i = 0, percentage = -1;
            for (String key : keys) {
                try {
                    Object data = from.load(key, null);
                    to.save(key, data);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (getPercentage(i, keys.size()) % 5 == 0) {
                        percentage = getPercentage(i, keys.size());
                        plugin.getLogger().info(pairi + ". " + pair.toString());
                        plugin.getLogger().info("Transfer [" + percentage + "%] done...");
                    }
                    i++;
                }
            }
            plugin.getLogger().info(pairi + ". " + pair.toString());
            plugin.getLogger().info("Transfer [100%] finished!");
            pairi++;
        }

        Bukkit.getPluginManager().enablePlugin(plugin);
        System.gc();
    }

    private int getPercentage(int cur, int outOf) {
        return (int) (((double) cur / outOf) * 100);
    }

    public static class TransferPair<T> {
        private Database<T> from;
        private Database<T> to;

        public TransferPair(Database<T> from, Database<T> to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return from.getClass().getSimpleName() + " ==> " + to.getClass().getSimpleName();
        }

    }

}