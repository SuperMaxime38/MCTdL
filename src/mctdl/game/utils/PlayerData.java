package mctdl.game.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mctdl.game.Main;
import mctdl.game.dev.ItemGiver;
import mctdl.game.dev.ItemGiver.CustomItem;

public class PlayerData {
	
	static HashMap<String, HashMap<Integer, String>> items = new HashMap<String, HashMap<Integer, String>>();
	static HashMap<String, HashMap<String, String>> stats = new HashMap<String, HashMap<String, String>>();
	static HashMap<UUID, HashMap<Object, Object>> other = new HashMap<UUID, HashMap<Object, Object>>(); // Reset on reload / restart
	
	static HashMap<UUID, ItemStack> chestplates = new HashMap<UUID, ItemStack>();
	
	public static void loadHashMap(Main main) {
	    
		File f = FileLoader.loadFile("lobby.yml", "playerdata/");
	    
	    FileConfiguration config = YamlConfiguration.loadConfiguration(f);
	    
	    if(config.getConfigurationSection("players").getKeys(false).isEmpty()) {
	    	System.out.println("[§6MCTdL §cWARN§f] > §cPLAYUERDATA HASHMAP DIDNT LOAD --> Empty config\n"
	    			+ "THIS MAY CAUSE BUGS AND THROW ERRORS");
	    }
	    
	    //Items
	    items.clear();
	    for(String uuid : config.getConfigurationSection("players").getKeys(false)) {
	    	HashMap<Integer, String> playerdata = new HashMap<Integer, String>();
	    	for(String index : config.getConfigurationSection("players." + uuid).getKeys(false)) {
	    		playerdata.put(Integer.parseInt(index), (String) config.get("players."+ uuid + "."+ index));
	    	}
	    	items.put(uuid, playerdata);
	    }
	    
	    //Statistics
	    stats.clear();
	    for(String uuid : config.getConfigurationSection("stats").getKeys(false)) {
	    	HashMap<String, String> playerdata = new HashMap<String, String>();
	    	for(String index : config.getConfigurationSection("stats." + uuid).getKeys(false)) {
	    		playerdata.put(index, (String) config.get("stats."+ uuid + "."+ index));
	    	}
	    	stats.put(uuid, playerdata);
	    }
	}
	
	public static HashMap<String, HashMap<Integer, String>> getPlayersData() {
		return items;
	}
	
	public static HashMap<String, HashMap<String, String>> getPlayersStats() {
		return stats;
	}
	
	public static HashMap<UUID, HashMap<Object, Object>> getOtherData() {
		return other;
	}
	
	public static void updateConfig(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "playerdata"); //A FAIRE
	    File f = new File(userdata, File.separator + "lobby.yml");
	    
	    //fileCheck(main);
	    FileLoader.loadFile("lobby.yml", "playerdata/");
	    
	    FileConfiguration data = YamlConfiguration.loadConfiguration(f);
	    
	    data.set("players", null);
	    data.createSection("players");
	    for (String uuid : items.keySet()) {
			HashMap<Integer, String> playerdata = items.get(uuid);
			data.set("players." + uuid, playerdata);
		}

	    data.set("stats", null);
	    data.createSection("stats");
	    for (String uuid : stats.keySet()) {
			HashMap<String, String> playerdata = stats.get(uuid);
			data.set("stats." + uuid, playerdata);
		}
	    
