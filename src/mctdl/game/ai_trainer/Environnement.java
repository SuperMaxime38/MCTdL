package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
	
	private final int dividingCoef = 10; // plus les données sont variées plus le modèle galère, on va diviser les distances par ce nombre pour réduir cet effet
	
	
	float[] datas;
	
	float[] inputs;

	public static final List<Material> transparentBlocks = Arrays.asList(Material.AIR, Material.LADDER, Material.WATER);
	
	public Environnement(PlayerAI player) {
		this.player = player;
		this.world = player.getBukkitEntity().getWorld();
		
		this.view = new AiView(player.getBukkitEntity().getWorld(), Arrays.asList(new Vector(0,0.25,0), new Vector(0,0.75,0)), 8, player, dividingCoef);
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
		
		this.inputs = new float[size+1];
		//System.out.println("Inputs size: " + inputs.length + " | size: " + size);
		
		int i = 0;
		for(double rayDistance : rayDistances) {
			this.inputs[i] = (float) rayDistance;
			i++;
		}
		//System.out.println("index: " + i);
		
		for(double terrain : this.terrain) {
			this.inputs[i] = (float) terrain;
			i++;
		}
		//System.out.println("index: " + i);
		
		for(Pair pair : this.ennemies) {
			this.inputs[i] = (float) pair.getKey();
			this.inputs[i+1] = (float) pair.getValue();
			
			System.out.println(" dist | wall: " + pair.getKey() + " | " + pair.getValue());
			
			i += 2;
		}
		//System.out.println("index: " + i);
		
		for(Pair pair : this.allies) {
			this.inputs[i] = (float) pair.getKey();
			this.inputs[i+1] = (float) pair.getValue();
			i += 2;
		}
		//System.out.println("index: " + i);
		
		for(int j = 0; j < datas.length; j++) {
			//System.out.println("Index i|j: " + i + " | " + j);
			this.inputs[i] = datas[j];
			i++;
		}
		
		System.out.println(" inputs: " + Arrays.toString(inputs));
	}
	
	public void updatePlayersDistances() {
		this.ennemies.clear();
		this.allies.clear();
		for(String team : TeamsManager.getOnlineTeams()) {
			
			if(!TeamsManager.getPlayerTeam(this.player.getUniqueIDString()).equals(team)) { // On check pas sa team vu que la c'est les ennemies
				
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					Player p = Bukkit.getPlayer(UUID.fromString(uuid));
					if(p == null) {
						p = NPCManager.getNpcPlayerIfItIs(uuid);
					}
					
					if(p != null) {// Maybe an offline player
					
						if(Meltdown.getRawPlayerDatas(uuid).get(1) ==1 || Meltdown.getRawPlayerDatas(uuid).get(0) == 0) { // Frozen / dead
							this.ennemies.add(new Pair(Float.MAX_VALUE, 1f));
						} else {
							Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
							float dist = (float) p.getLocation().distance(loc) / dividingCoef;
							
							
							Vector start = loc.toVector();
							start = start.add(new Vector(0,0.85,0));
							Vector end = p.getLocation().toVector();
							end = end.add(new Vector(0,0.85,0));
							
							boolean isNotStraightLine = new Ray(world, start, end).checkTransparencyToDirection();
							float floatVersion = 0f;
							if(isNotStraightLine) floatVersion = 1f;
							
							this.ennemies.add(new Pair(dist, floatVersion));
						}
						
						
					}
					
					
				}
				
			} else { // pis là qu'on y est autant handle direct les allies
				for(String uuid : TeamsManager.getTeamMembers(team)) {
					if(!uuid.equals(this.player.getUniqueIDString())) {
						Player p = Bukkit.getPlayer(UUID.fromString(uuid));
						if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
						
						if(p != null) {
							
							Location loc = new Location(world, this.player.locX(), this.player.locY(), this.player.locZ());
							float dist = (float) p.getLocation().distance(loc) / dividingCoef;
							
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
			this.datas = new float[5];
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
			
			Location gold = findNearestBlock(new Location(world, this.player.getX(), this.player.getY(), this.player.getZ()), Material.GOLD_BLOCK, 32);
			if(gold == null) {
				this.datas[4] = Float.MAX_VALUE;
			} else {
				this.datas[4] = (float) gold.distance(new Location(world, this.player.getX(), this.player.getY(), this.player.getZ())) / dividingCoef;
			}
			
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
				if(e.getVelocity().length() == 0 || e.isOnGround()) continue;
				if(pLoc.distance(e.getLocation()) < distance) {
					distance = (float) pLoc.distance(e.getLocation());
				}
			}
		}
		return distance;
	}
	
	private Location findNearestBlock(Location loc, Material material, int maxRadius) {
	    int playerX = loc.getBlockX();
	    int playerY = loc.getBlockY();
	    int playerZ = loc.getBlockZ();
	    //System.out.println(playerX + " " + playerY + " " + playerZ);

	    Location nearest = null;
	    double nearestDistanceSq = Double.MAX_VALUE;

	    for (int radius = 1; radius <= maxRadius; radius++) {
	        for (int x = -radius; x <= radius; x++) {
	            for (int z = -radius; z <= radius; z++) {
	                // On ne parcourt que la "coquille" externe (bord du carré)
	                if (Math.abs(x) != radius && Math.abs(z) != radius) continue;
	                for (int y = -5; y <= 5; y++) { // limite verticale (-5/+5 autour du joueur)
	                    Block block = world.getBlockAt(playerX + x, playerY + y, playerZ + z);
	                    //System.out.println("Block: " + block.getType() + " at : " + (x+playerX) + ", " + (y+playerY) + ", " + (z+playerZ));
	                    if (block.getType().equals(material)) {
	                        double distanceSq = block.getLocation().distanceSquared(loc);
	                        if (distanceSq < nearestDistanceSq) {
	                            nearestDistanceSq = distanceSq;
	                            nearest = block.getLocation();
	                            //System.out.println("nearest block: " + nearest);
	                        }
	                    }
	                }
	            }
	        }
	        // Optionnel : tu peux arrêter dès le premier trouvé si tu veux juste "le plus proche immédiat"
	        if (nearest != null) {
	            break;
	        }
	    }

	    return nearest;
	}
}
