package me.lightfall.warfare;

import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.maps.Map;
import me.lightfall.warfare.maps.MapList;
import me.lightfall.warfare.maps.MapTemplate;
import me.lightfall.warfare.maps.templates.TDMTemplate;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.Vars;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

abstract class MapCommands{

	static boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(args.length > 2) {
			if(args[1].equalsIgnoreCase("set"))
			{
				if(args.length < 4)
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need more args!");
				else
				{
					//if(args[2].equalsIgnoreCase("map"))
					//{
						Map m = null;
						for(Map map: Vars.maps)
							if(map.getName().equalsIgnoreCase(args[2]))
							{
								m = map;
								break;
							}
						if(m != null) {
							if(args[3].equalsIgnoreCase("reference"))
							{
								m.setReference(player.getLocation());
								player.sendMessage(Msg.PREFIX + m.getName() + "'s reference has been set!");
							}
							else if(args[3].equalsIgnoreCase("name"))
							{
								m.setName(args[4]);
								player.sendMessage(Msg.PREFIX + "The map is now called " + m.getName() + ".");
							}
							else if(args[3].equalsIgnoreCase("template"))
							{
								for(MapTemplate te : Vars.Templates)
									if(te.getName().equalsIgnoreCase(args[4]))
									{
										m.setTemplate(te);
										player.sendMessage(Msg.PREFIX + te.getName() + " template ("+ te.getType() + ") has been set for map " + m.getName());
									}
							}
							else
								player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "Unknown command!");
						}
						else
							player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "Map not found!");
					//}
					/*else if(args[2].equalsIgnoreCase("tempalte"))
					{
						
					}
					else
						player.sendMessage(Msg.PREFIX + "Unknown command!");
					*/
				}
			}
			else if(args[1].equalsIgnoreCase("createMap"))
			{
				if(args.length < 3)
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need to give a name!");
				else
				{
					Map m = null;
					for(Map map: Vars.maps)
						if(map.getName().equalsIgnoreCase(args[2]))
						{
							m = map;
							break;
						}
					if(m == null) {
						Map map = new Map(args[2], player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
						Vars.maps.add(map);
						player.sendMessage(Msg.PREFIX + "Map " + map.getName() + " created!");
					}
					else
						player.sendMessage(Msg.PREFIX + "Map " + m.getName() + " already exists!");
				}
			}
			else if(args[1].equalsIgnoreCase("createMapList"))
			{
				if(args.length < 3)
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need to give a name!");
				else
				{
					MapList m = null;
					for(MapList map: Vars.mapLists)
						if(map.getName().equalsIgnoreCase(args[2]))
						{
							m = map;
							break;
						}
					if(m == null) {
						MapList ml = new MapList(args[2]);
						Vars.mapLists.add(ml);
						player.sendMessage(Msg.PREFIX + "MapList " + ml.getName() + " created!");
					}
					else
						player.sendMessage(Msg.PREFIX + "MapList " + m.getName() + " exists!");
				}
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				if(args.length < 4)
				{
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need more arguments");
				}
				else
				{
					MapList ml = null;
					for(MapList map: Vars.mapLists)
						if(map.getName().equalsIgnoreCase(args[2]))
						{
							ml = map;
							break;
						}
					if(ml == null)
						player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "MapList '" + args[2] + "' not found!");
					else
					{
						Map m = null;
						for(Map map: Vars.maps)
							if(map.getName().equalsIgnoreCase(args[3]))
							{
								m = map;
								break;
							}
						if(m == null)
							player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "Map '" + args[3] + "' not found!");
						else
						{
							ml.addMap(m);
							player.sendMessage(Msg.PREFIX + "Added " + m.getName() + " to map list " + ml.getName() + ".");
						}
					}
				}
			}
			else if(args[1].equalsIgnoreCase("createTDMTemplate"))
			{
				if(args.length < 3)
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need to give a name!");
				else
				{
					for(MapTemplate t : Vars.Templates)
						if(t.getName().equalsIgnoreCase(args[2]))
						{
							player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + args[2] + " already exists.");
							return false;
						}
					
					TDMTemplate tp = new TDMTemplate(args[2], player.getLocation());
					Vars.Templates.add(tp);
					Vars.editingTemplates.put(player.getUniqueId(), tp);
					ItemStack s = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
					ItemMeta meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "SET RED SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.BLUE + "SET BLUE SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.WHITE + "SET REFERENCE LOCATION");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "TELEPORT TO RED SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.BLUE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.BLUE + "TELEPORT TO BLUE SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.BLACK.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.GRAY + "QUIT");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					player.updateInventory();
					
					player.sendMessage(Msg.PREFIX + "Created TDMTemplate " + args[2] + ". Your current location is the reference point!");
				}
			}
			else if(args[1].equalsIgnoreCase("editTemplate"))
			{
				if(args.length < 3) {
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "You need more arguments");
				}
				else
				{
					MapTemplate t = null;
					if(args.length == 3)
					{
					for(MapTemplate te : Vars.Templates)
						if(te.getName().equalsIgnoreCase(args[2]))
						{
							t = te;
							break;
						}
					if(t == null)
					{
						player.sendMessage(Msg.PREFIX + "No template found!");
						return false;
					}
					player.sendMessage(Msg.PREFIX + "Found template " + t.getName() + " which is type " + GameType.searchTemplate(t) + ".");
					
					
					}
					else if(args.length == 4)
					{
						Class<MapTemplate> cl = GameType.searchNameTemplate(args[3]);
						if(cl == null)
							player.sendMessage(Msg.PREFIX + "Unknown template type!");
						else
						{
							for(MapTemplate te : Vars.Templates)
								if(te.getClass().equals(cl) && te.getName().equalsIgnoreCase(args[2]))
								{
									t = te;
									break;
								}
							if(t == null)
							{
								player.sendMessage(Msg.PREFIX + "No template found!");
								return false;
							}
						}
						
					}
					
					//setup schtuff
					try {
						Vars.editingTemplates.put(player.getUniqueId(), (GameType.searchNameTemplate(t.getType())).newInstance());
					} catch (Exception e) {
						player.sendMessage(Msg.PREFIX + "ERROR TRYING TO EDIT TEMPLATE!");
						player.sendMessage(Msg.PREFIX + "This should not have happened!");
						e.printStackTrace();
						return false;
					}
					Vars.editingTemplates.get(player.getUniqueId()).copy(t);
					Vars.editingTemplates.get(player.getUniqueId()).setName(t.getName() + "_temp");
					Vars.saveTemplates.put(player.getUniqueId(), t);
					ItemStack s = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
					ItemMeta meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "SET RED SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.WOOL, 1, DyeColor.BLUE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.BLUE + "SET BLUE SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.WOOL, 1, DyeColor.WHITE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.WHITE + "SET REFERENCE LOCATION");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "TELEPORT TO RED SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.BLUE.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.BLUE + "TELEPORT TO BLUE SPAWN");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.GREEN.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "SAVE");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					s = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.BLACK.getData());
					meta = s.getItemMeta();
					meta.setDisplayName(ChatColor.GRAY + "QUIT");
					s.setItemMeta(meta);
					player.getInventory().addItem(s);
					
					player.updateInventory();
				}
			}
			else
				player.sendMessage(Msg.PREFIX + ChatColor.translateAlternateColorCodes('&', "&4Unknown command!"));
		}
		else
		{
			String s = "";
			for(Map m : Vars.maps)
			{
				s += m.getName() + ",";
			}
			player.sendMessage(Msg.PREFIX + "Maps: " + (s == "" ? "No maps!" : s));
		}
		
		return false;
	}
	
}
