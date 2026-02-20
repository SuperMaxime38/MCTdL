package mctdl.game.games.hungergames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.TimeFormater;

public final class HungerGames {
	
	private List<UUID> alivePlayers;
	private List<String> aliveTeams;
	private HashMap<UUID, int[]> playerdatas;
	private HashMap<String, Location> spawns;
	
	private Main main;
	
	boolean enable;
	
	
	// From config
	private String map;
	private boolean isGenerated;
	private int borderDelay;
	private int moneyPerKill;
	private int moneyPerSurviveTime;
	private int moneyPerAssist;
	private int moneyPerWin;
	private int moneyPerCoinDrop;
	
	public HungerGames(Main main) {
		this.main = main;
		
		alivePlayers = new ArrayList<UUID>();
		aliveTeams = new ArrayList<String>();
		playerdatas = new HashMap<UUID, int[]>();
		spawns = new HashMap<String, Location>();
		
		
		enable = false;
	}
	
	public void enable() {
		if(!canStart()) {
			System.out.println("§f[§fcHungerGames§f] §cYou need 2 teams in order to start a session");
			return;
		}
		
		enable = true;
		
		// Enable components
		init();
		gameLoop();
		
	}
	
	public void disable() {
		// Disable components
		
		enable = false;
	}
	
	private void init() {
		// Register teams
		aliveTeams = TeamsManager.getOnlineTeams();
		
		// Register spawns
		spawns.put("red", new Location(Bukkit.getWorld("mapz"), 0, 0, 0, 0, 0)); // Example, change values later + do other teams
		
		// Register players
		
		for(Player p : TeamsManager.getOnlinePlayersAsPlayers()) {
			alivePlayers.add(p.getUniqueId());
			int[] datas = {
					1,	// isAlive
					0,	// Coins
					0,	// Kills
					0,	// Damage dealt
					0,	// Items picked
					0	// Time survived (look at discord to check when this value is updated)
			};
			playerdatas.put(p.getUniqueId(), datas);
			
			// Teleport
			
			// Other stuff
			PlayerData.saveItems(p);
			p.setGameMode(GameMode.ADVENTURE);
			p.getInventory().clear();
			p.setFlying(false);
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setExp(0);
			p.setInvulnerable(true);
			
			
		}
		
		
	}
	
	private void gameLoop() {
		
		new BukkitRunnable() {
			
			int counter = 0;
			String time;

			@Override
			public void run() {
				
				time = TimeFormater.getFormatedTime(counter);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(playerdatas.containsKey(p.getUniqueId())) {
						p.setPlayerListFooter(
							 "§f----------------------------\n"
							+ "§3Mode de jeu §f: §cHungerGames\n"
							+ "§6Coins : " + playerdatas.get(p.getUniqueId())[1] + "\n"
							+ "§cTemps passé §f: " + time);
					}
				}
				
				counter++;
			}
			
		}.runTaskTimer(main, 0, 20);
	}
	
	
	private void resetBorder() {
		
	}
	
	public boolean isEnabled() {
		return enable;
	}
	
	public boolean canStart() {
		if(TeamsManager.getOnlineTeams().size() < 2) return false;
		
		return true;
	}
}
