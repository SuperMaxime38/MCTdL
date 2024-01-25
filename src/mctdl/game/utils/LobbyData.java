package mctdl.game.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class LobbyData {
	
	static HashMap<String, List<String>> canon = new HashMap<>();
	
	public static File fileCheck(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "lobbydata");
		File f = new File(userdata, File.separator + "datas.yml");
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);

	     
	    if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	        try {
	        	datas.createSection("canon");
	        	datas.save(f);
	        } catch (IOException exception) {
	            exception.printStackTrace();
	        }
	        return f;
	    } else {
	    	return f;
	    }
	}
	
	public static void updateConfig(Main main) {
		File f = fileCheck(main);
		FileConfiguration datas = YamlConfiguration.loadConfiguration(f);
		for(String id : canon.keySet()) {
			datas.set("canon." + id, canon.get(id));
		}
		
		try {
			datas.save(f);
		} catch (IOException exception) {
            exception.printStackTrace();
        }
	}
	
	public static void loadDatas(Main main) {
		File f = fileCheck(main);
		FileConfiguration datas = YamlConfiguration.loadConfiguration(f);
		if(datas.getConfigurationSection("canon") != null) {
			for(String sec : datas.getConfigurationSection("canon").getKeys(false)) {
				List<String> data = datas.getStringList("canon." + sec);
				canon.put(sec, data);
			}
		}
		
	}
	
	public static void setCanonDatas(HashMap<String, List<String>> data) {
		canon = data;
	}
	
	public static HashMap<String, List<String>> getCanonDatas() {
		return canon;
	}
}
