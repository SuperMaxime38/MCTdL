package mctdl.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import mctdl.game.commands.CommandsRegister;
import mctdl.game.games.lobby.items.PortalGun;
import mctdl.game.listeners.ListenersRegister;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.scoreboard.ScoreboardManager;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.FileLoader;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Time;

public class Main extends JavaPlugin{
	
	public static String game;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		new Time(this);
		
		game = "lobby";
		
		//Gamerules
		Bukkit.getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, false);
		Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_MOB_LOOT, false);
		Bukkit.getWorld("mapz").setGameRule(GameRule.DO_MOB_LOOT, false);
		Bukkit.getWorld("mapz").setGameRule(GameRule.FALL_DAMAGE, true);
		
		
		// Load files
		FileLoader.loadFiles();
		
		// Load HashMap
		TeamsManager.loadHashMap(this);
		MoneyManager.loadHashMap(this);
		PlayerData.loadHashMap(this);
		
		//Load TAB & NPCs (if server reload)
		if(!Bukkit.getOnlinePlayers().isEmpty()) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for (Player pl : Bukkit.getOnlinePlayers()) {
						NPCManager.onPlayerJoin(pl, 60);
						ScoreboardManager.initScoreboardForPlayer(pl);
					}
					TabManager.updateTabList();
				}
			}.runTaskLater(this, 40);
		}
		
		//NPC
		if(getConfig().getBoolean("enable-npc")) {
			NPCManager.loadHashMap(this);
		}
		
		
		//DOESN'TT WORK
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.ENTITY_ACTION) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				//Player p = e.getPlayer();
				
				//Extract Info
				if(e.getPacket().getType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					if(packet.getStrings().read(0).equals("block.lava.ambient") || e.getPacket().getStrings().read(0).equals("block.lava.pop") || e.getPacket().getStrings().read(0).equals("block.beacon.ambient")) {
						e.setCancelled(true);
					}
				}
				
				
			}
			
			@Override
			public void onPacketSending(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				Player p = e.getPlayer();
				p.stopSound(Sound.BLOCK_LAVA_AMBIENT);
				p.stopSound(Sound.BLOCK_LAVA_POP);
				
				if(e.getPacket().getType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					
					if(packet.getStrings().read(0).equals("block.lava.ambient") || e.getPacket().getStrings().read(0).equals("block.lava.pop") || e.getPacket().getStrings().read(0).equals("block.beacon.ambient")) {
						e.setCancelled(true);
					}
				}
			}
		});
		
		//Votes
		GameVoting.getGames();
		
		// Register Listeners
		ListenersRegister.register(this);
		
		
		PortalGun.displayPortals();
		
		//Other-----------------------------------------------------------------------
		
		
		// Register Commands
		CommandsRegister.register(this);
		
		
		String h = header();
		
		disableExternalGamesFeatures(this);
		
		
		
		System.out.println(h + "MCTdL MAIN PLUGIN started !");
	}
	
	@Override
	public void onDisable() {
		saveDefaultConfig();
		String h = header();
		
		//NPC
		if(getConfig().getBoolean("enable-npc")) {
			NPCManager.updateConfig(this);
			NPCManager.destroyNPCs();
		}
		

		TeamsManager.updateConfig(this);
		MoneyManager.updateConfig(this);
		PlayerData.updateConfig(this);
		
		//Clear Display
		GameVoting.clearDisplay();
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecart]");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
		System.out.println(h + "MCTdL MAIN PLUGIN stopped");
	}
	
	public static String header() { return "[" + ChatColor.GOLD + "MCTdL" + ChatColor.WHITE + "] > ";}
	
	public static void disableExternalGamesFeatures(Main main) {
		if(main.getServer().getPluginManager().getPlugin("HungerGames") != null) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					main.getServer().getPluginManager().getPlugin("HungerGames").getConfig().set("enable-teams", false);
					main.getServer().getPluginManager().getPlugin("HungerGames").saveDefaultConfig();
					System.out.println("MCTdL disabled Tablist & Teams features for HungerGames");
				}
			}.runTaskLater(main, 5);
		}
	}

	public static JavaPlugin getPlugin() {
		return Main.getPlugin(Main.class);
	}
}
