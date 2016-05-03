package me.lightfall.warfare.kits;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.PlayerProfile;
import me.lightfall.warfare.perks.Perk;
import me.lightfall.warfare.utils.Items;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.Vars;
import me.lightfall.warfare.weapons.BulletGun;
import me.lightfall.warfare.weapons.BurstGun;
import me.lightfall.warfare.weapons.Weapon;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitListener implements Listener{
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(e.getCurrentItem() != null && e.getCurrentItem().equals(Items.GENERAL_BACK_MATERIAL) && e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.GENERAL_BACK_NAME))
			e.getWhoClicked().closeInventory();
		else if(e.getCurrentItem() != null && e.getCurrentItem().equals(Items.GENERAL_CLOSE_MATERIAL) && e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.GENERAL_CLOSE_NAME))
		{
			Vars.inventory.get(e.getWhoClicked().getUniqueId()).clear();
			e.getWhoClicked().closeInventory();
		}
		else if(e.getInventory().getName().equalsIgnoreCase(Items.KIT_CHANGE_NAME) && e.getCurrentItem() != null){
			PlayerProfile pf = Vars.players.get(e.getWhoClicked().getUniqueId());
			if(e.getSlot() == Items.KIT_SELECT_KIT1_SLOT)
				pf.setKit((byte) 0);
			else if(e.getSlot() == Items.KIT_SELECT_KIT2_SLOT)
				pf.setKit((byte) 1);
			else if(e.getSlot() == Items.KIT_SELECT_KIT3_SLOT)
				pf.setKit((byte) 2);
			else
				return;
			e.getWhoClicked().sendMessage(Msg.PREFIX + "You will respawn with the new kit!");
			e.getWhoClicked().closeInventory();
		}
		else if(e.getInventory().getName().equalsIgnoreCase(Items.KIT_EDIT_NAME) && e.getCurrentItem() != null)
		{
			Player p = (Player) e.getWhoClicked();
			PlayerProfile pf = Vars.players.get(p.getUniqueId());
			if(e.getSlot() == Items.KIT_SELECT_KIT1_SLOT){
				pf.setEditKit((byte) 0);
				pf.setKit((byte) 0);
			}
			else if(e.getSlot() == Items.KIT_SELECT_KIT2_SLOT) {
				pf.setEditKit((byte) 1);
				pf.setKit((byte) 1);
			}
			else if(e.getSlot() == Items.KIT_SELECT_KIT3_SLOT) {
				pf.setEditKit((byte) 2);
				pf.setKit((byte) 2);
			}
			else
				return;
			
			//Add inventory to inventory stack
			Vars.inventory.get(p.getUniqueId()).add(p.getOpenInventory().getTopInventory());
			Vars.inventory.get(p.getUniqueId()).add(null);
			
			Inventory i = Bukkit.createInventory(null, 6 * 9, Items.KIT_EDIT_NAME + " " + ChatColor.BLACK + (pf.getEditKitVal() + 1));
			p.openInventory(i);
		}
		else if(e.getInventory().getName().startsWith(Items.KIT_EDIT_NAME) && e.getCurrentItem() != null)
		{
			if(e.getSlot() == Items.KIT_EDIT_WEAPON_SLOT)
			{
				Player p = (Player) e.getWhoClicked();
				
				Inventory i = Bukkit.createInventory(null, (int)(Math.ceil((double)(Main.Weapons.size()%45)/9) * 9), "Weapon Select");
				for(String s : Main.Weapons)
				{
					ConfigurationSection c = Main.WeaponConfig.getConfigurationSection("weapon." + s);
					if(c.contains("permission"))
						if(!p.hasPermission(Main.WeaponConfig.getString("weapon." + s + ".permission")))
							continue;
					if (c.contains("level") && c.getInt("level") > Vars.players.get(p.getUniqueId()).getLevel())
						continue;
					
					Material mat = Material.getMaterial(c.getString("material"));
					ItemStack is = new ItemStack(mat == null ? Material.WOOD_AXE : mat);
					ItemMeta m = is.getItemMeta();
					
					if(c.contains("name"))
						m.setDisplayName(ChatColor.translateAlternateColorCodes('&', c.getString("name")));
					
					final NumberFormat form = new DecimalFormat("#0.00");
					
					//Make weapon lore
					List<String> lore = new ArrayList<String>();
					
					if(c.contains("typeName"))
						lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + c.getString("typeName"));
					else
						lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Bullet Gun");
					if(c.contains("lore"))
						for(String str : c.getStringList("lore"))
						{
							lore.add(ChatColor.translateAlternateColorCodes('&', str));
						}
					if(c.contains("damage"))
						lore.add("Damage: " + c.getInt("damage"));
					if(c.contains("magSize"))
						lore.add("Mag Size: " + c.getInt("magSize"));
					if(c.contains("startingAmmo"))
						lore.add("Starting Ammo: " + c.getInt("startingAmmo"));
					if(c.contains("spread"))
						lore.add("Accuracy: " + (1 - c.getDouble("spread")) * 100 + "%");
					if(c.contains("reloadTime"))
						lore.add("Reload time: " + form.format((double)c.getInt("reloadTime")/20) + "s");
					if(c.contains("shotDelay"))
						lore.add("Rate of Fire: " + form.format((double)20/c.getInt("shotDelay")) + "/s");
					if(c.contains("shotsFired"))
						lore.add("Shots fired: " + c.getInt("shotsFired"));
					if(c.contains("burstDelay"))
						lore.add("Burst Delay: " + c.getInt("burstDelay"));
					
					m.setLore(lore);
					m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					is.setItemMeta(m);
					
					net.minecraft.server.v1_8_R2.ItemStack cis = CraftItemStack.asNMSCopy(is);
					NBTTagCompound tag = cis.hasTag() ? cis.getTag() : new NBTTagCompound();

					tag.set("weaponName", new NBTTagString(s));
					cis.setTag(tag);
					
					i.addItem(CraftItemStack.asBukkitCopy(cis));
				}
				
				Vars.inventory.get(p.getUniqueId()).add(p.getOpenInventory().getTopInventory());
				Vars.inventory.get(p.getUniqueId()).add(null);
				
				p.openInventory(i);
			}
			else if(e.getCurrentItem().getType().equals(Items.KIT_EDIT_PERK_HAS_NOT))
			{//If the player clicks on a perk that they do not have
				Player p = (Player) e.getWhoClicked();
				PlayerProfile pf = Vars.players.get(p.getUniqueId());
				pf.getEditKit().setPerk(Perk.searchByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())));
				updateKitEditInventory(e.getInventory(), (Player) e.getWhoClicked());
			}
			else if(e.getCurrentItem().getType().equals(Items.KIT_EDIT_PERK_HAS))
			{//If the player clicks on a perk that they do not have
				Player p = (Player) e.getWhoClicked();
				PlayerProfile pf = Vars.players.get(p.getUniqueId());
				pf.getEditKit().removePerk(Perk.searchByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())));
				updateKitEditInventory(e.getInventory(), (Player) e.getWhoClicked());
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		if(e.getInventory().getName().equals(Items.KIT_CHANGE_NAME) || e.getInventory().getName().equals(Items.KIT_EDIT_NAME))
		{
			ItemStack is;
			ItemMeta meta;
			Inventory i = e.getInventory();
			
			
			Kit[] k = Vars.players.get(e.getPlayer().getUniqueId()).getKits();
			for(int j = 0; j < k.length; j++)
			{
				is = new ItemStack(k[j].getWeapon().getMaterial());
				meta = is.getItemMeta();
				meta.setDisplayName(k[j].getWeapon().getName());
				ArrayList list = (ArrayList) genLore(k[j].getWeapon()); 
				if(e.getInventory().getName().equals(Items.KIT_CHANGE_NAME))
				{
					if(k[j].perk1() != null)
						list.add(ChatColor.DARK_AQUA + k[j].perk1().name);
					else
						list.add(ChatColor.DARK_GRAY + "No tier 1 perk");
					if(k[j].perk2() != null)
						list.add(ChatColor.DARK_AQUA + k[j].perk2().name);
					else
						list.add(ChatColor.DARK_GRAY + "No tier 2 perk");
					if(k[j].perk3() != null)
						list.add(ChatColor.DARK_AQUA + k[j].perk3().name);
					else
						list.add(ChatColor.DARK_GRAY + "No tier 3 perk");
				}
				meta.setLore(list);
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				is.setItemMeta(meta);
				
				if(j == 0) {
					i.setItem(Items.KIT_SELECT_KIT1_SLOT, is);
				}
				else if(j == 1) {
					
					i.setItem(Items.KIT_SELECT_KIT2_SLOT, is);
				}
				else if(j == 2) {
					i.setItem(Items.KIT_SELECT_KIT3_SLOT, is);
				}
			}
			
			//Place green square around current kit
			byte kitNum = Vars.players.get(e.getPlayer().getUniqueId()).getKitNum();
			ItemStack greenSquare = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GREEN.getData());
			ItemMeta gsMeta = greenSquare.getItemMeta();
			gsMeta.setDisplayName(ChatColor.GREEN + "Your current active kit is " + (kitNum + 1));
			greenSquare.setItemMeta(gsMeta);
			for(int slot : Items.KIT_SELECT_KIT1_OUTLINE_SLOTS)
				i.setItem(slot, null);
			for(int slot : Items.KIT_SELECT_KIT2_OUTLINE_SLOTS)
				i.setItem(slot, null);
			for(int slot : Items.KIT_SELECT_KIT3_OUTLINE_SLOTS)
				i.setItem(slot, null);
			switch (kitNum)
			{
			case 0:
				for(int slot : Items.KIT_SELECT_KIT1_OUTLINE_SLOTS)
					i.setItem(slot, greenSquare);
				break;
			case 1:
				for(int slot : Items.KIT_SELECT_KIT2_OUTLINE_SLOTS)
					i.setItem(slot, greenSquare);
				break;
			case 2:
				for(int slot : Items.KIT_SELECT_KIT3_OUTLINE_SLOTS)
					i.setItem(slot, greenSquare);
				break;
			}
			
			//Place back/close
			is = Items.GENERAL_BACK_MATERIAL;
			meta = is.getItemMeta();
			meta.setDisplayName(Items.GENERAL_BACK_NAME);
			is.setItemMeta(meta);
			i.setItem(0, is);
			
			is = Items.GENERAL_CLOSE_MATERIAL;
			meta = is.getItemMeta();
			meta.setDisplayName(Items.GENERAL_CLOSE_NAME);
			is.setItemMeta(meta);
			i.setItem(8, is);
			
		}
		else if(e.getInventory().getName().startsWith(Items.KIT_EDIT_NAME))
		{
			updateKitEditInventory(e.getInventory(), (Player) e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
			if(e.getItem() != null && e.getItem().getType().equals(Items.LOBBY_KIT_MATERIAL) && e.getItem().getItemMeta().getDisplayName().equals(Items.LOBBY_KIT_NAME))
			{
				Inventory i = Bukkit.createInventory(null, 3 * 9, Items.KIT_EDIT_NAME);
				
				e.getPlayer().openInventory(i);
				e.setCancelled(true);
			}
			else if(e.getItem() != null && e.getItem().getType().equals(Items.KIT_CHANGE_MATERIAL) && e.getItem().getItemMeta().getDisplayName().equals(Items.KIT_CHANGE_NAME))
			{
				Inventory i = Bukkit.createInventory(null, 3 * 9, Items.KIT_CHANGE_NAME);
				
				e.getPlayer().openInventory(i);
				e.setCancelled(true);
			}
		}
	}
	
	private List<String> genLore(Weapon w)
	{
		final NumberFormat form = new DecimalFormat("#0.00");
		
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Damage: " + ChatColor.RESET + form.format(w.getDamage()));
		lore.add(ChatColor.GREEN + "Mag Size: " + ChatColor.RESET + w.getMagSize());
		lore.add(ChatColor.GREEN + "Starting Ammo: " + ChatColor.RESET + w.getStartingAmmo());
		lore.add(ChatColor.GREEN + "Reload Time: " + ChatColor.RESET + w.getReloadTime());
		lore.add(ChatColor.GREEN + "Rate of Fire: " + ChatColor.RESET + form.format((double)20/w.getShotDelay()) + "/s");
		lore.add(ChatColor.GREEN + "Accuracy: " + ChatColor.RESET + form.format((double)(1 - w.getSpread()) * 100) + "%");
		if(w instanceof BulletGun)
			lore.add(ChatColor.GREEN + "Shots Fired: " + ChatColor.RESET + ((BulletGun)w).getShotsFired());
		if(w instanceof BurstGun)
			lore.add(ChatColor.GREEN + "Burst Delay: " + ChatColor.RESET + ((BurstGun)w).getBurstDelay());
		
		return lore;
	}
	
	private void updateKitEditInventory(Inventory i, Player p)
	{
		i.clear();
		ItemStack is;
		ItemMeta meta;
		
		//Place weapon
		Kit k = Vars.players.get(p.getUniqueId()).getEditKit();
		is = new ItemStack(k.getWeapon().getMaterial());
		meta = is.getItemMeta();
		meta.setDisplayName(k.getWeapon().getName());
		meta.setLore(genLore(k.getWeapon()));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(meta);
		i.setItem(Items.KIT_EDIT_WEAPON_SLOT, is);
		
		//Place perks
		PlayerProfile pf = Vars.players.get(p.getUniqueId());
		for(Perk perk : Perk.values())
		{
			if(pf.getEditKit().contains(perk))
				is = new ItemStack(Items.KIT_EDIT_PERK_HAS);
			else
			{
				if(pf.getLevel() >= perk.level)
					is = new ItemStack(Items.KIT_EDIT_PERK_HAS_NOT);
				else
					is = new ItemStack(Items.KIT_EDIT_PERK_NOT_AVAILABLE);
			}
			
			meta = is.getItemMeta();
			meta.setDisplayName((is.getType() == Items.KIT_EDIT_PERK_NOT_AVAILABLE ? ChatColor.DARK_GRAY : ChatColor.GOLD) + perk.name);
			ArrayList lore = new ArrayList();
			for(String s : perk.desc.split("\n")) lore.add(s);
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			is.setItemMeta(meta);
			i.setItem(perk.editSlot, is);
		}
		
		//Place back/close
		is = Items.GENERAL_BACK_MATERIAL;
		meta = is.getItemMeta();
		meta.setDisplayName(Items.GENERAL_BACK_NAME);
		is.setItemMeta(meta);
		i.setItem(0, is);
		
		is = Items.GENERAL_CLOSE_MATERIAL;
		meta = is.getItemMeta();
		meta.setDisplayName(Items.GENERAL_CLOSE_NAME);
		is.setItemMeta(meta);
		i.setItem(8, is);
		
		
	}
	
}
