package me.lightfall.warfare;

import java.util.ArrayList;
import java.util.List;

import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.utils.Vars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		if(args.length == 1)
		{
			list.add("arena");
			list.add("arenaList");
			list.add("map");
			list.add("reloadSigns");
			list.add("setSpawn");
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("arena")) {
				for(Game g : Vars.games)
					list.add(g.getId() + "");
			}
			else if(args[0].equalsIgnoreCase("map")) {
				list.add("add");
				list.add("createMap");
				list.add("createMapList");
				list.add("createTDMTemplate");
				list.add("editTemplate");
				list.add("set");
			}
			
		}
		else if(args.length == 3){
			if(args[0].equalsIgnoreCase("arena")) {
				Game g = null;
				for(Game n : Vars.games)
					if(n.getId() == Integer.parseInt(args[1]))
					{
						g = n;
						break;
					}
				
				list.add("ready");
				list.add("setDeathTime");
				list.add("setMaxTime");
				list.add("setMap");
				list.add("setMapList");
				list.add("setMinStart");
				list.add("setSize");
				list.add("setTime");
				
				if(g == null || g.getType() == GameType.FFA)
				{
					list.add("addSpawn");
				}
				if(g == null || g.getType() == GameType.TDM)
				{
					list.add("setRedSpawn");
					list.add("setBlueSpawn");
					list.add("teleportRedSpawn");
					list.add("teleportBlueSpawn");
				}
			}
		}
		
		for(String s : list) 
			if(args[args.length - 1].length() >= 1 && s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()) && !s.equalsIgnoreCase(args[args.length - 1])) {
				ArrayList<String> a = new ArrayList<String>();
				a.add(s);
				return a;
			}
		
		return list;
	}

}
