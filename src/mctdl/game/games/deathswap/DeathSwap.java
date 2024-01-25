package mctdl.game.games.deathswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Spectate;
import mctdl.game.utils.TimeFormater;

public class DeathSwap implements Listener{
	
	static Main main;
	static boolean enable = false;
	static boolean isGenerated;
	static String worldname;
	static HashMap<String, String> players;
	
	static HashMap<String, List<Integer>> playerdata = new HashMap<String, List<Integer>>();
	
	static List<Player> alives = new ArrayList<>();
	
	static HashMap<String, String> duels = new HashMap<String, String>();
	
	static Player abandonne = null;
	
	
	//MONEY INFO
	static int goldRound;
	static int goldKill;
	static int goldPlace;
	static int goldDamage;
	
	public DeathSwap(Main main) {
		DeathSwap.main = main;
		
		worldname = DeathSwap.main.getConfig().getString("games.deathswap.map");
		
		goldRound = main.getConfig().getInt("games.deathswap.money.round");
		goldKill = main.getConfig().getInt("games.deathswap.money.kill");
		goldPlace = main.getConfig().getInt("games.deathswap.money.placems");
		goldDamage = main.getConfig().getInt("games.deathswap.money.dmg");
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
		main.getConfig().set("game", "deathswap");
		main.saveConfig();
		//players = tt les joueurs en team online
		players = TeamsManager.getOnlinePlayers();
		
		
		//Teleport Players
		int counter = 0;

		matchmaking();
		
		for (String player : players.keySet()) {
			
			Player p = Bukkit.getPlayer(player);
			//Datas init
			List<Integer> datas = new ArrayList<>();
			datas.add(1);
			datas.add(0);
			datas.add(0);
			datas.add(0);
			datas.add(0);
			datas.add(0);
			datas.add(0);
			
			//Add players to playerdata
			playerdata.put(player, datas);
			
			//Permissions.addPerm(p, "mv.bypass.gamemode.SPECTATOR");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + player + " " + worldname);
			//Teleport script to make them distant one from another
			//-->
			if(abandonne != p) {
				int x = 1000*counter;
				Location loc = new Location(Bukkit.getWorld(worldname), x, 0, 0);
				int y = loc.getWorld().getHighestBlockAt(loc).getY();
				loc.setY(y +50);
				p.teleport(loc);
				
				
				p.setGameMode(GameMode.SURVIVAL);
				p.setInvisible(false);
				p.setInvulnerable(false);
				p.setAllowFlight(false);
				
			} else {
				new BukkitRunnable() {

					@Override
					public void run() {
						p.setGameMode(GameMode.SPECTATOR);
						Spectate.setSpectatingGroup(p, TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(player)));
					}
					
				}.runTaskLater(main, 20);
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 1));
			
			// Items
			PlayerData.saveItems(p);
			p.getInventory().clear();
			
