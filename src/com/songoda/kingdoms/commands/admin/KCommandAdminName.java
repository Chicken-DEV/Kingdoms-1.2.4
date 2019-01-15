package com.songoda.kingdoms.commands.admin;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class KCommandAdminName extends KCommandBase {
    @Override
    public void executeCommandConsole(Queue<String> args) {
        String kingdom = args.poll();
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdom);
        if (k==null){
            Bukkit.getLogger().warning("[Kingdoms] "+kingdom+" Does not exist");
        }
        String newName = args.poll();
        if(!GameManagement.getKingdomManager().renameKingdom(k,newName)){
            Bukkit.getLogger().warning("[kingdoms] Name is already in use");
            return;
        }
        Bukkit.getLogger().info("[Kingdoms] Renamed "+kingdom+" to "+newName);
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op,args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        String kingdom = args.poll();
        KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdom);
        if (k==null){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Doesnt_Exist_Error",kp.getLang()).replace("%kingdom%",kingdom));
        }
        String newName = args.poll();
        if(!GameManagement.getKingdomManager().renameKingdom(k,newName)){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Name_InUse",kp.getLang()));
            return;
        }
        kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Name_Changed",kp.getLang()).replace("%kingdom%",kingdom).replace("%newname%",newName));

    }

    @Override
    public int getArgsAmount() {
        return 2;
    }

    @Override
    public String[] getUsage() {
        return new String[0];
    }

    @Override
    public String getDescription(String language) {
        return "Forcefully change the name of a kingdom";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if (sender.isOp())return true;
        if (sender.hasPermission("kingdoms.admin"))return true;
        if (sender.hasPermission("kingdoms.admin.name"))return true;
        return false;
    }
}
