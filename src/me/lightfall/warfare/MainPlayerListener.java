package me.lightfall.warfare;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import me.lightfall.warfare.gamemodes.utils.Game;
import me.lightfall.warfare.gamemodes.utils.GameType;
import me.lightfall.warfare.perks.Perk;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.SQLConnection;
import me.lightfall.warfare.utils.Vars;
import me.lightfall.warfare.utils.inventorystack.InventoryStack;
import me.lightfall.warfare.weapons.BulletGun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class MainPlayerListener implements Listener{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		join(e.getPlayer());
		e.setJoinMessage(null);
	}
	
	static public void join(Player p)
	{
		
		if(Main.lobby != null)
			p.teleport(Main.lobby);
		
		p.setFoodLevel(20);
		p.setMaximumNoDamageTicks(0);
		p.setLevel(0);
		
		Vars.playerGames.put(p.getUniqueId(), null);
		Vars.weapons.put(p.getUniqueId(), new BulletGun(true, Material.DIAMOND_SPADE, p));
		
		p.sendMessage(Msg.parseMessage(Msg.WELCOME_MESSAGE, p.getDisplayName()));
		Msg.sendTitleSub(p, ChatColor.DARK_GREEN + "Welcome to " + ChatColor.DARK_RED + "Warfare!", ChatColor.GOLD + "Use the texturepack for the best experience!", 5, 40, 20);
		
		Vars.players.put(p.getUniqueId(), new PlayerProfile(p));
		Vars.inventory.put(p.getUniqueId(), new InventoryStack());
		
		try {
			SQLConnection sql = Main.sql;
			PreparedStatement ps = sql.prepare("SELECT * FROM warfare WHERE player = ?");
			ps.setString(1, p.getName());
			ResultSet res = ps.executeQuery();
			res.next();
			if(res.getRow() == 0)
			{
				System.out.println("Adding " + p.getName() + " to the database.");
				ps = sql.prepare("INSERT INTO warfare (player) Values(?)");
				ps.setString(1, p.getName());
				ps.executeUpdate();
				ps = sql.prepare("SELECT * FROM warfare WHERE player = ?");
				ps.setString(1, p.getName());
				res = ps.executeQuery();
				res.next();
				if(res.getRow() == 0)
					return; //Error
			}
			
			Vars.players.get(p.getUniqueId()).setTotalExp(res.getInt("experience"));
		}
		catch(Exception e1)
		{
			Main.plugin.getLogger().log(Level.WARNING, "Unable to connected to database and load player " + p.getName() + ". They will be in offline mode.");
			//e1.printStackTrace();
		}
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		p.setScoreboard(board);
		Main.setScoreboard(p.getUniqueId());
		Main.joinLobby(p.getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerSendMessage(AsyncPlayerChatEvent e)
	{
		Main.plugin.getLogger().log(Level.INFO, "<" + e.getPlayer().getName() + "> " + e.getMessage());
		e.setCancelled(true);
		Game g = Vars.playerGames.get(e.getPlayer().getUniqueId());
		if(g != null)
			g.sendPlayerMessage(e.getPlayer(),e.getMessage(), "");
		else
		{
			PlayerProfile pf = Vars.players.get(e.getPlayer().getUniqueId());
			for(Player p : e.getRecipients())
				if(Vars.playerGames.get(p.getUniqueId()) == null)
					Msg.sendJsonPlayerChatHover(p, "", e.getPlayer().getDisplayName() + ChatColor.RESET, e.getMessage(), ChatColor.GREEN + "Level: " + ChatColor.GOLD + pf.getLevel() + ChatColor.RESET + "\n" + ChatColor.GREEN + "Experience: " + ChatColor.GOLD + pf.getTotalExp());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		e.setDeathMessage(null);
		if(Vars.playerGames.get(e.getEntity().getUniqueId()) != null)
		{
			Vars.playerGames.get(e.getEntity().getUniqueId()).killPlayer(e.getEntity(), "unknown");
			Location l = e.getEntity().getLocation();
			l.setY(0);
			((Player)e.getEntity()).teleport(l);
			Vars.players.get(e.getEntity().getUniqueId()).die();
		}
		else
		{
			Main.joinLobby(e.getEntity().getUniqueId());
		}
	}

	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent e)
	{
		((Player) e.getEntity()).setFoodLevel(20);
		((Player) e.getEntity()).setSaturation(20);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(Vars.playerGames.get(e.getPlayer().getUniqueId()) != null)
			Vars.playerGames.get(e.getPlayer().getUniqueId()).removePlayer(e.getPlayer().getUniqueId());
		Vars.playerGames.remove(e.getPlayer().getUniqueId());
		if(Vars.weapons.get(e.getPlayer().getUniqueId()) != null)
			Vars.weapons.remove(e.getPlayer().getUniqueId());
		e.getPlayer().getInventory().clear();
		e.setQuitMessage(null);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerClickSign(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getClickedBlock().getType().equals(Material.WALL_SIGN) && Vars.playerGames.get(e.getPlayer().getUniqueId()) == null) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if(sign.getLine(0).contains("Arena "))
					for(Game g : Vars.games)
						if(sign.getLine(0).endsWith(String.valueOf(g.getId()))) {
							if(g.ready()) {
								Vars.playerGames.put(e.getPlayer().getUniqueId(), g);
								//Vars.players.get(e.getPlayer().getUniqueId()).reset();
								e.getPlayer().getInventory().clear();
								e.getPlayer().updateInventory();
								g.addPlayer(e.getPlayer().getUniqueId());
							}
							else
								e.getPlayer().sendMessage(Msg.PREFIX + "The game is not ready!");
							break;
						}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		if(e.getBlock().getType().equals(Material.WALL_SIGN)) {
			for(GameType n : GameType.values())
				if(e.getLine(0).equalsIgnoreCase("[" + n.name + "]"))
				{
					Game g = null;
					try {
						g = n.c.newInstance();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if(g != null)
					{
						Vars.games.add(g);
						g.setSign((Sign) e.getBlock().getState());
						g.updateBoard();
						e.setCancelled(true);
					}
					break;
				}
		}
	}
	
	@EventHandler
	public void doubleJump(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		Entity vehicle = e.getPlayer().getVehicle();
		if(vehicle != null && player.getTicksLived() > 5)
		{
			double x = (vehicle.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR? .1 : -.5);
			if(vehicle.getVelocity().getY() != 1.5 || x == -.5)
			{
				vehicle.setVelocity(player.getEyeLocation().getDirection().normalize().setY(x));
				vehicle.getLocation().setDirection(player.getEyeLocation().getDirection());
			}
			
		} else
		if(player.getGameMode() != GameMode.CREATIVE
				&& (player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
				&& !player.isFlying()
				&& Vars.players.get(player.getUniqueId()).getKit().contains(Perk.DOUBLE_JUMP))
		{
			player.setAllowFlight(true);
		}
	}
	
	@EventHandler
	public void onPlayerExitVehicle(VehicleExitEvent e)
	{
		if(e.getExited() instanceof Player)
		{
			Player p = ((Player) e.getExited());
			if(p.getGameMode() == GameMode.CREATIVE)
			{
				p.setTicksLived(1);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onVehicleCollide(VehicleBlockCollisionEvent e)
	{
		Vehicle vehicle = e.getVehicle();
		if(vehicle.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
			vehicle.setVelocity(vehicle.getVelocity().setY(1.5));
	}
	
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
	{
		Player player = e.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
			if(player.isSneaking())
				player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(.1));
			else
				player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(.75));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);
	}
}
