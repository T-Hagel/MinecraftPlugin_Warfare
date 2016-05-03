package me.lightfall.warfare;

import me.lightfall.warfare.maps.templates.TDMTemplate;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.Vars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MapsPlayerListener implements Listener{
	
	@EventHandler
	public void onPlayerUseWool(PlayerInteractEvent e)
	{
		if(e.getItem() != null && Vars.editingTemplates.containsKey(e.getPlayer().getUniqueId()))
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				if(e.getItem().getType() == Material.WOOL) {
					if(e.getItem().getItemMeta().getDisplayName().endsWith("SET RED SPAWN"))
					{
						((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).setRed(e.getPlayer().getLocation());
						e.getPlayer().sendMessage(Msg.PREFIX + ((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).getName() + "'s red spawn is set!");
					}
					else if(e.getItem().getItemMeta().getDisplayName().endsWith("SET BLUE SPAWN"))
					{
						((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).setBlue(e.getPlayer().getLocation());
						e.getPlayer().sendMessage(Msg.PREFIX + ((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).getName() + "'s blue spawn is set!");
					}
					else if(e.getItem().getItemMeta().getDisplayName().endsWith("SET REFERENCE LOCATION"))
					{
						((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).setReference(e.getPlayer().getLocation());
						e.getPlayer().sendMessage(Msg.PREFIX + ((TDMTemplate) Vars.editingTemplates.get(e.getPlayer().getUniqueId())).getName() + "'s reference is set!");
					}
				}
				else if(e.getItem().getType() == Material.STAINED_CLAY)
				{
					Player player = e.getPlayer();
					if(e.getItem().getItemMeta().getDisplayName().endsWith("TELEPORT TO RED SPAWN"))
					{
						Location l = player.getLocation();
						l.setX(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualRedX());
						l.setY(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualRedY());
						l.setZ(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualRedZ());
						l.setYaw(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getRedYaw());
						player.teleport(l);						
					}
					else if(e.getItem().getItemMeta().getDisplayName().endsWith("TELEPORT TO BLUE SPAWN"))
					{
						Location l = player.getLocation();
						l.setX(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualBlueX());
						l.setY(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualBlueY());
						l.setZ(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getActualBlueZ());
						l.setYaw(((TDMTemplate) Vars.editingTemplates.get(player.getUniqueId())).getBlueYaw());
						player.teleport(l);
					}
					else if(e.getItem().getItemMeta().getDisplayName().endsWith("SAVE"))
					{
						Vars.saveTemplates.get(player.getUniqueId()).copy(Vars.editingTemplates.get(player.getUniqueId()));
						player.sendMessage(Msg.PREFIX + "Saved!");
					}
					else if(e.getItem().getItemMeta().getDisplayName().endsWith("QUIT"))
					{
						Vars.editingTemplates.remove(player.getUniqueId());
						//if(Vars.saveTemplates.containsKey(player.getUniqueId()))
						Vars.saveTemplates.remove(player.getUniqueId());
						player.getInventory().clear();
						player.updateInventory();
					}
				}
				e.setCancelled(true);
			}
	}	
	
}
