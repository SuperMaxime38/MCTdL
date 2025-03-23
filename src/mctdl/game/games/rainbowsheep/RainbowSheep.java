package mctdl.game.games.rainbowsheep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.Map;
import mctdl.game.utils.objects.riffles.AssaultRiffle;

public class RainbowSheep {
	
	private Main main;
	
	private List<String> teams = new ArrayList<>();
	private List<Player> players = new ArrayList<>();
	private boolean hasCustomDatas = true;
	private HashMap<String, List<String>> datas = new HashMap<String, List<String>>();
	
	private Map map;
	
	private String world = "mapz";
	
	//Optionnal datas
	private List<Location> spawns = new ArrayList<>();
	private boolean areSpawnFixed = false;
	private HashMap<Player, Location> fixedSpawns = new HashMap<Player, Location>();
	
	//CLASSES-------------------------------------------------------------------------
	
	public RainbowSheep(Main main, String teamA, String teamB, String map, HashMap<String, List<String>> datas) { // 1v1
		this.main = main;
		
		this.teams.add(teamA);
		this.teams.add(teamB);

		players.addAll(TeamsManager.getTeamMembersAsPlayer(teamA));
		players.addAll(TeamsManager.getTeamMembersAsPlayer(teamB));
		
		
		if(datas == null) {
			hasCustomDatas = false;
		} else {
			this.datas = datas;
		}
	}
	
	public RainbowSheep(Main main, String map,  HashMap<String, List<String>> datas) { // FFA
		this.main = main;
		
		for(String team : TeamsManager.getNonEmptyTeams()) {
			this.teams.add(team);
		}
		
		this.players = TeamsManager.getOnlinePlayersAsPlayers();
		
		if(datas == null) {
			hasCustomDatas = false;
		} else {
			this.datas = datas;
		}
		
		if(hasCustomDatas) { //Datas (genre les spawns)
			if(datas.containsKey("world")) { //------------WORLD
				world = datas.get("world").get(0);
			}
			if(datas.containsKey("spawns")) { //-----------SPAWNS
				for(String txt : datas.get("spawns")) {
					String[] coords = txt.split(",");
					spawns.add(new Location(Bukkit.getWorld(world), Integer.valueOf(coords[0]), Integer.valueOf(coords[1]), Integer.valueOf(coords[2])));
				}
			}
			if(datas.containsKey("areSpawnFixed") && !spawns.isEmpty()) { //---------FIXED SPAWNS
				areSpawnFixed = Boolean.valueOf(datas.get("areSpawnFixed").get(0));
				if(areSpawnFixed) {
					if(players.size() > spawns.size()) { //A pas assez de spawns
						areSpawnFixed = false;
					} else {
						for(int i = 0; i < players.size(); i++) {
							fixedSpawns.put(players.get(i), spawns.get(i));
						}
					}
				}
			}
			
		}
		
		//MAP -->
		this.map = new Map(main, "rs-"+map, new Location(Bukkit.getWorld(world), 0, 0, 0), world, null, null, 0, 50, "RainbowSheep");
		Bukkit.broadcastMessage("[§6RainbowSheep§f] > §aGénération de la map... §cca va lag :(");
		this.map.build(true);
		
	}
	
	//RESPAWN--------------------------------------------------------------------
	
	public void respawnPlayer(Player p) {
		Location loc;
		if(areSpawnFixed) { //TP AVEC HASHMAP fixedSpawn
			loc = fixedSpawns.get(p);
			p.teleport(loc);
			
		} else if(!spawns.isEmpty()){ //TP randomly parmis la liste des spawns (spawns)
			
			Random rdm = new Random();
			int spawn = rdm.nextInt(spawns.size());
			loc = spawns.get(spawn);
			p.teleport(loc);
			
		} else { //jsp on verra
			
		}
	}
	
	//RANDOM FUNCTION------------------------------------------------------------
	
	public void resetAllPlayerInv() {
		for(Player p : players) {
			resetPlayerInv(p);
		}
	}
	
	public void resetPlayerInv(Player p) {
		p.getInventory().clear();
		p.getInventory().setItem(0, AssaultRiffle.getAssaultRiffle());
		
	}
	
	
	public void setDatas(HashMap<String, List<String>> datas) {
		if(datas != null)
			this.datas = datas;
	}
}
