package mctdl.game.games.nexus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.commands.BaltopCommand;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;

public class Nexus implements Listener{
	
	static List<String> matches = new ArrayList<>();
	static HashMap<String, List<String>> playerdatas = new HashMap<String, List<String>>();
	
	static Main main;
	static boolean enabled = false;
	
	public Nexus(Main main) {
		Nexus.main = main;
	}
	
	public List<String> getMatches() {
		return matches;
	}
	
	public static void start(boolean adminMode) {
		List<String> bt = BaltopCommand.getTeamClassement();
		List<String> teams = new ArrayList<>();
		
		List<String> basicdatas = new ArrayList<>();
		
		for(String team : bt) {
			System.out.println(team);
			if(!TeamsManager.isTeamEmpty(team)) {
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
			} else { //LES ATTAQUANTS
				for(String pl : TeamsManager.getTeamMembers(teams.get(i))) {
					basicdatas =new ArrayList<>();
					basicdatas.add(String.valueOf(i)); //Index 0 --> Terrain
					basicdatas.add("attacker"); //Index 1 --> Role (def/atk)
					playerdatas.put(pl, basicdatas);
				}
			}
		}
		//--------------------------------------------------------------------------------------------
		
		//Init les emplacement de stuff des bougs
		initStuff();
		
		//Joueurs
		playersInit();
		
		Bukkit.getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, true); //Enable Keep Inventory pr ce jeu
		
		enabled = true;
		
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
				
				p.sendMessage("u do exist");
				p.sendMessage(playerdatas.toString());
				
				//POUR LA TELEPORTATION FAIRE UNE BOITE COMME DS LE CAHIER DE BROUILLON
				
			}
		}
	}
	
	public static void stop() {
		enabled = false;
		
		Bukkit.getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, false);
	}
	
	@EventHandler
	public static void onJump(PlayerMoveEvent e) {
		if(enabled == false) return;
		Entity p = (Entity) e.getPlayer();
		if(!matches.contains(p.getName())) return;
		if(matches.indexOf(p.getName())%2 !=0) return; //Si n'est ps defenseur
		if(p.getVelocity().getY() > 0 && !p.isOnGround()) { //Est en l'air ¿
			if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.MAGENTA_GLAZED_TERRACOTTA) { //Saute sur les boost
				
				p.setVelocity(p.getVelocity().add(new Vector(0, 0.4, 0)));
			}
		}
	}
	
	@EventHandler
	public static void onDamage(EntityDamageByEntityEvent e) {
		if(!enabled) return;
		if(!playerdatas.containsKey(e.getEntity().getName())) return;
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player) e.getEntity();
		
	}
	
	@EventHandler
	public static void onDeath(PlayerDeathEvent e) {
		if(!enabled) return;
		if(!playerdatas.containsKey(e.getEntity().getName())) return;
		
		Player p = e.getEntity();
		if(playerdatas.get(p.getName()).get(1) == "defender") { //Spawn cooldown différent selon def & atk
			
		} else {
			
		}
	}
	
	
	//Random stuff --------------------
	
	public static ItemStack getMatraque() {
		ItemStack item = new ItemStack(Material.WOODEN_SHOVEL);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§cMatraque §3Electrique");
		meta.setLore(Arrays.asList("", "§7La matraque électrique est une arme CaC", "§7très puissante qui en plus de faire mal", "§7permet de ralentir les ennemis", "", "§2Damage §f: §23.95", "§bStunt §f: §31.0", "§dCooldown §f: 0.2"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getKnife() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§7Coûteau");
		meta.setLore(Arrays.asList("", "§7fLe couteau permet de tuer facilement une cible", "§7a condition d'un minimum de discretion évidement", "§7\"On ne participe pas à une fusillade avec un coûteau\"", "", "§2Damage §f: §211", "§dCooldown §f: 1.0"));
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
		
		meta.setDisplayName("§3Tazer");
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
}
