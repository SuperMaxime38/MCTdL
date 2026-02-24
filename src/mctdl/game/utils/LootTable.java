package mctdl.game.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class LootTable {
	
	private HashMap<ItemStack, Integer> table;
	private int weightSum;
	
	private static final Random rdm = new Random();

	public LootTable() {
		table = new HashMap<ItemStack, Integer>();
		
		// avoid null 
		table.put(new ItemStack(Material.AIR), 0);
		
		weightSum = 0;
	}
	
	
	public LootTable(String from) {
		table = new HashMap<ItemStack, Integer>();
		weightSum = 0;
		
		from(from);
	}
	
	public void from(String from) {
		File f = FileLoader.loadFile(from, "loot_tables/");

	    FileConfiguration config = YamlConfiguration.loadConfiguration(f);
	    
	    
	    int weight;
	    Material material;
	    String name;
	    List<String> lore;
	    
	    ItemStack result;
	    ItemMeta meta;
	    
	    for(String item : config.getConfigurationSection("loots").getKeys(false)) {
	    	weight = config.getInt("loots." + item + ".weight");
	    	material = Material.getMaterial(config.getString("loots." + item + ".material"));
	    	name = config.getString("loots." + item + ".name");
	    	if(config.contains("loots." + item + ".lore")) {
	    		lore = config.getStringList("loots." + item + ".lore");
	    	} else {
	    		lore = new ArrayList<String>();
	    	}
	    	
	    	result = new ItemStack(material);
	    	result.setAmount(config.getInt("loots." + item + ".amount"));
	    	
	    	meta = result.getItemMeta();
	    	
	    	meta.setDisplayName(name);
	    	meta.setLore(lore);
	    	
	    	result.setItemMeta(meta);

	    	
	    	if(config.contains("loots." + item + ".enchantments")) {
	    		addEnchantments(result, config);
	    	}
	    	
	    	table.put(result, weight);
	    	
	    	weightSum += weight;
	    }
	}
	
	/**
	 * Returns an item based on weight & randomness
	 */
	public ItemStack getItem() {
		int value = rdm.nextInt(weightSum);

		for(ItemStack item : table.keySet()) {
			value -= table.get(item);
			
			if(value <= 0) {
				return item;
			}
		}
		
		//
		return (ItemStack) table.values().toArray()[rdm.nextInt(table.size())];
	}
	
	private void addEnchantments(ItemStack item, FileConfiguration config) {
		if(item.getType() == Material.ENCHANTED_BOOK) {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();;
			
			for(String enchantment : config.getConfigurationSection("enchantments").getKeys(false)) {
				meta.addStoredEnchant(Enchantment.getByKey(NamespacedKey.fromString(enchantment)), config.getInt("enchantments." + enchantment), false);
			}
			
			item.setItemMeta(meta);
			
		} else {
			ItemMeta meta = item.getItemMeta();
			
			for(String enchantment : config.getConfigurationSection("enchantments").getKeys(false)) {
				meta.addEnchant(Enchantment.getByKey(NamespacedKey.fromString(enchantment)), config.getInt("enchantments." + enchantment), false);
			}
			
			item.setItemMeta(meta);
		}
	}
}
