package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import mctdl.game.npc.PlayerAI;
import mctdl.game.utils.Ray;

public class AiView {
	
	World world;
	List<Vector> origins;
	int rayAmount;
	double angle;
	List<Ray> rays;
	List<Double> distances;
	PlayerAI player;
	
	/**
	 * 
	 * @param world: (Bukkit) world
	 * @param origins: Liste des origines d'où doivent partir les rayons (l'origine est relative à la location du joueur)
	 * @param rayAmount: la quantité de rayon PAR origine
	 * @param player: le joueur IA lié aux rayons
	 */
	public AiView(World world, List<Vector> origins, int rayAmount, PlayerAI player) {
	
		this.world = world;
		this.origins = origins;
		this.rayAmount = rayAmount;
		this.angle = 360 / rayAmount;
		this.player = player;
		
		this.rays = new ArrayList<Ray>();
		this.distances = new ArrayList<Double>();
		this.update();
	}
	
	public void update() {
		rays.clear();
		for(int i = 0; i < rayAmount; i++) {
			for(Vector origin : origins) {
				Vector loc = new Vector(player.locX(), player.locY(), player.locZ());
				double angle = this.player.getYaw() + (this.angle * i);
				double rad = Math.toRadians(angle);
		    	double ZVal = Math.cos(rad);
		    	double XVal = -Math.sin(rad);
		    	Vector vect = loc.add(origin);
		    	world.spawnParticle(org.bukkit.Particle.BARRIER, new Location(world, vect.getX(), vect.getY(), vect.getZ()), 1);
				rays.add(new Ray(this.world, loc.add(origin), new Vector(XVal, 0, ZVal)));
			}
		}
	}
	
	public List<Double> compute() {
		
		distances.clear();
		for(Ray ray : rays) {
			distances.add(ray.distanceFromObstacle(200));
		}
		return distances;
	}
	
	public List<Double> getDistances() { return distances; }
}
