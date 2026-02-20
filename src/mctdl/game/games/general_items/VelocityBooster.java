package mctdl.game.games.general_items;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mctdl.game.utils.NBTAPI;

public class VelocityBooster implements Listener {
	
	public static ItemStack getItem() {
		ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bVelocity Booster");
		meta.setLore(Arrays.asList("§7Multiplies your §bvelocity §7by §65 §7!", "However you take §42HP �7 of §cdamage", "RIGHT CLICK to use","","§5CONSUMABLE"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		
		item.setItemMeta(meta);
		
		NBTAPI.addNBT(item, "mctdlID", "VELOCITY_BOOSTER");
		
		return item;
	}
	
	@EventHandler
	public static void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		ItemStack item = getItem();
		
		if(!p.getInventory().getItemInMainHand().isSimilar(item)) return;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			p.setVelocity(p.getVelocity().multiply(5));
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
		}
	}

}
