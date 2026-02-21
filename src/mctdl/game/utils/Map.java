package mctdl.game.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;

public class Map {
	
	private String schem;
	private Location center;
	protected HashMap<String, Location> spawns;
	private List<Cuboid> doors;
	private int lowestPoint;
	private int radius;
	private String name = "MAP";
	private boolean isGenerated;
	
	public static Map current;
	
	public Map(String schem) {
		this.schem = schem;
		
		
		
		current = this;
		isGenerated = false;
		spawns = new HashMap<String, Location>();
	}
	
	public Map(String schem, Location center) {
		
		this.schem = schem;
		this.center = center;
		
		
		isGenerated = false;
		this.spawns = new HashMap<String, Location>();
		
		current = this;
	}
	/**
	 * Create a map "view" --> allows to build & destroy a gamemode map
	 * @param schem (the schem name no need for extension)
	 * @param center (apply point of schem)
	 * @param world
	 * @param spawns
	 * @param doors
	 * @param lowestPoint
	 * @param radius
	 * @param name
	 */
	public Map(String schem, Location center, HashMap<String, Location> spawns, List<Cuboid> doors, int lowestPoint, int radius, String name) {
		
		this.schem = schem;
		this.center = center;
		this.spawns = spawns;
		this.doors = doors;
		this.lowestPoint = lowestPoint;
		this.radius = radius;
		this.name = name;
		
		isGenerated = false;
		
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

		int X = center.getBlockX();
		int Y = center.getBlockY();
		int Z = center.getBlockZ();
		
		
		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(center.getWorld());
		File file = FileLoader.loadFile(schem + ".schem", "schematics/");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		
		try(ClipboardReader reader = format.getReader(new FileInputStream(file));
				EditSession editSession = WorldEdit.getInstance().newEditSession(world);
				) {
			
			Clipboard clipboard = reader.read();
			Operation pasteOperation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(BlockVector3.at(X, Y, Z))
					.build();
			
			Operations.complete(pasteOperation);
			
			System.out.println("[" + name+ "] > Schematic pasted");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteTerrain() {

		System.out.println("[" + name + "] > LAG WARNING : a lag is incoming (map deletion)");
		
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world " + world);
		
		int XC = center.getBlockX();
		int YC = center.getBlockY();
		int ZC = center.getBlockZ();
		
		int X1 = XC - radius;
		int Y1 = YC - radius;
		int Z1 = ZC - radius;
		int X2 = XC + radius;
		int Y2 = YC + radius;
		int Z2 = ZC + radius;
		
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + X1 + "," + Y1 + "," + Z1);
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + X2 + "," + Y2 + "," + Z2);
//		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/cut");
		
		
		Location loc = new Location(center.getWorld(), X1, Y1, Z1);
		
		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(loc.getWorld());
		
		try(EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
			
			editSession.setBlocks(new CuboidRegion(BlockVector3.at(X1, Y1, Z1), BlockVector3.at(X2, Y2, Z2)), BlockTypes.AIR.getDefaultState());
			
			Operations.complete(editSession.commit());

			System.out.println("[" + name+ "] > Deleted da Terrain");
			
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public World getWorld() {
		return center.getWorld();
	}
	
	public boolean isGenerated() {
		return isGenerated;
	}
	
	//Setters
	public void setSpawn(String team, Location spawn) {
		this.spawns.put(team, spawn);
	}
	
	public void setSpawns(HashMap<String, Location> spawns) {
		this.spawns = spawns;
	}
	
	public void setIsGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSchem(String schem) {
		this.schem = schem;
	}

	public void setCenter(Location center) {
		this.center = center;
	}

	public void setDoors(List<Cuboid> doors) {
		this.doors = doors;
	}

	public void setLowestPoint(int lowestPoint) {
		this.lowestPoint = lowestPoint;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}
	
	
}