package me.lightfall.warfare.gamemodes;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.gamemodes.utils.GameMode;
import me.lightfall.warfare.gamemodes.utils.GameState;
import me.lightfall.warfare.gamemodes.utils.GameType;

public class FreeForAll extends Game implements GameMode {
	private ArrayList<Location> spawns;
	private ArrayList<UUID> players;
	
	public FreeForAll() {
		super(null);
		players = new ArrayList<UUID>();
		spawns = new ArrayList<Location>();
	}
	
	public FreeForAll(Sign b) {
		super(b);
		players = new ArrayList<UUID>();
		spawns = new ArrayList<Location>();
	}
	
	public GameType getType() {return GameType.FFA;}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void sendScoreboards()
	{
		
	}
	
	
	@Override
	public void addPlayer(UUID u) {
		
	}

	@Override
	public void addTeam(UUID[] u) {
		
	}
	
	@Override
	public void removePlayer(UUID u) {

	}

	@Override
	public boolean ready()
	{
		if(state != GameState.CREATING && spawns.size() > 0 && maxSize > 0)
		{
			if(state == GameState.CREATING)
				state = GameState.WAITING;
			return true;
		}
		return false;
	}

	@Override
	public void updateBoard() {
		board.setLine(1, ChatColor.GREEN + "" + ChatColor.BOLD + "FFA");
		board.setLine(3, players.size() + " / " + maxSize);
		super.updateBoard();
	}

	public boolean addSpawn(Player p) {
		return true;
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean DamagePlayer(Player src, Player p, double damage) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void spawnPlayer(Player p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killPlayer(Player p, String src) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateScoreboard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean loadMap() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void prepMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPlayerMessage(Player p, String message, String URL) {
		// TODO Auto-generated method stub
		
	}

}
