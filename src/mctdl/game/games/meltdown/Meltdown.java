package mctdl.game.games.meltdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import mctdl.game.money.MoneyManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Spectate;
import mctdl.game.utils.TimeFormater;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Meltdown implements Listener {

	static boolean enable = false;
	static Main main;
	//static FileConfiguration data;

	public Meltdown(Main main) {
		Meltdown.main = main;
	}

	// Setup
	static HashMap<String, List<Integer>> playerdata = new HashMap<String, List<Integer>>();

	public static void enable() {
		boolean isMapGenerated = MeltdownFiles.isMapGenerated(main);
		if (isMapGenerated == false) {
			Bukkit.broadcastMessage("§aThe game will start soon...");
			Bukkit.broadcastMessage("§fNote : Les §6Coins §fque vous obtennez pendant la partie sont affichés dans la §atablist");
			MDMap.generateMap(main);
			new BukkitRunnable() {
				int i = 0;

				@Override
				public void run() {
					if (i == 5)
						Bukkit.broadcastMessage("§fThe game will start in 5 seconds");
					if (i == 7) {
						Bukkit.broadcastMessage("§aThe game will start in 3 seconds");
						playerTeleport();
					}
					if (i == 8)
						Bukkit.broadcastMessage("§3The game will start in 2 seconds");
					if (i == 9)
						Bukkit.broadcastMessage("§5The game will start in 1 seconds");
					if (i == 10) {
						Bukkit.broadcastMessage("§6The game will start...");
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
			Player p = Bukkit.getPlayer(uuid);
			List<Integer> tp = MeltdownFiles.getSpawnCoords(main, TeamsManager.getPlayerTeam(uuid));
			p.teleport(new Location(Bukkit.getWorlds().get(0), tp.get(0), tp.get(1), tp.get(2), tp.get(3), 0));
			
			//Anti bug -- supposé useless
			//-->
			List<Integer> truc = new ArrayList<>();
			playerdata.put(uuid, truc);
			
			// Items
			PlayerData.saveItems(p);
			p.getInventory().clear();
		}
	}

	public static void playerBegin() {
		HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
		for (String player : teams.keySet()) {
			Player p = Bukkit.getPlayer(player);
			p.setGameMode(GameMode.SURVIVAL);
			p.setInvisible(false);
			
			p.getActivePotionEffects().clear();

			p.getInventory().setItem(0, getBow());

			p.getInventory().setItem(1, getCooldownPickaxe());
			
			p.getInventory().setItem(2, getHeater(TeamsManager.getPlayerTeam(player)));

			p.getInventory().setItem(27, new ItemStack(Material.ARROW));

			p.getActivePotionEffects().clear();
			
			regPlayer(p);

		}
		Bukkit.broadcastMessage("§6Une partie de §4Meltdown §6vient de commencer !");
		Bukkit.broadcastMessage("§7Récupérez l'or des salles et gelez les équipes adverses, mais faites attention, ce laboratoire est en train de fondre...");
		PlayerData.updateConfig(main);
		gameTimer();
		enable = true;
		main.getConfig().set("game", "meltdown");
		main.saveDefaultConfig();

		victoryChecker();
	}

	// End game
	public static void disable(Main main) {
		new BukkitRunnable() {

			@Override
			public void run() {
				HashMap<String, String> teams = TeamsManager.getOnlinePlayers();
				for (String player : teams.keySet()) {
					Player p = Bukkit.getPlayer(player);
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
		main.saveDefaultConfig();
		FileConfiguration map = MeltdownFiles.checkMap(main);
		map.set("isMapGenerated", false);
		MeltdownFiles.saveDatas(map, main);
	}

	public static boolean isEnabled() {
		return enable;
	}

	public static void applyMoneyWon() {
		HashMap<String, Integer> balances = MoneyManager.getRegsPlayer();
		List<String> compensation = new ArrayList<String>();
		for (String uuid : playerdata.keySet()) {

			int gold = balances.get(uuid);
			int meltdown_gold = playerdata.get(uuid).get(2);

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
			
			if(Bukkit.getPlayer(uuid) != null) {
				Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6+" + meltdown_gold + " Coins"));
				Bukkit.getPlayer(uuid).sendMessage("§aYou got " + "§6+" + meltdown_gold + " Coins");
			}
		}

		for (String string : compensation) {
			Bukkit.broadcastMessage(ChatColor.BOLD + "§6L'équipe §r" + TeamsManager.getTeamColorByTeam(string) + TeamsManager.getTeamNameByTeam(string)
					+ ChatColor.BOLD + "§c a reçu une §acompensation §cpour l'absence d'un ou plusieurs de leurs membres");
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
		datas.add(5, null);
		datas.add(6, null);
		datas.add(7, null);
		datas.add(8, null);
		datas.add(9, null);
		datas.add(10, null);
		datas.add(11, 0);
		datas.add(12, 0);
		datas.add(13, 0);
		datas.add(14, 100);
		datas.add(15, 1);
		datas.add(16, 0);
		playerdata.put(p.getName(), datas);
	}

	public static List<Integer> getRawPlayerDatas(String uuid) {
		return playerdata.get(uuid);
	}
	
	public static void doesPlayerJoined(String playername) {
		
	}

	public static ItemStack getBow() {
		ItemStack item = new ItemStack(Material.BOW); // Freezing gun
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bFreezing Gun");
		meta.setLore(Arrays.asList("§7Votre seul moyen d'éliminer les  équipes adverses",
				"Transforme les ennemis en §bglace"));
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getPickaxe() {
		ItemStack item = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6Coin Pickaxe");
		meta.setLore(Arrays.asList("§7C'est votre §6Pioche d'Equipe §7, §cUN SEUL §7membre peut l'avoir en même temps",
				"§aAbility §f: §6Mine Coins",
				"§3Cooldown §f: " + main.getConfig().getInt("games.meltdown.cooldown.pickaxe") + " secondes"));
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getCooldownPickaxe() {
		ItemStack item = new ItemStack(Material.WOODEN_PICKAXE); // Claiming pickaxe
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cClaim Pickaxe");
		meta.setLore(Arrays.asList("§7Clic droit pour obtenir la §6Pioche d'Equipe",
				"§cSeulement UN membre de l'équipe peut l'avoir en même temps",
				"§3Cooldown : §f" + main.getConfig().getInt("games.meltdown.cooldown.pickaxe") + " secondes"));
		meta.setUnbreakable(true);
		item.setItemMeta(meta);

		return item;
	}

	// Right Click Event
	@EventHandler
	public static void rightClick(PlayerInteractEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
			return;
		if(e.getClickedBlock() != null)
		if (e.getClickedBlock().getType() == Material.CRIMSON_TRAPDOOR)
			e.setCancelled(true);
		Player p = e.getPlayer();
		if (p.getInventory().getItemInMainHand() != null) {
			if (p.getInventory().getItemInMainHand().getType() == Material.WOODEN_PICKAXE) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { // Si a pioche bois dans les mains et fais clic droit
					if (playerdata.get(p.getName()).get(13) == 0) { // Si ya pas de cooldown

						p.getInventory().setItem(1, getPickaxe());
						Player pl;
						String team = TeamsManager.getPlayerTeam(p.getName());
						for (String player : playerdata.keySet()) {
							if (team == TeamsManager.getPlayerTeam(player)) { // Si le joueur appartient à la team qui a
																				// claim la pioche alors mettre un
																				// cooldown
								pl = Bukkit.getPlayer(player);
								playerdata.get(player).set(13,main.getConfig().getInt("games.meltdown.cooldown.pickaxe"));
								pickaxeCooldown(pl);
								if (pl != p) {
									pl.getInventory().setItem(1, getCooldownPickaxe());
								}
							}
						}
					} else { // SI ya du cooldown
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
								"§cLa pioche est en cooldown : " + playerdata.get(p.getName()).get(13) + " secondes"));
					}
				}
			}
		}
	}

	public static void pickaxeCooldown(Player p) {
		new BukkitRunnable() {
			int cd = main.getConfig().getInt("games.meltdown.cooldown.pickaxe");

			@Override
			public void run() {
				if (cd == 0) {
					playerdata.get(p.getName()).set(13, 0);
					cancel();
					return;
				}
				playerdata.get(p.getName()).set(13, cd);
				cd--;
			}
		}.runTaskTimer(main, 20, 20);
	}

	// Cancel item dropping
	@EventHandler
	public static void cancelDrop(PlayerDropItemEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
			return;
		e.setCancelled(true);
	}

	// Cancel block breaking
	@EventHandler
	public static void cancelBreaking(BlockBreakEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
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
			int amount = main.getConfig().getInt("games.meltdown.money.block");
			bal = bal + amount;
			data.set(2, bal);
			if(amount < 0) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6" + amount + " Coins"));
			} else {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6+" + amount + " Coins"));
			}

			return;
		} else { // SI BLOCK = HEATER
			whatHeater(e.getBlock().getLocation());
		}
	}

	public static void whatHeater(Location loc) { //A RECODER
		for (String player : playerdata.keySet()) {
			if(playerdata.get(player).get(8) != null) { //Si la valeur Heater X n'est pas nulle <=> ce joueur a placé un heater
				if(playerdata.get(player).get(8) == loc.getX() && playerdata.get(player).get(9) == loc.getY() && playerdata.get(player).get(10) == loc.getZ()){ //Si heater cassé est celui du joueur qui l'a cassé
					Player p = Bukkit.getPlayer(player);
					p.getInventory().addItem(getHeater(TeamsManager.getPlayerTeam(player)));
					playerdata.get(player).set(12, main.getConfig().getInt("games.meltdown.cooldown.heater"));
					playerdata.get(player).set(8, null);
					playerdata.get(player).set(9, null);
					playerdata.get(player).set(10, null);
					heaterCooldown(p);
					loc.getBlock().setType(Material.AIR);
				}
			} //Si le joueur n'a pas placé de heater -> skip
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
			item.getItemMeta().setDisplayName("§4Error Heater...");
			item.getItemMeta().setLore(Arrays.asList("§fSomething went wrong"));
			return item;
		}
		meta = item.getItemMeta();
		meta.setDisplayName(c + "Heater");
		meta.setLore(Arrays.asList("§7§oLe heater permet de §r§6décongeler §7§ovos équipiers",
				"§4Attention : §7§oIls peuvent être casser par l'équipe adverse"));
		item.setItemMeta(meta);

		return item;
	}

	public static void heaterCooldown(Player p) {
		new BukkitRunnable() {
			int cd = main.getConfig().getInt("games.meltdown.cooldown.heater");

			@Override
			public void run() {
				if (cd == 0) {
					List<Integer> datas = playerdata.get(p.getName());
					if(datas != null) {
						playerdata.get(p.getName()).set(12, 0);
					}
					cancel();
					return;
				}
				playerdata.get(p.getName()).set(12, cd);
				cd--;
			}
		}.runTaskTimer(main, 20, 20);
	}

	// Cancel block placing && Heater placing
	@EventHandler
	public static void cancelPlacing(BlockPlaceEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
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
		if(playerdata.get(p.getName()).get(1) == 1) {
			e.setCancelled(true);
			return;
		}
		if (playerdata.get(p.getName()).get(12) > 0) {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYour heater is on cooldown :" + playerdata.get(p.getName()).get(12) + "s"));
			e.setCancelled(true);
			return;
		} else { // SI LE HEATER PEUT ETRE PLACE ALORS
			Block b = e.getBlock();
			Location loc = b.getLocation();
			
			//Check si pas placé dans zone melted
			List<Location> banned = MDMap.getBannedLocs();
			if(banned.contains(loc)) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou cannot place your heater on a melted place ! >:("));
				e.setCancelled(true);
				return;
			}
			
			Material bt = b.getType();
			playerdata.get(p.getName()).set(8, loc.getBlockX());
			playerdata.get(p.getName()).set(9, loc.getBlockY());
			playerdata.get(p.getName()).set(10, loc.getBlockZ());

			// Particles
			particleEffect(loc, bt, b);

			new BukkitRunnable() {

				@Override
				public void run() {
					if (enable == false)
						cancel();
					if (bt != b.getType()) {
						cancel();
					}
					for (String player : playerdata.keySet()) {
						if (Bukkit.getPlayer(player) != null) { // Empeche les bugs
							Player pl = Bukkit.getPlayer(player);
							if (loc.distance(pl.getLocation()) <= 3) { // Rayon du heater
								if (playerdata.get(player).get(14) < 100) { // Si joueur freeze
									playerdata.get(player).set(14, playerdata.get(player).get(14) + 1);
									pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
											"§3Vous êtes dégelés à §b" + playerdata.get(player).get(14) + " §3%"));

								} else { // Si le joueur à son pourcentage de "dégel supérieur à 99%
									if (playerdata.get(player).get(1) == 1) { // Si il est gelé
										unFreeze(player); // Dégeler le joueur
									}
								}
							}
						}
					}
				}

			}.runTaskTimer(main, 0, 4);
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
					cancel();
				if (!enable)
					cancel();
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

	// Cancel Right Click
	/*
	 * @EventHandler public static void onRightClick(PlayerInteractEvent e) {
	 * if(!enable) return; if(!playerdata.containsKey(e.getPlayer().getName()))
	 * return; if(e.getAction() == Action.RIGHT_CLICK_BLOCK) { e.setCancelled(true);
	 * return; } }
	 */
	@EventHandler
	public static void rightClickEntity(PlayerInteractAtEntityEvent e) {
		if (enable != true)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
			return;
		if (e.getRightClicked() instanceof ItemFrame) {
			e.setCancelled(true);
		}
		if (e.getRightClicked() instanceof TrapDoor) {
			e.setCancelled(true);
		}
	}

	// When Froze
	/*
	@EventHandler
	public static void onMove(PlayerToggleSneakEvent e) {
		if (!enable)
			return;
		if (!playerdata.containsKey(e.getPlayer().getName()))
			return;

		String name = e.getPlayer().getName();

		if (playerdata.get(name).get(1).equals(1)) {
			e.setCancelled(true); //Empeche la personne de s'enlever du spectate mode
		}
	}*/

	@EventHandler
	public static void tryToShoot(ProjectileLaunchEvent e) {
		if (!enable)
			return;
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			String name = p.getName();
			if (!playerdata.containsKey(name))
				return;
			if (playerdata.get(name).get(1).equals(1)) {
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

		e.setDamage(0);

		HashMap<String, String> teams = TeamsManager.getOnlinePlayers();

		Player shooter = (Player) arrow.getShooter();
		Player p = (Player) e.getEntity();

		if (TeamsManager.getPlayerTeam(shooter.getName()).equals(TeamsManager.getPlayerTeam(p.getName())))
			return;

		List<Integer> shooterData = playerdata.get(shooter.getName());
		List<Integer> pData = playerdata.get(p.getName());

		int sgold = shooterData.get(2);
		int skills = shooterData.get(3);
		sgold = sgold + 50;
		skills = skills + 1;
		// shooterData.set(2, sgold); -->Update a la fin du script
		shooterData.set(3, skills);
		
		shooter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4KILL §6+" + main.getConfig().getInt("games.meltdown.money.kills") + " Coins"));

		int pgold = pData.get(2);
		int pdeaths = pData.get(4);
		pgold = pgold - 10;
		pdeaths = pdeaths + 1;
		pData.set(2, pgold);
		pData.set(4, pdeaths);
		pData.set(1, 1);

		iceCube(p.getUniqueId().toString());
		Bukkit.broadcastMessage(TeamsManager.getTeamColor(p.getName()) + p.getName() + " §fwas frozen by "
				+ TeamsManager.getTeamColor(shooter.getName()) + shooter.getName());

		String team = teams.get(p.getName()); // renvoie l'equipe (ex. "red")
		List<String> alive_mates = new ArrayList<String>(); // Va resenser tous les teammates en vie
		List<String> dead_mates = new ArrayList<String>(); // Va resenser tous les teammates hors jeu
		for (String player : teams.keySet()) {
			if (!player.equals(p.getName())) {
				if (teams.get(player).equals(team)) {
					if (!playerdata.get(player).get(1).equals(1)) {
						alive_mates.add(player); // Si le team mate n'est pas mort ca l'ajoute à cette liste

					} else { // Si le mate est aussi hors jeu
						dead_mates.add(player);
					}
				}
			}
		}
		if (alive_mates.isEmpty()) { // si aucun des mates est en vie alors élimination

			playerdata.get(p.getName()).set(0, 0); // Elimine dans la var playerdata tt la team
			// p.setGameMode(GameMode.SPECTATOR); //Mets la team en spec
			p.setInvisible(true);
			for (String mates : dead_mates) {
				playerdata.get(mates).set(0, 0);
				playerdata.get(mates).set(1, 1);
				// Bukkit.getPlayer(mates).setGameMode(GameMode.SPECTATOR);
				Bukkit.getPlayer(mates).setInvisible(true);
			}

			sgold = sgold + 100;
			Bukkit.broadcastMessage("§6L'équipe des " + TeamsManager.getTeamColor(p.getName())
					+ TeamsManager.getTeamName(p.getName()) + " §6a été §céliminé");

		}
		shooterData.set(2, sgold);
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
				for (String pl : playerdata.keySet()) {
					List<Integer> pData = playerdata.get(pl);
					Player pla = Bukkit.getPlayer(pl);
					if(pla == null) break;
					if(pla.getLocation().getY() <= fall_height && pData.get(0) != 0) {
						int pgold = pData.get(2);
						int pdeaths = pData.get(4);
						pgold = pgold - 10;
						pdeaths = pdeaths + 1;
						pData.set(2, pgold);
						pData.set(4, pdeaths);
						pData.set(0, 0);
						pData.set(1, 1);
						pData.set(16, 1);
							
						//Unset on fire
						Bukkit.getPlayer(pl).setFireTicks(0);
						
						playerdata.put(pl, pData);
						Bukkit.broadcastMessage(TeamsManager.getTeamColor(pl) + " " + pl + " §ffell in §clava");
							
						//Bukkit.getPlayer(pl).setGameMode(GameMode.SPECTATOR);
						List<String> teamates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(Bukkit.getPlayer(pl).getName()));
						/*if(teamates.get(0) == pl) { //Si le premier qui ressort de la liste est le joueur lui meme (bah on va pas le faire s'auto spectate c logique)
							Bukkit.getPlayer(pl).setSpectatorTarget(Bukkit.getPlayer(teamates.get(1)));
						} else {
							Bukkit.getPlayer(pl).setSpectatorTarget(Bukkit.getPlayer(teamates.get(0)));
						}*/
						
						Spectate.setSpectatingGroup(Bukkit.getPlayer(pl), teamates);
						
						infiniteDeathMessage(Bukkit.getPlayer(pl));
					}
					if (playerdata.get(pl).get(1).equals(0) || playerdata.get(pl).get(0).equals(1)) {
						teams.add(TeamsManager.getPlayerTeam(pl));
					}
				}
				if (teams.isEmpty()) {
					System.out.println("§cAn error happend in the meltdown game\n No winners found");
					// DO SMTH
				}
				String refTeam = teams.get(0);
				for (String team : teams) {
					if (refTeam != team)
						anotherTeam = true;
				}
				if (anotherTeam == false) { // si yen a pas alors c'est fin de game
					Bukkit.broadcastMessage("§6L'équipe des " + TeamsManager.getTeamColorByTeam(refTeam)
							+ TeamsManager.getTeamNameByTeam(refTeam) + " §6a gagné !");
					applyMoneyWon();
					disable(main);
				}
			}
		}.runTaskTimer(main, 0, 20);

	}

	public static void teamsChecker(String player) {

		String refTeam = TeamsManager.getPlayerTeam(player);
		boolean anotherTeam = false;
		for (String pl : playerdata.keySet()) { // Check si ya une autre team
			if (playerdata.get(pl).get(1).equals(0)) { //Sii le joueur n'est pas gelé
				if (refTeam != TeamsManager.getPlayerTeam(pl)) {
					anotherTeam = true;
				}
			} else { //Spectate team members
				if(playerdata.get(pl).get(16).equals(0)) { //Si il est gelé mais ne spectate pas
					
					//Bukkit.getPlayer(pl).setGameMode(GameMode.SPECTATOR);
					
					List<String> teamates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(Bukkit.getPlayer(pl).getName()));
					/*if(teamates.get(0) == Bukkit.getPlayer(pl).getName()) { //Si le premier qui ressort de la liste est le joueur lui meme (bah on va pas le faire s'auto spectate c logique)
						Bukkit.getPlayer(pl).setSpectatorTarget(Bukkit.getPlayer(teamates.get(1)));
					} else {
						Bukkit.getPlayer(pl).setSpectatorTarget(Bukkit.getPlayer(teamates.get(0)));
					}
					*/
					Spectate.setSpectatingGroup(Bukkit.getPlayer(pl), teamates);
					
					infiniteDeathMessage(Bukkit.getPlayer(pl));
				}
			}
		}
		if (anotherTeam == false) { // si yen a pas alors c'est fin de game
			Bukkit.broadcastMessage("§6L'équipe des " + TeamsManager.getTeamColor(player)
					+ TeamsManager.getTeamName(player) + " §6a gagné !");
			applyMoneyWon();
			disable(main);
		}
	}
	
	public static void infiniteDeathMessage(Player p) {
		new BukkitRunnable() {
			TextComponent txt = new TextComponent("§cYou are currently spectating...");
			String name;
			@Override
			public void run() {
				
				//Check
				if(!enable) {
					cancel();
					return;
				}
				if(playerdata.get(p.getName()).get(16) == 0) {
					cancel();
					return;
				}
				
				//Execute
				name = p.getSpectatorTarget().getName();
				txt = new TextComponent("§cYou are currently spectating " + TeamsManager.getTeamColor(name) + name + "  §7Press TAB to change View");
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
				
			}
		}.runTaskTimer(main, 0, 20);
	}

	
	// Animation (frozen dudes)
	public static void iceCube(String uuid) {
		Player p = Bukkit.getPlayer(uuid);
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
				new TextComponent("§3Vous êtes dégelés à §b" + playerdata.get(uuid).get(14) + " §3%"));
		unfreezeProcess(main, uuid);
	}
	
	public static void unfreezeProcess(Main main, String playername) {
		List<Integer> ls = playerdata.get(playername);
		String team = TeamsManager.getPlayerTeam(playername);
		
		new BukkitRunnable() {
			int HX, HY, HZ, X, Y, Z;
			Location Hloc;
			Location loc;
			
			int state = 0;
			
			@Override
			public void run() {
				
				if(state == 100) {
					unFreeze(playername);
					cancel();
					return;
				}
				for(String player : playerdata.keySet()) {
					if(playerdata.get(player) != null) {
						if(TeamsManager.getPlayerTeam(player) == team) {
							//Playr loc
							X = playerdata.get(playername).get(5);
							Y = playerdata.get(playername).get(6);
							Z = playerdata.get(playername).get(7);
							loc = new Location(Bukkit.getWorlds().get(0), X, Y, Z);
							
							//Heater loc
							HX = playerdata.get(player).get(8);
							HY = playerdata.get(player).get(9);
							HZ = playerdata.get(player).get(10);
							
							Hloc = new Location(Bukkit.getWorlds().get(0), HX, HY, HZ);
							
							if(Hloc.distance(loc) <= 3) {
								state++;
								ls.set(14, state);
								Bukkit.getPlayer(playername).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§3Vous êtes dégelés à §b" + state + " §3%"));
							}
						}
					}
				}
			}
		}.runTaskTimer(main, 20, 4);
	}

	public static void unFreeze(String uuid) {
		Player p = Bukkit.getPlayer(uuid);

		List<Integer> ls = playerdata.get(uuid);
		int X = ls.get(5);
		int Y = ls.get(6);
		int Z = ls.get(7);
		Location loc = new Location(p.getWorld(), X, Y, Z);

		p.teleport(loc);
		loc.getBlock().setType(Material.AIR);
		loc.add(0, 1, 0);
		loc.getBlock().setType(Material.AIR);

		p.setInvisible(false);
		p.setGameMode(GameMode.SURVIVAL);

		ls.set(1, 0);
		ls.set(5, null);
		ls.set(6, null);
		ls.set(7, null);
		ls.set(16, 0);
		playerdata.put(uuid, ls);
	}
	
	//Change de personne à spectate
	/*
	@EventHandler
	public static void changeView(PlayerInteractEvent e) {
		if (!enable) return;
		if (!playerdata.containsKey(e.getPlayer().getName())) return;
		
		Player p = e.getPlayer();
		if(playerdata.get(p.getName()).get(1) != 1) return;
		
		List<String> mates = TeamsManager.getTeamMembers(TeamsManager.getPlayerTeam(p.getName()));
		mates.remove(p.getName());
		for (String dude : mates) {
			if(playerdata.get(dude).get(1).equals(1)) mates.remove(dude);
		}
		
		String current = p.getSpectatorTarget().getName();
		
		if(mates.indexOf(current) == mates.size() - 1) {
			current = mates.get(0);
		} else {
			current = mates.get(mates.indexOf(current) + 1);
		}
		p.setSpectatorTarget(Bukkit.getPlayer(current));
	}*/

	public static void gameTimer() { //Time counter --> Trigger l'ouverture des portes par exemples
		List<Integer> room_times = MeltdownFiles.getRoomTimes(main); //Récupère les temps de déclenchement des alarmes
		List<Integer> room_coordsA = MeltdownFiles.getRoomCoords(main, "A");
		List<Integer> room_coordsB = MeltdownFiles.getRoomCoords(main, "B");
		List<Integer> room_coordsC = MeltdownFiles.getRoomCoords(main, "C");
		List<Integer> room_coordsD = MeltdownFiles.getRoomCoords(main, "D");
		List<Integer> room_coordsE = MeltdownFiles.getRoomCoords(main, "E");
		List<Integer> room_coordsM = MeltdownFiles.getRoomCoords(main, "M");
		new BukkitRunnable() {
			
			int counter = 0;
			int alarm_tA = room_times.get(0); //Récupère le temps A
			int alarm_tB = room_times.get(1); //Récupère le temps B
			int alarm_tC = room_times.get(2); //Récupère le temps C
			int alarm_tD = room_times.get(3); //Récupère le temps D
			int alarm_tE = room_times.get(4); //Récupère le temps E
			int alarm_tM = room_times.get(5); //Récupère le temps M
			
			
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
					p.setPlayerListFooter(
							 "§f----------------------------\n"
							+ "§3Mode de jeu §f: §4Meltdown\n"
							+ "§6Coins : " + playerdata.get(p.getName()).get(2) + "\n"
							+ "§cTemps passé §f: " + time); //Affiche depuis cb de temps la partie à commencée
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
		if(!playerdata.containsKey(name)) return;
		
		playerdata.get(name).set(15, 0); //Enregistrer le joueur comme déconnecté
		
		String team = TeamsManager.getPlayerTeam(name);
		for (String member : TeamsManager.getTeamMembers(team)) {
			Player mb = Bukkit.getPlayer(member);
			if(mb != null) {
				mb.sendMessage(TeamsManager.getTeamColorByTeam(team) + name + " s'est déconnecté, il a 3 minutes pour revenir avant d'être éliminé");
			}
		}
		new BukkitRunnable() {
			
			int counter = 0;
			
			@Override
			public void run() {
				List<Integer> datas = playerdata.get(name);
				if(datas != null) {
					if(datas.get(15) == 1) {
						cancel();
						return;
					}
				}
				if(counter == 30) {
					playerdata.get(name).set(0, 0);
					Bukkit.broadcastMessage("§fLe joueur " + TeamsManager.getTeamColor(name) + name + "§fa été éliminé car il s'est déconnecté");
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
			if(TeamsManager.getPlayerTeam(uuid) != "none") { //Si le joueur estenregistré dans une équipe et qu'il a pas e playerdata (= crash avant d'en avoir)
				
				//PlayerBegin() et PlayerTeleport()
				
				List<Integer> tp = MeltdownFiles.getSpawnCoords(main, TeamsManager.getPlayerTeam(uuid));
				p.teleport(new Location(Bukkit.getWorlds().get(0), tp.get(0), tp.get(1), tp.get(2), tp.get(3), 0));
				
				//Anti bug -- supposé useless
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
			playerdata.get(uuid).set(15, 1); //Enregistrer le joueur comme reconnecté
		}
	}
}
