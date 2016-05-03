package me.lightfall.warfare;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import me.lightfall.warfare.gamemodes.FreeForAll;
import me.lightfall.warfare.gamemodes.TeamDeathMatch;
import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.maps.Map;
import me.lightfall.warfare.maps.MapList;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.Vars;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

abstract class ArenaCommands {
	
	static boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		Game g = null;
		for(Game q : Vars.games)
			if(q.getId() == Integer.parseInt(args[1])) {
				g = q; 
				break;
		}
	  try
	  {
		if(g != null) {
			if(args[2].equalsIgnoreCase("ready")) {
				if(g.ready())
					player.sendMessage(Msg.PREFIX + "The game is ready!");
				else
					player.sendMessage(Msg.PREFIX + "The game is not ready!");
			}
			else if(args[2].equalsIgnoreCase("remove")) {
				player.sendMessage(Msg.PREFIX + "Removed arena " + g.getId() + ".");
				g.close();
				Vars.games.remove(g);
			}
			else if(args[2].equalsIgnoreCase("setTime")) {
				//Takes time in seconds
				final NumberFormat form = new DecimalFormat("#00");
				g.setTime(Integer.parseInt(args[3]));
				player.sendMessage(Msg.PREFIX + "Time has been set to " + g.getTime()/60 + ":" + form.format(g.getTime()%60) + ".");
			}
			else if(args[2].equalsIgnoreCase("setMaxTime")) {
				//Takes time in seconds
				final NumberFormat form = new DecimalFormat("#00");
				g.setMaxTime(Integer.parseInt(args[3]));
				player.sendMessage(Msg.PREFIX + "MaxTime has been set to " + g.getMaxTime()/60 + ":" + form.format(g.getMaxTime()%60) + ".");
			}
			else if(args[2].equalsIgnoreCase("setMinStart")) {
				g.setMinStart(Integer.parseInt(args[3]));
				player.sendMessage(Msg.PREFIX + "Minimum starting players has been set to " + g.getMinStart() + ".");
			}
			else if(args[2].equalsIgnoreCase("setDeathTime")) {
				//Takes time in seconds
				g.setDeathTime(Integer.parseInt(args[3]));
				//g.getMaxTime returns in ticks
				player.sendMessage(Msg.PREFIX + "Death time has been set to " + (double)g.getDeathTime()/20 + "s (" + g.getDeathTime() + " ticks)." );
			}
			else if(args[2].equalsIgnoreCase("setMap")) {
				Map m = null;
				for(Map map : Vars.maps)
					if(map.getName().equalsIgnoreCase(args[3]))
					{
						m = map;
						break;
					}
				if(m == null)
				{
					player.sendMessage(Msg.PREFIX + "Map \"" + m.getName() + "\" not found.");
				}
				else
				{
					g.setMap(m);
					player.sendMessage(Msg.PREFIX + "Arena " +g.getId() + " is now using map " + m.getName() + ".");
				}
			}
			else if(args[2].equalsIgnoreCase("setMapList")) {
				MapList ml = null;
				for(MapList map : Vars.mapLists)
					if(map.getName().equalsIgnoreCase(args[3]))
					{
						ml = map;
						break;
					}
				if(ml == null)
				{
					player.sendMessage(Msg.PREFIX + "MapList \"" + ml.getName() + "\" not found.");
				}
				else
				{
					g.setMapList(ml);
					if(g.loadNextMap())
						player.sendMessage(Msg.PREFIX + "Arena " +g.getId() + " is now using MapList " + ml.getName() + " and map " + g.getMap().getName() + ".");
					else
						player.sendMessage(Msg.PREFIX + "Arena " +g.getId() + " is now using MapList " + ml.getName() + ".");
				}
			}
			else if(args[2].equalsIgnoreCase("setRedSpawn")) {
				try {
					if(((TeamDeathMatch)g).setRedSpawn(player))
						player.sendMessage(Msg.PREFIX + "Set red spawn for arena " + g.getId() + "!");
				}
				catch (ClassCastException e)
				{
					player.sendMessage(Msg.PREFIX + "This arena does not support that command!");
				}
				catch (Exception e)
				{
					player.sendMessage(Msg.PREFIX + "Unable to complete command \"setRedSpawn\" for some reason.");
				}
			}
			else if(args[2].equalsIgnoreCase("setBlueSpawn")) {
				try {
					if(((TeamDeathMatch)g).setBlueSpawn(player))
						player.sendMessage(Msg.PREFIX + "Set blue spawn for arena " + g.getId() + "!");
				}
				catch (ClassCastException e)
				{
					player.sendMessage(Msg.PREFIX + "This arena does not support that command!");
				}
				catch (Exception e)
				{
					player.sendMessage(Msg.PREFIX + "Unable to complete command \"setBlueSpawn\" for some reason.");
				}
			}
			else if(args[2].equalsIgnoreCase("addSpawn")){
				try {
					if(((FreeForAll) g).addSpawn(player))
						player.sendMessage(Msg.PREFIX + "Added spawn for arena " + g.getId() + "!");
				}
				catch (ClassCastException e)
				{
					player.sendMessage(Msg.PREFIX + "This arena does not support that command!");
				}
				catch (Exception e)
				{
					player.sendMessage(Msg.PREFIX + "Unable to complete command \"addSpawn\" for some reason.");
				}
			}
			else if(args[2].equalsIgnoreCase("teleportRedSpawn")) {
				try {
					player.teleport(((TeamDeathMatch)g).getRedSpawn());
				}
				catch (NullPointerException e)
				{
					player.sendMessage(Msg.PREFIX + "Red spawn is not set!");
				}
				catch (ClassCastException e)
				{
					player.sendMessage(Msg.PREFIX + "This arena does not support that command!");
				}
				catch (Exception e)
				{
					player.sendMessage(Msg.PREFIX + "Unable to complete command \"teleportRedSpawn\" for some reason.");
				}
			}
			else if(args[2].equalsIgnoreCase("teleportBlueSpawn")) {
				try {
					player.teleport(((TeamDeathMatch)g).getBlueSpawn());
				}
				catch (NullPointerException e)
				{
					player.sendMessage(Msg.PREFIX + "Blue spawn is not set!");
				}
				catch (ClassCastException e)
				{
					player.sendMessage(Msg.PREFIX + "This arena does not support that command!");
				}
				catch (Exception e)
				{
					player.sendMessage(Msg.PREFIX + "Unable to complete command \"teleportBlueSpawn\" for some reason.");
				}
			}
			else if(args[2].equalsIgnoreCase("setSize")) {
					try {
						g.setMaxSize(Integer.parseInt(args[3]));
						player.sendMessage(Msg.PREFIX + "Set size for arena " + g.getId() + " to " + g.getMaxSize() + "!");
					}
					catch (Exception e)
					{
						player.sendMessage(Msg.PREFIX + "Unable to complete command \"setSize\" for some reason.");
					}
				}
			}
			else
				player.sendMessage(Msg.PREFIX + "Could not find that game ID.");
	  }
	  catch(Exception e)
	  {
		  player.sendMessage(Msg.PREFIX + ChatColor.DARK_GREEN + "Unable to complete command for some reason!");
		  e.printStackTrace();
	  }
		
		return false;
	}

}
