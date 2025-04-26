package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.Pair;
import mctdl.game.utils.Ray;

public class Environnement {
	
	PlayerAI player;
	World world;
	
	AiView view;
	TerrainObserver terrainObserver;
	
	List<Double> rayDistances, terrain;
	List<Pair> ennemies, allies;
	List<Float> other; // données supplémentaires
	
	
	float[] datas;
	

	public static final List<Material> transparentBlocks = Arrays.asList(Material.AIR, Material.LADDER, Material.WATER);
	
	public Environnement(PlayerAI player) {
		this.player = player;
		this.world = player.getBukkitEntity().getWorld();
		
		this.view = new AiView(player.getBukkitEntity().getWorld(), Arrays.asList(new Vector(0,0.25,0), new Vector(0,0.75,0)), 8, player);
		this.terrainObserver = new TerrainObserver(player, 4);
		
		this.rayDistances = new ArrayList<Double>();
		this.terrain = new ArrayList<Double>();
		this.other = new ArrayList<Float>();
		
		this.ennemies = new ArrayList<Pair>();
		this.allies = new ArrayList<Pair>();
	}
	
	public void update() {
		this.rayDistances.clear();
		this.terrain.clear();
		
		this.view.update();
		this.rayDistances = view.compute();
		
		this.terrain = terrainObserver.update();
		
		this.updatePlayersDistances();
	}
	
	public void updatePlayersDistances() {
		this.ennemies.clear();
		this.allies.clear();
		for(String team : TeamsManager.getOnlineTeams()) {
			if(!TeamsManager.getPlayerTeam(this.player.getUniqueIDString()).equals(team)) { // On check pas sa team vu que la c'est les ennemies
				
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					Player p = Bukkit.getPlayer(uuid);
					if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
					
					Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
					double dist = p.getLocation().distance(loc);
					
					this.ennemies.add(new Pair(dist, new Ray(world, loc.toVector(), p.getLocation().toVector()).checkTransparencyToDirection()));
				}
				
			} else { // pis là qu'on y est autant handle direct les allies
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					Player p = Bukkit.getPlayer(uuid);
					if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
					
					Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
					double dist = p.getLocation().distance(loc);
					
					this.allies.add(new Pair(dist, new Ray(world, loc.toVector(), p.getLocation().toVector()).checkTransparencyToDirection()));
				}
			}
		}
	}
	
	public static List<Material> getTransparentBlocks() {
		return transparentBlocks;
	}
}
