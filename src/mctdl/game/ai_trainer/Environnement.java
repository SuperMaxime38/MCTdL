package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.games.meltdown.Meltdown;
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
	
	
	float[] datas;
	
	float[] inputs;

	public static final List<Material> transparentBlocks = Arrays.asList(Material.AIR, Material.LADDER, Material.WATER);
	
	public Environnement(PlayerAI player) {
		this.player = player;
		this.world = player.getBukkitEntity().getWorld();
		
		this.view = new AiView(player.getBukkitEntity().getWorld(), Arrays.asList(new Vector(0,0.25,0), new Vector(0,0.75,0)), 8, player);
		this.terrainObserver = new TerrainObserver(player, 4);
		
		this.rayDistances = new ArrayList<Double>();
		this.terrain = new ArrayList<Double>();
		
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
		this.updateGamesDatas();
		
		int size = rayDistances.size() + terrain.size() + ennemies.size() + allies.size() + datas.length;
		
		this.inputs = new float[size+2];
		System.out.println("Inputs size: " + inputs.length + " | size: " + size);
		
		int i = 0;
		for(double rayDistance : rayDistances) {
			this.inputs[i] = (float) rayDistance;
			i++;
		}
		System.out.println("index: " + i);
		
		for(double terrain : this.terrain) {
			this.inputs[i] = (float) terrain;
			i++;
		}
		System.out.println("index: " + i);
		
		for(Pair pair : this.ennemies) {
			this.inputs[i] = (float) pair.getKey();
			this.inputs[i+1] = (float) pair.getValue();
			
			System.out.println(" dist | wall: " + pair.getKey() + " | " + pair.getValue());
			
			i += 2;
		}
		System.out.println("index: " + i);
		
		for(Pair pair : this.allies) {
			this.inputs[i] = (float) pair.getKey();
			this.inputs[i+1] = (float) pair.getValue();
			i += 2;
		}
		System.out.println("index: " + i);
		
		for(int j = 0; j < datas.length; j++) {
			System.out.println("Index i|j: " + i + " | " + j);
			this.inputs[i] = datas[j];
			i++;
		}
		
		//System.out.println(" inputs: " + Arrays.toString(inputs));
	}
	
	public void updatePlayersDistances() {
		this.ennemies.clear();
		this.allies.clear();
		for(String team : TeamsManager.getOnlineTeams()) {
			System.out.println("Team: " + team);
			
			if(!TeamsManager.getPlayerTeam(this.player.getUniqueIDString()).equals(team)) { // On check pas sa team vu que la c'est les ennemies
				
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					System.out.println("UUID: " + uuid);
					Player p = Bukkit.getPlayer(UUID.fromString(uuid));
					if(p == null) {
						p = NPCManager.getNpcPlayerIfItIs(uuid);
					}
					
					if(p != null) {// Maybe an offline player
						System.out.println("Player: " + p.getName());
					
						if(Meltdown.getRawPlayerDatas(uuid).get(1) ==1 || Meltdown.getRawPlayerDatas(uuid).get(0) == 0) { // Frozen / dead
							this.ennemies.add(new Pair(Float.MAX_VALUE, 1f));
						} else {
							Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
						float dist = (float) p.getLocation().distance(loc);
						
						//System.out.println("Team : " + team + " player: " + p.getName() + "dist: " + dist);
						
						boolean isNotStraightLine = new Ray(world, loc.toVector(), p.getLocation().toVector()).checkTransparencyToDirection();
						float floatVersion = 0f;
						if(isNotStraightLine) floatVersion = 1f;
						
						this.ennemies.add(new Pair(dist, floatVersion));
						}
						
						
					}
					
					
				}
				
			} else { // pis là qu'on y est autant handle direct les allies
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					System.out.println("UUID: " + uuid);
					if(!uuid.equals(this.player.getUniqueIDString())) {
						Player p = Bukkit.getPlayer(UUID.fromString(uuid));
						if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
						
						if(p != null) {
							System.out.println("Player (ally): " + p.getName());
						
							Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
							float dist = (float) p.getLocation().distance(loc);
							
							this.allies.add(new Pair(dist, getAllyState(uuid)));
						}
					}
					
					
					
				}
			}
		}
	}
	
	public static List<Material> getTransparentBlocks() {
		return transparentBlocks;
	}
	
	public Object getAllyState(String allieUUID) {
		String gamemode = Main.getPlugin(Main.class).getConfig().getString("game");
		
		switch(gamemode) {
		case "lobby":
			return null;
		case "meltdown":
			return Meltdown.getRawPlayerDatas(allieUUID).get(1); // Is allie frozen
		case "nexus":
			return null;
		}
		
		return null;
	}
	
	public void updateGamesDatas() {
		String gamemode = Main.getPlugin(Main.class).getConfig().getString("game");
		String uuid = this.player.getUniqueIDString();
		
		switch(gamemode) {
		case "lobby":
			return;
		case "meltdown":
			this.datas = new float[4];
			int hasPickaxe = Meltdown.getRawPlayerDatas(uuid).get(11);
			int cooldown = Meltdown.getRawPlayerDatas(uuid).get(13);
			if(hasPickaxe==1) {
				this.datas[0] = 1;
			} else if(hasPickaxe==0 && cooldown==0) {
				this.datas[0] = 0;
			}else {
				this.datas[0] = 0.5f;
			}
			int heaterX = Meltdown.getRawPlayerDatas(uuid).get(8);
			int heaterY = Meltdown.getRawPlayerDatas(uuid).get(9);
			int heaterZ = Meltdown.getRawPlayerDatas(uuid).get(10);
			int heaterCD = Meltdown.getRawPlayerDatas(uuid).get(12);
			if(heaterX==0) { // Pas placé
				if(heaterCD == 0) { //pas de cooldown
					this.datas[1] = 1;
				} else { // pas placé mais cooldown
					this.datas[1] = 0.5f;
				}
				
				//Si pas placé distance du heater infinie
				this.datas[2] = Float.MAX_VALUE;
				
			} else {
				this.datas[1] = 0;
				Location pLoc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
				Location hLoc = new Location(world, heaterX, heaterY, heaterZ);
				this.datas[2] = (float) pLoc.distance(hLoc);
			}
			
			this.datas[3] = nearestArrowDistance();
			
			return;
		case "nexus":
			return;
		}
	}
	
	private float nearestArrowDistance() {
		
		float distance = Float.MAX_VALUE;
		Location pLoc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
		
		for(Entity e : world.getEntities()) {
			if(e instanceof Arrow) {
				if(e.getVelocity().length() == 0) continue;
				if(pLoc.distance(e.getLocation()) < distance) {
					distance = (float) pLoc.distance(e.getLocation());
				}
			}
		}
		return distance;
	}
}
