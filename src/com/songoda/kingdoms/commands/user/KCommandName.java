package com.songoda.kingdoms.commands.user;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.utils.EnglishChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class KCommandName extends KCommandBase {
    @Override
    public void executeCommandConsole(Queue<String> args) {
        Bukkit.getLogger().info("[Kingdoms] Console not supported for this command");
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op, args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
        if (kp.getKingdom()==null){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_Nokingdom",kp.getLang()));
            return;
        }
        Kingdom k = kp.getKingdom();
        if (!kp.getRank().isHigherOrEqualTo(Rank.KING)){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_NoKing",kp.getLang()));
            return;
        }
        String newName = args.poll();
        if (newName.contains("$")||newName.contains("%")){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_Invalid",kp.getLang()));
            return;
        }
        if(!EnglishChecker.isEnglish(newName) && !Config.getConfig().getBoolean("Plugin.allowSpecialCharactersInNamingKingdoms")){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_Invalid",kp.getLang()));
            return;
        }

        if (!GameManagement.getKingdomManager().renameKingdom(k,newName)){
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_InUse",kp.getLang()));
        }else {
            kp.sendMessage(Kingdoms.getLang().getString("Command_Name_Changed",kp.getLang()));
        }
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }

    @Override
    public String[] getUsage() {
        return new String[0];
    }

    @Override
    public String getDescription(String language) {
        return "Changes the kingdom's name";
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if (sender.isOp())return true;
        if (sender.hasPermission("kingdoms.player"))return true;
        if (sender.hasPermission("kingdoms.name"))return true;
        return false;
    }
}
