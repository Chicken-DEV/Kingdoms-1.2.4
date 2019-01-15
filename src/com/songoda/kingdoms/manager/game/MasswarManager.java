package com.songoda.kingdoms.manager.game;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.songoda.kingdoms.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class MasswarManager extends Manager implements Listener{

	private long massWarTimeStart = 0;
	private int time = -1; //in seconds
	public MasswarManager(final Plugin plugin) {
		super(plugin);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(plugin.isEnabled()){
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(time == -1) continue;
					if(!isMassWarOn()) stopMassWar();
				}
			}
		}).start();
	}

	@Override
	public void onDisable() {
		stopMassWar();
	}
	
	public boolean isMassWarOn(){
        if(getTimeLeft() > 0){
            return true;
        } else {
            return false;
        }
    }
	
	public long getTimeLeft(){
        if(time == -1) return -1;
        
        return (massWarTimeStart + time * 1000L) - System.currentTimeMillis();
	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("HH'h' mm'm' ss's'");
	static{
		format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
	}
	public String getTimeLeftInString(){
		if(time == -1) return Kingdoms.getLang().getString("EventManager_Masswar_NotOn");
		else{
			Date date = new Date(getTimeLeft() < 0 ? 0 : getTimeLeft());
			
			Kingdoms.logDebug("millisec :"+getTimeLeft());
			return format.format(date)+" left.";
		}
	}

	public void startMassWar(int time){
		this.time = time;
		Bukkit.broadcastMessage(ChatColor.GRAY + "============================================" );
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(" " + Kingdoms.getLang().getString("Command_Admin_Masswar_Broadcast").replaceAll("%time%", "" + time/60));
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GRAY + "============================================" );
		massWarTimeStart = System.currentTimeMillis();
	}

	public void stopMassWar(){
		if(time >= 0){
		Bukkit.broadcastMessage(ChatColor.GRAY + "============================================" );
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(" " + Kingdoms.getLang().getString("Command_Admin_Masswar_End_Broadcast").replaceAll("%time%", "" + time/60));
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GRAY + "============================================" );
		}
		
		time = -1;
	}
	
	

}
