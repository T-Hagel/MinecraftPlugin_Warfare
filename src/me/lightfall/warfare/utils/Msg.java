package me.lightfall.warfare.utils;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
//import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction;

abstract public class Msg {
	static public String PREFIX = ChatColor.translateAlternateColorCodes('&', 
									"&r&b[&a&lWarfare&b]&r ");
	static public String WELCOME_MESSAGE = 	ChatColor.translateAlternateColorCodes('&',
											"&r&2------------------------------------- \n"
											+ "&r       Welcome to &2Warfare&f &l{PLAYERNAME}! \n"
										  + "&r&2------------------------------------- \n");
	static public String MAIN_SCOREBOARD_NAME = ChatColor.translateAlternateColorCodes('&',
										"&4&n&lWarfare");
	
	static public String CONSOLE_PREFIX = "[Warfare] ";
	/*
	 * {PLAYERNAME}
	 * 
	 * 
	 * 
	 */
	static public String parseMessage(String s, String playerName)
	{
		while(s.contains("{"))
		{
			s = s.substring(0,s.indexOf('{')) + getString(s.substring(s.indexOf('{') + 1, s.indexOf('}')), playerName) + s.substring(s.indexOf('}')+1);
		}
		return ChatColor.translateAlternateColorCodes('&',s);
	}
	
	static private String getString(String s, String playerName)
	{
		if(s.equalsIgnoreCase("PLAYERNAME"))
			return playerName;
		return "";
	}
	
	static public void sendJsonPlayerChatHoverURL(Player player, String prefix, String name, String message, String hoverText, String URL)
	{
		//IChatBaseComponent msg = ChatSerializer.a("[\"\",{\"text\":\"<" + name + ">\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + URL + "\"}},{\"text\":\" "+ message +"\"}]");
		IChatBaseComponent msg = ChatSerializer.a("[\"\",{\"text\":\""+ prefix + "\"},{\"text\":\"<" + name + ">\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + URL + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + hoverText + "\"}]}}},{\"text\":\" " + message + "\"}]");
		PacketPlayOutChat chat = new PacketPlayOutChat(msg);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(chat); // Send chatPacket
	}
	
	static public void sendJsonPlayerChatHover(Player player, String prefix, String name, String message, String hoverText)
	{
		//IChatBaseComponent msg = ChatSerializer.a("[\"\",{\"text\":\"<" + name + ">\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + URL + "\"}},{\"text\":\" "+ message +"\"}]");
		IChatBaseComponent msg = ChatSerializer.a("[\"\",{\"text\":\""+ prefix + "\"},{\"text\":\"<" + name + ">\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + hoverText + "\"}]}}},{\"text\":\" " + message + "\"}]");
		PacketPlayOutChat chat = new PacketPlayOutChat(msg);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(chat); // Send chatPacket
	}
	
	static public void sendTitle(Player player, String message, int fadeIn, int stayUp, int fadeOut)
	{
		IChatBaseComponent title = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, title);
		PacketPlayOutTitle timingPacket = new PacketPlayOutTitle(fadeIn, stayUp, fadeOut);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket); // Send titlePacket
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timingPacket);// Send timingPacket
	}
	
	static public void sendTitleSub(Player player, String title, String subtitle, int fadeIn, int stayUp, int fadeOut)
	{
		IChatBaseComponent t = ChatSerializer.a("{\"text\": \"" + title + "\"}");
		IChatBaseComponent st = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, t);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, st);
		PacketPlayOutTitle timingPacket = new PacketPlayOutTitle(fadeIn, stayUp, fadeOut);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket); // Send titlePacket
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket); // Send titlePacket
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timingPacket);// Send timingPacket
	}
	
	static public void sendSub(Player player, String subtitle, int fadeIn, int stayUp, int fadeOut)
	{
		IChatBaseComponent t = ChatSerializer.a("{\"text\": \"\"}");
		IChatBaseComponent st = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, t);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, st);
		PacketPlayOutTitle timingPacket = new PacketPlayOutTitle(fadeIn, stayUp, fadeOut);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket); // Send titlePacket
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket); // Send titlePacket
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timingPacket);// Send timingPacket
	}
	
	static public void sendActionBar(Player player, String message)
	{
		CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}
	
}
