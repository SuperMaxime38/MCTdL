package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.List;

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
	
	private final int dividingCoef;

	/**
	 * 
	 * @param world: (Bukkit) world
	 * @param origins: Liste des origines d'o� doivent partir les rayons (l'origine est relative � la location du joueur)
	 * @param rayAmount: la quantit� de rayon PAR origine
	 * @param player: le joueur IA li� aux rayons
	 */
	public AiView(World world, List<Vector> origins, int rayAmount, PlayerAI player) {
	
		this.world = world;
		this.origins = origins;
		this.rayAmount = rayAmount;
		this.angle = 360 / rayAmount;
		this.player = player;
		
		this.dividingCoef = 10;
		
		this.rays = new ArrayList<Ray>();
		this.distances = new ArrayList<Double>();
		this.update();
	}
	
	/**
	 * 
	 * @param world: (Bukkit) world
	 * @param origins: Liste des origines d'o� doivent partir les rayons (l'origine est relative � la location du joueur)
	 * @param rayAmount: la quantit� de rayon PAR origine
	 * @param player: le joueur IA li� aux rayons
	 * @param divingCoef: le coefficient de division: les distances seront diviser par ce coefficient
	 */
	public AiView(World world, List<Vector> origins, int rayAmount, PlayerAI player, int dividingCoef) {
	
		this.world = world;
		this.origins = origins;
		this.rayAmount = rayAmount;
		this.angle = 360 / rayAmount;
		this.player = player;
		
		this.dividingCoef = dividingCoef;
		
		this.rays = new ArrayList<Ray>();
		this.distances = new ArrayList<Double>();
		this.update();
	}
	
	public void update() {
		rays.clear();
		for(int i = 0; i < rayAmount; i++) {
			for(Vector origin : origins) {
				Vector loc = new Vector(player.getX(), player.getY(), player.getZ());
				double angle = this.player.getYaw() + (this.angle * i);
				double rad = Math.toRadians(angle);
		    	double ZVal = Math.cos(rad);
		    	double XVal = -Math.sin(rad);
		    	Vector vect = loc.add(origin);
		    	//world.spawnParticle(org.bukkit.Particle.BARRIER, new Location(world, vect.getX(), vect.getY(), vect.getZ()), 1);
				rays.add(new Ray(this.world, vect, new Vector(XVal, 0, ZVal)));
			}
		}
	}
	
	public List<Double> compute() {
		
		distances.clear();
		for(Ray ray : rays) {
			distances.add(ray.distanceFromObstacle(200) / dividingCoef);
		}
		return distances;
	}
	
	public List<Double> getDistances() { return distances; }
}
