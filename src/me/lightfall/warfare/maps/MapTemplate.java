package me.lightfall.warfare.maps;

import org.bukkit.configuration.ConfigurationSection;

public interface MapTemplate {
	public String getName();
	public void setName(String s);
	public String getType();
	public void copy(MapTemplate s);
	public void writeToConfig(ConfigurationSection s);
	public void loadFromConfig(ConfigurationSection s);
}