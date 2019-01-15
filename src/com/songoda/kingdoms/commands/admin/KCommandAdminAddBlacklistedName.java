package com.songoda.kingdoms.commands.admin;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Kingdoms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class KCommandAdminAddBlacklistedName extends KCommandBase {


    @Override
    public void executeCommandConsole(Queue<String> args) {
        final CommandSender sender = Bukkit.getConsoleSender();
        String name = args.poll();
        if (Kingdoms.getInstance().getConfig().getStringList("disallowed-kingdom-names").contains(name)) {
            sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Blacklist_AlreadyInlist"));
            return;
        }
        Kingdoms.getInstance().getConfig().set("disallowed-kingdom-names", Kingdoms.getInstance().getConfig().getStringList("disallowed-kingdom-names").add(name));
        Kingdoms.getInstance().saveConfig();
        sender.sendMessage(Kingdoms.getLang().getString("Command_Admin_Blacklist_NameAdded"));
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op, args);
    }

    @Override
    public void executeCommandUser(Player user, Queue<String> args) {
        KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession(user);
        String name = args.poll();
        if (Kingdoms.getInstance().getConfig().getStringList("disallowed-kingdom-names").contains(name)) {
            user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Blacklist_AlreadyInlist", kp.getLang()));
            return;
        }
        Kingdoms.getInstance().getConfig().set("disallowed-kingdom-names", Kingdoms.getInstance().getConfig().getStringList("disallowed-kingdom-names").add(name));
        Kingdoms.getInstance().saveConfig();
        user.sendMessage(Kingdoms.getLang().getString("Command_Admin_Blacklist_NameAdded", kp.getLang()));
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                Kingdoms.getLang().getString("Command_Usage_Blacklist_Add")
        };
    }

    @Override
    public String getDescription(String language) {
        return Kingdoms.getLang().getString("Command_Help_Admin_Blacklist_Add", language);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if(sender.isOp()){
            return true;
        }
        if(sender.hasPermission("kingdoms.admin")){
            return true;
        }
        if(sender.hasPermission("kingdoms.admin.blacklist.add")){
            return true;
        }
        return false;
    }
}
