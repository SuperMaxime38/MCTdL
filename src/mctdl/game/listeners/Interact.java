package mctdl.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mctdl.game.Main;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.objects.Canon;

public class Interact implements Listener{
	
	static Main main;
	public Interact(Main main) {
		Interact.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void onInteract(PlayerInteractEvent e) {
		Player p =e.getPlayer();
		
		if(main.getConfig().getString("game").equals("lobby")) {
			if(p.getGameMode().equals(GameMode.CREATIVE)) return;
			e.setCancelled(true);
		}
		
		ItemStack item = e.getItem();
		if(item == null || item.getType() == Material.AIR) return;
		
		if(item.isSimilar(PlayerData.welcolme())) { //Si l'item est le compass de bienvenue
			Inventory inv = Bukkit.createInventory(null, 54, "§bWarps");
			//Do stuff
			
			
			p.openInventory(inv);
		}
		//PLACE CANNON --random mais trql--
		if(item.isSimilar(Canon.getItem())) {
			Block b = p.getTargetBlockExact(6);
			if(b != null) {
				Location lc = new Location(p.getWorld(), p.getTargetBlockExact(6).getX(), p.getTargetBlockExact(6).getY(), p.getTargetBlockExact(6).getZ());
				if(lc.getBlock().getType() != Material.AIR) {

					Canon.placeCanon(p, lc, main);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public static void onEntityInteractAtEntity(PlayerInteractAtEntityEvent e) {
		
		if(main.getConfig().getString("game").equals("lobby")) {
			if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public static void onEntityInteract(PlayerInteractEntityEvent e) {
		if(main.getConfig().getString("game").equals("lobby")) {
			if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
			e.setCancelled(true);
			return;
		}
	}

}
