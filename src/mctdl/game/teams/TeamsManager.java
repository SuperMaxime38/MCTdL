package mctdl.game.teams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;

public class TeamsManager{
	
	static HashMap<String, String> teams = new HashMap<String, String>();
	static HashMap<String, String> uuidToPseudo = new HashMap<String, String>();
	
	/**
	 * Check if the file teams.yml exists, if not, generated it and fill it with default values
	 * 
	 * @param Main instance
	 * @return true if the file exists, false if not
	 */
	public static boolean fileCheck(Main main){
    	
	     File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "teams");
	     File f = new File(userdata, File.separator + "teams.yml");
	     FileConfiguration preset = YamlConfiguration.loadConfiguration(f);

	     
	     if (!f.exists()) { //CREER LES SECTIONS AVEC DES LISTES VIDES SI FICHIER N'EXISTE PAS
	         try {
	        	 List<String> username = new ArrayList<>();
	        	 
	        	 preset.createSection("teams");
	        	 preset.set("teams.red", username);
	        	
	        	 username = new ArrayList<>();
	        	 preset.set("teams.blue", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.green", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.yellow", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.purple", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.aqua", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.black", username);
	        	 
	        	 username = new ArrayList<>();
	        	 preset.set("teams.orange", username);
	        	 
	        	 // Bind UUID x Pseudo
	        	 preset.createSection("pseudo");
	        	 
	             preset.save(f);
	             
	         } catch (IOException exception) {

	             exception.printStackTrace();
	         }
	         return false;
	     } else {
	    	 return false;
	     }
	     
     }
	
	public static void loadHashMap(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "teams");
	    File f = new File(userdata, File.separator + "teams.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
		for (String uuid : yaml.getStringList("teams.red")) {
			teams.put(uuid, "red");
		}
		for (String uuid : yaml.getStringList("teams.blue")) {
			teams.put(uuid, "blue");
		}
		for (String uuid : yaml.getStringList("teams.green")) {
			teams.put(uuid, "green");
		}
		for (String uuid : yaml.getStringList("teams.yellow")) {
			teams.put(uuid, "yellow");
		}
		for (String uuid : yaml.getStringList("teams.purple")) {
			teams.put(uuid, "purple");
		}
		for (String uuid : yaml.getStringList("teams.aqua")) {
			teams.put(uuid, "aqua");
		}
		for (String uuid : yaml.getStringList("teams.black")) {
			teams.put(uuid, "black");
		}
		for (String uuid : yaml.getStringList("teams.orange")) {
			teams.put(uuid, "orange");
		}
		
		for(String s : yaml.getConfigurationSection("pseudo").getKeys(false)) {
			uuidToPseudo.put(s, yaml.getString("pseudo." + s));
		}
	}
	/**
	 * Get the teams and tehir members
	 * @return a HashMap<String, String> --> First string is the player name, Second is the team "id" (ex. red)
	 */
	public static HashMap<String, String> getTeams() {return teams;}
	
	public static void setPlayerTeam(String uuid, String team) {teams.put(uuid, team);}
	
	public static void removePlayerTeam(String uuid) {
		if(teams.containsKey(uuid)) {
			teams.remove(uuid);
		}
	}
	
	public static void removePlayerTeamByName(String name) {
		removePlayerTeam(getUUIDByPseudo(name).toString());
	}
	
	public static void clearTeams(Main main) {
		teams.clear();
		updateConfig(main);
	}
	
