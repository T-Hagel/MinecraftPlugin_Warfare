package me.lightfall.warfare.maps;

public class MapList {
	String name;
	Map[] maps;
	
	public MapList(String n)
	{
		name = n;
		maps = new Map[0];
	}
	
	public void addMap(Map m)
	{
		Map[] tmp = new Map[maps.length + 1];
		for(int i = 0; i < maps.length; i++)
			tmp[i] = maps[i];
		tmp[tmp.length - 1] = m;
		maps = tmp;
		
	}
	
	synchronized public Map getMap(int gameID)
	{
		int current = -1, next = -1;
		for(int i = 0; i < maps.length; i++)
			if(maps[i].gameId() == gameID) {
				current = i;
				break;
			}
		for(int i = ((current + 1) % maps.length); i != current ; i = (++i % maps.length)) {
			if(!maps[i].inUse()) {
				next = i;
				break;
			}
		}
		if(next == -1)
			return null;
		else
		{
			if(current >= 0)
				maps[current].setGameId(0);
			maps[next].setGameId(gameID);
			return maps[next];
		}
	}
	
	public Map[] getMaps()
	{
		return maps;
	}
	
	public String getName() {return name;}
	
	public static void main(String args[])
	{
		for(int i = 5; i != 4 ; i = (++i % 8)) {
			System.out.println(i);
		}
	}
}
