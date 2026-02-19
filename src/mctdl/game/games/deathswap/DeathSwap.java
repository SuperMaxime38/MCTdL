package mctdl.game.games.deathswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;

public class DeathSwap implements Listener {
	
	static Main main;
	static boolean enable = false;
	static boolean isGenerated;
	static int delay;
	static String worldname;
	static HashMap<String, String> players; // UUID : team
	
	static HashMap<String, List<Integer>> playerdata = new HashMap<String, List<Integer>>();
	
	static HashMap<UUID, UUID> matching = new HashMap<UUID, UUID>();
	
	static String abandonne = "";
	
	
	public DeathSwap(Main main){
		DeathSwap.main = main;
		
		isGenerated = DeathSwap.main.getConfig().getBoolean("games.deathswap.generated");
		worldname = DeathSwap.main.getConfig().getString("games.deathswap.map");
		delay = DeathSwap.main.getConfig().getInt("games.deathswap.delay");
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
//		if(TeamsManager.getOnlinePlayers().keySet().size() % 2 != 0) {
//			Random random = new Random();
//			int rint = random.nextInt(TeamsManager.getOnlinePlayers().keySet().size());
//			int counter = 0;
//			for (String uuid : players.keySet()) {
//				if(counter == rint) {
//					Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage("§cIl y a un nombre impair de joueurs... vous §tes le volontaire pour ne pas participer !");
//					
//					abandonne = uuid;
//					break;
//				}
//				counter++;
//			}
//		}
		
		matchmaking();
		
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
		for (String uuid : players.keySet()) {
			if(abandonne == uuid) {
				datas.set(0, 0);
			}
			//Add players to playerdata
			playerdata.put(uuid, datas);
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + TeamsManager.getPseudo(uuid) + " " + worldname);
			//Teleport script to make them distant one from another
		}
		
