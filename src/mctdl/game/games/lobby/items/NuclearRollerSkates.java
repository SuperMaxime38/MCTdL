package mctdl.game.games.lobby.items;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mctdl.game.Main;

public class NuclearRollerSkates implements Listener{
	

	public static ItemStack getItem() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta lMeta = (LeatherArmorMeta) meta;
		lMeta.setDisplayName("§6Nuclear §bRoller Skates");
		lMeta.setColor(Color.fromBGR(205,205,100));
		lMeta.setLore(Arrays.asList("§7Imagine having nuclear powered shoes, well that's what this item does!", "§dWin all your races with these!", "§7You could even become the new §4f§6a§es§2h§ai§bo§3n §7model!","","§5COSMETIC"));
		lMeta.setUnbreakable(true);
		lMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		lMeta.addItemFlags(ItemFlag.HIDE_DYE);
		
		item.setItemMeta(lMeta);
		return item;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!Main.game.equals("lobby")) return;
		Location from = e.getFrom();
		Location to = e.getTo();
		if(from.distance(to) < 0.1) return;
		
		Player p = e.getPlayer();
		if(p.getInventory().getBoots() == null || !p.getInventory().getBoots().isSimilar(getItem())) return;
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3, 10));
		p.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 3, 0.25, 0.25, 0.25, -0.0005);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!Main.game.equals("lobby")) return;
		
		Player p = e.getPlayer();
		ItemStack item = getItem();
		
		if(!p.getInventory().getItemInMainHand().isSimilar(item)) return;
		
		ItemStack feet = p.getInventory().getBoots();
		p.getInventory().setBoots(item);
		if(feet != null) {
			p.getInventory().setItemInMainHand(feet);
		} else {
			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
		p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 0);
	}
	
}
