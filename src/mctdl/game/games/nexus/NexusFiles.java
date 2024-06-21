package mctdl.game.games.nexus;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class NexusFiles {
	public static boolean fileCheck(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + "nexus.yml");
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);

	     
	    if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	        try {
	        	datas.set("isMapGenerated", false);
	        	datas.set("map", "nexus");
	        	//set les spawns

	        	datas.set("respawn_cooldown", 5);
	        	datas.set("invulnerability", 3);
	        	datas.save(f);
	        }  catch (IOException exception) {

	            exception.printStackTrace();
	        }
	        return false;
	    } else {
	    	return true;
	    }
	}
	
	static FileConfiguration checkMap(Main main) {
		String map = main.getConfig().getString("games.nexus.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			fileCheck(main);
		}
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);
	    return datas;
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
	
	public static void getSpawns() {
		
	}
	
	public static void saveDatas(FileConfiguration datas, Main main) {
		String map = main.getConfig().getString("games.nexus.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			f = new File(userdata, File.separator + "nexus.yml");
		}
		try {
			datas.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
