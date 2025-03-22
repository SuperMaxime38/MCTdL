package mctdl.game.games.deathswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;

public class DeathSwap {
	
	static Main main;
	static boolean enable = false;
	static boolean isGenerated;
	static String worldname;
	static HashMap<String, String> players;
	
	static HashMap<String, List<Integer>> playerdata = new HashMap<String, List<Integer>>();
	
	static String abandonne = "";
	
	
	public DeathSwap(Main main) {
		DeathSwap.main = main;
		
		isGenerated = DeathSwap.main.getConfig().getBoolean("games.deathswap.generated");
		worldname = DeathSwap.main.getConfig().getString("games.deathswap.map");
	}
	//List datas ----->
	/*
	 * Index 0: alive   ---> 1 = alive
	 * Index 1: rounds survived
	 * Index 2: gold
	 * Index 3: kills
	 * Index 4: damage done
	 * Index 5: blocks placed
	 * Index 6: blocks broke
	 */
	
	public static void enable() {
		if(!genWorld()) {
			System.out.println("§cDeathSwap cannot be enabled");
			return;
		}
		//players = tt les joueurs en team online
		players = TeamsManager.getOnlinePlayers();
		
		
		//Handle nb de joueurs impair
		if(TeamsManager.getOnlinePlayers().keySet().size() % 2 != 0) {
			Random random = new Random();
			int rint = random.nextInt(TeamsManager.getOnlinePlayers().keySet().size());
			int counter = 0;
			for (String pl : players.keySet()) {
				if(counter == rint) {
					Bukkit.getPlayer(pl).sendMessage("§cIl y a un nombre impair de joueurs... vous êtes le volontaire pour ne pas participer !");
					
					abandonne = pl;
					break;
				}
				counter++;
			}
		}
		//Datas init
		List<Integer> datas = new ArrayList<>();
		datas.add(1);
		datas.add(0);
		datas.add(0);
		datas.add(0);
		datas.add(0);
		datas.add(0);
		datas.add(0);
		
		//Teleport Players
		for (String player : players.keySet()) {
			if(abandonne == player) {
				datas.set(0, 0);
			}
			//Add players to playerdata
			playerdata.put(player, datas);
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + player + " " + worldname);
			//Teleport script to make them distant one from another
		}
	}
	
	public static void disable() {
		for (String player : TeamsManager.getOnlinePlayers().keySet()) {
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + player + " " + Bukkit.getWorlds().get(0).getName());
			Player p = Bukkit.getPlayer(player);
			p.setGameMode(GameMode.ADVENTURE);
			p.setInvisible(false);

			p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
			
			p.getInventory().clear();
			
			PlayerData.registerPlayer(p);
		}
	}
	
	
	/**
	 * Generated or regenerate the map
	 * @return if multiverse is not on the server or games.deathswap.map is null --> return false
	 */
	public static boolean genWorld() {
		
		if(main.getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
			System.out.println(ChatColor.RED + "[MCTdL Error] §f> §cThe plugin Multiverse Core cannot be find");
			return false;
		}
		
		if(worldname == null) return false;
		
		if(main.getServer().getWorld(worldname) == null) { //Si le monde n'existe pas
			
			Bukkit.broadcastMessage("§7Generating a brand new world for §cDeathSwap");
			Bukkit.broadcastMessage("§7CA VA LAGGER !");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create " + worldname + " normal");
			
		} else { //Si le monde existe
			
			if(!isGenerated) {

				Bukkit.broadcastMessage("§7Regenerating a world for §cDeathSwap");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv regen " + worldname); //Si le monde existe mais qu'il a déja été utilisé
			} else {
				//Start
			}
		}
		
		//Set the world as generated in config
		main.getConfig().set("games.deathswap.generated", true);
		main.saveConfig();
		
		return true;
	}
	
	public static List<Integer> getDataOf(String playername) {
		return playerdata.get(playername);
	}
}
