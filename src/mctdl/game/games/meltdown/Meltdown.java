package mctdl.game.games.meltdown;

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
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.ai_trainer.Environnement;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Ray;
import mctdl.game.utils.Spectate;
import mctdl.game.utils.TimeFormater;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class Meltdown implements Listener {

	static boolean enable = false;
	static Main main;
	
	public static int counter = 0;


	// Random datas
	static int pickaxe_cooldown, money_per_block, heater_cooldwon, money_per_kill, money_per_death, money_per_final, money_for_eliminated;
	

	
	//NPC related stuff
	static HashMap<Player, List<MeltdownNPC>> inViewNPCs;
	static List<MeltdownNPC> IAs;
	

	public Meltdown(Main main) {
		Meltdown.main = main;
		
		inViewNPCs = new HashMap<Player, List<MeltdownNPC>>();
		IAs = new ArrayList<MeltdownNPC>();
	}

	// Setup
	static HashMap<String, List<Integer>> playerdata = new HashMap<String, List<Integer>>();

	public static void enable() {
		if(!canStart()) {
			System.out.println("[�4Meltdown�f] You need 2 teams in order to start a session");
			return;
		}
		
		// Load random datas
		pickaxe_cooldown = main.getConfig().getInt("games.meltdown.cooldown.pickaxe");
		money_per_block = main.getConfig().getInt("games.meltdown.money.block");
		heater_cooldwon = main.getConfig().getInt("games.meltdown.cooldown.heater");
		money_per_kill = main.getConfig().getInt("games.meltdown.money.kills");
		money_per_death = main.getConfig().getInt("games.meltdown.money.deaths");
		money_per_final = main.getConfig().getInt("games.meltdown.money.final-kill");
		money_for_eliminated = main.getConfig().getInt("games.meltdown.money.eliminated");
		

		counter = 0; // Var used for gameTimer (super important)
		
		boolean isMapGenerated = MeltdownFiles.isMapGenerated(main);
		if (isMapGenerated == false) {
			Bukkit.broadcastMessage("�aThe game will start soon...");
			Bukkit.broadcastMessage("�fNote : Les �6Coins �fque vous obtennez pendant la partie sont affich�s dans la �atablist");
			MDMap.generateMap(main);
			new BukkitRunnable() {
				int i = 0;

				@Override
				public void run() {
					if (i == 5)
						Bukkit.broadcastMessage("�fThe game will start in 5 seconds");
					if (i == 7) {
						Bukkit.broadcastMessage("�aThe game will start in 3 seconds");
						playerTeleport();
					}
					if (i == 8)
						Bukkit.broadcastMessage("�3The game will start in 2 seconds");
					if (i == 9)
						Bukkit.broadcastMessage("�5The game will start in 1 seconds");
					if (i == 10) {
						Bukkit.broadcastMessage("�6The game will start...");
						playerBegin();
						MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 0));
						cancel();
						return;
						}

					i++;
				}
			}.runTaskTimer(main, 0, 20);
		} else {
			playerTeleport();
			playerBegin();
		}
	}
	
	public static void playerTeleport() {
		HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
		for (String uuid : teams.keySet()) {
			
			List<Integer> tp = MeltdownFiles.getSpawnCoords(main, TeamsManager.getPlayerTeam(uuid));
			
			//Check if npc
			PlayerAI npc = (PlayerAI) NPCManager.getNpcByUUID(uuid);
			if(npc != null) {
				delayedNPCTeleport(npc, tp.get(0), tp.get(1), tp.get(2), tp.get(3), 0);
			} else {
				Player p = Bukkit.getPlayer(UUID.fromString(uuid));
				p.teleport(new Location(Bukkit.getWorlds().get(0), tp.get(0), tp.get(1), tp.get(2), tp.get(3), 0)); 
				
				// Items
				PlayerData.saveItems(p);
				p.getInventory().clear();
			}
			
			//Anti bug -- suppos� useless
			//-->
			List<Integer> truc = new ArrayList<>();
			playerdata.put(uuid, truc);
		}
	}
	
	public static void delayedNPCTeleport(PlayerAI npc, int x, int y, int z, int yaw, int pitch) {
		new BukkitRunnable() {

			@Override
			public void run() {
				//NPCManager.teleportNPC(npc, x, y, z);
				npc.teleport(x, y, z, yaw, pitch);
				System.out.println("TELEPORT NPC");
			}
			
		}.runTaskLater(main, 20);
	}

	public static void playerBegin() {
		HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
		for (String uuid : teams.keySet()) {
			
			//Check for npc
			EntityPlayer npc = NPCManager.getNpcByUUID(uuid);
			if(npc != null) {
				npc.inventory.clear();
				npc.inventory.setItem(0, CraftItemStack.asNMSCopy(getBow()));
				npc.inventory.setItem(1, CraftItemStack.asNMSCopy(getCooldownPickaxe()));
				npc.inventory.setItem(2, CraftItemStack.asNMSCopy(getHeater(TeamsManager.getPlayerTeam(uuid))));
				npc.inventory.setItem(27, CraftItemStack.asNMSCopy(new ItemStack(Material.ARROW)));
				
				regPlayer(npc.getBukkitEntity());
			} else {
				Player p = Bukkit.getPlayer(UUID.fromString(uuid));
				p.setGameMode(GameMode.SURVIVAL);
				p.setInvisible(false);
				
				p.getActivePotionEffects().clear();
	
				p.getInventory().setItem(0, getBow());
	
				p.getInventory().setItem(1, getCooldownPickaxe());
				
				p.getInventory().setItem(2, getHeater(TeamsManager.getPlayerTeam(uuid)));
	
				p.getInventory().setItem(27, new ItemStack(Material.ARROW));
	
				p.getActivePotionEffects().clear();
				
				regPlayer(p);
			}
			
			
			

		}
		Bukkit.broadcastMessage("�6Une partie de �4Meltdown �6vient de commencer !");
		Bukkit.broadcastMessage("�7R�cup�rez l'or des salles et gelez les �quipes adverses, mais faites attention, ce laboratoire est en train de fondre...");
		PlayerData.updateConfig(main);
		gameTimer();
		enable = true;
		main.getConfig().set("game", "meltdown");
		Main.game = "meltdown";
		main.saveDefaultConfig();

		victoryChecker();
	}

	// End game
	public static void disable(Main main) {
		new BukkitRunnable() {

			@Override
			public void run() {
				HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
				for (String uuid : teams.keySet()) {
					
					Player p = Bukkit.getPlayer(UUID.fromString(uuid));
					
					if(p == null) {
						p = NPCManager.getNpcPlayerIfItIs(uuid);
						new BukkitRunnable() {

							@Override
							public void run() {
								//NPCManager.teleportNPC(NPCManager.getNpcByUUID(uuid), 8, 6, 8);
								PlayerAI npc = (PlayerAI) NPCManager.getNpcByUUID(uuid);
								npc.teleport(8, 6, 8, npc.getYaw(), npc.getPitch());
							}
							
						}.runTaskLater(main, 5);
					}
					
					p.setGameMode(GameMode.ADVENTURE);
					p.setInvisible(false);

					p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
					
					p.getInventory().clear();
					
					PlayerData.registerPlayer(p);
					
					
				}
			}
		}.runTaskLater(main, 5);

		playerdata.clear();
		enable = false;
		main.getConfig().set("game", "lobby");
		Main.game = "lobby";
		main.saveDefaultConfig();
		FileConfiguration map = MeltdownFiles.checkMap(main);
		map.set("isMapGenerated", false);
		MeltdownFiles.saveDatas(map, main);
		
		TabManager.updateTabList(); // Display new scores
		
		System.gc();
	}

	public static boolean isEnabled() {
		return enable;
	}
	
	public static boolean canStart() {
		if(TeamsManager.getOnlineTeams().size() < 2) return false;
		
		return true;
	}
	
	public static List<MeltdownNPC> getNPCs() {
		return IAs;
	}
	
	public static void addNPC(MeltdownNPC npc) {
		IAs.add(npc);
	}
	
	public static void removeNPC(MeltdownNPC npc) {
		IAs.remove(npc);
	}
	
	public static void clearNPCs() {
		IAs.clear();
	}

	public static void applyMoneyWon() {
		HashMap<String, Integer> balances = MoneyManager.getRegsPlayer();
		List<String> compensation = new ArrayList<String>();
		for (String uuid : playerdata.keySet()) {

			int gold = balances.get(uuid);
			int meltdown_gold = playerdata.get(uuid).get(2);
			
			//Check if NPS for scoring fn
			if(NPCManager.getNpcByUUID(uuid) != null) {
				PlayerAI npc = (PlayerAI) NPCManager.getNpcByUUID(uuid);
				npc.setScore(npc.getScore() + meltdown_gold);
			}

			// Check is player can get compensation from offline teammate
			String team = TeamsManager.getPlayerTeam(uuid);
			boolean b = TeamsManager.isAllTeammatesOnline(team);
			if (!b) {
				if(meltdown_gold > 0) {
					meltdown_gold = (int) Math.round(meltdown_gold * 1.1);
				} else {
					meltdown_gold = (int) Math.round(meltdown_gold * 0.9);
				}
				if (!compensation.contains(team))
					compensation.add(team);
			}

			gold = gold + meltdown_gold;
			MoneyManager.setPlayerMoney(uuid, gold);
			
			Player p = Bukkit.getPlayer(UUID.fromString(uuid));
			if(p != null) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�6+" + meltdown_gold + " Coins"));
				p.sendMessage("�aYou got " + "�6+" + meltdown_gold + " Coins");
			}
		}

		for (String string : compensation) {
			Bukkit.broadcastMessage(ChatColor.BOLD + "�6L'�quipe �r" + TeamsManager.getTeamColorByTeam(string) + TeamsManager.getTeamNameByTeam(string)
					+ ChatColor.BOLD + "�c a re�u une �acompensation �cpour l'absence d'un ou plusieurs de leurs membres");
		}
	}

	/*
	 * Player data list format : 
	 * Index 0 : is alive (is alive = 1 / death = 0) 
	 * Index 1 : is frozen (is frozen = 1 / not = 0) 
	 * Index 2 : Gold earned 
	 * Index 3 : "Kills" 
	 * Index 4 : "Deaths" 
	 * Index 5 : Freeze Loc X 
	 * Index 6 : Freeze Loc Y
	 * Index 7 : Freeze Loc Z 
	 * Index 8 : Heater Loc X 
	 * Index 9 : Heater Loc Y 
	 * Index 10 : Heater Loc Z 
	 * Index 11 : hasPickaxe 
	 * Index 12: Heater cooldown 
	 * Index 13: Pickaxe cooldown 
	 * Index 14: Unfreeze state
	 * Index 15 : isOnline
	 * Index 16 : isSpectating
	 */

	// Player
	public static void regPlayer(Player p) {
		List<Integer> datas = new ArrayList<Integer>();
		datas.add(0, 1);
		datas.add(1, 0);
		datas.add(2, 0);
		datas.add(3, 0);
		datas.add(4, 0);
		datas.add(5, 0);
		datas.add(6, 0); // LOC "0" mean no loc (bcs 0 is near spawn)
		datas.add(7, 0);
		datas.add(8, 0);
		datas.add(9, 0);
		datas.add(10, 0);
		datas.add(11, 0);
		datas.add(12, 0);
		datas.add(13, 0);
		datas.add(14, 100);
		datas.add(15, 1);
		datas.add(16, 0);
		playerdata.put(p.getUniqueId().toString(), datas);
		
		inViewNPCs.put(p, new ArrayList<MeltdownNPC>());
	}

	public static List<Integer> getRawPlayerDatas(String uuid) {
		return playerdata.get(uuid);
	}
	
	public static void doesPlayerJoined(String playername) {
		
	}

	public static ItemStack getBow() {
		ItemStack item = new ItemStack(Material.BOW); // Freezing gun
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("�bFreezing Gun");
		meta.setLore(Arrays.asList("�7Votre seul moyen d'�liminer les  �quipes adverses",
				"Transforme les ennemis en �bglace"));
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getPickaxe() {
		ItemStack item = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("�6Coin Pickaxe");
		meta.setLore(Arrays.asList("�7C'est votre �6Pioche d'Equipe �7, �cUN SEUL �7membre peut l'avoir en m�me temps",
				"�aAbility �f: �6Mine Coins",
				"�3Cooldown �f: " + pickaxe_cooldown + " secondes"));
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getCooldownPickaxe() {
		ItemStack item = new ItemStack(Material.WOODEN_PICKAXE); // Claiming pickaxe
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("�cClaim Pickaxe");
		meta.setLore(Arrays.asList("�7Clic droit pour obtenir la �6Pioche d'Equipe",
				"�cSeulement UN membre de l'�quipe peut l'avoir en m�me temps",
				"�3Cooldown : �f" + pickaxe_cooldown + " secondes"));
		meta.setUnbreakable(true);
		item.setItemMeta(meta);

		return item;
	}

	// Right Click Event
	@EventHandler
	public static void rightClick(PlayerInteractEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString()))
			return;
		if(e.getClickedBlock() != null) {
			switch(e.getClickedBlock().getType()) {
				case CRIMSON_TRAPDOOR:
					e.setCancelled(true);
				case DISPENSER:
					e.setCancelled(true);
				case DROPPER:
					e.setCancelled(true);
				case WARPED_TRAPDOOR:
					e.setCancelled(true);
			default:
				break;
			}
			if (e.getClickedBlock().getType() == Material.CRIMSON_TRAPDOOR)
			e.setCancelled(true);
		
			// CANCEL "OPENING" Computer
		}
		
		
		
		Player p = e.getPlayer();
		String uid = p.getUniqueId().toString();
		if (p.getInventory().getItemInMainHand() != null) {
			if (p.getInventory().getItemInMainHand().getType() == Material.WOODEN_PICKAXE) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { // Si a pioche bois dans les mains et fais clic droit
					if (playerdata.get(uid).get(13) == 0) { // Si ya pas de cooldown

						p.getInventory().setItem(1, getPickaxe());
						playerdata.get(uid).set(11, 1);
						Player pl;
						String team = TeamsManager.getPlayerTeam(uid);
						for(String mateUID : TeamsManager.getTeamMembers(team)) {
							pl = Bukkit.getPlayer(UUID.fromString(mateUID));
							if(pl == null) pl = NPCManager.getNpcPlayerIfItIs(mateUID);
							if(pl == null) continue; // offline
                            playerdata.get(mateUID).set(13,pickaxe_cooldown);
                            pickaxeCooldown(pl);
                            if (pl != p) {
                                pl.getInventory().setItem(1, getCooldownPickaxe());
                                playerdata.get(uid).set(11, 0);
                            }
						}
					} else { // SI ya du cooldown
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
								"�cLa pioche est en cooldown: " + playerdata.get(p.getUniqueId().toString()).get(13) + " secondes"));
					}
				}
			}
		}
	}
	
	public static void getPickaxeForNPC(MeltdownNPC npc) {
		String uuid = npc.getNPC().getUniqueIDString();
		
		if(playerdata.get(uuid).get(11) == 0 && playerdata.get(uuid).get(13) == 0) {
			playerdata.get(uuid).set(11, 1);
			
			PlayerAI npcAI = (PlayerAI) npc.getNPC();
			
			Location loc = new Location(Bukkit.getWorlds().get(0), npcAI.getX(), npcAI.getY(), npcAI.getZ());
			if(npc.getEnvironnement().findNearestBlock(loc, Material.GOLD_BLOCK, 3) != null) {
				npcAI.setScore(npcAI.getScore() + 1);
			}
			
			for(String mateUID : TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(uuid))) {
				if(mateUID != uuid) {
					playerdata.get(mateUID).set(13, pickaxe_cooldown);
				}
			}
		}
	}

	public static void pickaxeCooldown(Player p) {
		new BukkitRunnable() {
			int cd = pickaxe_cooldown;
			String uuid = p.getUniqueId().toString();

			@Override
			public void run() {
				if (cd == 0) {
					playerdata.get(uuid).set(13, 0);
					cancel();
					return;
				}
				playerdata.get(uuid).set(13, cd);
				cd--;
			}
		}.runTaskTimer(main, 20, 20);
	}

	// Cancel item dropping
	@EventHandler
	public static void cancelDrop(PlayerDropItemEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString()))
			return;
		e.setCancelled(true);
	}

	// Cancel block breaking
	@EventHandler
	public static void cancelBreaking(BlockBreakEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString()))
			return;
		Player p = e.getPlayer();
		Material b = e.getBlock().getType();
		List<Material> heaters = new ArrayList<Material>();
		heaters.add(Material.RED_TERRACOTTA);
		heaters.add(Material.BLUE_TERRACOTTA);
		heaters.add(Material.GREEN_TERRACOTTA);
		heaters.add(Material.YELLOW_TERRACOTTA);
		heaters.add(Material.PURPLE_TERRACOTTA);
		heaters.add(Material.LIGHT_BLUE_TERRACOTTA);
		heaters.add(Material.BLACK_TERRACOTTA);
		heaters.add(Material.ORANGE_TERRACOTTA);
		heaters.add(Material.GOLD_BLOCK);
		if (!heaters.contains(b)) { // SI LA LISTE NE CONTIENT PAS LE BLOCK CASSE ALORS CANCEL
			e.setCancelled(true);
			return;
		}
		if (b == Material.GOLD_BLOCK) { // SI BLOCK = OR
			if (p.getInventory().getItemInMainHand().getType() != Material.IRON_PICKAXE) { // Si mine le heater a la
																							// main --> No problem, Si
																							// mine or a la main -->
																							// Nope
				e.setCancelled(true);
				return;
			}
			List<Integer> data = playerdata.get(e.getPlayer().getUniqueId().toString());
			int bal = data.get(2);
			int amount = money_per_block;
			bal = bal + amount;
			data.set(2, bal);
			if(amount < 0) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�6" + amount + " Coins"));
			} else {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�6+" + amount + " Coins"));
			}

			return;
		} else { // SI BLOCK = HEATER
			whatHeater(e.getBlock().getLocation());
		}
	}

	public static void whatHeater(Location loc) { //A RECODER
		for (String uuid : playerdata.keySet()) {
			if(playerdata.get(uuid).get(8) != 0) { //Si la valeur Heater X n'est pas nulle <=> ce joueur a plac� un heater
				if(playerdata.get(uuid).get(8) == loc.getX() && playerdata.get(uuid).get(9) == loc.getY() && playerdata.get(uuid).get(10) == loc.getZ()){ //Si heater cass� est celui du joueur qui l'a cass�
					Player p = Bukkit.getPlayer(UUID.fromString(uuid));
					if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
					if(p == null) return;
					p.getInventory().addItem(getHeater(TeamsManager.getPlayerTeam(uuid)));
					playerdata.get(uuid).set(12, heater_cooldwon);
					playerdata.get(uuid).set(8, 0);
					playerdata.get(uuid).set(9, 0);
					playerdata.get(uuid).set(10, 0);
					heaterCooldown(p);
					loc.getBlock().setType(Material.AIR);
				}
			} //Si le joueur n'a pas plac� de heater -> skip
		}

	}

	public static ItemStack getHeater(String team) {
		ItemStack item;
		ItemMeta meta;
		ChatColor c;
		switch (team) {
		case "red":
			item = new ItemStack(Material.RED_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "blue":
			item = new ItemStack(Material.BLUE_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "green":
			item = new ItemStack(Material.GREEN_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "yellow":
			item = new ItemStack(Material.YELLOW_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "purple":
			item = new ItemStack(Material.PURPLE_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "aqua":
			item = new ItemStack(Material.LIGHT_BLUE_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "black":
			item = new ItemStack(Material.BLACK_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		case "orange":
			item = new ItemStack(Material.ORANGE_TERRACOTTA);
			c = TeamsManager.getTeamColorByTeam(team);
			break;
		default:
			item = new ItemStack(Material.BARRIER);
			item.getItemMeta().setDisplayName("�4Error Heater...");
			item.getItemMeta().setLore(Arrays.asList("�fSomething went wrong"));
			return item;
		}
		meta = item.getItemMeta();
		meta.setDisplayName(c + "Heater");
		meta.setLore(Arrays.asList("�7�oLe heater permet de �r�6d�congeler �7�ovos �quipiers",
				"�4Attention : �7�oIls peuvent �tre casser par l'�quipe adverse"));
		item.setItemMeta(meta);

		return item;
	}

	public static void heaterCooldown(Player p) {
		new BukkitRunnable() {
			int cd = heater_cooldwon;
			String uuid = p.getUniqueId().toString();

			@Override
			public void run() {
				if(!enable) {
					cancel();
					return;
				}
				if (cd == 0) {
					List<Integer> datas = playerdata.get(uuid);
					if(datas != null) {
						playerdata.get(uuid).set(12, 0);
					}
					cancel();
					return;
				}
				playerdata.get(uuid).set(12, cd);
				cd--;
			}
		}.runTaskTimer(main, 20, 20);
	}

	// Cancel block placing && Heater placing
	@EventHandler
	public static void cancelPlacing(BlockPlaceEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString()))
			return;
		List<Material> heaters = new ArrayList<Material>();
		heaters.add(Material.RED_TERRACOTTA);
		heaters.add(Material.BLUE_TERRACOTTA);
		heaters.add(Material.GREEN_TERRACOTTA);
		heaters.add(Material.YELLOW_TERRACOTTA);
		heaters.add(Material.PURPLE_TERRACOTTA);
		heaters.add(Material.LIGHT_BLUE_TERRACOTTA);
		heaters.add(Material.BLACK_TERRACOTTA);
		heaters.add(Material.ORANGE_TERRACOTTA);

		if (!heaters.contains(e.getBlock().getType())) {
			e.setCancelled(true);
			return;
		}
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		if(playerdata.get(uuid).get(1) == 1) {
			e.setCancelled(true);
			return;
		}
		if (playerdata.get(uuid).get(12) > 0) {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�cYour heater is on cooldown: " + playerdata.get(uuid).get(12) + "s"));
			e.setCancelled(true);
			return;
		} else { // SI LE HEATER PEUT ETRE PLACE ALORS
			Block b = e.getBlock();
			Location loc = b.getLocation();
			
			//Check si pas plac� dans zone melted
			List<Location> banned = MDMap.getBannedLocs();
			if(banned.contains(loc)) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�cYou cannot place your heater on a melted place ! >:("));
				e.setCancelled(true);
				return;
			}
			
			Material bt = b.getType();
			playerdata.get(uuid).set(8, loc.getBlockX());
			playerdata.get(uuid).set(9, loc.getBlockY());
			playerdata.get(uuid).set(10, loc.getBlockZ());

			// Particles
			particleEffect(loc, bt, b);
		}
	}
	
	public static Material getHeaterForTeam(String team) {
		switch(team) {
		case "red": return Material.RED_TERRACOTTA;
		case "blue": return Material.BLUE_TERRACOTTA;
		case "green": return Material.GREEN_TERRACOTTA;
		case "yellow": return Material.YELLOW_TERRACOTTA;
		case "purple": return Material.PURPLE_TERRACOTTA;
		case "aqua": return Material.LIGHT_BLUE_TERRACOTTA;
		case "black": return Material.BLACK_TERRACOTTA;
		case "orange": return Material.ORANGE_TERRACOTTA;
		default: return Material.AIR;
		}
	}
	
	public static void placeHeaterForNpc(MeltdownNPC npc) {
		String uuid = npc.getNPC().getUniqueIDString();
		List<Integer> datas = playerdata.get(uuid);
		
		List<Location> banned = MDMap.getBannedLocs();

		Ray ray = new Ray(npc.getNPC().getBukkitEntity().getWorld(), npc.getNPC().getLoc().toVector(), npc.getNPC().getLoc().getDirection());
		//Block block = npc.getNPC().getBukkitEntity().getTargetBlock(null, 5);
		Block block = ray.getTargetedBlock(5);
		
		System.out.println("Targeted block: " + block.getType() + " at " + block.getX() + " " + block.getY() + " " + block.getZ());
		
		if(Environnement.getTransparentBlocks().contains(block.getType())) return;
		Location loc = block.getLocation().add(0, 1, 0);
		if(loc.getBlock().getType() != Material.AIR) return;
		
		if(datas.get(0)== 1 && datas.get(1) == 0 && datas.get(8) == 0 && datas.get(12) == 0 && !banned.contains(loc)) { // Peut placer le heater
			playerdata.get(uuid).set(8, loc.getBlockX());
			playerdata.get(uuid).set(9, loc.getBlockY());
			playerdata.get(uuid).set(10, loc.getBlockZ());

			loc.getBlock().setType(getHeaterForTeam(TeamsManager.getPlayerTeam(npc.getNPC().getUniqueIDString())));
			
			// Particles
			particleEffect(loc, loc.getBlock().getType(), loc.getBlock());
		}
	}

	public static void particleEffect(Location loc, Material bt, Block b) {
		Location cloc = loc.clone();
		cloc.add(3.5, 0.5, 0.5);

		Location ploc = loc.clone();
		ploc.add(0.5, 0.5, 0.5);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (bt != b.getType())
					System.out.println("Heater removed at " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
					cancel();
				if (!enable) {
					cancel();
					return;
				}
					
					
				// Particle radius
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(-1, 0, 2);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(-2, 0, 1);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(-2, 0, -1);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(-1, 0, -2);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(1, 0, -2);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(2, 0, -1);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(2, 0, 1);
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, cloc, 4, 0.05, 0.1, 0.05, -0.0005);
				cloc.add(1, 0, 2);

				// Around the heater
				Bukkit.getWorlds().get(0).spawnParticle(Particle.FLAME, ploc, 8, 0.3, 0.1, 0.3, -0.0005);

			}
		}.runTaskTimer(main, 0, 4);
	}

	/*
	 * Used blocks* Gold : Coins <Teams color> Terracotta : Heaters Crimson trapdoor
	 * : alarm Dispenser & Dropper : Computer
	 */

	// Cancel Item Frames breaking
	@EventHandler
	public static void cancelFrameBreaking(HangingBreakByEntityEvent e) {
		if (!enable)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public static void rightClickEntity(PlayerInteractAtEntityEvent e) {
		if (enable != true)
			return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString()))
			return;
		if (e.getRightClicked() instanceof ItemFrame) {
			e.setCancelled(true);
		}
		if (e.getRightClicked() instanceof TrapDoor) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public static void tryToShoot(ProjectileLaunchEvent e) {
		if (!enable)
			return;
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			String uuid = p.getUniqueId().toString();
			if (!playerdata.containsKey(uuid))
				return;
			if (playerdata.get(uuid).get(1).equals(1)) {
				e.setCancelled(true);
			}
		}
	}

	// Shoot with Freezing Gun
	@EventHandler
	public static void onShoot(EntityDamageByEntityEvent e) {
		
		if (!enable)
			return;

		if (!(e.getDamager() instanceof Arrow))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player))
			return;
		
		String uuid = ((Entity) arrow.getShooter()).getUniqueId().toString();
		if(NPCManager.getNpcByUUID(uuid) != null) return; // Not a player

		e.setDamage(0);

		Player shooter = (Player) arrow.getShooter();
		Player p = (Player) e.getEntity();

		shootingChecker(shooter.getUniqueId().toString(), p.getUniqueId().toString());
	}
	
	//Check if player shot is NPC
	@EventHandler
	public static void onNPCShoot(ProjectileHitEvent e) {
		if(!enable) return;
		if(!(e.getEntity() instanceof Arrow)) return;
		if(e.getHitEntity() == null) return;
		
		if(!(e.getHitEntity() instanceof Player)) return;
		if(!(e.getEntity().getShooter() instanceof Player)) return;
		
		Player shooter = (Player) e.getEntity().getShooter();
		Player victim = (Player) e.getHitEntity();

		e.getEntity().remove();
		
		shootingChecker(shooter.getUniqueId().toString(), victim.getUniqueId().toString());
		
	}
	
	
	//Shooting checker
	public static void shootingChecker(String shooterUUID, String shotUUID) {
		if (!playerdata.containsKey(shooterUUID))
			return;
		if (!playerdata.containsKey(shotUUID))
			return;
		if (TeamsManager.getPlayerTeam(shooterUUID).equals(TeamsManager.getPlayerTeam(shotUUID)))
			return;
		
		HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
		
		List<Integer> shooterData = playerdata.get(shooterUUID);
		List<Integer> pData = playerdata.get(shotUUID);

		int sgold = shooterData.get(2);
		int skills = shooterData.get(3);
		sgold = sgold + money_per_kill;
		skills = skills + 1;
		shooterData.set(3, skills);
		
		Player shooter = Bukkit.getPlayer(shooterUUID);
		if(shooter == null) shooter = NPCManager.getNpcPlayerIfItIs(shooterUUID);
		shooter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�4KILL �6+" + money_per_kill + " Coins"));

		int pgold = pData.get(2);
		int pdeaths = pData.get(4);
		pgold = pgold + money_per_death;
		pdeaths = pdeaths + 1;
		pData.set(2, pgold);
		pData.set(4, pdeaths);
		pData.set(1, 1);
		
		iceCube(shotUUID);
		
		Player p = Bukkit.getPlayer(UUID.fromString(shotUUID));
		if(p == null) p = NPCManager.getNpcPlayerIfItIs(shotUUID);
		
		Bukkit.broadcastMessage(TeamsManager.getTeamColor(shotUUID) + p.getName() + " �fwas frozen by "
				+ TeamsManager.getTeamColor(shooterUUID) + shooter.getName());

		String team = teams.get(shotUUID); // renvoie l'equipe (ex. "red")
		List<String> alive_mates = new ArrayList<String>(); // Va resenser tous les teammates en vie
		List<String> dead_mates = new ArrayList<String>(); // Va resenser tous les teammates hors jeu
		for (String player : teams.keySet()) {
			if (!player.equals(shotUUID)) {
				if (teams.get(player).equals(team)) {
					if (!playerdata.get(player).get(1).equals(1)) {
						alive_mates.add(player); // Si le team mate n'est pas mort ca l'ajoute � cette liste

					} else { // Si le mate est aussi hors jeu
						dead_mates.add(player);
					}
				}
			}
		}
		if (alive_mates.isEmpty()) { // si aucun des mates est en vie alors �limination

			playerdata.get(shotUUID).set(0, 0); // Elimine dans la var playerdata tt la team
			// p.setGameMode(GameMode.SPECTATOR); //Mets la team en spec
			p.setInvisible(true);
			for (String mates : dead_mates) {
				playerdata.get(mates).set(0, 0);
				playerdata.get(mates).set(1, 1);
				// Bukkit.getPlayer(mates).setGameMode(GameMode.SPECTATOR);
				Player mate = Bukkit.getPlayer(UUID.fromString(mates));
				if(mate == null) mate = NPCManager.getNpcPlayerIfItIs(mates);
				mate.setInvisible(true);
			}

			sgold = sgold + money_per_final;
			pgold += money_for_eliminated;
			Bukkit.broadcastMessage("�6L'�quipe des " + TeamsManager.getTeamColor(shotUUID)
					+ TeamsManager.getTeamName(shotUUID) + " �6a �t� �c�limin�");

		}
		shooterData.set(2, sgold);
		pData.set(2, pgold);
		teamsChecker(shooter.getName());
	}

	// Check teams left

	public static void victoryChecker() {
		new BukkitRunnable() {

			int fall_height = MeltdownFiles.getFallHeight(main);
			
			@Override
			public void run() {
				if (!enable) {
					cancel();
					return;
				}

				List<String> teams = new ArrayList<>();
				boolean anotherTeam = false;
				for (String uuid : playerdata.keySet()) {
					List<Integer> pData = playerdata.get(uuid);
					
					Player pla;
					
					if(NPCManager.getNpcPlayerIfItIs(uuid) != null) {
						pla = NPCManager.getNpcPlayerIfItIs(uuid);
					} else {
						pla = Bukkit.getPlayer(UUID.fromString(uuid));
					}
					
					
					if(pla == null) {
						// well il parait que != bug donc dans le doute...
					} else
					if(pla.getLocation().getY() <= fall_height && pData.get(0) != 0) {
						
						playerFallen(pData, uuid);

						//Unset on fire
						pla.setFireTicks(0);
						Bukkit.broadcastMessage(TeamsManager.getTeamColor(uuid) + " " + pla.getName() + " �ffell in �clava");
						
						if(NPCManager.getNpcByUUID(uuid) == null) { // Si c'est un NPC il spectate pas
							List<String> teamates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(uuid));
						
							Spectate.setSpectatingGroup(pla, teamates);
						
							infiniteDeathMessage(pla);
						}
							
						
					}
					
					if (playerdata.get(uuid).get(1).equals(0) || playerdata.get(uuid).get(0).equals(1)) {
						teams.add(TeamsManager.getPlayerTeam(uuid));
					}
				}
				if (teams.isEmpty()) {
					System.out.println("�cAn error happend in the meltdown game\n No winners found");
					applyMoneyWon(); // Still we apply the money
					disable(main); // Stop the game cause error
				}
				String refTeam = teams.get(0);
				for (String team : teams) {
					if (refTeam != team)
						anotherTeam = true;
				}
				if (anotherTeam == false) { // si yen a pas alors c'est fin de game
					Bukkit.broadcastMessage("�6L'�quipe des " + TeamsManager.getTeamColorByTeam(refTeam)
							+ TeamsManager.getTeamNameByTeam(refTeam) + " �6a gagn� !");
					
					for(String uuid : playerdata.keySet()) {
						if(playerdata.get(uuid).get(0).equals(1) && playerdata.get(uuid).get(0).equals(0)) { // Prolly the last players alive
							for(MeltdownNPC npc : Meltdown.IAs) {
								if(npc.getNPC().getUniqueIDString().equals(uuid)) {
									npc.getNPC().setScore(npc.getNPC().getScore() + 50);
								}
							}
						}
					}
					
					applyMoneyWon();
					disable(main);
				}
			}
		}.runTaskTimer(main, 0, 20);

	}
	
	public static void playerFallen(List<Integer> pData, String uuid) {
		int pgold = pData.get(2);
		int pdeaths = pData.get(4);
		pgold = pgold - money_for_eliminated;
		pdeaths = pdeaths + 1;
		pData.set(2, pgold);
		pData.set(4, pdeaths);
		pData.set(0, 0);
		pData.set(1, 1);
		pData.set(16, 1);
			
		
		playerdata.put(uuid, pData);
	}

	public static void teamsChecker(String player) {

		String refTeam = TeamsManager.getPlayerTeam(player);
		boolean anotherTeam = false;
		for (String pl : playerdata.keySet()) { // Check si ya une autre team
			if (playerdata.get(pl).get(1).equals(0)) { //Sii le joueur n'est pas gel�
				if (refTeam != TeamsManager.getPlayerTeam(pl)) {
					anotherTeam = true;
				}
			} else { //Spectate team members
				if(playerdata.get(pl).get(16).equals(0)) { //Si il est gel� mais ne spectate pas
					
					List<String> teamates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(pl));
					
					Player p = Bukkit.getPlayer(UUID.fromString(pl));
					
					if(p != null) { // If it is an NPC we don't care about specating
						
						Spectate.setSpectatingGroup(p, teamates);
						infiniteDeathMessage(p);
					}
					
				}
			}
		}
		if (anotherTeam == false) { // si yen a pas alors c'est fin de game
			Bukkit.broadcastMessage("�6L'�quipe des " + TeamsManager.getTeamColor(player)
					+ TeamsManager.getTeamName(player) + " �6a gagn� !");
			

			for(MeltdownNPC npc : Meltdown.IAs) {
				if(npc.getNPC().getUniqueIDString().equals(player)) {
					npc.getNPC().setScore(npc.getNPC().getScore() + 50);
				}
			}
			applyMoneyWon();
			disable(main);
		}
	}
	
	public static void infiniteDeathMessage(Player p) {
		new BukkitRunnable() {
			TextComponent txt = new TextComponent("�cYou are currently spectating...");
			String name;
			@Override
			public void run() {
				
				//Check
				if(!enable || p == null) {
					cancel();
					return;
				}
				if(playerdata.get(p.getUniqueId().toString()).get(16) == 0) {
					cancel();
					return;
				}
				
				//Execute
				name = p.getSpectatorTarget().getName();
				txt = new TextComponent("�cYou are currently spectating " + TeamsManager.getTeamColor(name) + name + "  �7Press TAB to change View");
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
				
			}
		}.runTaskTimer(main, 0, 20);
	}

	
	// Animation (frozen dudes)
	public static void iceCube(String uuid) {
		Player p = Bukkit.getPlayer(UUID.fromString(uuid));
		
		if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
		
		Location loc = p.getLocation();
		p.setGameMode(GameMode.SPECTATOR);

		List<Integer> ls = playerdata.get(uuid);
		ls.set(1, 1);
		ls.set(5, loc.getBlockX());
		ls.set(6, loc.getBlockY());
		ls.set(7, loc.getBlockZ());
		ls.set(14, 0);
		playerdata.put(uuid, ls);

		loc.getBlock().setType(Material.PACKED_ICE);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.PACKED_ICE);
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
				new TextComponent("�3Vous �tes d�gel�s � �b" + playerdata.get(uuid).get(14) + " �3%"));
		unfreezeProcess(main, uuid);
	}
	
	public static void unfreezeProcess(Main main, String uuid) {
		List<Integer> ls = playerdata.get(uuid);
		String team = TeamsManager.getPlayerTeam(uuid);
		
		new BukkitRunnable() {
			int HX, HY, HZ, X, Y, Z;
			Location Hloc;
			Location loc;
			
			int state = 0;
			
			@Override
			public void run() {
				
				if(!enable) {
					cancel();
					return;
				}
				
				if(state == 100) {
					unFreeze(uuid);
					cancel();
					return;
				}
				for(String player : playerdata.keySet()) {
					if(playerdata.get(player) != null) {
						if(TeamsManager.getPlayerTeam(player) == team) {
							//Playr loc
							X = playerdata.get(uuid).get(5);
							Y = playerdata.get(uuid).get(6);
							Z = playerdata.get(uuid).get(7);
							loc = new Location(Bukkit.getWorlds().get(0), X, Y, Z);
							
							//Heater loc
							HX = playerdata.get(player).get(8); // No need to check if is = to 0 bcs if it is the case, distance will be greater than 3
							HY = playerdata.get(player).get(9);
							HZ = playerdata.get(player).get(10);
							
							Hloc = new Location(Bukkit.getWorlds().get(0), HX, HY, HZ);
							
							if(Hloc.distance(loc) <= 3) {
								state++;
								ls.set(14, state);
								Player p = Bukkit.getPlayer(UUID.fromString(uuid));
								if(p != null) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�3Vous �tes d�gel�s � �b" + state + " �3%")); // != null pr les NPC on sait jamais
							}
						}
					}
				}
			}
		}.runTaskTimer(main, 20, 4);
	}

	public static void unFreeze(String uuid) {

		List<Integer> ls = playerdata.get(uuid);
		int X = ls.get(5);
		int Y = ls.get(6);
		int Z = ls.get(7);
		Location loc = new Location(Bukkit.getWorlds().get(0), X, Y, Z);
		
		Player p = Bukkit.getPlayer(UUID.fromString(uuid));
		if(p == null) {
			p = NPCManager.getNpcPlayerIfItIs(uuid);
			PlayerAI npc = (PlayerAI) NPCManager.getNpcByUUID(uuid);
			npc.teleport(X, Y, Z, npc.getYaw(), npc.getPitch());
		}

		p.teleport(loc);
		loc.getBlock().setType(Material.AIR);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.AIR);

		p.setInvisible(false);
		p.setGameMode(GameMode.SURVIVAL);

		ls.set(1, 0);
		ls.set(5, 0);
		ls.set(6, 0);
		ls.set(7, 0);
		ls.set(16, 0);
		playerdata.put(uuid, ls);
	}
	
	//Change de personne � spectate
	/*
	@EventHandler
	public static void changeView(PlayerInteractEvent e) {
		if (!enable) return;
		if (!playerdata.containsKey(e.getPlayer().getUniqueId().toString())) return;
		
		Player p = e.getPlayer();
		if(playerdata.get(p.UniqueId.toString()).get(1) != 1) return;
		
		List<String> mates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(p.getUniqueId().toString()));
		mates.remove(p.getUniqueId().toString());
		for (String dude : mates) {
			if(playerdata.get(dude).get(1).equals(1)) mates.remove(dude);
		}
		
		String current = p.getSpectatorTarget().getUniqueId().toString();
		
		if(mates.indexOf(current) == mates.size() - 1) {
			current = mates.get(0);
		} else {
			current = mates.get(mates.indexOf(current) + 1);
		}
		p.setSpectatorTarget(Bukkit.getPlayer(current));
	}*/

	public static void gameTimer() { //Time counter --> Trigger l'ouverture des portes par exemples
		new BukkitRunnable() {
			
			List<Integer> room_times = MeltdownFiles.getRoomTimes(main); //R�cup�re les temps de d�clenchement des alarmes
			List<Integer> room_coordsA = MeltdownFiles.getRoomCoords(main, "A");
			List<Integer> room_coordsB = MeltdownFiles.getRoomCoords(main, "B");
			List<Integer> room_coordsC = MeltdownFiles.getRoomCoords(main, "C");
			List<Integer> room_coordsD = MeltdownFiles.getRoomCoords(main, "D");
			List<Integer> room_coordsE = MeltdownFiles.getRoomCoords(main, "E");
			List<Integer> room_coordsM = MeltdownFiles.getRoomCoords(main, "M");
			
			int alarm_tA = room_times.get(0); //R�cup�re le temps A
			int alarm_tB = room_times.get(1); //R�cup�re le temps B
			int alarm_tC = room_times.get(2); //R�cup�re le temps C
			int alarm_tD = room_times.get(3); //R�cup�re le temps D
			int alarm_tE = room_times.get(4); //R�cup�re le temps E
			int alarm_tM = room_times.get(5); //R�cup�re le temps M
			
			
			List<Integer> door_times = MeltdownFiles.getDoorTimes(main);
			
			int door1 = door_times.get(0);
			int door2 = door_times.get(1); 
			int door3 = door_times.get(2);
			int door4 = door_times.get(3);
			int door5 = door_times.get(4);
			
			
			//List<Integer> door_coords = MeltdownFiles.getDoorCoords(main, 1);
			
			String time;
			
			@Override
			public void run() {
				if(!enable) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.setPlayerListFooter("");
					}
					cancel();
					return;
				}
				
				//Alarm
			    if(counter == alarm_tA - 60) MDMap.alarmTrigger(room_coordsA);
			    if(counter == alarm_tB - 60) MDMap.alarmTrigger(room_coordsB);
			    if(counter == alarm_tC - 60) MDMap.alarmTrigger(room_coordsC);
			    if(counter == alarm_tD - 60) MDMap.alarmTrigger(room_coordsD);
			    if(counter == alarm_tE - 60) MDMap.alarmTrigger(room_coordsE);
			    if(counter == alarm_tM - 60) MDMap.alarmTrigger(room_coordsM);
			    
			    //Room
			    if(counter == alarm_tA) MDMap.roomTrigger(room_coordsA, main);
			    if(counter == alarm_tB) MDMap.roomTrigger(room_coordsB, main);
			    if(counter == alarm_tC) MDMap.roomTrigger(room_coordsC, main);
			    if(counter == alarm_tD) MDMap.roomTrigger(room_coordsD, main);
			    if(counter == alarm_tE) MDMap.roomTrigger(room_coordsE, main);
			    if(counter == alarm_tM) MDMap.roomTrigger(room_coordsM, main);
			    
			    //Door
			    if(counter == door1) MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 1));
			    if(counter == door2) MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 2));
			    if(counter == door3) MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 3));
			    if(counter == door4) MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 4));
			    if(counter == door5) MDMap.doorTrigger(MeltdownFiles.getDoorCoords(main, 5));
			    
			    
				counter++;
				time = TimeFormater.getFormatedTime(counter);
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(playerdata.containsKey(p.getUniqueId().toString())) {
						p.setPlayerListFooter(
							 "�f----------------------------\n"
							+ "�3Mode de jeu �f: �4Meltdown\n"
							+ "�6Coins : " + playerdata.get(p.getUniqueId().toString()).get(2) + "\n"
							+ "�cTemps pass� �f: " + time); //Affiche depuis cb de temps la partie � commenc�e
						
						
						// Handle NPC re teleport when close enought to player (<128)
						for(MeltdownNPC npc : IAs) {
							
							Location loc = new Location(p.getWorld(), npc.getNPC().locX(), npc.getNPC().locY(), npc.getNPC().locZ());
							double distance = p.getLocation().distance(loc);
							if(distance < 128 && !inViewNPCs.get(p).contains(npc)) {
								List<MeltdownNPC> npcs = inViewNPCs.get(p);
								npcs.add(npc);
								inViewNPCs.put(p, npcs);
								
								NPCManager.showNpcWithoutTabFor(npc.getNPC(), p, null);

							}
						}
					}
					
				}
				for(MeltdownNPC npc : IAs) {
					//Reward
					if(counter%30 == 0 && playerdata.get(npc.getNPC().getUniqueIDString()).get(0) == 1 && playerdata.get(npc.getNPC().getUniqueIDString()).get(1) == 0) { // Toutes les 30s
						npc.getNPC().setScore(npc.getNPC().getScore() + 1);
					}
				}
				
			}
		}.runTaskTimer(main, 0, 20);
	}
	
	//Handle In game disconnexion
	@EventHandler
	public static void onDisconnect(PlayerQuitEvent e) {
		if(!isEnabled()) return;
		Player p = e.getPlayer();
		String name = p.getName();
		String uuid = p.getUniqueId().toString();
		if(!playerdata.containsKey(uuid)) return;
		
		playerdata.get(uuid).set(15, 0); //Enregistrer le joueur comme d�connect�
		
		String team = TeamsManager.getPlayerTeam(uuid);
		for (String member : TeamsManager.getTeamMembers(team)) {
			Player mb = Bukkit.getPlayer(UUID.fromString(member));
			if(mb != null) {
				mb.sendMessage(TeamsManager.getTeamColorByTeam(team) + name + " s'est d�connect�, il a 3 minutes pour revenir avant d'�tre �limin�");
			}
		}
		new BukkitRunnable() {
			
			int counter = 0;
			
			@Override
			public void run() {
				List<Integer> datas = playerdata.get(uuid);
				if(!enable) return;
				
				if(datas != null) {
					if(datas.get(15) == 1) {
						cancel();
						return;
					}
				}
				if(counter == 30) {
					playerdata.get(uuid).set(0, 0);
					Bukkit.broadcastMessage("�fLe joueur " + TeamsManager.getTeamColor(name) + name + "�fa �t� �limin� car il s'est d�connect�");
					cancel();
					return;
				}
				counter++;
			}
		}.runTaskTimer(main, 0, 20);
	}
	
	
	//Handle reconnexion
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		if(!isEnabled()) return;
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		if(!playerdata.containsKey(uuid)) {
			if(TeamsManager.getPlayerTeam(uuid) != "none") { //Si le joueur estenregistr� dans une �quipe et qu'il a pas e playerdata (= crash avant d'en avoir)
				
				//PlayerBegin() et PlayerTeleport()
				
				List<Integer> tp = MeltdownFiles.getSpawnCoords(main, TeamsManager.getPlayerTeam(uuid));
				p.teleport(new Location(Bukkit.getWorlds().get(0), tp.get(0), tp.get(1), tp.get(2), tp.get(3), 0));
				
				//Anti bug -- suppos� useless
				//-->
				List<Integer> truc = new ArrayList<>();
				playerdata.put(uuid, truc);
				
				p.setGameMode(GameMode.SURVIVAL);
				p.setInvisible(false);

				// Items
				PlayerData.saveItems(p);
				p.getInventory().clear();
				
				p.getActivePotionEffects().clear();

				p.getInventory().setItem(0, getBow());

				p.getInventory().setItem(1, getCooldownPickaxe());
				
				p.getInventory().setItem(2, getHeater(TeamsManager.getPlayerTeam(uuid)));

				p.getInventory().setItem(27, new ItemStack(Material.ARROW));

				p.getActivePotionEffects().clear();
				
				regPlayer(p);
			}
		} else {
			playerdata.get(uuid).set(15, 1); //Enregistrer le joueur comme reconnect�
		}
	}
}