	public static void updateConfig(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "teams");
	    File f = new File(userdata, File.separator + "teams.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	    
	    List<String> players = new ArrayList<>();
	    yaml.set("teams.red", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.blue", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.green", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.yellow", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.purple", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.aqua", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.black", players);
	    
	    players = new ArrayList<>();
	    yaml.set("teams.orange", players);
	    
	    try {
			yaml.save(f);
			for (String uuid : teams.keySet()) {
				if(teams.get(uuid).equals("red")) {
					players = yaml.getStringList("teams.red");
					players.add(uuid);
					yaml.set("teams.red", players);
				}
				if(teams.get(uuid).equals("blue")) {
					players = yaml.getStringList("teams.blue");
					players.add(uuid);
					yaml.set("teams.blue", players);
				}
				if(teams.get(uuid).equals("green")) {
					players = yaml.getStringList("teams.green");
					players.add(uuid);
					yaml.set("teams.green", players);
				}
				if(teams.get(uuid).equals("yellow")) {
					players = yaml.getStringList("teams.yellow");
					players.add(uuid);
					yaml.set("teams.yellow", players);
				}
				if(teams.get(uuid).equals("purple")) {
					players = yaml.getStringList("teams.purple");
					players.add(uuid);
					yaml.set("teams.purple", players);
				}
				if(teams.get(uuid).equals("aqua")) {
					players = yaml.getStringList("teams.aqua");
					players.add(uuid);
					yaml.set("teams.aqua", players);
				}
				if(teams.get(uuid).equals("black")) {
					players = yaml.getStringList("teams.black");
					players.add(uuid);
					yaml.set("teams.black", players);
				}
				if(teams.get(uuid).equals("orange")) {
					players = yaml.getStringList("teams.orange");
					players.add(uuid);
					yaml.set("teams.orange", players);
				}
			}
			for(String s : uuidToPseudo.keySet()) {
				yaml.set("pseudo." + s, uuidToPseudo.get(s));
			}
			yaml.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Get the team of a certain player
	 * <p>
	 * Exmple if the player Roberto belongs to the red team, getPlayerTeam() will return "red"
	 * WATCH OUT, UUID required
	 * </p>
	 * @param player's name
	 * @return The team id (ex. red)
	 */
	public static String getPlayerTeam(String uuid) {
		String team = teams.get(uuid);
		if(team == null) return "none";
		return team;
	}
	
	public static ChatColor getTeamColor(String uuid) {
		String team = getPlayerTeam(uuid);
		switch (team) {
		case "red": return ChatColor.RED;
		case "blue": return ChatColor.BLUE;
		case "green": return ChatColor.GREEN;
		case "yellow": return ChatColor.YELLOW;
		case "purple": return ChatColor.DARK_PURPLE;
		case "aqua": return ChatColor.DARK_AQUA;
		case "black": return ChatColor.DARK_GRAY;
		case "orange": return ChatColor.GOLD;

		default: return ChatColor.WHITE;
		}
	}
	
	public static ChatColor getTeamColorByTeam(String team) {
		switch (team) {
		case "red": return ChatColor.RED;
		case "blue": return ChatColor.BLUE;
		case "green": return ChatColor.GREEN;
		case "yellow": return ChatColor.YELLOW;
		case "purple": return ChatColor.DARK_PURPLE;
		case "aqua": return ChatColor.DARK_AQUA;
		case "black": return ChatColor.DARK_GRAY;
		case "orange": return ChatColor.GOLD;

		default: return ChatColor.WHITE;
		}
	}
	
	public static String getTeamName(String uuid) {
		String team = getPlayerTeam(uuid);
		switch (team) {
		case "red": return "§4Red Rocket";
		case "blue": return "§1Blue Whale";
		case "green": return "§2Green Turtle";
		case "yellow": return "§eYellow Stone";
		case "purple": return "§5Purple Amethyst";
		case "aqua": return "§3Aqua Dolphin";
		case "black": return "§0Black Raven";
		case "orange": return "§6Orange Mechanic";

		default: return "none";
		}
	}
	
	public static String getTeamNameByTeam(String teamname) {
		switch (teamname) {
		case "red": return "§4Red Rocket";
		case "blue": return "§1Blue Whale";
		case "green": return "§2Green Turtle";
		case "yellow": return "§eYellow Stone";
		case "purple": return "§5Purple Amethyst";
		case "aqua": return "§3Aqua Dolphin";
		case "black": return "§0Black Raven";
		case "orange": return "§6Orange Mechanic";

		default: return "none";
		}
	}
	/**
	 * Get every online players that are in a team
	 * @return Returns the same HashMap as getTeams() but this one contains only online players
	 */
	public static HashMap<String, String> getOnlinePlayers() {
		HashMap<String, String> teams = getTeams();
		HashMap<String, String> online = new HashMap<String, String>();
		Player p;
		for (String uuid : teams.keySet()) {
			p = Bukkit.getPlayer(UUID.fromString(uuid));
			if(p == null) { //Le != null est buggé des fois
				if(NPCManager.getNpcPlayerIfItIs(uuid) != null) {
					online.put(uuid, teams.get(uuid));
				}
			} else {
				online.put(uuid, teams.get(uuid));
			}
		}
		return online;
	}
	
	public static boolean isAllTeammatesOnline(String team) {
		HashMap<String, String> teams = getTeams();
		Player p;
		
		for (String uuid : teams.keySet()) { //NOT WORKING !!!
			if(teams.get(uuid).equals(team)) {
				p = Bukkit.getPlayer(UUID.fromString(uuid));
				if(p == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static List<String> getOnlineTeams() {
		List<String> teams = new ArrayList<>();
		List<String> teams_name = Arrays.asList("red", "blue", "green", "yellow", "purple", "aqua", "black", "orange");
		
		for(String team : teams_name) {
			for(String uuid : TeamsManager.getTeamMembers(team)) {
				if(Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
					teams.add(team);
					break;
				}
				if(NPCManager.getNpcPlayerIfItIs(uuid) != null) {
					System.out.println("NPC: " + NPCManager.getNpcPlayerIfItIs(uuid).getName());
					teams.add(team);
					break;
				}
			}
		}
		System.out.println("Teams Online: " + teams);
		return teams;
	}
	
	/**
	 * Get every player than are in a team as a list of players
	 * @return Returns a List<Player> with all da players in a team that are online
	 */
	public static List<Player> getOnlinePlayersAsPlayers() {
		HashMap<String, String> onlinePlayers = getOnlinePlayers();
		List<Player> players = new ArrayList<>();
		
		for(String pl : onlinePlayers.keySet()) {
			players.add(Bukkit.getPlayer(pl));
		}
		
		return null;
	}
	
	public static boolean hasATeammateOnline(String team) {
		for(String p : getTeamMembers(team)) {
			if(Bukkit.getPlayerExact(p) != null) return true;
		}
		return false;
	}
	
	public static List<String> getTeamMembers(String team) {
		List<String> members = new ArrayList<>();
		for (String uuid : teams.keySet()) {
			if(teams.get(uuid) == team) {
				members.add(uuid);
			}
		}
		return members;
	}
	
	public static List<Player> getTeamMembersAsPlayer(String team) {
		List<String> members = getTeamMembers(team);
		List<Player> players = new ArrayList<>();
		
		for(String pl : members) {
			players.add(Bukkit.getPlayer(pl));
		}
		
		
		return players;
	}
	
	/**
	 * Get une liste des teams qui ont des membres (qui sont pas vides)
	 * @return List<String> des team (ex:red)
	 */
	public static List<String> getNonEmptyTeams() {
		List<String> teams = new ArrayList<>();
		for(String team : getTeams().values()) {
			if(teams.contains(team)) teams.add(team);
		}
		return teams;
	}
	
	public static void updatePseudo(String uuid, String pseudo) {
		uuidToPseudo.put(uuid, pseudo);
	}
	
	public static String getPseudo(String uuid) {
		
		if(!uuidToPseudo.containsKey(uuid)) {
			if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuid)))) {
				return Bukkit.getPlayer(UUID.fromString(uuid)).getName();
			} else {
				return uuid;
			}
		}
		
		return uuidToPseudo.get(uuid);
	}
	
	public static UUID getUUIDByPseudo(String pseudo) {
		for(String uuid : uuidToPseudo.keySet()) {
			if(uuidToPseudo.get(uuid).equals(pseudo)) {
				return UUID.fromString(uuid);
			}
		}
		return null;
	}
	
	public static void removeUUIDToPseudo(String uuid) {
		uuidToPseudo.remove(uuid);
	}
}
