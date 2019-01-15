package com.songoda.kingdoms.manager.external;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.kingdom.OfflineKingdom;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.constants.player.OfflineKingdomPlayer;
import com.songoda.kingdoms.manager.game.GameManagement;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
public class MVdWPlaceholderManager extends Manager {

	protected MVdWPlaceholderManager(Plugin plugin) {
		super(plugin);
		init();
	}
	
	private void init(){
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"kingdom", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflinePlayer op = e.getOfflinePlayer();
				if(op == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				
				OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
				if(okp == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				if(okp.getKingdomName() == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				return okp.getKingdomName();
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"rp", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflinePlayer op = e.getOfflinePlayer();
				if(op == null) return "0";
				
				OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
				if(okp == null) return "0";
				
				OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(okp.getKingdomName());
				if(ok == null) return "0";
				
				return String.valueOf(ok.getResourcepoints());
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"land", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflinePlayer op = e.getOfflinePlayer();
				if(op == null) return "0";
				
				OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
				if(okp == null) return "0";
				
				Kingdom ok = GameManagement.getKingdomManager().getOrLoadKingdom(okp.getKingdomName());
				if(ok == null) return "0";
				
				return String.valueOf(ok.getLand());
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"rank", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflineKingdomPlayer okp = Kingdoms.getManagers().getPlayerManager().getOfflineKingdomPlayer(e.getOfflinePlayer());
				return String.valueOf(okp.getRank().getFancyMark());
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"onlinemembers", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player op = e.getPlayer();
				if(op == null) return "0";
				
				KingdomPlayer kp = GameManagement.getPlayerManager().getSession(op);
				if(kp == null) return "0";
				
				Kingdom k = kp.getKingdom();
				if(k == null) return "0";
				
				return String.valueOf(k.getOnlineMembers().size());
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"members", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflinePlayer op = e.getOfflinePlayer();
				if(op == null) return "0";
				
				OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
				if(okp == null) return "0";
				
				OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(okp.getKingdomName());
				if(ok == null) return "0";
			
				return String.valueOf(ok.getMembersList().size());
			}
			
		});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(Kingdoms.getInstance(),"king", new be.maximvdw.placeholderapi.PlaceholderReplacer(){

			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				OfflinePlayer op = e.getOfflinePlayer();
				if(op == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				
				OfflineKingdomPlayer okp = GameManagement.getPlayerManager().getOfflineKingdomPlayer(op);
				if(okp == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				
				OfflineKingdom ok = GameManagement.getKingdomManager().getOfflineKingdom(okp.getKingdomName());
				if(ok == null) return Kingdoms.getLang().getString("PlaceHolders_None");
				
				return ok.getKingName();
			}
			
		});
	}

	@Override
	public void onDisable() {

	}

}
