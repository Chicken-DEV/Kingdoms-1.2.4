package com.songoda.kingdoms.constants;

import java.util.ArrayList;

import com.songoda.kingdoms.main.Kingdoms;
import com.songoda.kingdoms.utils.LoreOrganizer;
import com.songoda.kingdoms.utils.Materials;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
/**
 * 
 * @Deprecated Old siege concept. No longer used.
 * @author Leonard
 *
 */
public enum SiegeType {

//	CONCENTRATED(Kingdoms.getLang().getString("Guis_SiegeEngine_ConcentratedBlast,
//			Kingdoms.getLang().getString("Guis_SiegeEngine_ConcentratedBlast_Desc),
//	SHIELD_SHATTER(Kingdoms.getLang().getString("Guis_SiegeEngine_ShieldShatter,
//			Kingdoms.getLang().getString("Guis_SiegeEngine_ShieldShatter_Desc),;
	;
	private String title;
	private String desc;
	
	SiegeType(String title, String desc){
		this.title = title;
		this.desc = desc;
	}
	
	public int getCost(){
//		switch(this){
//			case CONCENTRATED:
//				return Kingdoms.config.siegeConcentratedBlastCost;
//			case SHIELD_SHATTER:
//				return Kingdoms.config.siegeShieldShatterCost;
//		}
		return 0;
//		
	}
	
	public ItemStack getTorch(){

		ItemStack torch = new ItemStack(Materials.REDSTONE_TORCH.parseMaterial());
		ItemMeta concmeta = torch.getItemMeta();
		concmeta.setDisplayName(title);
		ArrayList concl = new ArrayList();
		concl.add(desc);
		concl.add(Kingdoms.getLang().getString("Guis_SiegeEngine_Deploy_Instructions"));
		concmeta.setLore(LoreOrganizer.organize(concl));
		torch.setItemMeta(concmeta);
		
		return torch;
	
	}
	

	public ItemStack getPurchaseIcon(){

		ItemStack torch = new ItemStack(Materials.REDSTONE_TORCH.parseMaterial());
		ItemMeta concmeta = torch.getItemMeta();
		concmeta.setDisplayName(title);
		ArrayList concl = new ArrayList();
		concl.add(desc);
		concl.add(Kingdoms.getLang().getString("Guis_Cost_Text").replaceAll("%cost%", ""+ getCost()));
		concmeta.setLore(LoreOrganizer.organize(concl));
		torch.setItemMeta(concmeta);
		
		return torch;
	
	}
	
	public static SiegeType fromItemStack(ItemStack item){
		if(item == null) return null;
		if(item.getItemMeta() == null) return null;
		if(item.getItemMeta().getDisplayName() == null) return null;
		for(SiegeType type:SiegeType.values()){
			if(type.getTorch().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())){
				return type;
			}
		}
		return null;
	}
	
}
