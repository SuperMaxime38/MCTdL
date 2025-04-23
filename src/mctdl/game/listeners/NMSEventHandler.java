package mctdl.game.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import mctdl.game.npc.PlayerAI;
import mctdl.game.utils.custom_events.NpcHitByProjectileEvent;
import net.minecraft.server.v1_16_R3.DamageSource;

public class NMSEventHandler {
	
	private static final Map<UUID, Location> arrowLastLocations = new HashMap<>();
	
	public static void loop(Main main) {
		new BukkitRunnable() {
		    @Override
		    public void run() {
		    	for (World world : Bukkit.getWorlds()) {
		    		for (org.bukkit.entity.Entity entity : world.getEntities()) {
		    		    if (!(entity instanceof org.bukkit.entity.Arrow)) continue;

		    		    org.bukkit.entity.Arrow arrow = (org.bukkit.entity.Arrow) entity;
		    		    //net.minecraft.server.v1_16_R3.Entity arrowNMS = ((CraftArrow) arrow).getHandle();

		    		    Location lastLoc = arrowLastLocations.get(arrow.getUniqueId());
		    		    Location currentLoc = arrow.getLocation();
		    		    
		    		    if (lastLoc != null) {
		    		        for (MeltdownNPC meltdownNPC : Meltdown.getNPCs()) {
		    		            PlayerAI npc = meltdownNPC.getNPC();
		    		            if (!npc.getBukkitEntity().getWorld().equals(arrow.getWorld())) continue;

		    		            // Interpolation : Est-ce que le segment (lastLoc -> currentLoc) croise le NPC ?
		    		            if (intersectsNPC(lastLoc, currentLoc, npc)) {

		    		                NpcHitByProjectileEvent event = new NpcHitByProjectileEvent(npc, arrow,
		    		                    (arrow.getShooter() instanceof LivingEntity) ? (LivingEntity) arrow.getShooter() : null,
		    		                    4f
		    		                );

		    		                Bukkit.getPluginManager().callEvent(event);

		    		                if (!event.isCancelled()) {
		    		                    npc.damageEntity(DamageSource.GENERIC, event.getDamage());

		    		                    if (event.getShooter() != null) {
		    		                        double dx = npc.locX() - arrow.getLocation().getX();
		    		                        double dz = npc.locZ() - arrow.getLocation().getZ();
		    		                        double magnitude = Math.sqrt(dx * dx + dz * dz);
		    		                        if (magnitude > 0) {
		    		                            dx /= magnitude;
		    		                            dz /= magnitude;
		    		                            npc.setMot(npc.getMot().add(dx * 0.4, 0.3, dz * 0.4));
		    		                        }
		    		                    }

		    		                    arrow.remove();
		    		                }
		    		                break;
		    		            }
		    		        }
		    		    }
		    		    // Mettre à jour la position
		    		    arrowLastLocations.put(arrow.getUniqueId(), currentLoc);
		    		}
		        }
		    }
		}.runTaskTimer(main, 0L, 1L); // toutes les ticks
	}
	
	private static boolean intersectsNPC(Location start, Location end, PlayerAI npc) {
	    // Centre du NPC en 3D
	    double cx = npc.locX();
	    double cy = npc.locY() + 0.9;
	    double cz = npc.locZ();

	    // Vecteur de déplacement de la flèche
	    double dx = end.getX() - start.getX();
	    double dy = end.getY() - start.getY();
	    double dz = end.getZ() - start.getZ();

	    double px = start.getX() - cx;
	    double py = start.getY() - cy;
	    double pz = start.getZ() - cz;

	    double lengthSq = dx * dx + dy * dy + dz * dz;
	    if (lengthSq < 1e-5) return false; // trop petit

	    // Projection du centre du NPC sur la trajectoire de la flèche
	    double t = -(px * dx + py * dy + pz * dz) / lengthSq;
	    t = Math.max(0, Math.min(1, t)); // clamp entre 0 et 1

	    // Point le plus proche sur le segment
	    double closestX = start.getX() + dx * t;
	    double closestY = start.getY() + dy * t;
	    double closestZ = start.getZ() + dz * t;

	    double distSq =
	            (cx - closestX) * (cx - closestX) +
	            (cy - closestY) * (cy - closestY) +
	            (cz - closestZ) * (cz - closestZ);


	    double horizSq = (cx - closestX) * (cx - closestX) + (cz - closestZ) * (cz - closestZ);
	    double vert = Math.abs((npc.locY() + 0.9) - closestY);
	    

	    // Debug
	    System.out.println("Dist = " + Math.sqrt(distSq) + " Horiz = " + Math.sqrt(horizSq) + " Vert = " + vert);

	    return distSq <= 1.8 * 1.8; // rayon de 0.5 bloc
	    
	}

}
