package com.songoda.kingdoms.manager.game;

import java.util.ArrayList;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.constants.ChatChannel;
import com.songoda.kingdoms.constants.Rank;
import com.songoda.kingdoms.main.Kingdoms;

public class KingdomChatManager extends Manager implements Listener {
	
	protected KingdomChatManager(Plugin plugin) {
		super(plugin);
		
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatToPublic(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			return;
		}
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if (kp.getKingdom() == null)
			return;
		
		if(kp.getChannel() != ChatChannel.PUBLIC)
			return;
		
		if(Config.getConfig().getBoolean("useKingdomPrefixes")){

			String prefix = "";
			String message = ChatColor.GRAY + e.getMessage();

			prefix += getFancyBracket(kp);
			e.setFormat(prefix + e.getFormat());
		}
	}
	
	public ArrayList<World> getEnabledWorlds(){
		ArrayList<World> world = new ArrayList<World>();
		for(World w:Bukkit.getWorlds()){
			if(Config.getConfig().getStringList("enabled-worlds").contains(w.getName())){
				world.add(w);
			}
		}
		return world;
	}
	
	public void sendMessageToKingdomPlayers(String message, Kingdom kingdom){
		Bukkit.getConsoleSender().sendMessage(message);
		for (World w : Bukkit.getWorlds()) {
			if (Config.getConfig().getStringList("enabled-worlds").contains(w.getName())) {
				for (Player p : w.getPlayers()) {
					if(p == null) continue;
					KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
					if(kp == null) continue;
					if (kingdom.isMember(kp)||kp.isAdminMode()) {
						p.sendMessage(message);
					}
				}
			}
		}
	}
	
	public void sendMessageToKingdomAllies(String message, Kingdom kingdom){
		Bukkit.getConsoleSender().sendMessage(message);
		for(World w:Bukkit.getWorlds()){			
			if(Config.getConfig().getStringList("enabled-worlds").contains(w.getName())){
				
				for(Player p:w.getPlayers()){
					KingdomPlayer kp = GameManagement.getPlayerManager().getSession(p);
					if(kp.isAdminMode()){
						p.sendMessage(message);
						continue;
					}
					if(kp.getKingdom() == null){
						continue;
					}
					if(kingdom.isMember(kp) 
							||( kingdom.isAllianceWith(kp.getKingdom())
							&& kp.getKingdom().isAllianceWith(kingdom))){
						p.sendMessage(message);
					}
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChatToAlly(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			return;
		}
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if (kp.getKingdom() == null)
			return;
		
		if(kp.getChannel() != ChatChannel.ALLY)
			return;

		String tag = Kingdoms.getLang().getString("Chat_Allychat_Prefix");
		e.setCancelled(true);
		//e.setFormat(color + prefix + kp.getName() + ": " + e.getMessage());
		sendMessageToKingdomAllies(tag.replaceAll("%player%", kp.getName()).replaceAll("%rank%", kp.getRank().getFancyMark()).replaceAll("%kingdom%", kp.getKingdomName()) + e.getMessage(), kp.getKingdom());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChatToKingdom(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		if(!Config.getConfig().getStringList("enabled-worlds").contains(e.getPlayer().getWorld().getName())){
			return;
		}
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp == null)
			return;
		if (kp.getKingdom() == null)
			return;
		
		if(kp.getChannel() != ChatChannel.KINGDOM)
			return;

		String prefix = "";
		String tag = Kingdoms.getLang().getString("Chat_Kingdomchat_Prefix");
		if(Config.getConfig().getBoolean("useKingdomPrefixes")){
			String message = ChatColor.GRAY + e.getMessage();
			
			prefix += ChatColor.stripColor(getFancyBracket(kp));

		}

		//e.setFormat();
		//e.setMessage(ChatColor.GREEN + e.getMessage());
		e.setCancelled(true);
		sendMessageToKingdomPlayers(tag.replaceAll("%player%", kp.getName()).replaceAll("%rank%", kp.getRank().getFancyMark()).replaceAll("%kingdom%", kp.getKingdomName()) + e.getMessage(), kp.getKingdom());
	}
//
	private String getFancyBracket(KingdomPlayer kp) {
		Rank rank = kp.getRank();
		if(rank == Rank.ALL){
			return Kingdoms.getLang().getString("Chat_Prefix_Members").replaceAll("%kingdom%", kp.getKingdomName());
		}else if(rank == Rank.MODS){
			return Kingdoms.getLang().getString("Chat_Prefix_Mods").replaceAll("%kingdom%", kp.getKingdomName());
		}else if(rank == Rank.GENERALS){
			return Kingdoms.getLang().getString("Chat_Prefix_Generals").replaceAll("%kingdom%", kp.getKingdomName());
		}else if(rank == Rank.KING){
			return Kingdoms.getLang().getString("Chat_Prefix_Kings").replaceAll("%kingdom%", kp.getKingdomName());
		}else{
			return Kingdoms.getLang().getString("Chat_Prefix_Members").replaceAll("%kingdom%", kp.getKingdomName());
		}
	}

	@Override
	public void onDisable() {

	}

}
