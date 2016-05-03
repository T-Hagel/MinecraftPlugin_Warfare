package me.lightfall.warfare.utils.inventorystack;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.utils.Vars;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryStackListener implements Listener{
	
	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent e)
	{
		if(Vars.inventory.get(e.getPlayer().getUniqueId()).hasNext()) {
			new BukkitRunnable(){
				  @Override
				  public void run(){
					  Inventory i = Vars.inventory.get(e.getPlayer().getUniqueId()).get();
					  if(i != null)
						  ((Player) e.getPlayer()).openInventory(i);
					  this.cancel();
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, 0/*Period*/);
		}
	}
}
