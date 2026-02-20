package mctdl.game.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class LobbyData {
	
	static HashMap<String, List<String>> canon = new HashMap<>();
	
	
	public static void updateConfig(Main main) {
	    
	    File f =  FileLoader.loadFile("datas.yml", "lobbydata/"); // new way to create new file if not exist
	    
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
		File f = FileLoader.loadFile("datas.yml", "lobbydata/");
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
