package me.lightfall.warfare;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.gamemodes.utils.GamemodeListener;
import me.lightfall.warfare.kits.Kit;
import me.lightfall.warfare.kits.KitListener;
import me.lightfall.warfare.maps.Map;
import me.lightfall.warfare.maps.MapList;
import me.lightfall.warfare.maps.MapTemplate;
import me.lightfall.warfare.perks.Perk;
import me.lightfall.warfare.utils.Items;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.SQLConnection;
import me.lightfall.warfare.utils.Vars;
import me.lightfall.warfare.utils.inventorystack.InventoryStackListener;
import me.lightfall.warfare.weapons.Weapon;
import me.lightfall.warfare.weapons.WeaponsListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Main extends JavaPlugin{
	public static Plugin plugin;
	public static Location lobby;
	public static YamlConfiguration WeaponConfig;
	public static YamlConfiguration ArenaConfig;
	public static YamlConfiguration MapConfig;
	public static YamlConfiguration MapListConfig;
	public static YamlConfiguration TemplateConfig;
	public static Set<String> Weapons;
	public static Set<String> Arenas;
	public static Set<String> Maps;
	public static Set<String> MapLists;
	public static Set<String> Templates;
	public static SQLConnection sql;
	
	@Override
	public void onEnable()
	{
		//
		plugin = this;
		lobby = null;
		sql = new SQLConnection("127.0.0.7:3306", "user", "pass", "db");
		try {
			sql.connect();
		}
		catch(Exception e)
		{
			System.out.println(Msg.CONSOLE_PREFIX + "Error connecting to DB!\n" + e.getMessage());
		}
		
		/* Load configs
		 * 	-MainConfig
		 *  -WeaponConfig
		 *  -ArenaConfig
		 *  -MapConfig
		 */
		
		//Load weapons
		loadWeaponConfig();
		
		//Load templates
		TemplateConfig = loadDefault(new File(getDataFolder(), "TemplateConfig.yml"));
		Templates = loadConfig(TemplateConfig, "template");
		loadTemplates();
		
		//Load maps
		MapConfig = loadDefault(new File(getDataFolder(), "MapConfig.yml"));
		Maps = loadConfig(MapConfig, "map");
		loadMaps();
		
		//Load maplists
		MapListConfig = loadDefault(new File(getDataFolder(), "MapListConfig.yml"));
		MapLists = loadConfig(MapListConfig, "mapList");
		loadMapLists();
		
		//load arenas
		ArenaConfig = loadDefault(new File(getDataFolder(), "ArenaConfig.yml"));
		Arenas = loadConfig(ArenaConfig, "arena");
		loadArenas();
		
		//Event Listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MainPlayerListener(), this);
		pm.registerEvents(new MapsPlayerListener(), this);
		pm.registerEvents(new WeaponsListener(), this);
		pm.registerEvents(new GamemodeListener(), this);
		pm.registerEvents(new KitListener(), this);
		pm.registerEvents(new InventoryStackListener(), this);
		
		//Tab completer
		getCommand("Warfare").setTabCompleter(new CommandTabCompleter());
		
		//Deal with reloads
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "Server reloading... re-joining you to game!");
			MainPlayerListener.join(p);
		}
		
		
		//Lobby scoreboard manager
		new BukkitRunnable(){
			  @Override
			  public void run(){
				  for(Player p : Bukkit.getOnlinePlayers())
					  if(Vars.playerGames.get(p.getUniqueId()) == null)
						  setScoreboard(p.getUniqueId());
			  }
		}.runTaskTimer(Main.plugin, 0/*DELAY*/, 100/*Period*/);

	}
	
	@Override
	public void onDisable()
	{
		saveTemplates();
		saveMaps();
		saveMapLists();
		saveArenas();
	}
	
	private void saveTemplates()
	{
		for(String s : TemplateConfig.getKeys(false))
			TemplateConfig.set(s, null);
		
		for(MapTemplate t : Vars.Templates)
		{
			ConfigurationSection s = TemplateConfig.createSection("template." + t.getName());
			s.set("type", t.getType());
			t.writeToConfig(s);
		}
		try {
			TemplateConfig.save(new File(getDataFolder(), "TemplateConfig.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveMaps()
	{
		for(String s : MapConfig.getKeys(false))
			MapConfig.set(s, null);
		
		for(Map a : Vars.maps)
		{
			MapConfig.set("map." + a.getName() + ".x", a.getX());
			MapConfig.set("map." + a.getName() + ".y", a.getY());
			MapConfig.set("map." + a.getName() + ".z", a.getZ());
			for(GameType g : GameType.values())
				if(a.getTemplate(g) != null)
					MapConfig.set("map." + a.getName() + "." + g.name, a.getTemplate(g).getName());
			
		}
		try {
			MapConfig.save(new File(getDataFolder(), "MapConfig.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveMapLists()
	{
		for(String s : MapListConfig.getKeys(false))
			MapListConfig.set(s, null);
		
		for(MapList a : Vars.mapLists)
		{
			List<String> s = new ArrayList<String>();
			for(Map m : a.getMaps())
				s.add(m.getName());
			MapListConfig.set("mapList." + a.getName(), s);
		}
		try {
			MapListConfig.save(new File(getDataFolder(), "MapListConfig.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadTemplates()
	{
		if(Templates != null)
		for(String s : Templates)
		{
			ConfigurationSection c = TemplateConfig.getConfigurationSection("template." + s);
			MapTemplate t = null;
			String name = c.getString("type");
			for(GameType g : GameType.values())
				if(g.name.equalsIgnoreCase(name))
					try {
						t = g.t.newInstance();
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
			if(t != null)
			{
				t.setName(s);
				t.loadFromConfig(c);
			}
			Vars.Templates.add(t);
		}
	}
	
	private void loadMaps()
	{
		if(Maps != null)
		for(String s : Maps)
		{
			ConfigurationSection c = MapConfig.getConfigurationSection("map." + s);
			Map m = new Map(s, c.getDouble("x"), c.getDouble("y"), c.getDouble("z"));
			for(GameType g : GameType.values())
			{
				if(c.contains(g.name))
				{
					for(MapTemplate t : Vars.Templates)
						if(t.getName().equalsIgnoreCase(c.getString(g.name)))
						{	
							m.setTemplate(t);
							break;
						}
				}
			}
			Vars.maps.add(m);
		}
	}
	
	private void loadMapLists()
	{
		if(MapLists != null)
		for(String s : MapLists)
		{
			//ConfigurationSection c = MapListConfig.getConfigurationSection("mapList." + s);
			MapList m = new MapList(s);
			for(String map : MapListConfig.getStringList("mapList." + s))
			{
				for(Map mL : Vars.maps)
					if(mL.getName().equalsIgnoreCase(map))
					{
						m.addMap(mL);
						break;
					}
			}
			
			Vars.mapLists.add(m);
		}
	}
	
	
	private void saveArenas()
	{
		for(String s : ArenaConfig.getKeys(false))
			ArenaConfig.set(s, null);
		
		for(Game a : Vars.games)
		{
			ArenaConfig.set("arena." + a.getId() + ".type", GameType.searchNameGame(a));
			ArenaConfig.set("arena." + a.getId() + ".maxSize", a.getMaxSize());
			ArenaConfig.set("arena." + a.getId() + ".minStart", a.getMinStart());
			ArenaConfig.set("arena." + a.getId() + ".deathTime", a.getDeathTime());
			ArenaConfig.set("arena." + a.getId() + ".timeMax", a.getMaxTime());
			if(a.getMapList() != null)
				ArenaConfig.set("arena." + a.getId() + ".mapList", a.getMapList().getName());
			else
			{
				if(a.getMap() != null)
					ArenaConfig.set("arena." + a.getId() + ".map", a.getMap().getName());
				else
				{
					
				}
			}
			ArenaConfig.set("arena." + a.getId() + ".sign.x", a.getSign().getX());
			ArenaConfig.set("arena." + a.getId() + ".sign.y", a.getSign().getY());
			ArenaConfig.set("arena." + a.getId() + ".sign.z", a.getSign().getZ());
		}
		try {
			ArenaConfig.save(new File(getDataFolder(), "ArenaConfig.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadArenas()
	{
		if(Arenas != null)
		for(String s : Arenas)
		{
			Block b = Bukkit.getWorlds().get(0).getBlockAt(ArenaConfig.getInt("arena." + s + ".sign.x"),
					ArenaConfig.getInt("arena." + s + ".sign.y"),
					ArenaConfig.getInt("arena." + s + ".sign.z"));
			b.setType(Material.WALL_SIGN);
			Sign sign = (Sign) b.getState();
			
			for(GameType n : GameType.values())
				if(ArenaConfig.getString("arena." + s + ".type").equalsIgnoreCase(n.name))
				{
					Game g = null;
					try {
						g = n.c.newInstance();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if(g != null)
					{
						ConfigurationSection c = ArenaConfig.getConfigurationSection("arena." + s);
						Vars.games.add(g);
						g.setSign(sign);
						g.setMaxSize(c.getInt("maxSize"));
						g.setMinStart(c.getInt("minStart"));
						g.setDeathTime(c.getInt("deathTime"));
						g.setMaxTime(c.getInt("timeMax"));
						if(c.contains("mapList"))
						{
							for(MapList m : Vars.mapLists)
								if(m.getName().equalsIgnoreCase(c.getString("mapList")))
								{
									g.setMapList(m);
									g.loadNextMap();
									break;
								}
							if(g.getMapList() == null)
								System.out.println("Error loading mapList for " + g.getId());
						}
						else if(c.contains("map"))
						{
							for(Map m : Vars.maps)
								if(m.getName().equalsIgnoreCase(c.getString("map")))
								{
									g.setMap(m);
									break;
								}
						}
						g.ready();
						g.updateBoard();
					}
					break;
				}
		}
	}
	
	private Set<String> loadConfig(YamlConfiguration config, String name)
	{
		Set<String> var = null;
		if(config.contains(name))
		{
			var = config.getConfigurationSection(name).getKeys(false);
			System.out.println(Msg.CONSOLE_PREFIX + "Loaded " + var.size() + " " + name + "s.");
		}
		else
			System.out.println(Msg.CONSOLE_PREFIX + "No " + name + "s found");
		return var;
	}
	
	public static void setDefault(Player p)
	{
		p.setMaxHealth(20);
		p.setHealth(20);
		for(PotionEffect eff : p.getActivePotionEffects())
			p.removePotionEffect(eff.getType());
	}
	
	private void loadWeaponConfig()
	{
		WeaponConfig = loadDefault(new File(getDataFolder(), "WeaponConfig.yml"));
		Weapons = loadConfig(WeaponConfig, "weapon");
	}
	
	public static void joinLobby(UUID u)
	{
		Player p = Bukkit.getPlayer(u);
		setDefault(p);
		Vars.playerGames.put(u, null);
		if(lobby != null)
			p.teleport(lobby);
		setScoreboard(u);
		p.getInventory().clear();
		p.setLevel(0);
		
		ItemStack i = new ItemStack(Items.LOBBY_WEAPON_SELECT_MATERIAL);
		ItemMeta data = i.getItemMeta();
		data.setDisplayName(Items.LOBBY_WEAPON_SELECT_NAME);
		i.setAmount(1);
		i.setItemMeta(data);
		p.getInventory().setItem(8, i);
		
		i = new ItemStack(Items.LOBBY_KIT_MATERIAL);
		data = i.getItemMeta();
		data.setDisplayName(Items.LOBBY_KIT_NAME);
		i.setAmount(1);
		i.setItemMeta(data);
		p.getInventory().setItem(0, i);
	}
	
	
	static YamlConfiguration loadDefault(File file)
	{		
		try {
			String name = file.getName();
			
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
				
				InputStream is = plugin.getResource("defaults/" + name);
				BufferedReader bf = new BufferedReader(new InputStreamReader(is));
				PrintWriter pw = new PrintWriter(file);
				System.out.println(Msg.CONSOLE_PREFIX + "Printing default file for " + name);
				String s = bf.readLine();
				while(s != null) {
					pw.println(s);
					s = bf.readLine();
				}
				
				bf.close();
				pw.close();
			}
			else
			{
				System.out.println(Msg.CONSOLE_PREFIX + "Found config for " + name + ".");
			}
		}
		catch(Exception e)
		{
			System.out.println(Msg.CONSOLE_PREFIX + "Error trying to create " + file.getAbsolutePath() + ".");
			e.printStackTrace();
		}
		
		return YamlConfiguration.loadConfiguration(file);
	}
	
	
	static public void setScoreboard(UUID u)
	{
		
		Scoreboard s = Bukkit.getPlayer(u).getScoreboard();
		Objective obj;
		String name = Msg.MAIN_SCOREBOARD_NAME;
		if(name.length() > 16)
			name = name.substring(0, 15);
		if(s.getObjective(name) != null) {
			obj = s.getObjective(name);
			obj.unregister();
		}
		obj = s.registerNewObjective(name, "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score;
		score = obj.getScore(ChatColor.AQUA + "Arenas ");
		score.setScore(15);
		score = obj.getScore(ChatColor.WHITE.toString() + "   " + Vars.games.size());
		score.setScore(14);
		score = obj.getScore(ChatColor.AQUA + "Players online");
		score.setScore(13);
		score = obj.getScore(ChatColor.RESET.toString() + "   " + Bukkit.getOnlinePlayers().size());
		score.setScore(12);
		score = obj.getScore(ChatColor.AQUA + "Maps");
		score.setScore(11);
		score = obj.getScore(ChatColor.RESET + ChatColor.WHITE.toString() + "   " + Vars.maps.size());
		score.setScore(10);
		score = obj.getScore(ChatColor.AQUA + "Templates");
		score.setScore(9);
		score = obj.getScore(ChatColor.RESET + "" + ChatColor.RESET + ChatColor.WHITE.toString() + "   " + Vars.Templates.size());
		score.setScore(8);
		score = obj.getScore(ChatColor.AQUA + "Level");
		score.setScore(7);
		score = obj.getScore(ChatColor.GOLD + "   " + Vars.players.get(u).getLevel());
		score.setScore(6);
		score = obj.getScore(ChatColor.AQUA + "Total experience");
		score.setScore(5);
		score = obj.getScore(ChatColor.RESET.toString() + ChatColor.GOLD + "   " + Vars.players.get(u).getTotalExp());
		score.setScore(4);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = null;
		if(sender instanceof Player)
			player = (Player) sender;
		try {
			if(label.equalsIgnoreCase("Warfare"))
			{
				if(args.length == 0)
					player.sendMessage(Msg.PREFIX + "You need more arguments!");
				else
				{
					if(args[0].equalsIgnoreCase("updateboard"))
					{
						if(Vars.playerGames.get(player.getUniqueId()) != null)
							Vars.playerGames.get(player.getUniqueId()).updateBoard();
					}
					else if(args[0].equalsIgnoreCase("setLevel"))
					{
						Vars.players.get(player.getUniqueId()).setLevel(Integer.parseInt(args[1]));
						player.sendMessage("Level set to: " + Vars.players.get(player.getUniqueId()).getLevel());
					}
					else if(args[0].equalsIgnoreCase("saveArenas"))
					{
						saveArenas();
						player.sendMessage(Msg.PREFIX + "Saving arenas...");
					}
					else if(args[0].equalsIgnoreCase("reloadWeapons"))
					{
						loadWeaponConfig();
						if(sender instanceof Player)
							player.sendMessage(Msg.PREFIX + "Weapons config reloaded!");
						else
							System.out.println(Msg.CONSOLE_PREFIX + "Weapons config reloaded!");
					}
					else if(args[0].equalsIgnoreCase("giveweapon"))
					{
						Vars.players.get(player.getUniqueId()).giveWeapon();
					}
					else if(args[0].equalsIgnoreCase("giveperks"))
					{
						Kit k = Vars.players.get(player.getUniqueId()).getKit();
						k.setPerk(Perk.SLEIGHT_OF_HAND);
						k.setPerk(Perk.BANDOLIER);
						k.setPerk(Perk.STOPPING_POWER);
					}
					else if(args[0].equalsIgnoreCase("weaponStats"))
					{
						Weapon w = Vars.weapons.get(player.getUniqueId());
						player.sendMessage(	  "Damage: " + w.getDamage() + '\n'
											+ "Mag Size: " + w.getMagSize() + '\n'
											+ "Reload Time: " + w.getReloadTime() + '\n'
											+ "Spread: " + w.getSpread() + '\n'
											+ "Shift Spread: " + w.getShiftSpread() + '\n'
											+ "Shot Delay: " + w.getShotDelay() + '\n'
											+ "Starting Ammo: " + w.getStartingAmmo() + '\n');
					}
					else if(args[0].equalsIgnoreCase("arenaList"))
					{
						for(Game g :Vars.games)
							player.sendMessage(Msg.PREFIX + g.toString());
					}
					else if(args[0].equalsIgnoreCase("setspawn"))
					{
						lobby = player.getLocation();
						player.sendMessage(Msg.PREFIX + "Spawn set!");
					}
					else if(args[0].equalsIgnoreCase("reloadSigns")) {
						for(Game g: Vars.games)
							g.updateBoard();
					}
					else if(args[0].equalsIgnoreCase("arena")) {
						ArenaCommands.cmd(sender, cmd, label, args);
					}
					else if(args[0].equalsIgnoreCase("map"))
					{
						MapCommands.cmd(sender, cmd, label, args);
					}
					else if(args[0].equalsIgnoreCase("killTest"))
					{
						if(Vars.playerGames.get(player.getUniqueId()) != null)
							Vars.playerGames.get(player.getUniqueId()).killPlayer(player, "console");
					}
					else
					{
						player.sendMessage(Msg.PREFIX + ChatColor.translateAlternateColorCodes('&', "&4Unknown command!"));
					}
				}
					
			}
			else if(label.equalsIgnoreCase("Leave"))
			{
				if(Vars.playerGames.containsKey(player.getUniqueId()))
				{
					Vars.playerGames.get(player.getUniqueId()).removePlayer(player.getUniqueId());
					joinLobby(player.getUniqueId());
				}
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("You cannot do that command!");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
}
