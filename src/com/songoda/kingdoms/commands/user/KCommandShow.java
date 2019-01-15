package com.songoda.kingdoms.commands.user;

import com.songoda.kingdoms.commands.KCommandBase;
import com.songoda.kingdoms.constants.kingdom.BotKingdom;
import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;

public class KCommandShow extends KCommandBase {

    @Override
    public int getArgsAmount() {
        return -1;
    }

    @Override
    public String[] getUsage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canExecute(CommandSender sender){
        if(sender.isOp()){
            return true;
        }
        if(sender.hasPermission("kingdoms.player")){
            return true;
        }
	  return sender.hasPermission("kingdoms.show");

	}

    @Override
    public void executeCommandConsole(final Queue<String> args) {
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();
        if(args.size() == 0){
            sender.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom"));
            return;
        }

        new Thread(new Runnable() {
            private int count = 0;

            @Override
            public void run() {
                try {
                    while(isProcessing){
                        if(count == 100){
                            Kingdoms.logDebug("k show requested but was pending for 10secs.");
                            isProcessing = false;
                            return;
                        }

                        count++;
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                isProcessing = true;
                if (args.size() == 0) {
                    return;
                }else{
                    String kingdomName = args.poll();
                    Kingdoms.logDebug("kingdomName=" + kingdomName);

                    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
                    if (kingdom == null) {
                        //kp.sendMessage(Kingdoms.getLang().getString("Command_Show_Kingdom_Non_Existant));
                        //isProcessing = false;
                        OfflinePlayer op = Bukkit.getOfflinePlayer(kingdomName);
                        if(op == null){
                            sender.sendMessage(Kingdoms.getLang().getString("Command_Show_Non_Existant"));
                            isProcessing = false;
                            return;

                        }
                        OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op.getUniqueId());
                        if(okp == null){
                            sender.sendMessage(Kingdoms.getLang().getString("Command_Show_Non_Existant"));
                            isProcessing = false;
                            return;

                        }
                        if(okp.getKingdomName() != null){
                            kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
                            displayInfo(sender, kingdom);
                            isProcessing = false;
                            return;
                        }else{
                            sender.sendMessage(Kingdoms.getLang().getString("Command_Show_Player_No_Kingdom"));
                            isProcessing = false;
                            return;

                        }
                    }

                    displayInfo(sender, kingdom);
                    isProcessing = false;
                }
                isProcessing = false;
            }

        }).start();
    }

    @Override
    public void executeCommandOP(Player op, Queue<String> args) {
        executeCommandUser(op, args);
    }

    private static boolean isProcessing = false;
    @Override
    public void executeCommandUser(Player user, final Queue<String> args) {
        final KingdomPlayer kp = GameManagement.getPlayerManager().getSession(user);
        if(args.size() == 0 && kp.getKingdom() == null){
            kp.getPlayer().sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
            return;
        }

        new Thread(new Runnable() {
            private int count = 0;

            @Override
            public void run() {
                try {
                    while(isProcessing){
                        if(count == 100){
                            Kingdoms.logDebug("k show requested but was pending for 10secs.");
                            isProcessing = false;
                            return;
                        }

                        count++;
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                isProcessing = true;
                if (args.size() == 0 && kp.getKingdom() != null) {
                    displayInfo(kp.getPlayer(), kp.getKingdom());
                    isProcessing = false;
                    return;
                }else{
                    String kingdomName = args.poll();
                    Kingdoms.logDebug("kingdomName=" + kingdomName);

                    Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(kingdomName);
                    if (kingdom == null) {
                        //kp.sendMessage(Kingdoms.getLang().getString("Command_Show_Kingdom_Non_Existant));
                        //isProcessing = false;
                        OfflinePlayer op = Bukkit.getOfflinePlayer(kingdomName);
                        if(op == null){
                            kp.sendMessage(Kingdoms.getLang().getString("Command_Show_Non_Existant", kp.getLang()));
                            isProcessing = false;
                            return;

                        }
                        OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
                        if(okp == null){
                            kp.sendMessage(Kingdoms.getLang().getString("Command_Show_Non_Existant", kp.getLang()));
                            isProcessing = false;
                            return;
                        }
                        if(okp.getKingdomName() != null){
                            kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
                            displayInfo(kp.getPlayer(), kingdom);
                            isProcessing = false;
                            return;
                        }else{
                            kp.sendMessage(Kingdoms.getLang().getString("Command_Show_Player_No_Kingdom", kp.getLang()));
                            isProcessing = false;
                            return;
                        }

                    }
                    displayInfo(kp.getPlayer(), kingdom);
                    isProcessing = false;
                }
                isProcessing = false;
            }

        }).start();
    }
    private final String sideLine = ChatColor.AQUA + "| " ;
    private void displayInfo(CommandSender sender, Kingdom kingdom) {
        if(kingdom == null) return;
        if(sender == null) return;

		if(kingdom instanceof BotKingdom){
			((BotKingdom) kingdom).displayInfo(sender);
			return;
		}

		KingdomPlayer kp = GameManagement.getPlayerManager().getSession((Player) sender);

        String king = Kingdoms.getLang().getString("Command_Show_King");
        String members = Kingdoms.getLang().getString("Command_Show_Members");
        String allies = Kingdoms.getLang().getString("Command_Show_Allies");
        String land = Kingdoms.getLang().getString("Command_Show_Land");
        String maxLandClaims = Kingdoms.getLang().getString("Command_Show_MaxLandClaims");
        String enemies = Kingdoms.getLang().getString("Command_Show_Enemies");
        String rp = Kingdoms.getLang().getString("Command_Show_ResourcePoints");
        String might = Kingdoms.getLang().getString("Command_Show_Might");
        String home = Kingdoms.getLang().getString("Command_Show_Home");
        String nexus = Kingdoms.getLang().getString("Command_Show_Nexus");
        String extra = "";
        if(kingdom.getExtraLandClaims() > 0){
            extra =  ChatColor.GREEN + " + (" + kingdom.getExtraLandClaims() + ")";
        }else if(kingdom.getExtraLandClaims() < 0){
            extra =  ChatColor.RED + " - (" + (kingdom.getExtraLandClaims()*-1) + ")";
        }
        sender.sendMessage(ChatColor.GRAY + "==== " + ChatColor.LIGHT_PURPLE + kingdom.getKingdomName()
                + ChatColor.GRAY + " ====");
        if(kingdom.getKingdomLore() != null) sender.sendMessage(sideLine + ChatColor.YELLOW + kingdom.getKingdomLore());
        if(kingdom.isShieldUp()) sender.sendMessage(sideLine + ChatColor.GREEN + Kingdoms.getLang().getString("Command_Show_Shield", kp.getLang()) + ": " + TimeUtils.parseTimeMillis(kingdom.getTimeLeft(OfflineKingdom.SHIELD)));
        if(kingdom.isNeutral()) sender.sendMessage(sideLine + ChatColor.GREEN + Kingdoms.getLang().getString("Misc_Neutral", kp.getLang()));
        sender.sendMessage(sideLine + king + ": " + ChatColor.YELLOW
                + Bukkit.getOfflinePlayer(kingdom.getKing()).getName());
        sender.sendMessage(sideLine + land + ": " + ChatColor.YELLOW + kingdom.getLand());
        sender.sendMessage(sideLine + maxLandClaims + ": " + ChatColor.YELLOW + (kingdom.getMembersList().size()* Config.getConfig().getInt("land-per-member")) + extra);
        sender.sendMessage(sideLine + might + ": " + ChatColor.YELLOW + kingdom.getMight());
        sender.sendMessage(sideLine + rp + ": " + ChatColor.YELLOW + kingdom.getResourcepoints());
        if(sender.hasPermission("kingdoms.admin.toggle")){
            Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(kingdom.getKingdomName());

            sender
                    .sendMessage(sideLine + home + ": " + ChatColor.YELLOW + SimpleLocation.locToStr(k.getHome_loc()));
            sender
                    .sendMessage(sideLine + nexus + ": " + ChatColor.YELLOW + SimpleLocation.locToStr(k.getNexus_loc()));
        }
        sender.sendMessage(sideLine + members + ": ");
        ArrayList<String> sentNames = new ArrayList<>();
        for (UUID uuid : kingdom.getMembersList()) {
            OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(uuid);
            if(okp == null){
                Kingdoms.logDebug("okp was null (k show)");
                continue;
            }
            if(sentNames.contains(okp.getName())) continue;
            sentNames.add(okp.getName());
            String rank = okp.getRank().getColor() + okp.getRank().getFancyMark();
            ChatColor status = ChatColor.RED;
            if (okp.isOnline()) {
                if(!GameManagement.getPlayerManager().getSession(okp.getUuid()).isVanishMode())
                    status = ChatColor.GREEN;
            }
            sender.sendMessage(sideLine + status + "" + ChatColor.GRAY + "[" + rank
                    + ChatColor.GRAY + "] " + status + okp.getName());
        }
        sender.sendMessage(sideLine + allies + ":");
        for (UUID ally : kingdom.getAlliesList()) {
        	Kingdom kally = GameManagement.getKingdomManager().getOrLoadKingdom(ally);
        	if(kally == null) continue;
        	if(kingdom.isAllianceWith(kally)){
            	sender.sendMessage(sideLine + ChatColor.GREEN + kally.getKingdomName());
            }else{
            	sender.sendMessage(sideLine + ChatColor.RED + kally.getKingdomName() + " (" + Kingdoms.getLang().getString("Command_Show_Pending", kp.getLang()) + ")");
            }
        }
        sender.sendMessage(sideLine + enemies + ":");
        for (UUID enemy : kingdom.getEnemiesList()) {
            Kingdom k = GameManagement.getKingdomManager().getOrLoadKingdom(enemy);
            sender.sendMessage(sideLine + ChatColor.RED + k.getKingdomName());
        }
    }

    @Override
    public String getDescription(String lang) {
        return Kingdoms.getLang().getString("Command_Help_Show", lang);
    }

}
