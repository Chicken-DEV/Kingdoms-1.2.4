package com.songoda.kingdoms.commands.user;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Kingdoms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class KCommandChangeLang extends KCommandBase {
    @Override
    public void executeCommandConsole(Queue<String> args) {
        Bukkit.getConsoleSender().sendMessage(Kingdoms.getLang().getString("Command_ChangeLang_NoConsole"));
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op, args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        String lang = args.poll();
        KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(user);
        if (!Kingdoms.getLang().isRegistered(lang)) {
            user.sendMessage(Kingdoms.getLang().getString("Command_ChangeLang_InvalidLang"));
            return;
        }
        kp.setLang(lang);
        user.sendMessage(Kingdoms.getLang().getString("Command_ChangeLang_LangSet", kp.getLang()).replace("%lang%", kp.getLang()));
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/k lang [lang]"};
    }

    @Override
    public String getDescription(String language) {
        return Kingdoms.getLang().getString("Command_ChangeLang_Help", language);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if(sender.isOp()){
            return true;
        }
        if(sender.hasPermission("kingdoms.changelang")){
            return true;
        }
        if (sender.hasPermission("kingdoms.player")) {
            return true;
        }
        return false;
    }
}
