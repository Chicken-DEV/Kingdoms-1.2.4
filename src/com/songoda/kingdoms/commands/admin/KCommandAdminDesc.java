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

public class KCommandAdminDesc extends KCommandBase {
    @Override
    public void executeCommandConsole(Queue<String> args) {
        String kname = args.poll();
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kname);
        if (k==null){
            Bukkit.getLogger().warning("[Kingdoms] "+kname+" Does not exists");
            return;
        }
        String desc = args.poll();
        k.setKingdomLore(desc);
        Bukkit.getLogger().info("[Kingdoms] "+kname+"'s description has been edited");
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op, args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
        String kname = args.poll();
        Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kname);
        if (k==null){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Ally_Kingdom_Doesnt_Exist_Error",kp.getLang()).replace("%kingdom%",kname));
            return;
        }
        String desc = args.poll();
        k.setKingdomLore(desc);
        kp.sendMessage(Kingdoms.getLang().getString("Command_Admin_Desc_Changed",kp.getLang()));
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
        return "Forcefully changes the description of a kingdom";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if (sender.isOp())return true;
        if (sender.hasPermission("kingdoms.admin"))return true;
        if (sender.hasPermission("kingdoms.admin.desc"))return true;
        return false;
    }
}
