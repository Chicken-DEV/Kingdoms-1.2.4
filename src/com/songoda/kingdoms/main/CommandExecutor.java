package com.songoda.kingdoms.main;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentSkipListMap;

import com.songoda.kingdoms.commands.KCommand;
import com.songoda.kingdoms.commands.admin.*;
import com.songoda.kingdoms.commands.user.*;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandExecutor {
    private Kingdoms plugin;

    private static final Map<String, KCommand> commands = new ConcurrentSkipListMap<String, KCommand>(String.CASE_INSENSITIVE_ORDER) {{
        this.put("create", new KCommandCreateKingdom());
        this.put("disband", new KCommandDisbandKingdom());
        this.put("claim", new KCommandClaimLand());
        this.put("unclaim", new KCommandUnclaim());
        this.put("invade", new KCommandInvade());
        this.put("nexus", new KCommandNexus());
        this.put("map", new KCommandMap());
        this.put("invite", new KCommandInvite());
        this.put("accept", new KCommandAccept());
        this.put("decline", new KCommandDecline());
        this.put("leave", new KCommandLeave());
        this.put("kick", new KCommandKick());
        this.put("c", new KCommandChat());
        this.put("chat", new KCommandChat());
        this.put("broad", new KCommandBroadcast());
        this.put("bc", new KCommandBroadcast());
        this.put("king", new KCommandKing());
        this.put("general", new KCommandGeneral());
        this.put("info", new KCommandInfo());
        this.put("show", new KCommandShow());
        this.put("sethome", new KCommandSethome());
        this.put("home", new KCommandHome());
        this.put("ally", new KCommandAlly());
        this.put("enemy", new KCommandEnemy());
        this.put("setlore", new KCommandSetlore());
        this.put("top", new KCommandTop());
        this.put("mod", new KCommandMod());
        this.put("demote", new KCommandDemote());
        this.put("markers", new KCommandMarkers());
        this.put("tradable", new KCommandTradable());
        this.put("donate", new KCommandDonate());
        this.put("admin", new KCommandAdminHelp());
        this.put("defend", new KCommandDefend());
        this.put("deposit", new KCommandDeposit());
        this.put("joinconquest", new KCommandJoinConquest());
        this.put("surrender", new KCommandSurrender());
        this.put("ff", new KCommandSurrender());
        this.put("lang", new KCommandChangeLang());
        this.put("changename", new KCommandName());
    }};

    private static final Map<String, KCommand> adminCommands = new ConcurrentSkipListMap<String, KCommand>(String.CASE_INSENSITIVE_ORDER) {{
        this.put("toggle", new KCommandAdminToggle());
        this.put("vanish", new KCommandAdminVanish());
        this.put("disband", new KCommandAdminDisband());
        this.put("extralandclaimmax", new KCommandAdminExtraLandClaimMax());
        this.put("extralandclaimmaxforplayer", new KCommandAdminExtraLandClaimMaxForPlayer());
        this.put("elcm", new KCommandAdminExtraLandClaimMax());
        this.put("rp", new KCommandAdminRp());
        this.put("rpforall", new KCommandAdminRpForAll());
        this.put("clear", new KCommandAdminClear());
        this.put("rpforplayer", new KCommandAdminRpForPlayer());
        this.put("masswar", new KCommandAdminMassWar());
        this.put("forcestopmasswar", new KCommandAdminForceStopMasswar());
        this.put("king", new KCommandAdminKing());
        this.put("join", new KCommandAdminJoin());
        this.put("reload", new KCommandAdminReload());
        this.put("backup", new KCommandAdminBackup());
        this.put("import", new KCommandAdminImport());
        this.put("seenexus", new KCommandAdminSeeNexus());
        this.put("removeshield", new KCommandAdminRemoveShield());
        this.put("addshield", new KCommandAdminAddShield());
        this.put("mod", new KCommandAdminMod());
        this.put("general", new KCommandAdminGeneral());
        this.put("debugdelete", new KCommandAdminDeleteKingdom());
        this.put("debugkingdomtag", new KCommandAdminDebugKingdomTag());
        this.put("pacifisttoggle", new KCommandAdminPacifistToggle());
        this.put("createconquestmap", new KCommandAdminCreateConquestMap());
        this.put("deleteconquestmap", new KCommandAdminDeleteConquestMap());
        this.put("dynmap", new KCommandAdminDynmap());
        this.put("allshield", new KCommandAdminAllShield());
        this.put("blacklist", new KCommandAdminAddBlacklistedName());
        this.put("name",new KCommandAdminName());
        this.put("desc",new KCommandAdminDesc());
    }};


    CommandExecutor(Plugin plugin) {
        this.plugin = (Kingdoms) plugin;
    }

    private static final int MAXLINES = 6;

    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        if (!label.equals("k") && !label.equals("kingdom") && !label.equals("kingdoms")) return;

        if (player != null) {
            if (!Config.getConfig().getStringList("enabled-worlds").contains(player.getWorld().getName())) {
                player.sendMessage(Kingdoms.getLang().getString("Misc_Invalid_World"));
                return;
            }
        }

        //PRINT HELP.
        if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")))) {
            List<Map.Entry<String, KCommand>> entries = new ArrayList<Map.Entry<String, KCommand>>();
            entries.addAll(commands.entrySet());
            for (Map.Entry<String, KCommand> entry : adminCommands.entrySet()) {
                if (entry.getValue().canExecute(sender))
                    entries.add(new AbstractMap.SimpleEntry<String, KCommand>("admin " + entry.getKey(), entry.getValue()));
            }

            sender.sendMessage(ChatColor.GRAY + "======== " + ChatColor.GOLD + plugin.getLang().getString("Plugin_Display") + ChatColor.GRAY + " ========");
            for (int i = 0; i < MAXLINES; i++) {
                String key = entries.get(i).getKey();
                KCommand command = entries.get(i).getValue();
                //if(player != null){
                //sender.sendMessage(ChatColor.LIGHT_PURPLE + "/k " + key + ChatColor.WHITE + " - "
                //		+ (command.getDescription() == null ? null : command.getDescription()));

                if (sender instanceof Player) {
                    KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession((Player) sender);
                    sender.sendMessage(Kingdoms.getLang().getString("Command_Help_CommandLine")
                            .replaceAll("%command%", "/k " + key).
                                    replaceAll("%desc%", (command.getDescription(kp.getLang()) == null ? null : command.getDescription(kp.getLang()))));
                } else {
                    sender.sendMessage(Kingdoms.getLang().getString("Command_Help_CommandLine")
                            .replaceAll("%command%", "/k " + key).
                                    replaceAll("%desc%", (command.getDescription(null) == null ? null : command.getDescription(null))));
                }

//				}else{
//					sender.sendMessage("/k " + key +" - "
//							+ (command.getDescription() == null ? null : command.getDescription()));
//				}
            }
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "");
            if (entries.size() % MAXLINES == 0)
                sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.GRAY + "1/" + (entries.size() / MAXLINES));
            else sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.GRAY + "1/" + (entries.size() / MAXLINES + 1));
            sender.sendMessage(plugin.getLang().getString("Command_Help_Next_Page"));
            sender.sendMessage(ChatColor.GRAY + "");
            return;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))) {
            int page = 0;
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Kingdoms.getLang().getString("Command_Help_Invalid_Page").replaceAll("%args%", args[1]));
                return;
            }

            page -= 1;

            List<Map.Entry<String, KCommand>> entries = new ArrayList<Map.Entry<String, KCommand>>();
            entries.addAll(commands.entrySet());
            for (Map.Entry<String, KCommand> entry : adminCommands.entrySet()) {
                if (entry.getValue().canExecute(sender))
                    entries.add(new AbstractMap.SimpleEntry<String, KCommand>("admin " + entry.getKey(), entry.getValue()));
            }

            if (page * MAXLINES >= entries.size() || page < 0) {
                sender.sendMessage(Kingdoms.getLang().getString("Command_Help_Invalid_Page").replaceAll("%args%", args[1]));
                return;
            }

            sender.sendMessage(ChatColor.GRAY + "======== " + ChatColor.GOLD + plugin.getLang().getString("Plugin_Display") + ChatColor.GRAY + " ========");
            for (int i = page * MAXLINES; i < (page + 1) * MAXLINES; i++) {
                if (i == entries.size()) break;

                String key = entries.get(i).getKey();
                KCommand command = entries.get(i).getValue();

                if (sender instanceof Player) {
                    KingdomPlayer kp = Kingdoms.getManagers().getPlayerManager().getSession((Player) sender);
                    sender.sendMessage(Kingdoms.getLang().getString("Command_Help_CommandLine")
                            .replaceAll("%command%", "/k " + key).
                                    replaceAll("%desc%", (command.getDescription(kp.getLang()))));
                } else {
                    sender.sendMessage(Kingdoms.getLang().getString("Command_Help_CommandLine")
                            .replaceAll("%command%", "/k " + key).
                                    replaceAll("%desc%", (command.getDescription(null))));
                }

//				if(player != null){
//					sender.sendMessage(ChatColor.LIGHT_PURPLE + "/k " + key + ChatColor.WHITE + " - "
//							+ (command.getDescription() == null ? null : command.getDescription()));
//				}else{
//					sender.sendMessage("/k " + key +" - "
//							+ (command.getDescription() == null ? null : command.getDescription()));
//				}
            }
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "");
            if (entries.size() % MAXLINES == 0)
                sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.GRAY + args[1] + "/" + (entries.size() / MAXLINES));
            else
                sender.sendMessage(ChatColor.GOLD + "Page " + ChatColor.GRAY + args[1] + "/" + (entries.size() / MAXLINES + 1));
            sender.sendMessage(plugin.getLang().getString("Command_Help_Next_Page"));
            sender.sendMessage(ChatColor.GRAY + "");
            return;
        }
        ////////////////////////////////////////////
        KCommand command = null;
        Queue<String> arguments = new LinkedList<String>();
        if (args.length > 1 && args[0].equalsIgnoreCase("admin")) {//admin commands
//			if(!sender.isOp()){
//				sender.sendMessage(Kingdoms.getLang().getString(Misc_Not_Enough_Permissions));
//				return;
//			}
            for (int i = 2; i < args.length; i++) arguments.add(args[i]);
            command = adminCommands.get(args[1]);
        } else {//user commands
            for (int i = 1; i < args.length; i++) arguments.add(args[i]);
            command = commands.get(args[0]);
        }

        if (command == null) {
            if (player != null) {
                sender.sendMessage(Kingdoms.getLang().getString("Misc_Invalid_Command"));
            } else {
                sender.sendMessage(Kingdoms.getLang().getString("Misc_Invalid_Command"));
            }
        } else {
            if (!sender.isOp() && !command.canExecute(sender)) {
                sender.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Permissions"));
                return;
            }

            if (player != null) {
                if (command.getArgsAmount() == -1) {
                    command.execute(player, arguments);
                } else if (arguments.size() == command.getArgsAmount()) {
                    command.execute(player, arguments);
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "/k " + args[0] + ":");
                    if (command.getUsage() != null) {
                        for (String str : command.getUsage()) sender.sendMessage(ChatColor.GRAY + "   " + str);
                    } else {
                        sender.sendMessage(ChatColor.GRAY + command.getDescription(Kingdoms.getManagers().getPlayerManager().getSession(player).getLang()));
                    }
                }
            } else {
                if (command.getArgsAmount() == -1) {
                    command.execute(player, arguments);
                } else if (arguments.size() == command.getArgsAmount()) {
                    command.execute(player, arguments);
                } else {
                    sender.sendMessage("/k " + args[0] + ":");
                    if (command.getUsage() != null) {
                        for (String str : command.getUsage()) sender.sendMessage("    " + str);
                    } else {
                        sender.sendMessage(ChatColor.GRAY + command.getDescription(null));
                    }
                }
            }
        }
    }

    public Map<String, KCommand> getCommands() {
        return commands;
    }

    public Map<String, KCommand> getAdminCommands() {
        return adminCommands;
    }
}