		main.getConfig().set("game", "deathswap");
		Main.game = "deathswap";
	}
	
	public static void matchmaking() {
		List<String> non_sorted_teams = TeamsManager.getOnlineTeams();
		HashMap<String, String> non_sorted_players = TeamsManager.getOnlinePlayers();
		

		int teamIndex = 0;
		
		while(non_sorted_players.size() > 1) {
			
//			List<String> teams_sorted = new ArrayList<String>();
			
			String currentTeam = non_sorted_teams.get(teamIndex);
			String player1 = "not_found";
			for(String player : non_sorted_players.keySet()) { // Pour chaque joueur pas encore plac§
				if(non_sorted_players.get(player).equals(currentTeam)) { // S'il appartient § la team
					player1 = player;
				}
			}
			non_sorted_players.remove(player1);
			if(!non_sorted_players.values().contains(currentTeam) || player1 == "not_found") {

				non_sorted_teams.remove(currentTeam);
				teamIndex--;
			}
			
			if(non_sorted_teams.size() == teamIndex+1) {
				teamIndex=0;
			} else {
				teamIndex++;
			}
			
			currentTeam = non_sorted_teams.get(teamIndex);
			
			String player2 = "not_found_2";
			for(String player : non_sorted_players.keySet()) { // Pour chaque joueur pas encore plac§
				if(non_sorted_players.get(player).equals(currentTeam)) { // S'il appartient § la team
					player2 = player;
				}
			}
			non_sorted_players.remove(player2);
			if(!non_sorted_players.values().contains(currentTeam) || player2 == "not_found_2") {

				non_sorted_teams.remove(currentTeam);
				teamIndex--;
			}
			
			matching.put(UUID.fromString(player1), UUID.fromString(player2));
			matching.put(UUID.fromString(player2), UUID.fromString(player1));
			
			System.out.println("Matched " + TeamsManager.getPseudo(player1) + " with " + TeamsManager.getPseudo(player2));
			
			if(non_sorted_teams.size() == teamIndex+1) {
				teamIndex=0;
			} else {
				teamIndex++;
			}
			
			
//			for(int i = 0; i < non_sorted_teams.size(); i++) { // Pour chaque team dont il reste des membres § placer
//				String player1 = "not_found";
//				for(String player : non_sorted_players.keySet()) { // Pour chaque joueur pas encore plac§
//					if(non_sorted_players.get(player).equals(non_sorted_teams.get(i))) { // S'il appartient § la team
//						player1 = player;
//					}
//				}
//
//				non_sorted_players.remove(player1);
//				if(!non_sorted_players.values().contains(non_sorted_teams.get(i)) || player1 == "not_found") {
//					teams_sorted.add(non_sorted_teams.get(i));
//				}
//				
//				
//				if(non_sorted_teams.size() == i+1) {
//					i=0;
//				} else {
//					i++;
//				}
//				
//				String player2 = "not_found_2";
//				for(String player : non_sorted_players.keySet()) { // Pour chaque joueur pas encore plac§
//					if(non_sorted_players.get(player).equals(non_sorted_teams.get(i))) { // S'il appartient § la team
//						player2 = player;
//					}
//				}
//
//				non_sorted_players.remove(player2);
//				if(!non_sorted_players.values().contains(non_sorted_teams.get(i)) || player2 == "not_found_2") {
//					teams_sorted.add(non_sorted_teams.get(i));
//				}
//				
//				System.out.println("Player 1 : " + player1 + " Player 2 : " + player2);
//				
//				if(player1 == "not_found") {
//					volontary_exil(player2);
//					continue;
//					
//					
//				} else if(player2 == "not_found_2") {
//					volontary_exil(player1);
//					continue;
//				}
//				
//				matching.put(UUID.fromString(player1), UUID.fromString(player2));
//				matching.put(UUID.fromString(player2), UUID.fromString(player1));
//				
//				System.out.println("Matched " + TeamsManager.getPseudo(player1) + " with " + TeamsManager.getPseudo(player2));
//				
//			}
//			
//			non_sorted_teams.removeAll(teams_sorted);
		}
		
		if(non_sorted_players.size() == 1) {
			String uuid = non_sorted_players.keySet().iterator().next();
			volontary_exil(uuid);
		}
		
		System.out.println("Matching complete");
		
	}
	
	public static void volontary_exil(String uuid) {
		abandonne = uuid;
		
		System.out.println("Cannot match " + TeamsManager.getPseudo(uuid) + " with anyone");

		if(NPCManager.isAnNPC(uuid)) return;
		Player p = Bukkit.getPlayer(UUID.fromString(uuid));
		p.sendMessage("§cIl y a un nombre impair de joueurs... vous §tes le volontaire pour ne pas participer !");

	}
	
	public static void disable() {
		for (String uuid : TeamsManager.getOnlinePlayers().keySet()) {
			
			if(NPCManager.isAnNPC(uuid)) {
				continue;
			}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + TeamsManager.getPseudo(uuid) + " " + Bukkit.getWorlds().get(0).getName());
			Player p = Bukkit.getPlayer(UUID.fromString(uuid));
			p.setGameMode(GameMode.ADVENTURE);
			p.setInvisible(false);

			p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
			
			p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			p.setFoodLevel(20);
			
			p.getInventory().clear();
			
			PlayerData.registerPlayer(p);
		}

		main.getConfig().set("game", "lobby");
		Main.game = "lobby";
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
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv regen " + worldname); //Si le monde existe mais qu'il a d§ja §t§ utilis§
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
	
	public static void randomTeleport(Player p) {
		Location loc = roll();
		Material mat = loc.getBlock().getType();
		for(int i = 0; i < 8; i++) {
			switch(mat) {
			default:
				loc.add(0, 32, 0);
				p.setInvulnerable(true);
				p.teleport(loc);
				System.out.println("[DeathSwap - RTP] > Succesfuly teleported " + p.getName() + " to " + loc);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						p.setInvulnerable(false);
					}
				}.runTaskLater(main, 200);
				return;
			case WATER:
				if(i == 7) {
					loc.add(0, 32, 0);
					p.setInvulnerable(true);
					p.teleport(loc);
					System.out.println("[DeathSwap - RTP] > Couldnt find a 'dry' location...  " + p.getName() + " is above water at " + loc);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							p.setInvulnerable(false);
						}
					}.runTaskLater(main, 200);
					return;
				}
				loc = roll();
				mat = loc.getBlock().getType();
				System.out.println("[DeathSwap - RTP] > The location found was in the water... Rerolling RTP for " + p.getName() + " coords : " + loc);
				break;
			case LAVA:
				if(i == 7) {
					loc.add(0, 32, 0);
					p.setInvulnerable(true);
					p.teleport(loc);
					System.out.println("[DeathSwap - RTP] > Found 7 times a lava source as location...  " + p.getName() + " will probably burn at " + loc);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							p.setInvulnerable(false);
						}
					}.runTaskLater(main, 200);
					return;
				}
				loc = roll();
				mat = loc.getBlock().getType();
				System.out.println("[DeathSwap - RTP] > The location found was in the lava... Rerolling RTP for " + p.getName() + " coords : " + loc);
				break;
			}
		}
	}
	
	public static Location roll() {
		Random rdm = new Random();
		int X = rdm.nextInt(10000) - 5000; //permet de tp entre -5000 et 5000
		
		rdm = new Random();
		int Z = rdm.nextInt(10000) - 5000;
		
		int Y = Bukkit.getWorld("world").getHighestBlockYAt(X, Z);
		
		Location loc = new Location(Bukkit.getWorld("world"), X, Y, Z);
		return loc;
	}
	
	public static boolean exceptions(Player p1, Player p2) {
		if(p1 == null) {
			System.out.println("[DeathSwap] > 'PLAYER1' isnt connected !");
			return false;
		}
		if(p2 == null) {
			System.out.println("[DeathSwap] > 'PLAYER2' isnt connected !");
			return false;
		}
		return true;
	}
	
	public static void timer() {
		
		new BukkitRunnable() {
			int left = delay;
			
			@Override
			public void run() {
				enable = main.getConfig().getBoolean("isActive");
				if(!enable) {
					this.cancel();
					return;
				}
				if(left <= 10) {
					Bukkit.broadcastMessage("§6[§4DeathSwap§6] §f> §4The next Swap is in §f: " + left + " seconds");
				}
				if(left == 0) {
					left = delay;
					
					//ToDo : TP all players
					
				}
				left--;
			}
		}.runTaskTimer(main, 0, 20);
	}
}
