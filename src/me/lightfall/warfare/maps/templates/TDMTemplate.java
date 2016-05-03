package me.lightfall.warfare.maps.templates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.maps.MapTemplate;

public class TDMTemplate implements MapTemplate{
	private String name;
	private double redX;
	private double redY;
	private double redZ;
	private float redYaw;
	private double blueX;
	private double blueY;
	private double blueZ;
	private float blueYaw;
	private Location reference;

	public TDMTemplate() {
		
	}

	public TDMTemplate(String name, Location reference) {
		this.name = name;
		this.reference = reference;
		setRed(reference);
		setBlue(reference);
	}
	
	public TDMTemplate(String name, double redX, double redY, double redZ, float redYaw, double blueX, double blueY, double blueZ, float blueYaw, Location reference) {
		this.name = name;
		this.redX = redX;
		this.redY = redY;
		this.redZ = redZ;
		this.redYaw = redYaw;
		this.blueX = blueX;
		this.blueY = blueY;
		this.blueZ = blueZ;
		this.blueYaw = blueYaw;
		this.reference = reference;
	}
	
	@Override
	public void copy(MapTemplate s)
	{
		if(s instanceof TDMTemplate) {
			TDMTemplate src = (TDMTemplate) s;
			this.redX = src.redX;
			this.redY = src.redY;
			this.redZ = src.redZ;
			this.redYaw = src.redYaw;
			this.blueX = src.blueX;
			this.blueY = src.blueY;
			this.blueZ = src.blueZ;
			this.blueYaw = src.blueYaw;
			this.reference = src.reference;
		}
	}
	
	@Override
	public void writeToConfig(ConfigurationSection s)
	{
		//ConfigurationSection s = c.createSection("template." + this.name);
		s.set("redX", redX);
		s.set("redY", redY);
		s.set("redZ", redZ);
		s.set("redYaw", redYaw);
		s.set("blueX", blueX);
		s.set("blueY", blueY);
		s.set("blueZ", blueZ);
		s.set("blueYaw", blueYaw);
		s.set("referenceX", reference.getBlockX());
		s.set("referenceY", reference.getBlockY());
		s.set("referenceZ", reference.getBlockZ());
		
	}
	
	@Override
	public void loadFromConfig(ConfigurationSection s)
	{
		redX = s.getDouble("redX");
		redY = s.getDouble("redY");
		redZ = s.getDouble("redZ");
		redYaw = Float.valueOf(s.getString("redYaw"));
		blueX = s.getDouble("blueX");
		blueY = s.getDouble("blueY");
		blueZ = s.getDouble("blueZ");
		blueYaw = Float.valueOf(s.getString("blueYaw"));
		reference = new Location(Bukkit.getWorlds().get(0), s.getInt("referenceX"), s.getInt("referenceY"), s.getInt("referenceZ"));
		
	}
	
	public String getType()
	{
		return GameType.TDM.name;
	}
	
	public void setRed(Location l)
	{
		redX = (l.getX() - reference.getX());
		redY = (l.getY() - reference.getY());
		redZ = (l.getZ() - reference.getZ());
		redYaw = l.getYaw();
	}
	
	public void setBlue(Location l)
	{
		blueX = (l.getX() - reference.getX());
		blueY = (l.getY() - reference.getY());
		blueZ = (l.getZ() - reference.getZ());
		blueYaw = l.getYaw();
	}
	
	public void setReference(Location l)
	{
		redX = (redX + reference.getX() - l.getX());
		redY = (redY + reference.getY() - l.getY());
		redZ = (redZ + reference.getZ() - l.getZ());
		blueX = (blueX + reference.getX() - l.getX());
		blueY = (blueY + reference.getY() - l.getY());
		blueZ = (blueZ + reference.getZ() - l.getZ());
		reference = l;
	}
	
	public Location getReference()
	{
		return reference;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setRedX(double redX) {
		this.redX = redX;
	}

	public void setRedY(double redY) {
		this.redY = redY;
	}

	public void setRedZ(double redZ) {
		this.redZ = redZ;
	}

	public void setRedYaw(float redYaw) {
		this.redYaw = redYaw;
	}

	public void setBlueX(double blueX) {
		this.blueX = blueX;
	}

	public void setBlueY(double blueY) {
		this.blueY = blueY;
	}

	public void setBlueZ(double blueZ) {
		this.blueZ = blueZ;
	}

	public void setBlueYaw(float blueYaw) {
		this.blueYaw = blueYaw;
	}

	public String getName() {
		return name;
	}

	public double getActualRedX() {return (redX + reference.getX());}
	public double getActualRedY() {return (redY + reference.getY());}
	public double getActualRedZ() {return (redZ + reference.getZ());}
	public double getActualBlueX() {return (blueX + reference.getX());}
	public double getActualBlueY() {return (blueY + reference.getY());}
	public double getActualBlueZ() {return (blueZ + reference.getZ());}
	
	public double getRedX() {
		return redX;
	}

	public double getRedY() {
		return redY;
	}

	public double getRedZ() {
		return redZ;
	}

	public float getRedYaw() {
		return redYaw;
	}

	public double getBlueX() {
		return blueX;
	}

	public double getBlueY() {
		return blueY;
	}

	public double getBlueZ() {
		return blueZ;
	}

	public float getBlueYaw() {
		return blueYaw;
	}
	
}
