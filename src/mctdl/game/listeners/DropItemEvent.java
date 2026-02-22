package mctdl.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import mctdl.game.Main;

public class DropItemEvent implements Listener{
	
	static Main main;
	public DropItemEvent(Main main) {
		Move.main = main;
	}
	
	@EventHandler
	public static void onDropItemEvent(PlayerDropItemEvent e) {
		e.setCancelled(true);
		
		// FUTURE ME: If in some cases you want to allow players to drop items, add conditions here
	}

}
