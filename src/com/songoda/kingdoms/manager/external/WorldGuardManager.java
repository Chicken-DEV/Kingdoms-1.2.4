package com.songoda.kingdoms.manager.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class WorldGuardManager extends Manager {

    protected WorldGuardManager(Plugin plugin) {
        super(plugin);

        worldGuard = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null) {
            Kingdoms.logInfo("WorldGuard Hooked!");
            Kingdoms.logInfo("Version: " + worldGuard.getDescription().getVersion());
            if (worldGuard.isEnabled()) {
            } else {
                Kingdoms.logInfo("WorldGuard is not enabled!");
                Kingdoms.logInfo("Disabled support for WorldGuard.");
            }
        }

    }


    private static com.sk89q.worldguard.bukkit.WorldGuardPlugin worldGuard;

    @Override
    public void onDisable() {
        // TODO Auto-generated method stub

    }

    public com.sk89q.worldguard.bukkit.WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    public static List<String> getRegionsAtLocation(Location loc) {
        if (worldGuard != null) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            ApplicableRegionSet regionSet = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
            List<String> regions = new ArrayList<>();
            for (ProtectedRegion region : regionSet.getRegions()){
                regions.add(region.getId());
            }
            return regions;
        }

        return null;
    }

    public boolean canBuild(Player p, Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ApplicableRegionSet regionSet = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
        LocalPlayer wgplayer = getWorldGuard().wrapPlayer(p);
        for (ProtectedRegion region : regionSet.getRegions()) {
            if (region.isMember(wgplayer) || region.isOwner(wgplayer)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    public static boolean cannotClaimInRegion(Location loc) {

        if (worldGuard != null) {
            List<String> regions = getRegionsAtLocation(loc);

            if (regions == null || regions.size() == 0) return false;

            for (String id : getRegionsAtLocation(loc)) {
                if (Config.getConfig().getString("worldguard.regions-that-allow-claiming").contains(id)) {
                    return false;
                }
            }

            return true;
        }

//        if (worldGuard != null) {
//            try {
//                com.sk89q.worldedit.Vector v = com.sk89q.worldedit.bukkit.BukkitUtil.toVector(loc);
//                com.sk89q.worldguard.protection.managers.RegionManager manager = worldGuard.getRegionManager(loc.getWorld());
//                com.sk89q.worldguard.protection.ApplicableRegionSet set = manager.getApplicableRegions(v);
//                for (com.sk89q.worldguard.protection.regions.ProtectedRegion rg : set) {
//                    if (Config.getConfig().getStringList("worldguard.regions-that-allow-claiming").contains(rg.getId()))
//                        return false;
//                }
//                return set.size() > 0;
//            } catch (NullPointerException e) {
//                return false;
//            } catch (IncompatibleClassChangeError e) {
//                Map<String, com.sk89q.worldguard.protection.regions.ProtectedRegion> rgs = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getRegions();
//                for (com.sk89q.worldguard.protection.regions.ProtectedRegion rg : rgs.values()) {
//                    if (Config.getConfig().getStringList("worldguard.regions-that-allow-claiming").contains(rg.getId()))
//                        return false;
//                    boolean inside = rg.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//                    if (inside) {
//
//                        return true;
//
//                    }
//                }
//            }
//        }


        return false;
    }

    public static boolean isInRegion(Location loc) {
        return getRegionsAtLocation(loc) != null || getRegionsAtLocation(loc).size() != 0;

//        if (worldGuard != null) {
//            try {
//                com.sk89q.worldedit.Vector v = com.sk89q.worldedit.bukkit.BukkitUtil.toVector(loc);
//                com.sk89q.worldguard.protection.managers.RegionManager manager = worldGuard.getRegionManager(loc.getWorld());
//                com.sk89q.worldguard.protection.ApplicableRegionSet set = manager.getApplicableRegions(v);
//                return set.size() > 0;
//            } catch (NullPointerException e) {
//                return false;
//            } catch (IncompatibleClassChangeError e) {
//                Map<String, com.sk89q.worldguard.protection.regions.ProtectedRegion> rgs = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getRegions();
//                for (com.sk89q.worldguard.protection.regions.ProtectedRegion rg : rgs.values()) {
//                    boolean inside = rg.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//                    if (inside) {
//
//                        return true;
//
//                    }
//                }
//            }
//        }
    }

}
