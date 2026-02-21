package mctdl.game.games.hungergames;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.utils.FileLoader;
import mctdl.game.utils.Map;

public class HungerGamesMap extends Map{
	
	private final int minBorderSize, maxBorderSize;
	
	public HungerGamesMap(String schem) {
		super(schem);
		
		File f = FileLoader.loadFile(schem + ".yml", "maps/");
	    FileConfiguration config = YamlConfiguration.loadConfiguration(f);
	    
	    setIsGenerated(config.getBoolean("isGenerated"));
	    int X = config.getInt("schemX");
	    int Y = config.getInt("schemY");
	    int Z = config.getInt("schemZ");
	    String world = config.getString("world");
	    
	    Location center = new Location(Bukkit.getWorld(world), X, Y, Z);
	    setCenter(center);
	    
	    minBorderSize = config.getInt("border.min");
	    maxBorderSize = config.getInt("border.max");
	    
	    for(String team : config.getConfigurationSection("teams").getKeys(false)) {
	    	Location loc = new Location(Bukkit.getWorld(world), config.getInt("spawns." + team + ".X"), config.getInt("spawns." + team + ".Y"), config.getInt("spawns." + team + ".Z"));
	    	spawns.put(team, loc);
	    }
	}
	
	public int getMinBorderSize() {
		return minBorderSize;
	}
	
	public int getMaxBorderSize() {
		return maxBorderSize;
	}
	
}
