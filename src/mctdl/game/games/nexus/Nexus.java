package mctdl.game.games.nexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.commands.BaltopCommand;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.Map;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.TimeFormater;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Nexus implements Listener{
	
	static List<String> matches = new ArrayList<>();
	static HashMap<String, List<String>> playerdatas = new HashMap<String, List<String>>(); //String Playername, List<String> datas
	
	static HashMap<Player, Player> lastdamager = new HashMap<Player, Player>(); //Associe au joueur la derniere personne qui lui a fait des degats
	
	static Main main;
	static boolean enabled = false;
	static Map map;
	
	static List<String> teams = new ArrayList<>();
	
	static int respawn_cooldown=0;
	static int invulnerability=0;
	
	//VARIABLES RANDOM PR LE JEU
	static double MATRAQUE_DAMAGE = 3.95;
	static double KNIFE_DAMAGE = 11;

	static int COINS_PER_KILL = 80;
	static int COINS_PER_DEATH = -10;
	static int COINS_PER_WIN = 1000;
	static int COINS_PER_MINUTES_SURVIVED = 10;
	
	public Nexus(Main main) {
		Nexus.main = main;
	}
	
	public List<String> getMatches() {
		return matches;
	}
	
	public static void start(boolean adminMode) {
		main.getConfig().set("game", "nexus");
		Main.game = "nexus";
		main.saveConfig();
		List<String> bt = BaltopCommand.getTeamClassement();

		FileConfiguration cfg = NexusFiles.checkMap(main);
		
		//Map variable stuff
		HashMap<String, Location> spawns = new HashMap<String, Location>();
		map = new Map(main, cfg.getString("map"), new Location(Bukkit.getWorld("mapz"), 8, 13, 8), "mapz", null, null, 0, -1, "Nexus");
		
		if(!NexusFiles.isMapGenerated(main)) { //Génère la map
			Bukkit.broadcastMessage("§6Generating §bNexus §6map... §cThis could cause some lag :)");
			map.build(false);
			NexusFiles.setMapGenerated(main, true);
		} else {
			Bukkit.broadcastMessage("§f[§bNexus§f] > §aThe game will start soon");
		}
		
		new BukkitRunnable() {
			
			int timer=0;
			
			List<String> basicdatas = new ArrayList<>();
			
			@Override
			public void run() {
				if(timer==0) {
					for(String team : bt) {
						if(TeamsManager.hasATeammateOnline(team)) {
							teams.add(team);
						}
					}
					
					if(teams.size()%2 != 0 && !adminMode) { //Si le nombre de team n'est pas pair ET que le mode admin est pas adctif
						System.out.println("Bruh ! Retry when the number of teams is pair");
						return;
					}
					matches = teams;
					
					// "Matchmaking" ----------------------------------------------------------------------------
					for(int i = 0; i < teams.size(); i++) {
						if(i%2 == 0) { // LES DEFENDERS
							for(String pl : TeamsManager.getTeamMembers(teams.get(i))) { //pr tt les membre de l'équipe
								basicdatas =new ArrayList<>();
								basicdatas.add(String.valueOf(i)); //Index 0 --> Terrain
								basicdatas.add("defender"); //Index 1 --> Role (def/atk)
								playerdatas.put(pl, basicdatas);
							}

							
							//Define their spawns
							spawns.put(teams.get(i), new Location(Bukkit.getWorld("mapz"), -3+(143*(i/2)), 6, 0, -90, 0)); //143 blocks entre les spawns
						} else { //LES ATTAQUANTS
							for(String pl : TeamsManager.getTeamMembers(teams.get(i))) {
								basicdatas =new ArrayList<>();
								basicdatas.add(String.valueOf(i)); //Index 0 --> Terrain
								basicdatas.add("attacker"); //Index 1 --> Role (def/atk)
								playerdatas.put(pl, basicdatas);
							}

							//Define their spawns
							spawns.put(teams.get(i), new Location(Bukkit.getWorld("mapz"), -3+(143*(i/2)), 6, 16, -90, 0));
						}
					}
					map.setSpawns(spawns);
					//--------------------------------------------------------------------------------------------
					
					//Init les emplacement de stuff des bougs
					initStuff();

					Bukkit.getWorld("mapz").setGameRule(GameRule.KEEP_INVENTORY, true); //Enable Keep Inventory pr ce jeu
					Bukkit.getWorld("mapz").setGameRule(GameRule.FALL_DAMAGE, false); //Desactive degats chute

				}
				if(timer==7) Bukkit.broadcastMessage("§f[§bNexus§f] > Téléportation dans §c3 §fsecondes");
				if(timer==8) Bukkit.broadcastMessage("§f[§bNexus§f] > Téléportation dans §e2 §fsecondes");
				if(timer==9) Bukkit.broadcastMessage("§f[§bNexus§f] > Téléportation dans §a1 §fsecondes");
				if(timer==10) {
					Bukkit.broadcastMessage("§f[§bNexus§f] > Téléportation ! §7La partie va commencer dans quelques instants");
					//Joueurs
					playersInit();

					//HIDE NAMETAGS -------------->
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hidenametags enable");
					//-----------------------------
				}
				if(timer==12) Bukkit.broadcastMessage("§aPréparez vous...");
				if(timer==15) {
					Bukkit.broadcastMessage("§f[§bNexus§f] > §6La partie commence !");

					enabled = true;
					gameTimer();
					cancel();
				}
			
				timer++;
			}
		}.runTaskTimer(main,0, 20);
		
		//RANDOML STUFF INIT HERE
		respawn_cooldown = cfg.getInt("respawn_cooldown");
		invulnerability = cfg.getInt("invulnerability");
		
	}
	
	// Initalise les données associées au joueur + les teleporte
	public static void playersInit() {
		System.out.println("matches : " + matches);
		for(String team : matches) { //Pour toutes les équipes
			List<String> members = TeamsManager.getTeamMembers(team); //Get les membres de l'équipe
			
			for(String player : members) { //Pour tous les joueurs de cette team
				List<String> datas;
				
				Player p = Bukkit.getPlayer(player);
				
				PlayerData.saveItems(p);
				p.getInventory().clear();
				
				
				if(playerdatas.get(player) == null) { //Ce n'est jamais supposé être null mais somehow on sait jamais
					datas = new ArrayList<>();
					datas.add(String.valueOf(matches.indexOf(team)/2)); //Le terrain probably
					if(matches.indexOf(team)%2 == 0) { // --> Defenseur
						datas.add("defender");
						
						//ITEMS-->
						PlayerData.setInventory(p, defStuff);
					} else {
						datas.add("attacker");
						
						//ITEMS-->
						PlayerData.setInventory(p, atkStuff);
					}
					playerdatas.put(player, datas);
				}
				datas = playerdatas.get(player);
				
				datas.add("1"); //isAlive
				datas.add("0"); //Coins
				datas.add("0"); //Kills
				datas.add("0"); //Death
				datas.add("0"); //Respawn cooldown
				datas.add("0"); //Last death
				
				
				p.teleport(map.getSpawns().get(team));
				p.setInvisible(true);
				
				setPlayerInv(p);
			}
		}
	}
	
	public static void stop() {
		enabled = false;
		main.getConfig().set("game", "lobby");
		Main.game = "lobby";
		main.saveConfig();
		
		teams = new ArrayList<>();
		matches = new ArrayList<>();
		playerdatas = new HashMap<>();
		System.gc();
		
		Bukkit.getWorld("mapz").setGameRule(GameRule.KEEP_INVENTORY, false);
		Bukkit.getWorld("mapz").setGameRule(GameRule.FALL_DAMAGE, true);
		for(String pl : TeamsManager.getOnlinePlayers().keySet()) {
			Player p = Bukkit.getPlayer(pl);
			p.getInventory().clear();
			p.teleport(new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
			p.setGameMode(GameMode.ADVENTURE);
			PlayerData.registerPlayer(p);
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hidenametags disable");
	}
	
	public static void gameTimer() {
		
		new BukkitRunnable() {

			int counter = 0;
			String time;
			
			
			@Override
			public void run() {
				if(!enabled) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.setPlayerListFooter("");
					}
					cancel();
					return;
				}
				
				for(String dude : playerdatas.keySet()) {
					if(playerdatas.get(dude).get(7) == "60") { //Quand le boug a passé 1 minute sans mourrir
						int coins = Integer.valueOf(playerdatas.get(dude).get(3));
						coins += COINS_PER_MINUTES_SURVIVED;
						playerdatas.get(dude).set(3, String.valueOf(coins));
						playerdatas.get(dude).set(7, "1");
					}
					int survived = Integer.valueOf(playerdatas.get(dude).get(7)) +1;;
					playerdatas.get(dude).set(3, String.valueOf(survived));
				}
				
				counter++;
				time = TimeFormater.getFormatedTime(counter);
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.setPlayerListFooter(
							 "§f----------------------------\n"
							+ "§3Mode de jeu §f: §4Meltdown\n"
							+ "§6Coins : " + playerdatas.get(p.getUniqueId().toString()).get(3) + "\n"
							+ "§aKills : " + playerdatas.get(p.getUniqueId().toString()).get(4) + "\n"
							+ "§cDeaths : " + playerdatas.get(p.getUniqueId().toString()).get(5) + "\n"
							+ "§bTemps passé §f: " + time); //Affiche depuis cb de temps la partie à commencée
				}
			}
			
		}.runTaskTimer(main, 0, 20);
	}
	
	@EventHandler
	public static void teleport(PlayerInteractEvent e) {
		if(!enabled) return;
		Player p = e.getPlayer();
		if(p.getLocation().getY() > 6) return;
		Block b = e.getClickedBlock();
		double z = b.getLocation().getZ();
		
		if(b.getType() == Material.LIGHT_BLUE_CONCRETE) {
			String cooldown = playerdatas.get(p.getUniqueId().toString()).get(6);
			if(!cooldown.equals("0")) {
				p.sendMessage("§cVous pourrez réapparaître dans " + cooldown + " seconds");
				return;
			}
			if(playerdatas.get(p.getUniqueId().toString()).get(1) == "defender") {
				if(-4<=z && z<= -3) { //=> spawn 1 defenseur
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() + 41, 35, -44, -90, 0));
				}
				if(-1<=z && z<= 1) { //=> spawn 2 defenseur
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() + 22, 35, 47, 180, 0));
				}
				if(3<=z && z<= 4) { //=> spawn 2 defenseur
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() - 44, 40, -3, -90, 0));
				}
			} else { //Attackers
				if(12<=z && z<= 13) { //=> spawn 1 atk
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() - 37, 35, -47, -90, 0));
				}
				if(15<=z && z<= 17) { //=> spawn 2 atk
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() + 72, 44, 5, 90, 0));
				}
				if(19<=z && z<= 20) { //=> spawn 2 atk
					p.teleport(new Location(map.getWorld(), map.getSpawns().get(TeamsManager.getPlayerTeam(p.getName())).getX() - 7, 32, 77, 180, 0));
				}
			}
			
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§bYou are invulnerable for " + invulnerability + " seconds"));
			
			//Disable invulnerability
			new BukkitRunnable() {

				@Override
				public void run() {
					p.setInvulnerable(false);
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou can take damages now"));
				}
				
			}.runTaskLater(main, invulnerability*20);
		}
	}
	
	@EventHandler
	public static void onJump(PlayerMoveEvent e) { //JUMP DES DEFENSEURS
		if(enabled == false) return;
		Entity p = (Entity) e.getPlayer();
		if(!playerdatas.get(p.getUniqueId().toString()).get(1).equals("defender")) return;
		if(p.getVelocity().getY() > 0 && !p.isOnGround()) { //Est en l'air ¿

			if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.MAGENTA_GLAZED_TERRACOTTA) { //Saute sur les boost
				
				p.setVelocity(p.getVelocity().add(new Vector(0, 0.38, 0)));
			}
		}
	}
	
	@EventHandler
	public static void onDamage(EntityDamageByEntityEvent e) { //Triggered quand les joueurs se tapent avec clic gauche
		if(!enabled) return;
		if(!playerdatas.containsKey(e.getEntity().getName())) return;
		if(!(e.getEntity() instanceof Player)) return;
		if(!(e.getDamager() instanceof Player)) return;
		
		Player p = (Player) e.getEntity();
		Player d = (Player) e.getDamager();
		
		ItemStack item = d.getInventory().getItemInMainHand();
		if(!(item.isSimilar(getMatraque()) || item.isSimilar(getKnife()))) e.setDamage(0); //Si c'est pas une arme CaC les dégats sont annulés
		if(item.isSimilar(getMatraque())) { //Apply damages
			e.setDamage(MATRAQUE_DAMAGE);
		}
		if(item.isSimilar(getKnife())) { //Apply damages
			e.setDamage(KNIFE_DAMAGE);
		}
		
		lastdamager.put(p, d);
	}
	
	
	@EventHandler
	public static void onDeath(PlayerDeathEvent e) {
		if(!enabled) return;
		if(!playerdatas.containsKey(e.getEntity().getName())) return;
		
		Player p = e.getEntity();
		String uuid = p.getUniqueId().toString();
		if(lastdamager.containsKey(p)) { //DONNE COINS AU TUEUR
			Player dmg = lastdamager.get(p);
			String dmgUUID = dmg.getUniqueId().toString();
			int coins = Integer.parseInt(playerdatas.get(dmgUUID).get(3));
			coins+= COINS_PER_KILL;
			playerdatas.get(dmgUUID).set(3, String.valueOf(coins));
			
			//AJOUTE 1 KILL AU BOUG
			int kills = Integer.parseInt(playerdatas.get(dmgUUID).get(4)) + 1;
			playerdatas.get(dmgUUID).set(4, String.valueOf(kills));
		}
		//RETIRE COINS AU GENS QUI EST MORT
		int coins = Integer.parseInt(playerdatas.get(uuid).get(3));
		coins += COINS_PER_DEATH;
		playerdatas.get(uuid).set(3, String.valueOf(coins));
		
		//AJOUTE 1 DEATH AU BOUG QUI EST MORT
		int deaths = Integer.parseInt(playerdatas.get(uuid).get(5)) + 1;
		playerdatas.get(uuid).set(5, String.valueOf(deaths));
		
		//SET SA LAST DEATH
		playerdatas.get(uuid).set(7, "0");
	}
	
	@EventHandler
	public static void toRespawn(PlayerRespawnEvent e) {
		System.out.println("respawn event triggered");
		if(!enabled) return;
		Player p = e.getPlayer();
		if(!playerdatas.containsKey(p.getUniqueId().toString())) return;
		
		p.getInventory().clear();
		setPlayerInv(p);
		p.setInvulnerable(true);


		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.teleport(map.getSpawns().get(TeamsManager.getPlayerTeam(p.getUniqueId().toString())));
				System.out.println(map.getSpawns().get(TeamsManager.getPlayerTeam(p.getUniqueId().toString())));
			}
		}.runTaskLater(main, 5);
		
		new BukkitRunnable() {
			int cooldown = respawn_cooldown;
			
			@Override
			public void run() {
				playerdatas.get(p.getUniqueId().toString()).set(6, String.valueOf(cooldown));
				if(cooldown == 0) {
					cancel();
				}
				cooldown--;
			}

		}.runTaskTimer(main, 0, 20);
	}
	
	
	//Random stuff --------------------
	
	public static ItemStack getMatraque() {
		ItemStack item = new ItemStack(Material.WOODEN_SHOVEL);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§cMatraque §3Electrique");
		meta.setLore(Arrays.asList("", "§7La matraque électrique est une arme CaC", "§7très puissante qui en plus de faire mal", "§7permet de ralentir les ennemis", "", "§2Damage §f: §23.95", "§bStunt §f: §3" + MATRAQUE_DAMAGE, "§dCooldown §f: 0.2"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getKnife() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§7Coûteau");
		meta.setLore(Arrays.asList("", "§7fLe couteau permet de tuer facilement une cible", "§7a condition d'un minimum de discretion évidement", "§7\"On ne participe pas à une fusillade avec un coûteau\"", "", "§2Damage §f: §2" + KNIFE_DAMAGE , "§dCooldown §f: 1.0"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getTazer() {
		ItemStack item = new ItemStack(Material.IRON_SHOVEL);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§3Tazer");
		meta.setLore(Arrays.asList("", "§7Le tazer est retoutable tant par le fait", "§7qu'il s'agit d'une arme à distance mais aussi", "§7car elle permet de §bstunt§7 les ennemis", "", "§2Damage §f: §21.5", "§bStunt §f: §32.0", "§5Range §f: 10", "§dCooldown §f: 3.0"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getChocker() {
		ItemStack item = new ItemStack(Material.IRON_SHOVEL);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§3Chocker");
		meta.setLore(Arrays.asList("", "§7La grenade de choc est unne arme extremement", "§7efficace, elle permet de §bstunt §7les ennemis", "§7pendant une courte période. Elle est définitivement", "§7meilleure qu'un tazer mais il y en a une quantité limitée...", "", "§2Damage §f: §20.5", "§bStunt §f: §33.0", "§5Range §f: 6", "§dCooldown §f: 4.0"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getPinger() {
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§6Pinger");
		meta.setLore(Arrays.asList("", "§7Le pinger permet d'indiquer une §6position", "§7aux alliés. Ceux ci voient un marqueur à travers des murs", "", "§bDuration §f: §310", "§dCooldown §f: 8.0"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static void setPlayerInv(Player p) {
		if(playerdatas.get(p.getUniqueId().toString()).get(1) == "defender") {
			p.getInventory().setItem(0, getMatraque());
			p.getInventory().setItem(1, getTazer());
			p.getInventory().setItem(2, getPinger());
		} else {
			p.getInventory().setItem(0, getKnife());
			p.getInventory().setItem(1, getChocker());
			p.getInventory().setItem(2, getPinger());
		}
	}
	
	static HashMap<Integer, ItemStack> defStuff = new HashMap<Integer, ItemStack>();
	static HashMap<Integer, ItemStack> atkStuff = new HashMap<Integer, ItemStack>();
	
	public static void initStuff() {
		defStuff.put(1, getMatraque());
		defStuff.put(2, getTazer());
		defStuff.put(9, getPinger());
		

		atkStuff.put(1, getKnife());
		atkStuff.put(2, getChocker());
		atkStuff.put(9, getPinger());
	}
	
	public static HashMap<String,List<String>> getDatas() {
		return playerdatas;
	}
}
