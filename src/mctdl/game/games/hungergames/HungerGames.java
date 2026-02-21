package mctdl.game.games.hungergames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.NBTAPI;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.TimeFormater;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public final class HungerGames implements Listener{
	
	private List<UUID> alivePlayers;
	private List<String> aliveTeams;
	private HashMap<UUID, int[]> playerdatas;
	private HashMap<String, Location> spawns;
	private WorldBorder border;
	private boolean reachedBorderMinValue;
	
	private Main main;
	
	private boolean enable;
	
	private HungerGamesMap map;
	
	
	// From config
	private final int borderDelay;
	private final int moneyPerKill;
	private final int moneyPerSurviveTime;
	private final int moneyPerAssist;
	private final int moneyPerWin;
	private final int moneyPerCoinDrop;
	
	
	// Random hardcoded datas
	private static int BORDER_SHRINK_VALUE;
	
	public HungerGames(Main main) {
		this.main = main;
		
		alivePlayers = new ArrayList<UUID>();
		aliveTeams = new ArrayList<String>();
		playerdatas = new HashMap<UUID, int[]>();
		spawns = new HashMap<String, Location>();
		
		// From config
		map = new HungerGamesMap(main.getConfig().getString("games.hungergames.map"));
		borderDelay = main.getConfig().getInt("games.hungergames.border.delay");
		moneyPerKill = main.getConfig().getInt("games.hungergames.money.kill");
		moneyPerSurviveTime = main.getConfig().getInt("games.hungergames.money.survive");
		moneyPerAssist = main.getConfig().getInt("games.hungergames.money.assist");
		moneyPerWin = main.getConfig().getInt("games.hungergames.money.win");
		moneyPerCoinDrop = main.getConfig().getInt("games.hungergames.money.coinDrop");
		
		border = map.getWorld().getWorldBorder();
		reachedBorderMinValue = false;
		BORDER_SHRINK_VALUE = (map.getMaxBorderSize() - map.getMinBorderSize()) / 10; // 10 %
		
		enable = false;
	}
	
	public void enable() {
		if(!canStart()) {
			System.out.println("§f[§fcHungerGames§f] §cYou need 2 teams in order to start a session");
			return;
		}
		
		enable = true;
		
		
		// Generated the map if needed
		if(!map.isGenerated()) {
			map.build(true);
		}
		
		// Enable components
		resetBorder();
		init();
		gameLoop();
		
	}
	
	public void disable() {
		// ### Disable components ###
		
		// Disable worldborder
		border.setCenter(0, 0);
		border.setSize(29999984); // Default & Max value
		
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
			
			p.getInventory().setItem(8, getTracker());;
			p.setCompassTarget(map.getCenter());
		}
		
		
	}
	
	private void gameLoop() {
		
		new BukkitRunnable() {
			
			int counter = 1;
			String time;

			@Override
			public void run() {
				
				victoryChecker();
				
				if(!reachedBorderMinValue && counter % borderDelay == 0) borderShrink();
				
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
	
	private void victoryChecker() {
		if(aliveTeams.size() == 1) {
			// Victory
			Bukkit.broadcastMessage("§6L'équipe des " + TeamsManager.getTeamColorByTeam(aliveTeams.get(0))
			+ TeamsManager.getTeamNameByTeam(aliveTeams.get(0)) + " §6a gagné !");
			
			// Ajout argent aux vainqueurs
			for(String member : TeamsManager.getTeamMembers(aliveTeams.get(0))) {
				int[] datas = playerdatas.get(UUID.fromString(member));
				datas[1] = datas[1] + moneyPerWin;
				playerdatas.put(UUID.fromString(member), datas);
			}
			
			applyMoneyWon();
		}
	}
	
	private void borderShrink() {
		if(border.getSize() - BORDER_SHRINK_VALUE < map.getMinBorderSize()) {
			reachedBorderMinValue = true;
			border.setSize(map.getMinBorderSize());
			return;
		}
		
		border.setSize(border.getSize() - BORDER_SHRINK_VALUE);
		
		for(UUID uuid : playerdatas.keySet()) {
			
			if(!NPCManager.isAnNPC(uuid.toString())) {
				Player p = Bukkit.getPlayer(uuid);
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cThe border is shrinking"));
			}
			
		}
	}
	
	public void applyMoneyWon() {
		List<String> compensation = new ArrayList<String>();
		for (UUID uuid : playerdatas.keySet()) {

			int coins = playerdatas.get(uuid)[1];
			
			//Check if NPS for scoring fn
			if(NPCManager.getNpcByUUID(uuid.toString()) != null) {
				PlayerAI npc = (PlayerAI) NPCManager.getNpcByUUID(uuid.toString());
				npc.setScore(npc.getScore() + coins);
			}

			// Check is player can get compensation from offline teammate
			String team = TeamsManager.getPlayerTeam(uuid.toString());
			boolean b = TeamsManager.isAllTeammatesOnline(team);
			if (!b) {
				if(coins > 0) {
					coins = (int) Math.round(coins * 1.1);
				} else {
					coins = (int) Math.round(coins * 0.9);
				}
				
				if(!compensation.contains(team)) compensation.add(team);
			}
			
			MoneyManager.addPlayerMoney(uuid.toString(), coins);
			
			Player p = Bukkit.getPlayer(uuid);
			if(p != null) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6+" + coins + " Coins"));
				p.sendMessage("§aYou got " + "§6+" + coins + " Coins");
			}
		}

		for (String string : compensation) {
			Bukkit.broadcastMessage(ChatColor.BOLD + "§6L'équipe §r" + TeamsManager.getTeamColorByTeam(string) + TeamsManager.getTeamNameByTeam(string)
					+ ChatColor.BOLD + "§c a reçu une §acompensation §cpour l'absence d'un ou plusieurs de leurs membres");
		}
	}
	
	private void resetBorder() {
		border.setCenter(map.getCenter().getBlockX(), map.getCenter().getBlockZ());
		border.setSize(map.getMaxBorderSize());
	}
	
	public static ItemStack getTracker() {
		ItemStack item = new ItemStack(Material.COMPASS, 1);
		CompassMeta meta = (CompassMeta) item.getItemMeta();
		meta.setDisplayName("§6Player Tracker");
		meta.setLore(Arrays.asList("§7When the time will come, this tracker will point to the nearest §cennemy", "", "§5UTIL"));
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		NBTAPI.addNBT(item, "mctdlID", "HUNGERGAMES_TRACKER");
		
		return item;
	}
	
	public boolean isEnabled() {
		return enable;
	}
	
	public boolean canStart() {
		if(TeamsManager.getOnlineTeams().size() < 2) return false;
		
		return true;
	}
	
	
	// ############ LISTENERS ############
	
	@EventHandler
	public static void onPlayerDeath(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(NPCManager.getNpcByUUID(e.getEntity().getUniqueId().toString()) != null) return; // Not a player
		
		Player p = (Player) e.getEntity();
		
		Entity damager = e.getDamager();
		
		if(p.getHealth() <= 0) {
			
		} else {
			
		}
	}
}
