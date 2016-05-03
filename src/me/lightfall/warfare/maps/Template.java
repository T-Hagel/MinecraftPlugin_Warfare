package me.lightfall.warfare.maps;

public abstract class Template {
	static void copyTemplate(MapTemplate src, MapTemplate dest)
	{
		if(src.getType() == dest.getType())
			dest.copy(src);
	}
}
