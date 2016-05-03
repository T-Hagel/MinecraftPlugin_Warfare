package me.lightfall.warfare.weapons;

import me.lightfall.warfare.Main;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BurstGun extends BulletGun{
	private int burstDelay;
	
	public BurstGun(boolean zoom, Material mat, Player p) {
		super(zoom, mat, p);
		burstDelay = 5;
	}
	
	public void setBurstDelay(int i) {burstDelay = i;} 
	public int getBurstDelay() {return burstDelay;}

	@Override
	public void fire()
	{
		if(!reload && canShoot  && ammo > 0) {
			canShoot = false;
			final double acc = (player.isSneaking() ? shiftSpread : spread);
			
			new BukkitRunnable(){
				private int i = 0;
				  @Override
				  public void run(){
					  if(i < shotsFired && ammo > 0)
					  {
						  shootBall(acc);
						  ammo--;
						  player.setLevel(--totAmmo);
						  player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
						  if(player.getItemInHand() != null && player.getItemInHand().getType() == material)
							  player.getItemInHand().setAmount(ammo > 127 ? 127 : ammo);
					  }
					  else {
						  cancel();
						  if(ammo == 0)
							  reload();
						  new BukkitRunnable(){
							  private int i = 0;
							  	@Override
							  	public void run(){
							  		if(shotDelay > 40) {
							  			player.setExp(1 - (((float)i )/burstDelay));
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
					  i++;
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, burstDelay/*Period*/);	
			
			}
		else if(ammo == 0)
			reload();
	}
}
