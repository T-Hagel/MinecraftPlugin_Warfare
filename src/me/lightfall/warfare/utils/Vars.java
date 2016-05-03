package me.lightfall.warfare.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.lightfall.warfare.PlayerProfile;
import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.maps.Map;
import me.lightfall.warfare.maps.MapList;
import me.lightfall.warfare.maps.MapTemplate;
import me.lightfall.warfare.utils.inventorystack.InventoryStack;
import me.lightfall.warfare.weapons.Weapon;

abstract public class Vars {
	public static HashMap<UUID,PlayerProfile> players = new HashMap<UUID, PlayerProfile>();
	
	//GAMES
	public static HashMap<UUID,Game> playerGames = new HashMap<UUID,Game>();
	public static ArrayList<Game> games = new ArrayList<Game>();
	
	//WEAPONS
	public static HashMap<UUID, Weapon> weapons = new HashMap<UUID, Weapon>();
	
	//MAPS
	public static ArrayList<Map> maps = new ArrayList<Map>();
	public static ArrayList<MapList> mapLists = new ArrayList<MapList>();
	public static HashMap<UUID, MapTemplate> editingTemplates = new HashMap<UUID, MapTemplate>();
	public static HashMap<UUID, MapTemplate> saveTemplates = new HashMap<UUID, MapTemplate>();
	public static ArrayList<MapTemplate> Templates = new ArrayList<MapTemplate>();
	
	//INVENTORY STACK
	public static HashMap<UUID, InventoryStack> inventory = new HashMap<UUID, InventoryStack>();
}
