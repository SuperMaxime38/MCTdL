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

import mctdl.game.commands.BaltopCommand;
import mctdl.game.commands.DummyCommand;
import mctdl.game.commands.NPCCommand;
import mctdl.game.commands.PouleZookaCMD;
import mctdl.game.commands.TDLPacketCommand;
import mctdl.game.commands.TdLCommand;
import mctdl.game.commands.TdLTabCompleter;
import mctdl.game.commands.TestCommand;
import mctdl.game.dev.ItemGiver;
import mctdl.game.dev.ItemGiverCompleter;
import mctdl.game.games.deathswap.DeathSwap;
import mctdl.game.games.deathswap.DeathSwapCommand;
import mctdl.game.games.lobby.LobbyJump;
import mctdl.game.games.lobby.PouleZooka;
import mctdl.game.games.meltdown.MDCommand;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.MeltdownFiles;
import mctdl.game.games.nexus.Nexus;
import mctdl.game.games.nexus.NexusCommand;
import mctdl.game.games.nexus.NexusFiles;
import mctdl.game.listeners.Damage;
import mctdl.game.listeners.Interact;
import mctdl.game.listeners.Join;
import mctdl.game.listeners.Move;
import mctdl.game.listeners.NMSEventHandler;
import mctdl.game.listeners.NpcEvents;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCCommandCompleter;
import mctdl.game.npc.NPCManager;
import mctdl.game.scoreboard.ScoreboardManager;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Spectate;
import mctdl.game.utils.Time;
import mctdl.game.utils.objects.Canon;
import mctdl.game.utils.objects.riffles.AssaultRiffle;

public class Main extends JavaPlugin{

	Main main = this;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		new Time(this);
		
		//Gamerules
		Bukkit.getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, false);
		Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_MOB_LOOT, false);
		Bukkit.getWorld("mapz").setGameRule(GameRule.DO_MOB_LOOT, false);
		Bukkit.getWorld("mapz").setGameRule(GameRule.FALL_DAMAGE, true);
		
		
		//FileCheck
		TeamsManager.fileCheck(this);
		MoneyManager.fileCheck(this);
		MeltdownFiles.fileCheck(this);
		PlayerData.fileCheck(this);
		NexusFiles.fileCheck(this);
		
		//Load HashMap
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
			NPCManager.fileCheck(this);
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
		
		//Register Events------------------------------------------------------------
		getServer().getPluginManager().registerEvents(new TabManager(this), this);
		getServer().getPluginManager().registerEvents(new ScoreboardManager(this), this);
		getServer().getPluginManager().registerEvents(new GameVoting(this), this);
		
		//Register Games-------------------------------------------------------------
		
		//Meltdown
		getServer().getPluginManager().registerEvents(new Meltdown(this), this);
		
		//DeathSwap
		getServer().getPluginManager().registerEvents(new DeathSwap(this), this);
		
		//Nexus
		getServer().getPluginManager().registerEvents(new Nexus(this), this);
		
		
		//NMS Listener
		NMSEventHandler.loop(main);
		
		//Register LISTENERS---------------------------------------------------------
		getServer().getPluginManager().registerEvents(new Damage(this), this);
		getServer().getPluginManager().registerEvents(new Join(this), this);
		getServer().getPluginManager().registerEvents(new Interact(this), this);
		getServer().getPluginManager().registerEvents(new Move(this), this);
		getServer().getPluginManager().registerEvents(new Spectate(this), this);
		Bukkit.getPluginManager().registerEvents(new NpcEvents(), this); // Handles NPC events (such as projectile hit)
		
		
		//Lobby Games----------------------------------------------------------------
			// --> Jump
		getServer().getPluginManager().registerEvents(new LobbyJump(this), this);
		getCommand("jump").setExecutor(new LobbyJump(this));
		getServer().getPluginManager().registerEvents(new Canon(this), this);
			// --> PouleZooka
		getServer().getPluginManager().registerEvents(new PouleZooka(this), this);
		
		//Other-----------------------------------------------------------------------
		//Commands -->
		getCommand("mctdl").setExecutor(new TdLCommand(this));
		getCommand("baltop").setExecutor(new BaltopCommand(this));
		getCommand("npc").setExecutor(new NPCCommand(this));
		getCommand("dg").setExecutor(new ItemGiver());
		getCommand("poulezooka").setExecutor(new PouleZookaCMD());
		getCommand("dummy").setExecutor(new DummyCommand(this));
		getCommand("testcmd").setExecutor(new TestCommand(this));
		getCommand("tdlpacket").setExecutor(new TDLPacketCommand());
		
		//meltdown
		getCommand("meltdown").setExecutor(new MDCommand(this));
		
		//deathswap
		getCommand("deathswap").setExecutor(new DeathSwapCommand(this));
		
		//nexus
		getCommand("nexus").setExecutor(new NexusCommand(this));
		
		//TabCompleter -->
		getCommand("npc").setTabCompleter(new NPCCommandCompleter());
		getCommand("mctdl").setTabCompleter(new TdLTabCompleter());
		getCommand("dg").setTabCompleter(new ItemGiverCompleter());
		
		String h = header();
		
		disableExternalGamesFeatures(this);
		
		// Objects
		getServer().getPluginManager().registerEvents(new AssaultRiffle(this), this);
		
		
		
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
}
