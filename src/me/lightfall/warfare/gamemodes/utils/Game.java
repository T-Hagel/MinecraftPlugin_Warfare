package me.lightfall.warfare.gamemodes.utils;
import java.util.UUID;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.maps.Map;
import me.lightfall.warfare.maps.MapList;
import me.lightfall.warfare.utils.Msg;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


abstract public class Game implements GameMode{
	private static int idCounter = 1;
	private int id;
	protected int maxSize;	//This is the MAX TEAM SIZE
	protected int minStart;
	protected int deathTime; //time in ticks
	protected int timeMax; //time in seconds
	protected int time;	//time in seconds
	private boolean timer;
	protected GameState state;
	protected Sign board;
	protected MapList mapList;
	protected Map map;
	
	public Game(Sign b)
	{
		id = idCounter++;
		maxSize = 0;
		minStart = 2;
		state = GameState.CREATING;
		board = b;
		deathTime = 65;
		timer = false;
		time = timeMax = 180;
	}
	
	abstract public boolean ready();
	abstract public void close();
	abstract public void sendScoreboards();
	abstract public void sendMessage(String s);
	abstract public void sendPlayerMessage(Player p, String message, String URL);
	abstract public void addPlayer(UUID u);
	abstract public void addTeam(UUID[] u);
	abstract public void removePlayer(UUID u);
	abstract protected void spawnPlayer(Player p); 
	abstract public void killPlayer(Player p, String src);
	abstract protected void updateScoreboard();
	abstract protected boolean loadMap();
	abstract protected void prepMap();
	abstract public GameType getType();
	
	public int getActiveCount() {return idCounter - 1;}
	public int getId() {return id;}
	public int getMaxSize() {return maxSize;}
	public void setMaxSize(int i) {maxSize = i; updateBoard();}
	public Sign getSign() {return board;}
	public void setSign(Sign s) {board = s;}
	public void setMapList(MapList list) {mapList = list;}
	public MapList getMapList() {return mapList;}
	public void setMap(Map map)
	{
		if(this.map != null)
			this.map.setGameId(0);
		this.map = map;
		map.setGameId(id);
		loadMap();
	}
	public Map getMap() {return map;}
	public int getMaxTime() {return this.timeMax;}
	public void setMaxTime(int i) {this.timeMax = i;}
	public int getTime() {return this.time;}
	public void setTime(int i) {this.time = i; updateScoreboard();}
	public int getDeathTime() {return this.deathTime;}
	public void setDeathTime(int i) {this.deathTime = i;}
	public int getMinStart() { return this.minStart;}
	public void setMinStart(int i) {this.minStart = i;}
	public boolean loadNextMap()
	{
		Map m = null;
		m = mapList.getMap(id);
		if(m == null)
			return false;
		else
			map = m;
		prepMap();
		return true;
	}
	
	public void updateBoard()
	{
		board.setLine(0, ChatColor.BOLD + "Arena " + id);
		board.setLine(2, ChatColor.AQUA + "" + state.message);
		board.update();
	}
	
	protected void deathTimer(final Player player)
	{
		player.setGameMode(org.bukkit.GameMode.SPECTATOR);
		Msg.sendActionBar(player, ChatColor.GOLD + "You will respawn in " + ChatColor.DARK_GREEN + ((double) deathTime / 20) + ChatColor.GOLD + " seconds.");
		//final NumberFormat form = new DecimalFormat("#0.00");
		new BukkitRunnable(){
			private int i = 0;
			  @Override
			  public void run(){
				  //player.setLevel((deathTime - i)/20);
				  //player.setExp((((float) (deathTime - i)%20)/20));
				  if((deathTime - i) % 2 == 0)
					  Msg.sendActionBar(player, ChatColor.GOLD + "You will respawn in " + ChatColor.DARK_GREEN + ((double) (deathTime - i)/20) + ChatColor.GOLD + " seconds.");
				  if(i >= deathTime) {
					  this.cancel();
					  //player.setExp(0);
					  //player.setLevel(0);
					  Msg.sendActionBar(player, "");
					  spawnPlayer(player);
					  player.setGameMode(org.bukkit.GameMode.SURVIVAL);
				  }
				  i++;
			  }
		}.runTaskTimer(Main.plugin, 0/*DELAY*/, 1/*Period*/);
	}
	
	protected void timer()
	{
		if(!timer) {
			timer = true;
		new BukkitRunnable(){
			  @Override
			  public void run(){
				  if(time <= 0 || state != GameState.INGAME)
				  {
					  //End Game
					  end();
					  this.cancel();
					  time = timeMax;
					  timer = false;
				  }
				  else
				  {
					  time--;
					  updateScoreboard();
				  }
			  }
		}.runTaskTimer(Main.plugin, 0/*DELAY*/, 20/*Period*/);
		}
	}
	
	public String toString()
	{
		return "[" + id + "] State: " + state;
	}
	
}
