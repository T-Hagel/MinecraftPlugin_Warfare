package me.lightfall.warfare.gamemodes.utils;

import org.bukkit.entity.Player;

public interface GameMode {
	void start();
	void end();
	void quit();
	boolean DamagePlayer(Player src, Player p, double damage);
}
