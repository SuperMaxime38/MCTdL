package mctdl.game.games.hungergames;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.commands.BaltopCommand;
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
	    
	    List<String> teams = BaltopCommand.getTeamClassement();
	    
	    for(String placement : config.getConfigurationSection("spawns").getKeys(false)) {
	    	Location loc = new Location(Bukkit.getWorld(world), config.getInt("spawns." + placement + ".X"), config.getInt("spawns." + placement + ".Y"), config.getInt("spawns." + placement + ".Z"));
	    	spawns.put(teams.get(Integer.parseInt(placement) - 1), loc);
	    }
	}
	
	public int getMinBorderSize() {
		return minBorderSize;
	}
	
	public int getMaxBorderSize() {
		return maxBorderSize;
	}
	
}
