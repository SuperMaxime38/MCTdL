package mctdl.game.games.meltdown;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class MeltdownFiles {
	
	
	public static FileConfiguration checkMap(Main main) {
		String map = main.getConfig().getString("games.meltdown.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			f = new File(userdata, File.separator + "meltdown.yml");
		}
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);
	    return datas;
	}
	
	public static List<Integer> getRoomCoords(Main main, String which) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> coords = new ArrayList<>();
	    switch(which) {
	    case "A":
	    	coords = datas.getIntegerList("coords.room.A");
	    	return coords;
	    case "B":
	    	coords = datas.getIntegerList("coords.room.B");
	    	return coords;
	    case "C":
	    	coords = datas.getIntegerList("coords.room.C");
	    	return coords;
	    case "D":
	    	coords = datas.getIntegerList("coords.room.D");
	    	return coords;
	    case "E":
	    	coords = datas.getIntegerList("coords.room.E");
	    	return coords;
	    case "M":
	    	coords = datas.getIntegerList("coords.room.M");
	    	return coords;
	    }
	    return coords;
	}
	
	public static List<Integer> getRoomTimes(Main main) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> times = new ArrayList<>();
	    times.add(datas.getInt("times.room.A"));
	    times.add(datas.getInt("times.room.B"));
	    times.add(datas.getInt("times.room.C"));
	    times.add(datas.getInt("times.room.D"));
	    times.add(datas.getInt("times.room.E"));
	    times.add(datas.getInt("times.room.M"));
	    return times;
	}
	
	public static List<Integer> getDoorCoords(Main main, Integer which) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> coords = new ArrayList<>();
	    
	    switch(which) {
	    case 0:
	    	coords = datas.getIntegerList("coords.door.0");
	    	return coords;
	    case 1:
	    	coords = datas.getIntegerList("coords.door.1");
	    	return coords;
	    case 2:
	    	coords = datas.getIntegerList("coords.door.2");
	    	return coords;
	    case 3:
	    	coords = datas.getIntegerList("coords.door.3");
	    	return coords;
	    case 4:
	    	coords = datas.getIntegerList("coords.door.4");
	    	return coords;
	    case 5:
	    	coords = datas.getIntegerList("coords.door.5");
	    	return coords;
	    default:
	    	return null;
	    }
	}
	
	public static List<Integer> getDoorTimes(Main main) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> times = new ArrayList<>();
	    times.add(datas.getInt("times.door.1"));
	    times.add(datas.getInt("times.door.2"));
	    times.add(datas.getInt("times.door.3"));
	    times.add(datas.getInt("times.door.4"));
	    times.add(datas.getInt("times.door.5"));
	    return times;
	}
	
	public static List<Integer> getSpawnCoords(Main main, String team) {
		FileConfiguration datas = checkMap(main);
		List<Integer> coords = datas.getIntegerList("spawn." + team);
		return coords;
		
	}
	
	public static Integer getFallHeight(Main main) {
		FileConfiguration datas = checkMap(main);
		return datas.getInt("fall_height");
	}
	
	
	public static boolean isMapGenerated(Main main) {
		FileConfiguration datas = checkMap(main);
		if(datas.getBoolean("isMapGenerated")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setMapGenerated(Main main, boolean isGenerated) {
		FileConfiguration datas = checkMap(main);
		datas.set("isMapGenerated", isGenerated);
		saveDatas(datas, main);
	}
	
	public static void saveDatas(FileConfiguration datas, Main main) {
		String map = main.getConfig().getString("games.meltdown.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			f = new File(userdata, File.separator + "meltdown.yml");
		}
		try {
			datas.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