	    try {
			data.save(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateStats(String playerUUID, HashMap<String, String> statistics) {
		stats.put(playerUUID, statistics);
	}
	
	public static void registerPlayer(Player p) { //Quand joueur se connecte ou sors d'un minijeu (charge ses cosmétiques)
		String uuid = p.getUniqueId().toString();
		String type = "";
		if(!getPlayersData().containsKey(uuid)) {
			HashMap<Integer, String> empty = new HashMap<Integer, String>();
			empty.put(0, "welcome*1");
			items.put(uuid, empty);
			
			HashMap<String, String> empty2 = new HashMap<String, String>();
			stats.put(uuid, empty2);
			
			HashMap<Object, Object> empty3 = new HashMap<Object, Object>();
			other.put(p.getUniqueId(), empty3);
			
			registerPlayer(p);
		} else {
			HashMap<Integer, String> cosmetics = getPlayersData().get(uuid);
			ItemStack item;
			int amount = 1;
			String[] spliter;
			for(int i : cosmetics.keySet()) {
				String id = cosmetics.get(i);
				spliter = id.split("\\*");
				id = spliter[0];
				if(spliter[1] != null) amount = Integer.parseInt(spliter[1]);
				if(Material.getMaterial(id) != null) {
					item = new ItemStack(Material.getMaterial(id));
				} else {
					item = ItemGiver.getItem(CustomItem.valueOf(id.toUpperCase()));
				}
				
				item.setAmount(amount);
				
				if(type == "") {
					p.getInventory().setItem(i, item);
				} else {
					if(type == "helmet") p.getInventory().setHelmet(item);
					if(type == "chestplate") p.getInventory().setChestplate(item);
					if(type == "leggings") p.getInventory().setLeggings(item);
					if(type == "boots") p.getInventory().setBoots(item);
				}
				type = "";
				
				if(item.isSimilar(welcome())) {
					Location target = new Location(Bukkit.getWorlds().get(0), 8, 8, -81);
					p.setCompassTarget(target);
				}
			}
		}
	}
	
	/**
	 * FOR NPC ONLY
	 * @param id
	 * @return
	 */
	public static ItemStack getItem(String id) { //POUR LES NPCs !!!!!
		int amount = 1;
		ItemStack item;
		String[] spliter;
		spliter = id.split("\\*");
		id = spliter[0];
		if(spliter[1] != null) amount = Integer.parseInt(spliter[1]);
		if(Material.getMaterial(id) != null) {
			item = new ItemStack(Material.getMaterial(id));
		} else {
			item = ItemGiver.getItem(CustomItem.valueOf(id));
		}

		item.setAmount(amount);
		
		return item;
	}
	
	public static void saveItems(Player p) {
		//Datas
		String uuid = p.getUniqueId().toString();
		HashMap<Integer, String> playerinv = new HashMap<Integer, String>();
		
		//Items
		String id;
		int amount = 1;
		int slot;
		
		Inventory inv = p.getInventory();
		
		for (ItemStack item : inv.getContents()) {
			if(item != null) {
				amount = item.getAmount();
				slot = inv.first(item);
				
				
				if(NBTAPI.hasNBT(item, "mctdlID")) { // C'est un item custom
					id = NBTAPI.getNBT(item, "mctdlID");
				} else {
					id = item.getType().toString();
				}
				
				id += "*" + amount;
				
				//Save in player hashmap
				
				if(slot == -1) {
					System.out.println("somehow this item is in slot -1 ._. : " + item);
					continue;
				} 
				
				playerinv.put(slot, id);
			}
		}
		
		
		//Save in playersdata
		items.put(uuid, playerinv);
	}
	
	public static void addItem(Player p, int slot, String item) {
		//A FAIRE
	}
	
	/**
	 * Used in Move.java for double jump chestplate change
	 * @param uuid
	 * @param item
	 */
	public static void rememberChestplate(UUID uuid, ItemStack item) {
		chestplates.put(uuid, item);
	}
	
	/**
	 * Used in Move.java for double jump thingy
	 * 
	 * @param uuid
	 */
	public static ItemStack getPlayerChestplate(UUID uuid) {
		ItemStack item = chestplates.get(uuid);
		
		return item;
	}
	
	public static void setInventory(Player p, HashMap<Integer, ItemStack> items) {
		for(int slot : items.keySet()) {
			p.getInventory().setItem(slot, items.get(slot));
		}
	}
	
	public static HashMap<Object, Object> getOtherData(UUID uuid) {
		return other.get(uuid);
	}
	
	public static void setOtherData(UUID uuid, HashMap<Object, Object> data) {
		other.put(uuid, data);
	}
	
	public static ItemStack helicoHat() {
		ItemStack item = new ItemStack(Material.WHITE_CARPET);
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setDisplayName("§dHelico Hat");
		meta.setLore(Arrays.asList("§5:)"));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack welcome() {
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Welcome");
		meta.setLore(Arrays.asList("§7Bienvenue dans le §6§lTournoi des Légendes", "§aQue le meilleur gagne"));
		item.setItemMeta(meta);
		
		NBTAPI.addNBT(item, "mctdlID", "WELCOME");
		
		return item;
	}
	public static ItemStack supporter(String team) {
		ItemStack item;
		ItemMeta meta;
		switch(team) {
		case "red":
			item = new ItemStack(Material.RED_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§4Gant Supporter Red Rocket");
			meta.setLore(Arrays.asList("§cAffiche ton soutiens § l'équipe §4Red Rocket"));
			item.setItemMeta(meta);
			break;
		case "blue":
			item = new ItemStack(Material.BLUE_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§1Gant Supporter Red Rocket");
			meta.setLore(Arrays.asList("§9Affiche ton soutiens § l'équipe §1Blue Whale"));
			item.setItemMeta(meta);
			break;
		case "green":
			item = new ItemStack(Material.GREEN_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§2Gant Supporter Green Turtle");
			meta.setLore(Arrays.asList("§aAffiche ton soutiens § l'équipe §2Green Turtle"));
			item.setItemMeta(meta);
			break;
		case "yellow":
			item = new ItemStack(Material.YELLOW_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§eGant Supporter Yellow Stone");
			meta.setLore(Arrays.asList("§eAffiche ton soutiens § l'équipe Yellow Stone"));
			item.setItemMeta(meta);
			break;
		case "purple":
			item = new ItemStack(Material.PURPLE_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§5Gant Supporter Purple Amethyst");
			meta.setLore(Arrays.asList("§dAffiche ton soutiens § l'équipe §5Purple Amethyst"));
			break;
		case "aqua":
			item = new ItemStack(Material.LIGHT_BLUE_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§bGant Supporter Aqua Dolphin");
			meta.setLore(Arrays.asList("§bAffiche ton soutiens § l'équipe Aqua Dolphin"));
			item.setItemMeta(meta);
			break;
		case "orange":
			item = new ItemStack(Material.ORANGE_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§6Gant Supporter Orange Mechanic");
			meta.setLore(Arrays.asList("§6Affiche ton soutiens § l'équipe Orange Mechanic"));
			item.setItemMeta(meta);
			break;
		case "black":
			item = new ItemStack(Material.BLACK_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§0Gant Supporter Black Raven");
			meta.setLore(Arrays.asList("§8Affiche ton soutiens § l'équipe §0Black Raven"));
			item.setItemMeta(meta);
			break;
		default:
			item = new ItemStack(Material.WHITE_WOOL);
			break;
		}
		
		return item;
	}
	
	public static void deleteFromExistence(String uuid) {
		items.remove(uuid);
		stats.remove(uuid);
		other.remove(UUID.fromString(uuid));
	}
}