			counter++;
		}
		
		enable = true;
		
		tabTimer();
		gameTimer();
	}
	
	public static void disable() {

		enable = false;
		for (String player : TeamsManager.getOnlinePlayers().keySet()) {
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + player + " " + Bukkit.getWorlds().get(0).getName());
			Player p = Bukkit.getPlayer(player);
			p.setGameMode(GameMode.ADVENTURE);
			p.setInvisible(false);
			p.setHealth(20);
			p.setSaturation(20);
			p.getActivePotionEffects().clear();

			p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
			
			p.getInventory().clear();
			
			PlayerData.registerPlayer(p);
		}

		main.getConfig().set("games.deathswap.generated", false);
		main.getConfig().set("game", "lobby");
		main.saveConfig();
	}
	
	
	/**
	 * Generate or regenerate the map
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
			
			isGenerated = DeathSwap.main.getConfig().getBoolean("games.deathswap.generated");
			
			if(!isGenerated) {

				Bukkit.broadcastMessage("§7Regenerating a world for §cDeathSwap");
				//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv regen " + worldname); //Si le monde existe mais qu'il a déja été utilisé
				//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv confirm "); 
			} else {
				Bukkit.broadcastMessage("§aA game of §cDeath§7Swap §awill start soon");
			}
		}
		
		//Set the world as generated in config
		main.getConfig().set("games.deathswap.generated", true);
		main.saveConfig();
		
		Bukkit.getWorld(worldname).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		
		return true;
	}
	
	public static void matchmaking() {
		List<String> players = new ArrayList<>();
		for(String pl : TeamsManager.getOnlinePlayers().keySet()) {
			players.add(pl);
		}
		
		while(!players.isEmpty()) {
			if(players.size() == 1) {
				abandonne = Bukkit.getPlayer(players.get(0));
				break;
			}
			
			String player1 = players.get(0);
			String player2 = "nnone";
			for(int i = 1; i < players.size(); i++) {
				player2 = players.get(i);
				if(!TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(player1)).contains(player2)) { //Si player2 pas mm equipe
					break;
				}
			}
			duels.put(player1, player2);
			players.remove(player1);
			players.remove(player2);
			alives.add(Bukkit.getPlayer(player1));
			alives.add(Bukkit.getPlayer(player2));
		}
		System.out.println(duels);
	}
	
	public static void gameTimer() {
		if(abandonne != null) { //Si ya un nb entier de joueurs
			playerdata.get(abandonne.getName()).set(2, 100);
			System.out.println(playerdata);
			System.out.println("ABND?NONE " + abandonne);
			abandonne.sendMessage("§cIl y a un nombre impair de joueurs... Vous êtes le volontaire pour ne pas partciper");
		}
		
		new BukkitRunnable() {

			int counter = 0;
			int delay = main.getConfig().getInt("games.deathswap.delay");
			
			@Override
			public void run() {
				if(!enable) {
					cancel();
					return;
				}
				if(counter == delay-10) {
					Bukkit.broadcastMessage("§cDeathSwap is teleporting in 10 seconds");
				}
				if(counter == delay-5) {
					Bukkit.broadcastMessage("§fDeathSwap is teleporting in 5 seconds");
				}
				if(counter == delay-4) {
					Bukkit.broadcastMessage("§aDeathSwap is teleporting in 4 seconds");
				}
				if(counter == delay-3) {
					Bukkit.broadcastMessage("§bDeathSwap is teleporting in 3 seconds");
				}
				if(counter == delay-2) {
					Bukkit.broadcastMessage("§5DeathSwap is teleporting in 2 seconds");
				}
				if(counter == delay-1) {
					Bukkit.broadcastMessage("§6DeathSwap is teleporting in 1 second");
				}
				if(counter == delay) {
					Bukkit.broadcastMessage("§dDeathSwap is teleporting");
					
					for(String player1 : duels.keySet()) {
						Player p1 = Bukkit.getPlayer(player1);
						Player p2 = Bukkit.getPlayer(duels.get(player1));
						
						Location loc1 = p1.getLocation();
						Location loc2 = p2.getLocation();
						
						p1.teleport(loc2);
						p2.teleport(loc1);
					}
				}
				System.out.println(abandonne.getGameMode());
				counter++;
			}
			
		}.runTaskTimer(main, 0, 20);
		
	}
	
	public static void tabTimer() {
		
		new BukkitRunnable() {
			
			int counter = 0;
			
			@Override
			public void run() {
				if(!enable) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.setPlayerListFooter("");
					}
					cancel();
					return;
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					int coin = playerdata.get(player.getName()).get(2);
					int rounds = playerdata.get(player.getName()).get(1);
					String time = TimeFormater.getFormatedTime(counter);
					player.setPlayerListFooter("§f----------------------------"
							+ "\n§aGame : §cDeath§7Swap"
							+ "\n§aRounds survécu §f: " + rounds
							+ "\n§6Coins §f: " + coin
							+ "\n§cTemps passé §f: " + time);
				}
				counter++;
			}
		}.runTaskTimer(main, 0, 20);
	}
	
	@EventHandler
	public static void deathEvent(PlayerDeathEvent e) {
		if(!enable) return;
		Player p = e.getEntity();
		//Do stuff
		
		String name = p.getName();
		String opponent = "";
		if(duels.containsKey(name)) {
			opponent = duels.get(name);
			
		} else if(duels.containsValue(name)) {
			for(String ppl : duels.keySet()) {
				if(duels.get(ppl).equals(name)) {
					opponent = ppl;
				}
			}
			
		} else {return;}
		
		alives.remove(p);
		alives.remove(Bukkit.getPlayer(opponent));
		List<Integer> oppoDatas = playerdata.get(opponent);
		List<Integer> pDatas = playerdata.get(name);
		
		pDatas.set(0, 0);
		oppoDatas.set(3, oppoDatas.get(3)+1);
		int oppoCoin = oppoDatas.get(2);
		oppoCoin += goldKill;
		oppoDatas.set(2, oppoCoin);
		
		playerdata.put(name, pDatas);
		playerdata.put(opponent, oppoDatas);
		
		ChatColor pc = TeamsManager.getTeamColor(name);
		ChatColor oc = TeamsManager.getTeamColor(opponent);
		
		Bukkit.broadcastMessage(oc + opponent + " §6a gagné son duel contre " + pc + name);
		p.sendMessage("§aVous avez gagné §f" + playerdata.get(name).get(2) + " §6Coins");
		Bukkit.getPlayer(opponent).sendMessage("§aVous avez gagné §f" + playerdata.get(opponent).get(2) + " §6Coins");
		
		//Spectate Mode
		List<String> mates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(name));
		
		if(mates.size() != 1) { //Si il est pas tout seul
			Spectate.setSpectatingGroup(p, mates);
		} else {
			Spectate.setWanderingSpectator(p);
		}
		List<String> oppoMates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(opponent));
		
		if(oppoMates.size() != 1) { //Si il est pas tout seul
			Spectate.setSpectatingGroup(Bukkit.getPlayer(opponent), oppoMates);
		} else {
			Spectate.setWanderingSpectator(Bukkit.getPlayer(opponent));
		}
	}
	
	public static List<Integer> getRawPlayerDatas(String name) {
		return playerdata.get(name);
	}
	
	public static String getPayerDatas(String name) {
		List<Integer> datas = playerdata.get(name);
		String output = "isAlive : " + datas.get(0)
		+ "\nRounds Survived : " + datas.get(1)
		+ "\nCoins earned : " + datas.get(2)
		+ "\nKills : " + datas.get(3)
		+ "\nDamage Done : " + datas.get(4)
		+ "\nBlock placed : " + datas.get(5)
		+ "\nBlock broke : " + datas.get(6)
		;
		
		return output;
	}
}
