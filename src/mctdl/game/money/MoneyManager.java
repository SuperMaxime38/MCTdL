package mctdl.game.money;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class MoneyManager {
static HashMap<String, Integer> balances = new HashMap<String, Integer>();
static HashMap<String, Integer> lifetime = new HashMap<String, Integer>();
static HashMap<String, Integer> poutres_owners = new HashMap<String, Integer>();
	
	public static boolean fileCheck(Main main){
    	
	     File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "money");
	     File f = new File(userdata, File.separator + "balances.yml");
	     FileConfiguration preset = YamlConfiguration.loadConfiguration(f);

	     
	     if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	         try {
	        	 
	        	 preset.createSection("balances");
	        	 preset.createSection("lifetime");
	        	 preset.createSection("poutres");
	        	 
	             preset.save(f);
	             
	         } catch (IOException exception) {

	             exception.printStackTrace();
	         }
	         return false;
	     } else {
	    	 return true;
	     }
     }
	 
	public static HashMap<String, Integer> getRegsPlayer() {return balances;}
	public static HashMap<String, Integer> getLifeTimeCoins() {return lifetime;}
	public static HashMap<String, Integer> getWhoHasPoutres() {return poutres_owners;} //POUTRE
	
	public static void setPlayerMoney(String uuid, int amount) {balances.put(uuid, amount);}
	public static void setPlayerLifeTimeCoins(String uuid, int amount) {lifetime.put(uuid, amount);}
	public static void setPlayerPoutres(String uuid, int amount) {poutres_owners.put(uuid, amount);} //POUTRE
	
	public static void removePlayerMoney(String uuid, int amount) {
		if(balances.containsKey(uuid)) {
			int money = balances.get(uuid);
			money = money - amount;
			balances.put(uuid, money);
		}
	}
	public static void removePlayerPoutre(String uuid, int amount) { //POUTRE
		if(poutres_owners.containsKey(uuid)) {
			int money = poutres_owners.get(uuid);
			money = money - amount;
			poutres_owners.put(uuid, money);
		}
	}
	
	public static void addPlayerMoney(String uuid, int amount) {
		if(balances.containsKey(uuid)) {
			int money = balances.get(uuid);
			money = money + amount;
			balances.put(uuid, money);
		}
	}
	public static void addPlayerLifeTimeCoins(String uuid, int amount) {
		if(lifetime.containsKey(uuid)) {
			int money = lifetime.get(uuid);
			money = money + amount;
			lifetime.put(uuid, money);
		}
	}
	public static void addPlayerPoutre(String uuid, int amount) { //POUTRE
		if(poutres_owners.containsKey(uuid)) {
			int money = poutres_owners.get(uuid);
			money = money + amount;
			poutres_owners.put(uuid, money);
		}
	}
	
	public static void loadHashMap(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "money");
	    File f = new File(userdata, File.separator + "balances.yml");
	    if(!f.exists()) fileCheck(main);
	    FileConfiguration config = YamlConfiguration.loadConfiguration(f);
	    
	    balances.clear();
	    lifetime.clear();
	    poutres_owners.clear();
	    for (String uuid : config.getConfigurationSection("balances").getKeys(false)) {
			balances.put(uuid, config.getInt("balances." + uuid));
		}
	    for (String uuid : config.getConfigurationSection("lifetime").getKeys(false)) {
			lifetime.put(uuid, config.getInt("lifetime." + uuid));
		}
	    for (String uuid : config.getConfigurationSection("poutres").getKeys(false)) { //Poutre
			poutres_owners.put(uuid, config.getInt("poutres." + uuid));
		}
	}
	
	public static void updateConfig(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "money"); //A FAIRE
	    File f = new File(userdata, File.separator + "balances.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	    
	    yaml.set("balances", null);
	    for (String uuid : balances.keySet()) {
			yaml.set("balances." + uuid, balances.get(uuid));
		}

	    yaml.set("lifetime", null);
	    for (String uuid : lifetime.keySet()) {
			yaml.set("lifetime." + uuid, lifetime.get(uuid));
		}
	    
	    yaml.set("poutres", null); //POUTRE
	    for (String uuid : poutres_owners.keySet()) {
			yaml.set("poutres." + uuid, poutres_owners.get(uuid));
		}
	    
	    try {
			yaml.save(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Integer getPlayerMoney(String uuid) {
		Integer money = balances.get(uuid);
		if(money == null) return 0;
		return money;
	}
	public static Integer getPlayerLifeTimeCoins(String uuid) {
		Integer money = lifetime.get(uuid);
		if(money == null) return 0;
		return money;
	}
	public static Integer getPlayerPoutres(String uuid) { //POUTRE
		Integer money = poutres_owners.get(uuid);
		if(money == null) return 0;
		return money;
	}
}
