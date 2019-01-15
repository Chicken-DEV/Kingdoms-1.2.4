package com.songoda.kingdoms.manager.game;

import java.util.Iterator;

import com.songoda.kingdoms.constants.kingdom.Kingdom;
import com.songoda.kingdoms.constants.land.KChestSign;
import com.songoda.kingdoms.constants.land.Land;
import com.songoda.kingdoms.constants.land.SimpleChunkLocation;
import com.songoda.kingdoms.constants.land.SimpleLocation;
import com.songoda.kingdoms.constants.player.KingdomPlayer;
import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.gui.GUIManagement;
import com.songoda.kingdoms.manager.Manager;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

public class PrivateChestManager extends Manager implements Listener{
	public static final String SIGN = ChatColor.GOLD+"["+ChatColor.RED+"Protected"+ChatColor.GOLD+"]";
	private final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
	
	protected PrivateChestManager(Plugin plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onChestSet(SignChangeEvent e){
		if(!Config.getConfig().getBoolean("private-chests.enable-private-chests")){
			return;
		}
		if(e.getBlock().getType() != Material.WALL_SIGN) return;
		
		org.bukkit.material.Sign s = (org.bukkit.material.Sign) e.getBlock().getState().getData();
		
		if(e.getBlock().getRelative(s.getAttachedFace()).getType() != Material.CHEST
				&& e.getBlock().getRelative(s.getAttachedFace()).getType() != Material.TRAPPED_CHEST) return;
		
		//create protected only if it is written
		if(!e.getLine(0).equalsIgnoreCase(ChatColor.stripColor(SIGN)))
			return;
		
		Chest clickedChest = (Chest) e.getBlock().getRelative(s.getAttachedFace()).getState();
		Chest neighborChest = isDoubleChest(e.getBlock().getRelative(s.getAttachedFace()));
		
		SimpleLocation loc = new SimpleLocation(clickedChest.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;
		
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		if(kingdom == null) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(!kingdom.equals(kp.getKingdom())) return;
		
		//check the chest if it's already protected
		Sign oldSign = null;
		if((oldSign = isProtected(clickedChest)) != null){
			kp.sendMessage(Kingdoms.getLang().getString("PChest_AlreadyProtected", kp.getLang()));
			e.setCancelled(true);
			return;
		}
		
		if((oldSign = isProtected(neighborChest)) != null){
			kp.sendMessage(Kingdoms.getLang().getString("PChest_AlreadyProtected", kp.getLang()));
			e.setCancelled(true);
			return;
		}
		
		if(Config.getConfig().getInt("private-chests.sign-creation-rp-cost") > 0){
			if(kingdom.getResourcepoints() < Config.getConfig().getInt("private-chests.sign-creation-rp-cost")){
				kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Points", kp.getLang()).replaceAll("%cost%", ""+Config.getConfig().getInt("private-chests.sign-creation-rp-cost")));
				e.setCancelled(true);
				return;
			}else{
				kingdom.setResourcepoints(kingdom.getResourcepoints() - Config.getConfig().getInt("private-chests.sign-creation-rp-cost"));
				kp.sendMessage(Kingdoms.getLang().getString("PChest_CostDeducted", kp.getLang()).replaceAll("%cost%", ""+Config.getConfig().getInt("private-chests.sign-creation-rp-cost")));
			}
		}
		
		//set protected chest
		e.setLine(0, SIGN);
		e.setLine(1, e.getPlayer().getName());
		
		KChestSign kcSign = new KChestSign(new SimpleLocation(e.getBlock().getLocation()), kp.getUuid());
		
		land.addChestSign(kcSign);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChestAccess(PlayerInteractEvent e){
		if(e.isCancelled()) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		if(e.getClickedBlock().getType() != Material.CHEST
				&& e.getClickedBlock().getType() != Material.TRAPPED_CHEST) return;
		
		Block clickedBlock = e.getClickedBlock();
		if(!(clickedBlock.getState() instanceof Chest)) return;
		
		////////////////////////////////////////////////////////////////////////////////////////
		Chest clickedChest = (Chest) clickedBlock.getState();
		Chest neighborChest = isDoubleChest(clickedBlock);
		
		Sign oldSign = isProtected(clickedChest);
		if(oldSign == null) oldSign = isProtected(neighborChest);
		
		if(oldSign == null) return; //not protected
		
		SimpleLocation loc = new SimpleLocation(oldSign.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp.getKingdom() == null){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_In_Kingdom", kp.getLang()));
			return;
		}
		
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		if(kingdom == null) return;
		
		SimpleLocation backupLoc = new SimpleLocation(e.getClickedBlock().getRelative(((org.bukkit.material.Sign) oldSign
				.getData()).getAttachedFace()).getLocation());
		SimpleChunkLocation backupChunk = backupLoc.toSimpleChunk();
		
		KChestSign kcsign = land.getChestSign(loc);
		if(kcsign == null){
			land = GameManagement.getLandManager().getOrLoadLand(backupChunk);
			kcsign = land.getChestSign(loc);
			if(kcsign == null){
				return;
			}
		}
		
		if(kcsign.getOwner() == null) return;
		if(kcsign.getOwner().equals(kp.getUuid())){
			return;
		}else if(kcsign.getOwners().contains(kp.getUuid())){
			return;
		}else if(kp.isAdminMode()){
			return;
		}else if(kp.getPlayer().hasPermission("kingdoms.protectedchestbypass")){
			return;
		}else if(kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getOpenallchest())){
			return;
		}else{
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Permissions", kp.getLang()));
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChestBreak(BlockBreakEvent e){
		if(e.isCancelled()) return;
		
		if(e.getBlock().getType() != Material.CHEST
				&& e.getBlock().getType() != Material.TRAPPED_CHEST) return;
		
		Chest brokenChest = (Chest) e.getBlock().getState();
		Chest neighborChest = isDoubleChest(e.getBlock());
		
		SimpleLocation loc = new SimpleLocation(brokenChest.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		if(land.getOwnerUUID() == null) return;
		
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		if(kingdom == null) return;
		
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(!kingdom.equals(kp.getKingdom()) && !kp.isAdminMode()){
			//Kingdoms.getLang().addString(kingdom.getKingdomName());
			kp.sendMessage(Kingdoms.getLang().getString("PChest_NotYourKingdomChest", kp.getLang()).replace("%kingdom%",kingdom.getKingdomName()));
			e.setCancelled(true);
			return;
		}
		if(kp.isAdminMode()) return;
		boolean removeSign = true;
		Sign protectedSign = null;

		if((protectedSign = isProtected(brokenChest)) != null){
			removeSign = true;
		}else if((protectedSign = isProtected(neighborChest)) != null){
			removeSign = false;
		}
		if(protectedSign == null){
			return;
		}
		
		SimpleLocation locSign = new SimpleLocation(protectedSign.getLocation());
		

		SimpleLocation backupLoc = new SimpleLocation(protectedSign.getBlock().getRelative(((org.bukkit.material.Sign) protectedSign
				.getData()).getAttachedFace()).getLocation());
		SimpleChunkLocation backupChunk = backupLoc.toSimpleChunk();
		
		KChestSign kcsign = land.getChestSign(locSign);
		if(kcsign == null){
			land = GameManagement.getLandManager().getOrLoadLand(backupChunk);
			kcsign = land.getChestSign(locSign);
			if(kcsign == null){
				return;
			}
		}
		Kingdoms.logDebug("chest break");
		if(!kp.isAdminMode()
				&& !kcsign.getOwner().equals(kp.getUuid())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Permissions", kp.getLang()));
			e.setCancelled(true);
		}else{
			if(removeSign) land.removeChestSign(locSign);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChestExplode(EntityExplodeEvent e){
		if(!Config.getConfig().getBoolean("private-chests.explosion-immune")){
			return;
		}
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block.getType() != Material.CHEST
					&& block.getType() != Material.TRAPPED_CHEST) continue;
			
			Chest brokenChest = (Chest) block.getState();
			Chest neighborChest = isDoubleChest(block);
			
			SimpleLocation loc = new SimpleLocation(brokenChest.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) continue;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) continue;
			
			Sign protectedSign = null;
			if((protectedSign = isProtected(brokenChest)) == null){
				protectedSign = isProtected(neighborChest);
			}
			
			if(protectedSign != null) iter.remove();
		}
	}
	
	@EventHandler
	public void onHopperPlace(BlockPlaceEvent event){
		if(event.getBlock().getType() == Material.HOPPER||
				event.getBlock().getType() == Materials.RAIL.parseMaterial()){

			Block block = event.getBlock().getRelative(0,1,0);
			if(block.getType() != Material.CHEST
					&& block.getType() != Material.TRAPPED_CHEST) return;
			
			Chest brokenChest = (Chest) block.getState();
			Chest neighborChest = isDoubleChest(block);
			
			SimpleLocation loc = new SimpleLocation(brokenChest.getLocation());
			SimpleChunkLocation chunk = loc.toSimpleChunk();
			Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
			if(land.getOwnerUUID() == null) return;
			
			Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
			if(kingdom == null) return;
			
			Sign protectedSign = null;
			if((protectedSign = isProtected(brokenChest)) == null){
				protectedSign = isProtected(neighborChest);
			}
			
			if(protectedSign != null){
				event.setCancelled(true);
				Kingdoms.getManagers().getPlayerManager().getSession(event.getPlayer()).sendMessage(Kingdoms.getLang().getString("Misc_PrivateSigns_No_Hopper_Tracks"));
			}
		
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSignBreak(BlockBreakEvent e){
		if(e.getBlock().getType() != Material.WALL_SIGN) return;
		
		Sign sign = (Sign) e.getBlock().getState();
		
		if(sign.getLine(0) == null || !sign.getLine(0).equals(SIGN)) return;
		
		SimpleLocation loc = new SimpleLocation(sign.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);

		KChestSign kchestsign = land.getChestSign(loc);
		SimpleLocation backupLoc = new SimpleLocation(e.getBlock().getRelative(((org.bukkit.material.Sign) sign
				.getData()).getAttachedFace()).getLocation());
		SimpleChunkLocation backupChunk = backupLoc.toSimpleChunk();
		if(kchestsign == null){
			land = GameManagement.getLandManager().getOrLoadLand(backupChunk);
			kchestsign = land.getChestSign(loc);
			if(kchestsign == null){
				return;
			}
		}
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(kp == null){
			return;
		}
		if(!kp.getKingdomUuid().equals(land.getOwnerUUID())){
			return;
		}
		Kingdom kingdom = Kingdoms.getManagers().getKingdomManager().getOrLoadKingdom(land.getOwnerUUID());
		Kingdoms.logDebug("sign break");
		if (!kp.isAdminMode()
				&& !kchestsign.getOwner().equals(e.getPlayer().getUniqueId())
				&& !kp.getRank().isHigherOrEqualTo(kingdom.getPermissionsInfo().getOpenallchest())
				&& kp.getKingdom().getMembersList().contains(kchestsign.getOwner())) {
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Permissions", kp.getLang()));
			e.setCancelled(true);
		}else{
			land.removeChestSign(loc);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSignExplode(EntityExplodeEvent e){
		if(!Config.getConfig().getBoolean("private-chests.explosion-immune")){
			return;
		}
		for(Iterator<Block> iter = e.blockList().iterator(); iter.hasNext();){
			Block block = iter.next();
			if(block.getType() != Material.WALL_SIGN) continue;
			
			Sign sign = (Sign) block.getState();
			
			if(sign.getLine(0) != null && sign.getLine(0).equals(SIGN)) iter.remove();
		}
	}
	
	@EventHandler
	public void onRightClickSign(PlayerInteractEvent e){
		if(!Config.getConfig().getBoolean("private-chests.enable-private-chests")){
			return;
		}
		if(e.isCancelled()) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Sign sign = null;
		if((sign = isProtectedSign(e.getClickedBlock())) == null) return;
		
		SimpleLocation loc = new SimpleLocation(sign.getLocation());
		SimpleChunkLocation chunk = loc.toSimpleChunk();
		Land land = GameManagement.getLandManager().getOrLoadLand(chunk);
		
		SimpleLocation backupLoc = new SimpleLocation(e.getClickedBlock().getRelative(((org.bukkit.material.Sign) sign
				.getData()).getAttachedFace()).getLocation());
		SimpleChunkLocation backupChunk = backupLoc.toSimpleChunk();
		
		KChestSign kcsign = land.getChestSign(loc);
		if(kcsign == null){
			land = GameManagement.getLandManager().getOrLoadLand(backupChunk);
			kcsign = land.getChestSign(loc);
			if(kcsign == null){
				return;
			}
		}
		if(kcsign.getOwner() == null) return;
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(e.getPlayer());
		if(!kcsign.getOwner().equals(kp.getUuid())){
			kp.sendMessage(Kingdoms.getLang().getString("Misc_Not_Enough_Permissions", kp.getLang()));
			return;
		}
		
		kp.setModifyingSign(kcsign);
		
		GUIManagement.getPchestGUImanager().openMenu(kp);
	}
	
	/**
	 * 
	 * @param b blcok to check
	 * @return another chest; null if single chest
	 */
	private Chest isDoubleChest(Block b){
	    BlockState state = b.getState();
	    if(!(state instanceof Chest)) return null;
	    
	    Chest chest = (Chest) state;
	    InventoryHolder holder = chest.getInventory().getHolder();
	    
	    if((holder instanceof DoubleChest)){
		    DoubleChest dchest = (DoubleChest) holder;
		    
		    Chest left = (Chest) dchest.getLeftSide();
		    Chest right = (Chest) dchest.getRightSide();
		    
		    if(state.equals(left)){//return location of right chest
		    	return right;
		    }else{//return location of left chest
		    	return left;
		    }
	    }
	    return null;
	}
	
	private Sign isProtectedSign(Block signBlock){

		if(!Config.getConfig().getBoolean("private-chests.enable-private-chests")){
			return null;
		}
		if(signBlock.getType() != Material.WALL_SIGN) return null;
		
		if(!(signBlock.getState() instanceof Sign)) return null;
		Sign sign = (Sign) signBlock.getState();
		
		if(sign.getLine(0) == null || sign.getLine(1) == null) return null;
		if(sign.getLine(0).length() == 0 || sign.getLine(1).length() == 0) return null;
		
		if(sign.getLine(0).equals(SIGN)) return sign;
		
		return null;
	}
	
	/**
	 * 
	 * @param chest chest to check
	 * @return sign
	 */
	private Sign isProtected(Chest chest){

		if(!Config.getConfig().getBoolean("private-chests.enable-private-chests")){
			return null;
		}
		if(chest == null) return null;
		
		for(BlockFace face : faces){
			Block relative = chest.getBlock().getRelative(face);
			if(relative.getState() instanceof Sign){
				if(!relative.getRelative(((org.bukkit.material.Sign) relative.getState()
						.getData()).getAttachedFace()).getLocation().equals(chest.getLocation())){
					continue;
				}
			}
			Sign sign = isProtectedSign(relative);
			if(sign != null) return sign;
		}
		
		return null;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

}
