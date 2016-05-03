package me.lightfall.warfare.weapons;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import me.lightfall.warfare.Main;
import me.lightfall.warfare.utils.Items;
import me.lightfall.warfare.utils.Msg;
import me.lightfall.warfare.utils.Vars;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponsListener implements Listener{
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		if(Vars.weapons.get(e.getPlayer().getUniqueId()) != null) {
			Weapon weap = Vars.weapons.get(e.getPlayer().getUniqueId());
			if(e.getItemDrop() != null && e.getItemDrop().getItemStack().getType() == weap.getMaterial())
				Vars.weapons.get(e.getPlayer().getUniqueId()).reload();
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerSwitchHotbarSlot(PlayerItemHeldEvent e)
	{
		if(e.getPlayer().getInventory().getItem(e.getPreviousSlot()) != null && Vars.weapons.get(e.getPlayer().getUniqueId()) != null) {
			if(e.getPlayer().getInventory().getItem(e.getPreviousSlot()).getType() == Vars.weapons.get(e.getPlayer().getUniqueId()).getMaterial())
			{
				Vars.weapons.get(e.getPlayer().getUniqueId()).cancelReload();
				e.getPlayer().setLevel(0);
				return;
			}
		}
		if(e.getPlayer().getInventory().getItem(e.getNewSlot()) != null && Vars.weapons.get(e.getPlayer().getUniqueId()) != null)
		{
			if(e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Vars.weapons.get(e.getPlayer().getUniqueId()).getMaterial())
			{
				e.getPlayer().setLevel(Vars.weapons.get(e.getPlayer().getUniqueId()).getTotalAmmo());
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(Vars.weapons.get(e.getPlayer().getUniqueId()) != null) {
				Weapon weap = Vars.weapons.get(e.getPlayer().getUniqueId());
				if(e.getItem() != null && e.getItem().getType() == weap.getMaterial() && e.getPlayer() == e.getPlayer())
				{
					Vars.weapons.get(e.getPlayer().getUniqueId()).fire();
					return;
				}
			}
		}
		
		if(e.getItem() != null)
		{
			if(e.getItem().getType().equals(Items.LOBBY_WEAPON_SELECT_MATERIAL) && e.getItem().getItemMeta().getDisplayName().equals(Items.LOBBY_WEAPON_SELECT_NAME))
			{
				Inventory i = Bukkit.createInventory(null, (int)(Math.ceil((double)(Main.Weapons.size()%45)/9) * 9), "Weapon Select");
				for(String s : Main.Weapons)
				{
					ConfigurationSection c = Main.WeaponConfig.getConfigurationSection("weapon." + s);
					if(c.contains("permission")) {
						if(!e.getPlayer().hasPermission(Main.WeaponConfig.getString("weapon." + s + ".permission")))
						{
							continue;
						}
					}
					
					Material mat = Material.getMaterial(c.getString("material"));
					ItemStack is = new ItemStack(mat == null ? Material.WOOD_AXE : mat);
					ItemMeta m = is.getItemMeta();
					
					if(c.contains("level") && c.getInt("level") > Vars.players.get(e.getPlayer().getUniqueId()).getLevel())
					{
						m.setDisplayName(ChatColor.DARK_GRAY + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', c.getString("name"))));
						List<String> lore = new ArrayList<String>();
						lore.add(ChatColor.DARK_GRAY + "Level required: " + ChatColor.RED + c.getInt("level"));
						m.setLore(lore);
						is.setItemMeta(m);
						i.addItem(is);
						continue;
					}
					
					//if(c.contains("name"))
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
						lore.add("Accuracy: " + (1 - c.getDouble("spread")) + "%");
					if(c.contains("reloadTime"))
						lore.add("Reload time: " + form.format((double)c.getInt("reloadTime")/20) + "s");
					if(c.contains("shotDelay"))
						lore.add("Rate of Fire: " + form.format((double)20/c.getInt("shotDelay")) + "/s");
					if(c.contains("shotsFired"))
						lore.add("Shots fired: " + c.getInt("shotsFired"));
					if(c.contains("burstDelay"))
						lore.add("Burst Delay: " + c.getInt("burstDelay"));
					
					m.setLore(lore);
					is.setItemMeta(m);
					
					net.minecraft.server.v1_8_R2.ItemStack cis = CraftItemStack.asNMSCopy(is);
					NBTTagCompound tag = cis.hasTag() ? cis.getTag() : new NBTTagCompound();

					tag.set("weaponName", new NBTTagString(s));
					cis.setTag(tag);
					
					i.addItem(CraftItemStack.asBukkitCopy(cis));
				}
				e.getPlayer().openInventory(i);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(e.getInventory().getName().equalsIgnoreCase("Weapon Select") && e.getCurrentItem() != null)
		{
			net.minecraft.server.v1_8_R2.ItemStack cis = CraftItemStack.asNMSCopy(e.getCurrentItem());
			if(cis != null && cis.hasTag() && !cis.getTag().getString("weaponName").equals(""))
			{
				if(e.getInventory().getViewers().size() >= 1)
				{
					Player p = (Player) e.getInventory().getViewers().get(0);
					ConfigurationSection s = Main.WeaponConfig.getConfigurationSection("weapon." + cis.getTag().getString("weaponName"));
					
					String type = null;
					Weapon w;
					//Make weapon
					if(s.contains("type"))
						type = s.getString("type");
					if(type == null || type.equalsIgnoreCase("bulletGun"))
						w = new BulletGun(false, e.getCurrentItem().getType(), p);
					else if(type.equalsIgnoreCase("burstGun"))
						w = new BurstGun(false, e.getCurrentItem().getType(), p);
					//else if(type.equalsIgnoreCase("rocketGun"))
					//	w = new RocketGun(false, e.getCurrentItem().getType(), p);
					else
						w = new BulletGun(false, e.getCurrentItem().getType(), p);
						
					
					//Weapon properties
					if(s.contains("name"))
						w.setName(ChatColor.translateAlternateColorCodes('&', s.getString("name")));
					if(s.contains("lore"))
						w.setLore(s.getStringList("lore"));
					if(s.contains("magSize"))
						w.setMagSize(s.getInt("magSize"));
					if(s.contains("startingAmmo"))
						w.setStartingAmmo(s.getInt("startingAmmo"));
					if(s.contains("spread"))
						w.setSpread(s.getDouble("spread"));
					if(s.contains("shiftSpread"))
						w.setShiftSpread(s.getDouble("shiftSpread"));
					if(s.contains("reloadTime"))
						w.setReloadTime(s.getInt("reloadTime"));
					if(s.contains("shotDelay"))
						w.setShotDelay(s.getInt("shotDelay"));
					if(s.contains("damage"))
						w.setDamage(s.getInt("damage"));
					
					//Bulletgun properties
					if(w instanceof BulletGun) {
						if(s.contains("shotsFired"))
							((BulletGun)w).setShotsFired(s.getInt("shotsFired"));
						if(w instanceof BurstGun)
						{
							if(s.contains("burstDelay"))
								((BurstGun)w).setBurstDelay(s.getInt("burstDelay"));
						}
					}
					
					//Rocketgun properties
					//
					//
					//
					
					w.reset();
					//Give weapon
					//p.sendMessage(Msg.PREFIX + "You have been given " + w.getName() + ChatColor.RESET + "!");
					Vars.players.get(p.getUniqueId()).getEditKit().setWeapon(w);
					//Vars.weapons.put(p.getUniqueId(), w);
					
					//Close inventory
					p.closeInventory();
				}
			}
		}
	}
}
