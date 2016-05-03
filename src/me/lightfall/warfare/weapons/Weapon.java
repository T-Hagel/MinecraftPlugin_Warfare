package me.lightfall.warfare.weapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

abstract public class Weapon implements Cloneable{
	protected int mags;
	protected int totAmmo;
	protected int startingAmmo;
	protected int maxMag;
	protected int ammo;
	protected double damage;
	protected String name;
	protected List<String> lore;
	protected Player player;
	protected double spread;
	protected double shiftSpread;
	protected boolean reload;
	protected int reloadTime;	//In ticks
	protected int shotDelay;	//In ticks
	protected boolean canShoot;
	protected boolean zoomable;
	protected Material material;

	public Weapon()
	{
		lore = new ArrayList<String>();
		mags = 10;
		zoomable = false;
		material = Material.WOOD_SPADE;
		spread = 0.1;
		shiftSpread = 0.05;
		reload = false;
		reloadTime = 50;
		shotDelay = 5;
		damage = 1;
		canShoot = true;
	}
	
	abstract public void fire();
	abstract public void reload();
	
	public int getMagSize() {return maxMag;}
	public void setMagSize(int i) {ammo = i - (maxMag - ammo); maxMag = i;}
	
	public int getAmmo() {return ammo;}
	public void setAmmo(int i) {ammo = i;}
	
	public void giveAmmo()
	{
		totAmmo += ((int)maxMag * 0.1 < 1 ? 1 : (int) maxMag * 0.1);
		player.setLevel(totAmmo);
	}
	
	public int getTotalAmmo() {return totAmmo;}
	public void setTotalAmmo(int i) {totAmmo = i;}
	
	public int getStartingAmmo() {return startingAmmo;}
	public void setStartingAmmo(int i) {startingAmmo = i;}
	
	public double getSpread() {return spread;}
	public void setSpread(double i) {spread = i;}
	
	public double getShiftSpread() {return shiftSpread;}
	public void setShiftSpread(double i) {shiftSpread = i;}
	
	public int getReloadTime() {return reloadTime;}
	public void setReloadTime(int i) {reloadTime = i;}
	
	public int getShotDelay() {return shotDelay;}
	public void setShotDelay(int i) {shotDelay = i;}
	
	public double getDamage() {return damage;}
	public void setDamage(double i) {damage = i;}
	
	public String getName() {return name;}
	public void setName(String n) {name = n;}
	
	public List<String> getLore() {return lore;}
	public void setLore(List<String> l)
	{
		lore.clear();
		for(String s : l)
			lore.add(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	//public Player getPlayer() {return player;}
	//public void setPlayer(Player p) {player = p;}
	
	public boolean zoomable() {return zoomable;}
	public void setZoomable(boolean zoom) {zoomable = zoom;}
	
	public Material getMaterial() {return material;}
	
	public void cancelReload() {reload = false;}
	public void reset()
	{
		ammo = maxMag;
		totAmmo = startingAmmo;
	}
	
	public void giveWeapon()
	{
		if(!player.getInventory().contains(material))
		{
			ItemStack i = new ItemStack(material);
			ItemMeta m = i.getItemMeta();
			if(lore != null) m.setLore(lore);
			if(name != null) m.setDisplayName(name);
			m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			i.setAmount(ammo > 64 ? 64 : ammo);
			i.setItemMeta(m);
			player.getInventory().setItem(0, i);
			player.updateInventory();
			if(player.getItemInHand() != null && player.getItemInHand().getType() == material)
				player.setLevel(totAmmo);
		}
	}
	
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
