package me.lightfall.warfare.gamemodes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.PlayerProfile;
import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.gamemodes.utils.GameMode;
import me.lightfall.warfare.gamemodes.utils.GameState;
import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.maps.templates.TDMTemplate;
import me.lightfall.warfare.utils.Items;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.SQLConnection;
import me.lightfall.warfare.utils.Vars;
import net.minecraft.server.v1_8_R2.PacketPlayOutCamera;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamDeathMatch extends Game implements GameMode {
	private TDMTemplate template;
	private Location redSpawn;
	private Location blueSpawn;
	private ArrayList<UUID> redTeam;
	private ArrayList<UUID> blueTeam;
	private ArrayList<UUID> onTeam;
	private int maxScore;
	private int redScore;
	private int blueScore;
	private static String SCOREBOARD_NAME = ChatColor.AQUA + "[" + ChatColor.DARK_AQUA + ChatColor.BOLD + "TDM" + ChatColor.AQUA + "]";

	public TeamDeathMatch()
	{
		super(null);
		initialize();
	}
	
	public TeamDeathMatch(Sign b) {
		super(b);
		initialize();
	}
	
	public GameType getType() {return GameType.TDM;}
	
	private void initialize()
	{
		blueTeam = new ArrayList<UUID>();
		redTeam = new ArrayList<UUID>();
		onTeam = new ArrayList<UUID>();
		redSpawn = null;
		blueSpawn = null;
		maxScore = 200;
		redScore = blueScore = 200;
	}

	synchronized public void addBlue(UUID u)
	{
		blueTeam.add(u);
		updateBoard();
		Msg.sendSub(Bukkit.getPlayer(u), "Added to " + ChatColor.BLUE + "BLUE " + ChatColor.RESET + "team!", 5, 30, 5);
		//Bukkit.getPlayer(u).sendMessage(Msg.PREFIX + "Added to " + ChatColor.BLUE + "blue " + ChatColor.RESET + "team!");
		if(state == GameState.INGAME)
		{
			giveKitSelect(u);
			Vars.players.get(u).giveWeapon();
		}
		else
			Bukkit.getPlayer(u).getInventory().clear();
	}
	
	@Override
	public synchronized void addPlayer(UUID p)
	{
		if(state != GameState.CREATING) {
			if(!redTeam.contains(p) && !blueTeam.contains(p)) {
				if((redTeam.size() < maxSize) && redTeam.size() <= blueTeam.size())
					addRed(p);
				else if(blueTeam.size() < maxSize)
					addBlue(p);
			}
			if(redTeam.contains(p) || blueTeam.contains(p)) {
				if(redTeam.contains(p))
					Bukkit.getPlayer(p).teleport(redSpawn);
				else
					Bukkit.getPlayer(p).teleport(blueSpawn);
				Vars.players.get(p).reset();
				updateScoreboard(p);
				updateAllBoardTeams();
				updateBoard();
				checkStart();
			}
		}
	}
	
	synchronized public void addRed(UUID u)
	{
		redTeam.add(u);
		updateBoard();
		Msg.sendSub(Bukkit.getPlayer(u), "Added to " + ChatColor.RED + "RED " + ChatColor.RESET + "team!", 5, 30, 5);
		if(state == GameState.INGAME) {
			giveKitSelect(u);
			Vars.players.get(u).giveWeapon();
		}
		else
			Bukkit.getPlayer(u).getInventory().clear();
	}
	
	private void giveKitSelect(UUID u)
	{
		Player p = Bukkit.getPlayer(u);
		ItemStack is = new ItemStack(Items.KIT_CHANGE_MATERIAL);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(Items.KIT_CHANGE_NAME);
		is.setItemMeta(meta);
		p.getInventory().setItem(8, is);
		p.updateInventory();
	}
	
	synchronized public void addTeam(UUID[] u)
	{
		if(redTeam.size() + u.length < maxSize && redTeam.size() < blueTeam.size())
		{
			for(UUID q: u) {
				addRed(q);
				onTeam.add(q);
			}
		}
		else if(blueTeam.size() + u.length < maxSize)
		{
			for(UUID q: u) {
				addBlue(q);
				onTeam.add(q);
			}
		}
		checkStart();
	}
	
	private void cleanPlayers()
	{
		for(UUID u: redTeam)
		{
			Bukkit.getPlayer(u).getInventory().clear();
			Vars.players.get(u).reset();
		}
		for(UUID u: blueTeam)
		{
			Bukkit.getPlayer(u).getInventory().clear();
			Vars.players.get(u).reset();
		}
		updateScoreboard();
	}
	
	private boolean checkStart()
	{
		if(redTeam.size() + blueTeam.size() >= minStart && state == GameState.WAITING) {
			state = GameState.STARTING;
			cleanPlayers();
			updateBoard();
			new BukkitRunnable(){
				private int i = 0;
				  @Override
				  public void run(){
					  if(((20 - i) % 5 == 0 || (20 - i) <= 5) && 20 - i != 0) {
						  for(UUID u : redTeam)
							  Bukkit.getPlayer(u).sendMessage(Msg.PREFIX + "Game is starting in " + ChatColor.DARK_RED + (20 - i) + ChatColor.RESET + " seconds!");
						  for(UUID u : blueTeam)
							  Bukkit.getPlayer(u).sendMessage(Msg.PREFIX + "Game is starting in " + ChatColor.DARK_RED + (20 - i) + ChatColor.RESET + " seconds!");
					  }
					  if(i >= 20 || state != GameState.STARTING) {
						  this.cancel();
						  if(state != GameState.INGAME)
						  {
							  start();
						  }
					  }
					  i++;
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, 20/*Period*/);
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean DamagePlayer(final Player src, final Player p, double damage) {
		if(state == GameState.INGAME) {
			if((redTeam.contains(src.getUniqueId()) && blueTeam.contains(p.getUniqueId()))
			|| (blueTeam.contains(src.getUniqueId()) && redTeam.contains(p.getUniqueId())))
			{
				if(p.getHealth() - damage <= 0)
				{
					//player died
					killPlayer(p, src.getDisplayName());
					Vars.players.get(src.getUniqueId()).addExp(100);
					ChatColor t;
					if(redTeam.contains(src.getUniqueId()))
						t = ChatColor.DARK_RED;
					else
						t = ChatColor.BLUE;
					switch(Vars.players.get(src.getUniqueId()).kill())
					{
					case 2:
						sendMessage(Msg.PREFIX + t + src.getDisplayName() + ChatColor.RESET +" got a DOUBLE kill!");
						break;
					case 3:
						sendMessage(Msg.PREFIX + t + src.getDisplayName() + ChatColor.RESET +" got a " +ChatColor.DARK_GREEN +"TRIPLE" +ChatColor.RESET+ " kill!");
						break;
					case 5:
						sendMessage(Msg.PREFIX + t + src.getDisplayName() + ChatColor.RESET +" got a " +ChatColor.DARK_PURPLE + "PENTA" + ChatColor.RESET +" kill!");
						break;
					case 8:
						sendMessage(Msg.PREFIX + t + src.getDisplayName() + ChatColor.RESET +" got an "+ChatColor.GOLD + "OCTA" + ChatColor.RESET+ " kill!");
						break;
					}
					Msg.sendActionBar(src, ChatColor.GOLD + "You have killed " + ChatColor.BLUE + p.getDisplayName());
					Vars.players.get(p.getUniqueId()).die();
					//Vars.weapons.get(p.getUniqueId()).reset();
					if(redTeam.contains(p.getUniqueId()))
						redScore--;
					else
						blueScore--;
					new BukkitRunnable(){
						  @Override
						  public void run(){
							  if(!p.getGameMode().equals(org.bukkit.GameMode.SPECTATOR))
								  this.cancel();
							  else
							  {
								  p.teleport(src);
								  PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) src).getHandle());
						          
								((CraftPlayer) p).getHandle().playerConnection.sendPacket(camera);
							  }
						  }
					}.runTaskTimer(Main.plugin, 0/*DELAY*/, 30/*Period*/);
				}
				else
				{
					p.damage(damage);
				}
				updateScoreboard();
				winCondition();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void end()
	{
		if(state != GameState.ENDING) {
			//end game
			state = GameState.ENDING;
			updateBoard();
			new BukkitRunnable(){
				boolean f = false;
				  @Override
				  public void run(){
					  if(f) {
						if(!nextMap())
						{
							loadMap();
							/*
							System.out.println("Arena " + getId() + ": Cannot load next map from maplist!");
							System.out.println("Arena " + getId() + ": Attempting to load map...");
							if(!loadMap())
							{
								System.out.println("Arena " + getId() + ": Cannot load from map!");
								System.out.println("Arena " + getId() + ": Red and Blue spawns will not change.");
							}
							else
								System.out.println("Arena " + getId() + ": Map load success!");
								*/
						}
						redScore = blueScore = maxScore;
						state = GameState.WAITING;
						updateScoreboard();
						updateBoard();
						checkStart();
						this.cancel();
					  }
					  else
					  {
						  for(UUID u : redTeam)
						  {
							  Player p = Bukkit.getPlayer(u);
							  if(redScore > blueScore)
								  Msg.sendSub(p, ChatColor.DARK_RED + "RED TEAM " + ChatColor.GOLD + "wins!", 5, 40, 5);
							  else if(blueScore > redScore)
								  Msg.sendSub(p, ChatColor.DARK_BLUE + "BLUE TEAM " + ChatColor.GOLD + "wins!", 5, 40, 5);
							  else
								  Msg.sendSub(p,ChatColor.GOLD + "THE GAME ENDED IN A DRAW!", 5, 40, 5);
							  p.sendMessage(Msg.PREFIX + "The next map will be loaded in 10 seconds!");
							  Vars.players.get(u).reward(true, true, true);
						  }
						  for(UUID u : blueTeam)
						  {
							  Player p = Bukkit.getPlayer(u);
							  if(redScore > blueScore)
								  Msg.sendSub(p, ChatColor.DARK_RED + "RED TEAM " + ChatColor.GOLD + "wins!", 5, 40, 5);
							  else if(blueScore > redScore)
								  Msg.sendSub(p, ChatColor.DARK_BLUE + "BLUE TEAM " + ChatColor.GOLD + "wins!", 5, 40, 5);
							  else
								  Msg.sendSub(p,ChatColor.GOLD + "THE GAME ENDED IN A DRAW!", 5, 40, 5);
							  p.sendMessage(Msg.PREFIX + "The next map will be loaded in 10 seconds!");
							  Vars.players.get(u).reward(true, true, true);
						  }
						  f = true;
					  }
				  }
			}.runTaskTimer(Main.plugin, 0/*DELAY*/, 20*10/*Period*/);
		}
	}
	
	protected boolean loadMap()
	{
		try {
		TDMTemplate t = ((TDMTemplate)map.getTemplate(GameType.TDM));
		Location rs = new Location(Bukkit.getWorlds().get(0),
				map.getX() 
				+ t.getRedX(),
				map.getY() 
				+ t.getRedY(),
				map.getZ() 
				+ t.getRedZ(),
				t.getRedYaw(),
				0);
		Location bs = new Location(Bukkit.getWorlds().get(0), map.getX() + t.getBlueX(),
				map.getY() + t.getBlueY(),
				map.getZ() + t.getBlueZ(),
				t.getBlueYaw(),
				0);
		
		redSpawn = rs;
		blueSpawn = bs;
		return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public Location getBlueSpawn() {
		return blueSpawn;
	}
	
	public Location getRedSpawn() {
		return redSpawn;
	}
	
	@Override
	public void killPlayer(Player p, String src)
	{
		Msg.sendSub(p, ChatColor.DARK_RED + "You were killed by " + ChatColor.DARK_BLUE + src + ChatColor.DARK_RED + "!", 5, 30, 10);
		super.deathTimer(p);
		p.setHealth(p.getMaxHealth());
	}
	
	
	@Override
	public boolean ready()
	{
		if(redSpawn != null && blueSpawn != null && maxSize > 0 && Main.lobby != null)
		{
			if(state == GameState.CREATING)
				state = GameState.WAITING;
			updateBoard();
			return true;
		}
		return false;
	}
	
	private void removeBoardTeams(Player p)
	{
		Team blue;
		blue = p.getScoreboard().getTeam("blue");
		Team red;
		red = p.getScoreboard().getTeam("red");
		for(UUID id : redTeam)
			red.removePlayer(Bukkit.getPlayer(id));
		for(UUID id : blueTeam)
			blue.removePlayer(Bukkit.getPlayer(id));
	}
	
	@Override
	public void close()
	{
		sendMessage(Msg.PREFIX + "Arena is closing... you will be rewarded your experience!");
		List<UUID> l = (List<UUID>) redTeam.clone();
		for(UUID u : l)
		{
			Vars.players.get(u).reward(true, true, true);
			removePlayer(u);
		}
		l = (List<UUID>) blueTeam.clone();
		for(UUID u : l)
		{
			Vars.players.get(u).reward(true, true, true);
			removePlayer(u);
		}
	}
	
	@Override
	public synchronized void removePlayer(UUID u)
	{
		//remove from red/blue team
		if(redTeam.contains(u)) 
			redTeam.remove(u);
		else if(blueTeam.contains(u)) 
			blueTeam.remove(u);
		if(onTeam.contains(u))
			onTeam.remove(u);
		
		//clear scoreboard teams
		if(Bukkit.getPlayer(u).getScoreboard().getTeam("blue") != null)
			Bukkit.getPlayer(u).getScoreboard().getTeam("blue").unregister();
		if(Bukkit.getPlayer(u).getScoreboard().getTeam("red") != null)
			Bukkit.getPlayer(u).getScoreboard().getTeam("red").unregister();

		//remove scoreboard
		Player p = Bukkit.getPlayer(u);
		p.getScoreboard().getObjective(SCOREBOARD_NAME).unregister();
		updateBoard();
		Main.joinLobby(u);

		//clear exp and reward
		Vars.players.get(u).reward(true, true, false);
	}
	
	@Override
	public void sendMessage(String s)
	{
		for(UUID u : redTeam)
			Bukkit.getPlayer(u).sendMessage(s);
		for(UUID u : blueTeam)
			Bukkit.getPlayer(u).sendMessage(s);
	}
	
	@Override
	public void sendPlayerMessage(Player p, String message, String URL)
	{
		String prefix = (redTeam.contains(p.getUniqueId()) ? ChatColor.DARK_RED + "[RED]" : ChatColor.BLUE + "[BLUE]") + ChatColor.RESET;
		String name = p.getDisplayName();
		PlayerProfile pf = Vars.players.get(p.getUniqueId());
		String hoverText = ChatColor.GREEN + "Level: " + ChatColor.GOLD + pf.getLevel() + ChatColor.RESET + "\n" + ChatColor.GREEN + "Experience: " + ChatColor.GOLD + pf.getTotalExp();
		for(UUID u : redTeam)
			if(URL != "")
				Msg.sendJsonPlayerChatHoverURL(Bukkit.getPlayer(u), prefix, name, message, hoverText, URL);
			else
				Msg.sendJsonPlayerChatHover(Bukkit.getPlayer(u), prefix, name, message, hoverText);
		for(UUID u : blueTeam)
			if(URL != "")
				Msg.sendJsonPlayerChatHoverURL(Bukkit.getPlayer(u), prefix, name, message, hoverText, URL);
			else
				Msg.sendJsonPlayerChatHover(Bukkit.getPlayer(u), prefix, name, message, hoverText);
	}
	
	public void sendScoreboards()
	{
		for(UUID u : redTeam)
			updateScoreboard(u);
		for(UUID u : blueTeam)
			updateScoreboard(u);
	}
	
	public boolean setBlueSpawn(Player p) {
		blueSpawn = p.getLocation();
		return true;
	}

	public boolean setRedSpawn(Player p) {
		redSpawn = p.getLocation();
		return true;
	}
	
	@Override
	protected void spawnPlayer(Player p) {
		if(redTeam.contains(p.getUniqueId()))
			p.teleport(redSpawn);
		else if(blueTeam.contains(p.getUniqueId()))
			p.teleport(blueSpawn);
		
		PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) p).getHandle());
        
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(camera);
	}

	@Override
	public void start()
	{		
		time = timeMax;
		timer();
		state = GameState.INGAME;
		updateBoard();
		Player p;
		for(UUID u : redTeam) {
			p = Bukkit.getPlayer(u);
			p.teleport(redSpawn);
			//Vars.weapons.get(u).reset();
			giveKitSelect(u);
			Vars.players.get(u).giveWeapon();
			Vars.players.get(u).reset();
			updateBoardTeams(p);
			Msg.sendSub(p, ChatColor.DARK_AQUA + "THE GAME HAS STARTED!", 5, 20, 5);
		}
		for(UUID u : blueTeam) {
			p = Bukkit.getPlayer(u);
			p.teleport(blueSpawn);
			//Vars.weapons.get(u).reset();
			giveKitSelect(u);
			Vars.players.get(u).giveWeapon();
			Vars.players.get(u).reset();
			updateBoardTeams(p);
			Msg.sendSub(p, ChatColor.DARK_AQUA + "THE GAME HAS STARTED!", 5, 20, 5);
		}
	}

	public String toString() {
		return "[" + super.getId() + "]<TDM> '" + state + "' (" + redTeam.size() + " + " + blueTeam.size() + " / " + maxSize * 2
				+ ") RedSpawn:"  + (redSpawn != null ? "true" : "false") + " BlueSpawn:" + (blueSpawn != null ? "true" : "false");
	}
	
	@Override
	public void updateBoard() {
		board.setLine(1, ChatColor.GREEN + "" + ChatColor.BOLD + "TDM");
		board.setLine(3, redTeam.size() + blueTeam.size() + " / " + maxSize * 2);
		super.updateBoard();
	}

	private void updateAllBoardTeams()
	{
		for(UUID id : redTeam)
			updateBoardTeams(Bukkit.getPlayer(id));
		for(UUID id : blueTeam)
			updateBoardTeams(Bukkit.getPlayer(id));
	}
	
	private void updateBoardTeams(Player p)
	{
		Team blue;
		if(p.getScoreboard().getTeam("blue") != null)
			p.getScoreboard().getTeam("blue").unregister();
		blue = p.getScoreboard().registerNewTeam("blue");
		
		Team red;
		if(p.getScoreboard().getTeam("red") != null)
			p.getScoreboard().getTeam("red").unregister();;
		red = p.getScoreboard().registerNewTeam("red");
		blue.setPrefix(ChatColor.BLUE.toString());
		red.setPrefix(ChatColor.RED.toString());
		for(UUID id : redTeam)
			red.addPlayer(Bukkit.getPlayer(id));
		for(UUID id : blueTeam)
			blue.addPlayer(Bukkit.getPlayer(id));
	}
	
	protected void updateScoreboard()
	{
		for(UUID u: redTeam)
			updateScoreboard(u);
		for(UUID u: blueTeam)
			updateScoreboard(u);
	}

	private void updateScoreboard(UUID u)
	{
		final NumberFormat form = new DecimalFormat("#00");
		Scoreboard s = Bukkit.getPlayer(u).getScoreboard();
		Objective obj;
		if(s.getObjective(SCOREBOARD_NAME) != null) {
			obj = s.getObjective(SCOREBOARD_NAME);
			obj.unregister();
		}
		obj = s.registerNewObjective(SCOREBOARD_NAME, "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score;
		score = obj.getScore((redTeam.contains(u)? ChatColor.DARK_RED + "RED TEAM" : ChatColor.BLUE + "BLUE TEAM"));
		score.setScore(15);
		score = obj.getScore(ChatColor.RESET + "" + ChatColor.RESET + ChatColor.RESET);
		score.setScore(14);
		score = obj.getScore(ChatColor.DARK_RED + "Red Score");
		score.setScore(13);
		score = obj.getScore(ChatColor.RESET + "" + ChatColor.RESET + "  " + redScore);
		score.setScore(12);
		score = obj.getScore(ChatColor.BLUE + "Blue Score");
		score.setScore(11);
		score = obj.getScore(ChatColor.RESET + "" + ChatColor.RESET +ChatColor.RESET + "  " + blueScore);
		score.setScore(10);
		score = obj.getScore(ChatColor.GOLD + "Kills");
		score.setScore(9);
		score = obj.getScore(ChatColor.RESET + "  " + Vars.players.get(u).getKills());
		score.setScore(8);
		score = obj.getScore(ChatColor.GOLD + "Deaths");
		score.setScore(7);
		score = obj.getScore(ChatColor.WHITE + "  " + Vars.players.get(u).getDeaths());
		score.setScore(6);
		score = obj.getScore(ChatColor.LIGHT_PURPLE + "Time");
		score.setScore(5);
		score = obj.getScore(ChatColor.WHITE + "  " + time/60 + ":" + form.format(time%60));
		score.setScore(4);
		
		
		Bukkit.getPlayer(u).setScoreboard(s);
	}
	
	private void winCondition()
	{
		if(blueScore <= 0 || redScore <= 0)
			end();
	}
	
	@Override
	public void quit()
	{
		// remove their scoreboards
		  for(UUID u: redTeam) {
			  Bukkit.getPlayer(u).getInventory().clear();
			  removeBoardTeams(Bukkit.getPlayer(u));
			  Vars.players.get(u).reset();
			  Bukkit.getPlayer(u).getScoreboard().getObjective(SCOREBOARD_NAME).unregister();
			  Bukkit.getPlayer(u).teleport(Main.lobby);
			  Main.setScoreboard(u);
			  Vars.playerGames.put(u, null);
		  }
		  for(UUID u: blueTeam) {
			  Bukkit.getPlayer(u).getInventory().clear();
			  removeBoardTeams(Bukkit.getPlayer(u));
			  Vars.players.get(u).reset();
			  Bukkit.getPlayer(u).getScoreboard().getObjective(SCOREBOARD_NAME).unregister();
			  Bukkit.getPlayer(u).teleport(Main.lobby);
			  Main.setScoreboard(u);
			  Vars.playerGames.put(u, null);
		  }
		// state
		state = GameState.WAITING;
		redTeam.clear();
		blueTeam.clear();
	}
	
	private boolean nextMap()
	{
		try {
			map = mapList.getMap(this.getId());
			if(map == null)
				return false;
			template = (TDMTemplate) map.getTemplate(GameType.TDM);
			redSpawn.setYaw(template.getRedYaw());
			redSpawn.setX(map.getX() + template.getRedX());
			redSpawn.setY(map.getY() + template.getRedY());
			redSpawn.setZ(map.getZ() + template.getRedZ());
			
			blueSpawn.setYaw(template.getBlueYaw());
			blueSpawn.setX(map.getX() + template.getBlueX());
			blueSpawn.setY(map.getY() + template.getBlueY());
			blueSpawn.setZ(map.getZ() + template.getBlueZ());
			
			for(UUID u : redTeam)
				spawnPlayer(Bukkit.getPlayer(u));
			for(UUID u : blueTeam)
				spawnPlayer(Bukkit.getPlayer(u));

			cleanPlayers();
			return true;
		}
		catch(Exception e)
		{
			//sendMessage(Msg.PREFIX + "An error occured while trying to load the next map!");
			return false;
		}
	}
	
	protected void prepMap()
	{
		try {
			TDMTemplate t = ((TDMTemplate)map.getTemplate(GameType.TDM));
			if(redSpawn == null)
				redSpawn = new Location(Bukkit.getWorlds().get(0), map.getX() + t.getRedX(),
					map.getY() + t.getRedY(),
					map.getZ() + t.getRedZ(),
					t.getRedYaw(),
					0);
			if(blueSpawn == null)
				blueSpawn = new Location(Bukkit.getWorlds().get(0), map.getX() + t.getBlueX(),
					map.getY() + t.getBlueY(),
					map.getZ() + t.getBlueZ(),
					t.getBlueYaw(),
					0);
			}
			catch(Exception e)
			{
				System.out.println("Error trying to load map " + map.getName() + " for Arena " + getId() + "!");
			}
	}
	
}
