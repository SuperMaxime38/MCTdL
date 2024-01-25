package mctdl.game.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;

public class Damage implements Listener{
	 
	static Main main;
	public Damage(Main main) {
		Damage.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void checkTeamDamage(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
		
		String damager = e.getDamager().getName();
		String victim = e.getEntity().getName();
		
		if(TeamsManager.getPlayerTeam(victim).equals(TeamsManager.getPlayerTeam(damager))) e.setCancelled(true);
		return;
	}
	
	@EventHandler
	public static void damageCanceler(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		String gamemode = main.getConfig().getString("game");
		
		switch(gamemode) {
		case "lobby":
			e.setCancelled(true);
			return;
		case "meltdown":
			e.setCancelled(true);
			return;
		}
	}
	
	
	//CANCEL HUNGER !!
	@EventHandler
	public static void cancelHunger(FoodLevelChangeEvent e) {
		String gamemode = main.getConfig().getString("game");
		
		switch(gamemode) {
		case "lobby":
			e.setCancelled(true);
			return;
		case "meltdown":
			e.setCancelled(true);
			return;
		}
	}
}
