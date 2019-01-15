package com.songoda.kingdoms.commands.admin;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.commands.KCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.songoda.kingdoms.main.Kingdoms;

import java.util.Queue;

public class KCommandAdminDynmap extends KCommandBase {
    @Override
    public void executeCommandConsole(Queue<String> args) {
        String kingdomname = args.poll();
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomname);
        int r = 0;
        int g = 0;
        int b = 0;
        try{
             r = Integer.valueOf(args.poll());
             g = Integer.valueOf(args.poll());
             b = Integer.valueOf(args.poll());
        }catch (NumberFormatException e){
            Bukkit.getConsoleSender().sendMessage(Kingdoms.getLang().getString("Command_Admin_Dynmap_NotRGB"));
            return;
        }
        if(k==null){
            String msg = Kingdoms.getLang().getString("Command_Admin_Disband_KingdomNoutFound").replaceAll("%kingdom%",kingdomname);
            Bukkit.getConsoleSender().sendMessage(msg);
            return;
        }
        int rgb = (r << 16) + (g << 8) + b;
        k.setDynmapColor(rgb);
        String msg = Kingdoms.getLang().getString("Command_Admin_Dynmap_Success").replaceAll("%kingdom%",k.getKingdomName());
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op,args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        String kingdomname = args.poll();
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomname);
        int r = 0;
        int g = 0;
        int b = 0;
        try{
            r = Integer.valueOf(args.poll());
            g = Integer.valueOf(args.poll());
            b = Integer.valueOf(args.poll());
        }catch (NumberFormatException e){
            user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Dynmap_NotRGB", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()));
            return;
        }
        if(k==null){
            String msg = Kingdoms.getLang().getString("Command_Admin_Disband_KingdomNoutFound", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()).replaceAll("%kingdom%",kingdomname);
            user.sendMessage(msg);
            return;
        }
        int rgb = (r << 16) + (g << 8) + b;
        k.setDynmapColor(rgb);
        String msg = Kingdoms.getLang().getString("Command_Admin_Dynmap_Success", Kingdoms.getManagers().getPlayerManager().getSession(user).getLang()).replaceAll("%kingdom%",k.getKingdomName());
        user.sendMessage(msg);
    }

    @Override
    public int getArgsAmount() {
        return 4;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/k admin dynmap <kingdom> <r> <g> <b>"};
    }

    @Override
    public String getDescription(String lang) {
        return "Change the dynmap color (Only works if dynmap is present)";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if(sender.isOp())return true;
        if(sender.hasPermission("kingdoms.admin"))return true;
        if(sender.hasPermission("kingdoms.admin.dynmap"))return true;
        return false;
    }
}
