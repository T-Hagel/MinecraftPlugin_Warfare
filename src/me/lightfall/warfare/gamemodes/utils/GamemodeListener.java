package me.lightfall.warfare.gamemodes.utils;

import me.lightfall.warfare.utils.Vars;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class GamemodeListener implements Listener{
	
	@EventHandler
	public void onSignDestroy(BlockBreakEvent e)
	{
		if(e.getBlock().getType() == Material.WALL_SIGN)
			if(((Sign) e.getBlock().getState()).getLine(0).contains("Arena "))
				for(Game g : Vars.games)
					if(g.getSign().getLocation().distance(e.getBlock().getLocation()) == 0) {
						e.setCancelled(true);
						break;
					}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		e.blockList().clear();
	}
	
	@EventHandler
	public void onHitBySnowball(EntityDamageByEntityEvent e)
	{
		e.setDamage(0);
		if(e.getDamager() instanceof Snowball)
			if(e.getEntity() instanceof Player)
				if(e.getDamager().hasMetadata("damage") && Vars.playerGames.get(((Player) e.getEntity()).getUniqueId()) != null) {
					Player p = (Player) e.getEntity();
					p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.BLOCK_EXPLOSION, 2));
					Player src = (Player)((Snowball) e.getDamager()).getShooter();
					if(Vars.playerGames.get(p.getUniqueId()).DamagePlayer(src, p, e.getDamager().getMetadata("damage").get(0).asDouble()))
						e.setCancelled(false);
					else
						e.setCancelled(true);
					return;
				}
		e.setCancelled(true);
	}
	
}
