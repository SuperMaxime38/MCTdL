package mctdl.game.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class Move implements Listener{
	
	static Main main;
	public Move(Main main) {
		Move.main = main;
	}
	
	@EventHandler
	public static void onMove(PlayerMoveEvent e) {
		if(!main.getConfig().getString("game").equals("lobby")) return;
		
		Player p = e.getPlayer();
		
		if(p.getLocation().getBlockY() < 4) {
			Location nouvelle = p.getLocation().subtract(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
			p.setVelocity(nouvelle.toVector().multiply(-0.5));
		}
		if(p.getLocation().getY() < -10) {
			p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
		}
		
		List<EntityPlayer> moves = NPCManager.getLookingNPCs();
		for(EntityPlayer npc : moves) {
			if(npc.getBukkitEntity().getLocation().distance(p.getLocation()) < 32) { //Refresh seulement si npcs est dans un rayon de 2 chunks
				NPCManager.moveNPC(npc, p);
			}
		}
	}
}
