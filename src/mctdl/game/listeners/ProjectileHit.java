package mctdl.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;

public class ProjectileHit implements Listener{
	
	static Main main;
	public ProjectileHit(Main main) {
		ProjectileHit.main = main;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		new BukkitRunnable() {

			@Override
			public void run() {
				e.getEntity().remove();
			}
			
		}.runTaskLater(main, 200);
	}

}
