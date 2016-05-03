package me.lightfall.warfare.maps.templates;

import org.bukkit.configuration.ConfigurationSection;

import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.maps.MapTemplate;

public class FFATemplate implements MapTemplate{
	String name;
	int[] X;
	int[] Y;
	int[] Z;
	float[] Yaw;
	
	public FFATemplate(String name, int[] x, int[] y, int[] z, float[] yaw) {
		this.name = name;
		X = x;
		Y = y;
		Z = z;
		Yaw = yaw;
	}
	
	@Override
	public void writeToConfig(ConfigurationSection s) {
		s.set("X", X);
		s.set("Y", Y);
		s.set("Z", Z);
		s.set("Yaw", Yaw);
	}

	public String getType()
	{
		return GameType.FFA.name;
	}

	public String getName() {
		return name;
	}

	public int[] getX() {
		return X;
	}

	public int[] getY() {
		return Y;
	}

	public int[] getZ() {
		return Z;
	}

	public float[] getYaw() {
		return Yaw;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setX(int[] x) {
		X = x;
	}

	public void setY(int[] y) {
		Y = y;
	}

	public void setZ(int[] z) {
		Z = z;
	}

	public void setYaw(float[] yaw) {
		Yaw = yaw;
	}

	@Override
	public void copy(MapTemplate s) {
		
	}

	@Override
	public void loadFromConfig(ConfigurationSection s) {
		// TODO Auto-generated method stub
		
	}
	
	
}
