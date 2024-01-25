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
	
	public static void setPlayerMoney(String name, int amount) {balances.put(name, amount);}
	public static void setPlayerLifeTimeCoins(String name, int amount) {lifetime.put(name, amount);}
	public static void setPlayerPoutres(String name, int amount) {poutres_owners.put(name, amount);} //POUTRE
	
	public static void removePlayerMoney(String name, int amount) {
		if(balances.containsKey(name)) {
			int money = balances.get(name);
			money = money - amount;
			balances.put(name, money);
		}
	}
	public static void removePlayerPoutre(String name, int amount) { //POUTRE
		if(poutres_owners.containsKey(name)) {
			int money = poutres_owners.get(name);
			money = money - amount;
			poutres_owners.put(name, money);
		}
	}
	
	public static void addPlayerMoney(String name, int amount) {
		if(balances.containsKey(name)) {
			int money = balances.get(name);
			money = money + amount;
			balances.put(name, money);
		}
	}
	public static void addPlayerLifeTimeCoins(String name, int amount) {
		if(lifetime.containsKey(name)) {
			int money = lifetime.get(name);
			money = money + amount;
			lifetime.put(name, money);
		}
	}
	public static void addPlayerPoutre(String name, int amount) { //POUTRE
		if(poutres_owners.containsKey(name)) {
			int money = poutres_owners.get(name);
			money = money + amount;
			poutres_owners.put(name, money);
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
	    for (String player : config.getConfigurationSection("balances").getKeys(false)) {
			balances.put(player, config.getInt("balances." + player));
		}
	    for (String player : config.getConfigurationSection("lifetime").getKeys(false)) {
			lifetime.put(player, config.getInt("lifetime." + player));
		}
	    for (String player : config.getConfigurationSection("poutres").getKeys(false)) { //Poutre
			poutres_owners.put(player, config.getInt("poutres." + player));
		}
	}
	
	public static void updateConfig(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "money"); //A FAIRE
	    File f = new File(userdata, File.separator + "balances.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	    
	    yaml.set("balances", null);
	    for (String player : balances.keySet()) {
			yaml.set("balances." + player, balances.get(player));
		}

	    yaml.set("lifetime", null);
	    for (String player : lifetime.keySet()) {
			yaml.set("lifetime." + player, lifetime.get(player));
		}
	    
	    yaml.set("poutres", null); //POUTRE
	    for (String player : poutres_owners.keySet()) {
			yaml.set("poutres." + player, poutres_owners.get(player));
		}
	    
	    try {
			yaml.save(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Integer getPlayerMoney(String name) {
		Integer money = balances.get(name);
		if(money == null) return 0;
		return money;
	}
	public static Integer getPlayerLifeTimeCoins(String name) {
		Integer money = lifetime.get(name);
		if(money == null) return 0;
		return money;
	}
	public static Integer getPlayerPoutres(String name) { //POUTRE
		Integer money = poutres_owners.get(name);
		if(money == null) return 0;
		return money;
	}
}
