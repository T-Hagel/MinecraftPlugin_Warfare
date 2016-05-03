package me.lightfall.warfare.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

abstract public class Items {
	//GENERAL
	static public ItemStack GENERAL_BACK_MATERIAL = new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData());
	static public String GENERAL_BACK_NAME = ChatColor.GREEN + "Back";
	static public ItemStack GENERAL_CLOSE_MATERIAL = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
	static public String GENERAL_CLOSE_NAME = ChatColor.RED + "Close all";
	
	//LOBBY
	static public Material LOBBY_KIT_MATERIAL = Material.DIAMOND_SWORD;
	static public String LOBBY_KIT_NAME = ChatColor.GOLD + "Kit Edit";
	
	static public Material LOBBY_WEAPON_SELECT_MATERIAL = Material.SUGAR;
	static public String LOBBY_WEAPON_SELECT_NAME = ChatColor.GOLD + "Weapon Select";
	
	//KIT SELECT/EDIT
	static public String KIT_EDIT_NAME = ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Kit Edit" + ChatColor.RESET;
	static public int KIT_SELECT_KIT1_SLOT = 11;
	static public int KIT_SELECT_KIT2_SLOT = 13;
	static public int KIT_SELECT_KIT3_SLOT = 15;
	
	static public int[] KIT_SELECT_KIT1_OUTLINE_SLOTS = {1,2,3,10,12,19,20,21};
	static public int[] KIT_SELECT_KIT2_OUTLINE_SLOTS = {3,4,5,12,14,21,22,23};
	static public int[] KIT_SELECT_KIT3_OUTLINE_SLOTS = {5,6,7,14,16,23,24,25};
	
	static public int KIT_EDIT_WEAPON_SLOT = 4;
	static public Material KIT_EDIT_PERK_HAS = Material.GOLD_BLOCK;
	static public Material KIT_EDIT_PERK_HAS_NOT = Material.COAL_BLOCK;
	static public Material KIT_EDIT_PERK_NOT_AVAILABLE = Material.GLASS;
	
	static public String KIT_CHANGE_NAME = ChatColor.DARK_GREEN + "Kit Select";
	static public Material KIT_CHANGE_MATERIAL = Material.ARMOR_STAND;
}
