package me.lightfall.warfare.weapons;

import java.util.ArrayList;

import me.lightfall.warfare.Main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BulletGun extends Weapon {

	protected int shotsFired;
	public BulletGun(boolean zoom, Material mat, Player p)
	{
		super();
		shotsFired = 1;
		zoomable = zoom;
		material = mat;
		player = p;
		lore = new ArrayList<String>();
		ammo = maxMag = 32;
		totAmmo = maxMag * 5;
		startingAmmo = totAmmo;
	}
	
	public int getShotsFired() {return shotsFired;}
	public void setShotsFired(int i) {shotsFired = i;}
	
	@Override
	public void reload()
	{
		if(!reload && ammo != maxMag && totAmmo != ammo) {
			reload = true;
			new BukkitRunnable(){
				private int i = 0;
				  @Override
				  public void run(){
					  player.setExp(((float)i )/reloadTime);
					  if(i == reloadTime || reload == false) {
						  this.cancel();
						  player.setExp(0);
						  if(reload) {
								ammo = (totAmmo > maxMag ? maxMag : totAmmo);
								player.getItemInHand().setAmount(ammo > 127 ? 127 : ammo);
								//player.setLevel(totAmmo);
								reload = false;
							}
					  }
					  i++;
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, 1/*Period*/);
		}
	}
	
	@Override
	public void fire()
	{
		if(!reload && canShoot  && ammo > 0) {
			for(int i = 0; i < shotsFired; i++)
				if(player.isSneaking())
					shootBall(shiftSpread);
				else
					shootBall(spread);
			ammo--;
			player.setLevel(--totAmmo);
			player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
			player.getItemInHand().setAmount(ammo > 127 ? 127 : ammo);
			canShoot = false;
			if(ammo == 0)
				reload();
				new BukkitRunnable(){
					private int i = 0;
					  @Override
					  public void run(){
						  if(shotDelay > 20) {
							  player.setExp(1 - (((float)i )/shotDelay));
						  }
						  i++;
						  if(i >= shotDelay) {
							  this.cancel();
							  player.setExp(0);
							  canShoot = true;
						  }
					  }
				}.runTaskTimer(Main.plugin, 0/*DELAY*/, 1/*Period*/);
			}
		else if(ammo == 0)
			reload();
	}
	
	protected void shootBall(double acc)
	{
		Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection()).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
		Snowball snow = player.getWorld().spawn(loc, Snowball.class);
		snow.setShooter(player);
		snow.setVelocity(player.getEyeLocation().getDirection());
		snow.setMetadata("damage", new FixedMetadataValue(Main.plugin, damage));
		double snowX = snow.getVelocity().getX();
		double snowY = snow.getVelocity().getY();
		double snowZ = snow.getVelocity().getZ();
		
		snowX = snowX + (-acc/2.0)+(acc*Math.random()); //Add some variance in the shots. Will add between [-acc/2,acc/2) to velocity components
		snowY = snowY + (-acc/2.0)+(acc*Math.random());
		snowZ = snowZ + (-acc/2.0)+(acc*Math.random());
		snow.setVelocity(new Vector(snowX,snowY,snowZ).multiply(3)); //Set the velocity with directions multiplied by 3. Can be modified for less/more distance and speed
	}
			
}
