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
		meta.setLore(Arrays.asList("§7Boosts your §bvelocity §7in the direction you're looking at!", "However you take §42HP �7 of §cdamage", "RIGHT CLICK to use","","§5CONSUMABLE"));
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
			
			/*
			 * Notes:
			 * La velocité du joueur en X, Z est nulle sauf en cas de saut / qu'un autre évènement modifie cette valeur.
			 * On ne peut donc pas juste multiplier la velocité...
			 * Solution de secours: A la place de multiplier sa velocité, on boost celle ci dans la direction du joueur
			 */
			
			
			p.setVelocity(p.getVelocity().add(p.getLocation().getDirection().multiply(3)));
			
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
			
			p.damage(2);
		}
	}

}
