package me.lightfall.warfare.gamemodes.utils;

public enum GameState {
	CREATING("Creating..."),
	WAITING("Waiting..."),
	STARTING("Starting!"),
	INGAME("Ingame!"),
	ENDING("Ending..."),
	QUIT("Quit.");
	
	String message;
	
	GameState(String s)
	{
		message = s;
	}
}
