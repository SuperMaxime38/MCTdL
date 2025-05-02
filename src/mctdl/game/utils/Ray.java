package mctdl.game.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import mctdl.game.ai_trainer.Environnement;

public class Ray {
	
	World world;
	Vector origin, direction;
	
	public Ray(World world, Vector origin, Vector direction) {
		this.world = world;
		this.origin = origin;
		this.direction = direction;
	}
	
	public boolean collides(double maxDistance) {
		Vector current = this.origin.clone();
		
		while(current.distance(this.origin) < maxDistance) {
			current.add(this.direction);
			
			Block b = world.getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ());
			
			if(b.getType() != Material.AIR) {
				return true;
			}
		}
		return false;
	}
	
	public double distanceFromObstacle(double maxDistance) {
		Vector current = this.origin.clone();
		
		while(current.distance(this.origin) < maxDistance) {
			current.add(this.direction);
			
			Block b = world.getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ());
			
			//world.spawnParticle(Particle.FLAME, new Location(world, current.getX(), current.getY(), current.getZ()), 3, 0.01, 0.01, 0.01, -0.0005);
			
			if(!Environnement.getTransparentBlocks().contains(b.getType())) {
				return current.distance(this.origin);
			}
		}
		return maxDistance;
	}
	
	/**
	 * This one's special: Vector direction is the position of the entity/any object you wanna check.
	 * We just trace a ray between the origin and the object's location and we check if there's an obstacle.
	 * If so we return true, else false.
	 */
	public boolean checkTransparencyToDirection() {
		Vector current = this.origin.clone();
		
		Vector dir = this.direction.clone().subtract(this.origin.clone()).normalize();
		
		while(current.distance(this.direction) >= 1) {
			current.add(dir);
			
			Block b = world.getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ());
			
			//world.spawnParticle(Particle.FLAME, new Location(world, current.getX(), current.getY(), current.getZ()), 3, 0.01, 0.01, 0.01, -0.0005);
			
			if(!Environnement.getTransparentBlocks().contains(b.getType())) {
				return true;
			}
		}
		
		return false;
	}

}
