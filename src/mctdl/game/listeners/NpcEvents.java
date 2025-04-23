package mctdl.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import mctdl.game.npc.PlayerAI;
import mctdl.game.utils.custom_events.NpcHitByProjectileEvent;

public class NpcEvents implements Listener{
	
	@EventHandler
	public void onNpcHit(NpcHitByProjectileEvent event) {
	    PlayerAI npc = event.getNpc();
	    LivingEntity shooter = event.getShooter();
	    
	    Bukkit.broadcastMessage("Le NPC " + npc.getName() + " a été touché par une flèche !");
	    
	    // This is an example
	    if (shooter != null && shooter.getName().equals("JeanSniper")) {
	        // Infliger plus de dégâts si le tireur est JeanSniper
	        event.setDamage(10f);
	    }
	}

}
