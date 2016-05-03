package me.lightfall.warfare.kits;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.perks.Perk;
import me.lightfall.warfare.weapons.BulletGun;
import me.lightfall.warfare.weapons.Weapon;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Kit {
	private Weapon weap; //Raw weapon
	private Perk perk1;
	private Perk perk2;
	private Perk perk3;
	
	public Kit(Player p)
	{
		weap = new BulletGun(false, Material.WOOD_HOE, p);
		weap.setName(ChatColor.DARK_PURPLE + "Crappy weapon");
		perk1 = perk2 = perk3 = null;
	}
	
	public Perk perk1() {return perk1;}
	public Perk perk2() {return perk2;}
	public Perk perk3() {return perk3;}
	public Weapon getRawWeapon() {return weap;}
	public void setWeapon(Weapon w) {weap = w;}
	public Weapon getWeapon()
	{
		Weapon w = (Weapon) weap.clone();
		if(perk1 != null)
			perk1.modifyWeapon(w);
		if(perk2 != null)
			perk2.modifyWeapon(w);
		if(perk3 != null)
			perk3.modifyWeapon(w);
		
		return w;
	}
	
	public void setPerk(Perk p)
	{
		switch(p.slot){
		case 1:
			perk1 = p;
			break;
		case 2:
			perk2 = p;
			break;
		case 3:
			perk3 = p;
			break;
		}
	}
	
	public void removePerk(Perk p)
	{
		switch(p.slot){
		case 1:
			perk1 = null;
			break;
		case 2:
			perk2 = null;
			break;
		case 3:
			perk3 = null;
			break;
		}
	}
	
	public void applyPerks(Player p)
	{
		Main.setDefault(p);
		if(perk1 != null)
			perk1.applyPerk(p);
		if(perk2 != null)
			perk2.applyPerk(p);
		if(perk3 != null)
			perk3.applyPerk(p);
	}
	
	public boolean contains(Perk p)
	{
		if(perk1 == p || perk2 == p || perk3 == p)
			return true;
		return false;
	}
}
