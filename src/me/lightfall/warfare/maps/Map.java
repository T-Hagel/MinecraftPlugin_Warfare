package me.lightfall.warfare.maps;

import org.bukkit.Location;

import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.maps.templates.FFATemplate;
import me.lightfall.warfare.maps.templates.TDMTemplate;
import me.lightfall.warfare.utils.Msg;

public class Map {
	private TDMTemplate TDM;
	private FFATemplate FFA;
	
	private double x;	// * Reference
	private double y;	// * Location
	private double z;	// * Coordinates
	
	private int gameId;
	private String name;
	
	public Map(String name, double x, double y, double z)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		gameId = 0;
	}
	
	public MapTemplate getTemplate(GameType t)
	{
		switch(t)
		{
			case TDM:
				return TDM;
			case FFA:
				return FFA;
		}
		return null;
	}
	
	public void setReference(Location l)
	{
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int gameId()
	{
		return gameId;
	}
	
	public boolean inUse() 
	{
		if(gameId == 0)
			return false;
		return true;
	}
	
	public void setGameId(int id)
	{
		gameId = id;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String toString()
	{
		return (TDM == null ? "NO TDM TEMPLATE. " : "TDM TEMPLATE EXISTS");
	}
	
	public void setTemplate(MapTemplate t)
	{
		if(t instanceof TDMTemplate)
			TDM = (TDMTemplate) t;
		else if(t instanceof FFATemplate)
			FFA = (FFATemplate) t;
		else
			System.out.println(Msg.CONSOLE_PREFIX + "ERROR LOADING TEMPLATE!");
	}
	
	
}
