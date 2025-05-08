package mctdl.game.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class Move implements Listener{
	
	static Main main;
	static Location spawn;
	public Move(Main main) {
		Move.main = main;
		spawn = new Location(Bukkit.getWorlds().get(0), 8, 6, 8);
	}
	
	@EventHandler
	public static void onMove(PlayerMoveEvent e) {
		
		Player p = e.getPlayer();
		
		
		if(main.getConfig().getString("game").equals("lobby")) {
			
			double deltaX = p.getLocation().getX() - spawn.getX();
			double deltaZ = p.getLocation().getZ() - spawn.getZ();
			double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ); // distance horizontale seulement
			
			if(p.getLocation().getBlockY() < 4 || distance > 200) {
			Location nouvelle = p.getLocation().subtract(spawn);
			p.setVelocity(nouvelle.toVector().multiply(-0.5));
			}
			if(p.getLocation().getY() < -10) {
				p.teleport(spawn);
			}
		}
		
		
		if(NPCManager.getInViewNPCs().get(p) == null) {
			NPCManager.inViewNPCs.put(p, new ArrayList<EntityPlayer>());
		}
		//Refresh NPCs (chunk loading at 128 or something fin bref)
		for(EntityPlayer npc : NPCManager.getNPCs()) {
			Location loc = new Location(p.getWorld(), npc.locX(), npc.locY(), npc.locZ());
			double distance = p.getLocation().distance(loc);
			
			if(distance < 48 && !NPCManager.getInViewNPCs().get(p).contains(npc)) {
				List<EntityPlayer> npcs = NPCManager.getInViewNPCs().get(p);
				npcs.add(npc);
				NPCManager.inViewNPCs.put(p, npcs);
				
				NPCManager.showNpcWithoutTabFor(npc, p, null);

			} else if(distance > 48 && NPCManager.getInViewNPCs().get(p).contains(npc)) {
				List<EntityPlayer> npcs = NPCManager.getInViewNPCs().get(p);
				npcs.remove(npc);
				NPCManager.inViewNPCs.put(p, npcs);
			}
		}
		
		List<EntityPlayer> moves = NPCManager.getLookingNPCs();
		for(EntityPlayer npc : moves) {
			if(npc.getBukkitEntity().getLocation().distance(p.getLocation()) < 32) { //Refresh seulement si npcs est dans un rayon de 2 chunks
				NPCManager.moveNPC(npc, p);
			}
		}
	}
	
	@EventHandler
	public static void doubleJump(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if(p.getGameMode().equals(GameMode.SPECTATOR) || p.getGameMode().equals(GameMode.CREATIVE)) return;
		
		p.setFlying(false);
		e.setCancelled(true);
		
		if(Main.game.equals("lobby")) {
			p.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
			p.setGliding(true);
			
			new BukkitRunnable() {
				public void run() {
					if(((Entity) p).isOnGround()) {
						p.getInventory().setChestplate(new ItemStack(Material.AIR));
						cancel();
						return;
					}
					
				}
			}.runTaskTimer(main, 0, 10);
		}
	}
}
