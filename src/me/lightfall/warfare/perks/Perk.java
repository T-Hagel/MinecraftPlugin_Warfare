package me.lightfall.warfare.perks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.lightfall.warfare.weapons.Weapon;

public enum Perk {
	//Tier 1
	BANDOLIER("Bandolier", "Increases your starting ammo by 10%", 1, 19, 0),
	JUGGERNAUT("Juggernaut", "Increases your health to 25", 1, 20, 0),
	DOUBLE_TAP("Double tap", "Increases rate of fire", 1, 21, 0),
	
	//Tier 2
	SLEIGHT_OF_HAND("Sleight of hand", "Decreases reload time by 10%", 2, 28, 0),
	STEADY_AIM("Steady aim", "Increases hip-fire accuracy", 2, 29, 1),
	SWIFTNESS("Swiftness", "Gives speed I", 2, 30, 2),
	
	//Tier 3
	STOPPING_POWER("Stopping power", "Increases damage by 10%", 3, 37, 0),
	SCAVENGER("Scavenger", "Get ammo for kills", 3, 38, 0),
	DOUBLE_JUMP("Double jump", "Allows double jump and dash jump", 3, 39, 3);
	
	public String name;
	public String desc;
	public int slot;
	public int editSlot;
	public int level;
	
	Perk(String name, String description, int slot, int editSlot, int level)
	{
		this.name = name;
		this.desc = ChatColor.DARK_AQUA + description + "\nRequired level: " + ChatColor.LIGHT_PURPLE + level;
		this.slot = slot;
		this.editSlot = editSlot;
		this.level = level;
	}
	
	public void modifyWeapon(Weapon w)
	{
		switch(this)
		{
		case BANDOLIER:
			w.setStartingAmmo((int) ((int)w.getStartingAmmo() * 1.1 == w.getStartingAmmo() ? w.getStartingAmmo() + 1 : w.getStartingAmmo() * 1.1));
			break;
		case SLEIGHT_OF_HAND:
			w.setReloadTime((int) (((int)w.getReloadTime() * 0.90) == w.getReloadTime() ? w.getReloadTime() - 1 : w.getReloadTime() * 0.90));
			break;
		case STEADY_AIM:
			w.setSpread(w.getSpread() * .5);
			break;
		case STOPPING_POWER:
			w.setDamage(w.getDamage() * 1.1);
			break;
		case DOUBLE_TAP:
			w.setShotDelay((int) (w.getShotDelay() * .90));
			break;
		}
	}
	
	public void applyPerk(Player p)
	{
		switch(this)
		{
		case JUGGERNAUT:
			p.setMaxHealth(25);
			p.setHealth(25);
			break;
		case SWIFTNESS:
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
			break;
		}
	}
	
	static public Perk searchByName(String s)
	{
		for(Perk p : values())
			if(p.name.equalsIgnoreCase(s))
				return p;
		return null;
	}
}
