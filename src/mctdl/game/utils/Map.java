package mctdl.game.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;

public class Map {
	
	private Main main;
	
	private String schem;
	private Location center;
	private String world;
	private HashMap<String, Location> spawns;
	private List<Cuboid> doors;
	private int lowestPoint;
	private int radius;
	private String name = "MAP";
	
	public static Map current;
	
	public Map(Main main) {
		this.main = main;

		current = this;
	}
	
	public Map(Main main, String schem, Location center, String world) {
		this.main = main;
		
		this.schem = schem;
		this.center = center;
		this.world = world;
		
		this.spawns = new HashMap<String, Location>();
		
		current = this;
	}
	/**
	 * 
	 * @param main
	 * @param schem (the schem name no need for extension)
	 * @param center (apply point of schem)
	 * @param world
	 * @param spawns
	 * @param doors
	 * @param lowestPoint
	 * @param radius
	 * @param name
	 */
	public Map(Main main, String schem, Location center, String world, HashMap<String, Location> spawns, List<Cuboid> doors, int lowestPoint, int radius, String name) {
		this.main = main;
		
		this.schem = schem;
		this.center = center;
		this.world = world;
		this.spawns = spawns;
		this.doors = doors;
		this.lowestPoint = lowestPoint;
		this.radius = radius;
		this.name = name;
		
		this.spawns = new HashMap<String, Location>();
		

		current = this;
	}
	
	public static Map getCurrentMap() {
		return current;
	}
	
	public static void deleteCurrent() {
		current.deleteTerrain();
	}
	
	public void build(boolean withAir) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world " + world);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "schem load " + schem);

		int X = center.getBlockX();
		int Y = center.getBlockY();
		int Z = center.getBlockZ();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				System.out.println("[" + name +"] > Schemati loaded");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + X + "," + Y + "," + Z);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + X + "," + Y + "," + Z);
				if(withAir) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/paste");
				} else {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/paste -a");
				}
				System.out.println("[" + name+ "] > Schematic pasted");
			}
		}.runTaskLater(main, 40);
	}
	
	public void deleteTerrain() {

		System.out.println("[" + name+ "] > LAG WARNING : a lag is incoming (map deletion)");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world " + world);
		
		int XC = center.getBlockX();
		int YC = center.getBlockY();
		int ZC = center.getBlockZ();
		
		int X1 = XC - radius;
		int Y1 = YC - radius;
		int Z1 = ZC - radius;
		int X2 = XC + radius;
		int Y2 = YC + radius;
		int Z2 = ZC + radius;
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + X1 + "," + Y1 + "," + Z1);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + X2 + "," + Y2 + "," + Z2);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/cut");
		
		System.out.println("[" + name+ "] > Deleted da Terrain");
	}
	
	public void deleteDoors() {
		
	}
	
	
	//Getters
	
	public String getSchem() {
		return schem;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public String getWorldname() {
		return world;
	}
	
	public World getWorld() {
		return Bukkit.getWorld(world);
	}
	
	public HashMap<String, Location> getSpawns() {
		return spawns;
	}
	
	public List<Cuboid> getDoors() {
		return doors;
	}
	
	public int getLowestPoint() {
		return lowestPoint;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public String getMapName() {
		return name;
	}
	
	//Setters
	public void setSpawn(String team, Location spawn) {
		this.spawns.put(team, spawn);
	}
	
	public void setSpawns(HashMap<String, Location> spawns) {
		this.spawns = spawns;
	}
}