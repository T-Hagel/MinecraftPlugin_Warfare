package me.lightfall.warfare.gamemodes.utils;

import me.lightfall.warfare.gamemodes.FreeForAll;
import me.lightfall.warfare.gamemodes.TeamDeathMatch;
import me.lightfall.warfare.maps.MapTemplate;
import me.lightfall.warfare.maps.templates.FFATemplate;
import me.lightfall.warfare.maps.templates.TDMTemplate;

public enum GameType {
	TDM("TDM", TeamDeathMatch.class, TDMTemplate.class),
	FFA("FFA", FreeForAll.class, FFATemplate.class);
	
	public final String name;
	public final Class<Game> c;
	public final Class<MapTemplate> t;
	
	GameType(String s, Class<?> c, Class<?> t)
	{
		name = s;
		this.c = (Class<Game>) c;
		this.t = (Class<MapTemplate>) t;
	}
	
	public static Class<MapTemplate> searchNameTemplate(String s)
	{
		for(GameType g : GameType.values())
			if(g.name.equalsIgnoreCase(s))
				return g.t;
		return null;
	}
	
	public static Class<Game> searchNameGame(String s)
	{
		for(GameType g : GameType.values())
			if(g.name.equalsIgnoreCase(s))
				return g.c;
		return null;
	}
	
	public static String searchNameGame(Game s)
	{
		for(GameType g : GameType.values())
			if(g.c.isInstance(s))
				return g.name;
		return null;
	}
	
	public static String searchTemplate(MapTemplate t)
	{
		for(GameType g : GameType.values())
			if(g.t.isInstance(t))
				return g.name;
		return null;
	}
}
