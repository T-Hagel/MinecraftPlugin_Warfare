package me.lightfall.warfare;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.lightfall.warfare.kits.Kit;
import me.lightfall.warfare.perks.Perk;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.SQLConnection;
import me.lightfall.warfare.utils.Vars;
import me.lightfall.warfare.weapons.Weapon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerProfile {
	private int kills;
	private int deaths;
	private int streakTime; //IN SECONDS
	private int streak;
	private Player player;
	
	private Kit[] kits;
	private byte kit;
	private byte editKit;
	private boolean kitChange;
	
	private int level;
	private int totalExp;
	private int exp;
	private boolean offline;
	
	public PlayerProfile(Player p)
	{
		player = p;
		kits = new Kit[3];
		for(int i = 0; i < kits.length; i++)
			kits[i] = new Kit(p);
		kit = editKit = 0;
		totalExp = exp = level = 0;
		if(Main.sql.isValid())
			offline = false;
		else
			offline(true);
		reset();
	}
	
	public void reset()
	{
		kills = deaths = streakTime = streak = 0;
		kitChange = false;
		//Reset weapon
		Vars.weapons.get(player.getUniqueId()).reset();
		//Check for weapon refresh
		player.updateInventory();
	}
	
	public void reward(boolean rewardKills, boolean rewardDeaths, boolean rewardExperience)
	{
		if(offline)
		{
			if(Main.sql.isValid())
				player.sendMessage(Msg.PREFIX + "You are in " + ChatColor.RED + "offline" + ChatColor.RESET + " mode but the database connection is " + ChatColor.BOLD + "available" + ChatColor.RESET + ". Relog to access online mode.");
			else
				player.sendMessage(Msg.PREFIX + "You are in " + ChatColor.RED + "offline" + ChatColor.RESET + " mode. Your stats were not saved!");
			return;
		}
		
		try{
			SQLConnection sql = Main.sql;
			PreparedStatement ps = sql.prepare("SELECT * FROM warfare WHERE player = ?");
			ps.setString(1, player.getName());
			ResultSet res = ps.executeQuery();
			res.next();
			if(res.getRow() == 0)
			{
				System.out.println("Adding " + player.getName() + " to the database.");
				ps = sql.prepare("INSERT INTO warfare (player) Values(?)");
				ps.setString(1, player.getName());
				ps.executeUpdate();
				res = ps.executeQuery();
				res.next();
				if(res.getRow() == 0)
				{
					player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "An error occured while trying to save your stats and was not completed.");
					offline(true);
					return; //Error
				}
			}
			int kills = res.getInt("kills");
			if(rewardKills)
				kills += this.kills;
			int deaths = res.getInt("deaths");
			if(rewardDeaths)
				deaths += this.deaths;
			if(rewardExperience)
				totalExp += this.exp;
			
			ps = sql.prepare("UPDATE warfare SET kills = ?, deaths = ?, experience = ? where player = ?");
			ps.setInt(1, kills);
			ps.setInt(2, deaths);
			ps.setInt(3, totalExp);
			ps.setString(4, player.getName());
			ps.executeUpdate();
			if(rewardExperience) 	player.sendMessage(Msg.PREFIX + ChatColor.GOLD + "You have been rewarded " + ChatColor.DARK_AQUA + exp + ChatColor.GOLD + " exp.");
			else					player.sendMessage(Msg.PREFIX + ChatColor.GOLD + "You have been rewarded " + ChatColor.DARK_RED + "no" + ChatColor.GOLD + " exp.");
		}
		catch(Exception e)
		{
			player.sendMessage(Msg.PREFIX + ChatColor.DARK_RED + "An error occured while trying to save your stats and was not completed.");
			offline(true);
			//e.printStackTrace();
		}
		finally
		{
			exp = 0;
			updateLevel();
		}
	}
	
	public void offline(Boolean b)
	{
		offline = b;
		if(offline)
			player.sendMessage(Msg.PREFIX + "You are now in " + ChatColor.RED + "offline" + ChatColor.RESET + " mode and your stats will not be saved! Relog to access online mode.");
		else
			player.sendMessage(Msg.PREFIX + "You are now in " + ChatColor.GREEN + "online" + ChatColor.RESET +" mode! All stats will now be saved.");
	}
	
	public void updateLevel()
	{
		level = (int) Math.floor(Math.log10(totalExp));
		if(level < 0)
			level = 0;
	}

	public int kill()
	{
		kills++;
		streakTimer();
		streakTime = 3;
		if(kits[kit].contains(Perk.SCAVENGER))
			Vars.weapons.get(player.getUniqueId()).giveAmmo();
			
		return ++streak;
	}
	public void die()
	{
		deaths++; streak = streakTime = 0;
		if(kitChange)
			giveWeapon();
		else
			Vars.weapons.get(player.getUniqueId()).reset();
	}
	
	public int getKills() {return kills;}
	public int getDeaths() {return deaths;}
	public Kit[] getKits() {return kits;}
	public void setKits(Kit[] k) {kits = k;}
	public Kit getKit() {return kits[kit];}
	public byte getKitNum() {return kit;}
	public Kit getKit(int i) {return kits[i];}
	public Kit getEditKit() {return kits[editKit];}
	public short getEditKitVal() {return editKit;}
	public void setEditKit(byte i) {editKit = i;}
	public void setKit(Kit k) {kits[kit] = k;}
	public void setKit(byte i) {kit = (i > 2 || i < 0 ? 0 : i); kitChange = true;}
	public int getLevel() {return level;}
	public void setLevel(int i) {level = i;}
	public int getTotalExp() {return totalExp;}
	public void setTotalExp(int i) {totalExp = i; updateLevel();}
	public int getExp() {return exp;}
	public void addExp(int i) {exp+= i;}
	public void setExp(int i) {exp = i;}
	
	public void giveWeapon()
	{
		Weapon w = kits[kit].getWeapon();
		w.reset();
		Vars.weapons.put(player.getUniqueId(), w);
		w.giveWeapon();
		kits[kit].applyPerks(player);
	}
	
	
	private void streakTimer()
	{
		if(streakTime <= 0)
		{
			new BukkitRunnable(){
				  @Override
				  public void run(){
					  streakTime--;
					  if(streakTime <= 0)
					  {
						  //End Game
						  streak = 0;
						  this.cancel();
					  }
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, 20/*Period*/);
		}
	}
	
}
