package mctdl.game.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import mctdl.game.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameVoting implements Listener{
	
	static HashMap<String, List<String>> games = new HashMap<String, List<String>>();
	static List<UUID> ids = new ArrayList<>();
	static HashMap<String, Boolean> hasVoted = new HashMap<String, Boolean>();
	
	static boolean open = false;
	
	static int X1;
	static int Z1;
	
	static int X2;
	static int Z2;
	
	static int X3;
	static int Z3;
	
	static Main main;
	public GameVoting(Main main) {
		GameVoting.main = main;
		
		X1 = main.getConfig().getInt("lobby.vote.text1.X");
		Z1 = main.getConfig().getInt("lobby.vote.text1.Z");
		
		X2 = main.getConfig().getInt("lobby.vote.text2.X");
		Z2 = main.getConfig().getInt("lobby.vote.text2.Z");
		
		X3 = main.getConfig().getInt("lobby.vote.text3.X");
		Z3 = main.getConfig().getInt("lobby.vote.text3.Z");
	}
	
	public static HashMap<String, List<String>> getGames() {
		List<String> game = new ArrayList<>();
		/*
		 * Index 0 : Name
		 * Index 1 : Description
		 */
		
		//Meltdown
		game.add("§4Meltdown");
		game.add("§7Récupérez le maximum de Coins avant que les salles ne s'éffondrent... \nEliminez les teams ennemis en les gelants avec votre §bFreezing Gun");
		games.put("meltdown", game);
		
		//Test Games
		game = new ArrayList<>();
		game.add("§fTest");
		game.add("§7Testtt");
		games.put("test1", game);

		//Test Games
		game = new ArrayList<>();
		game.add("§fTest2");
		game.add("§7Testtttttt");
		games.put("test2", game);

		//Test Games
		game = new ArrayList<>();
		game.add("§fTest3");
		game.add("§7Testttttttttt");
		games.put("test3", game);
		
		return games;
	}
	
	public static List<String> getProposal() {
		
		List<String> gamesName = new ArrayList<>();
		for (String game : games.keySet()) {
			gamesName.add(game);
		}
		
		Random rdm = new Random();
		int prp = rdm.nextInt(gamesName.size());
		
		String proposal1 = gamesName.get(prp);
		gamesName.remove(prp);

		prp = rdm.nextInt(gamesName.size());
		
		String proposal2 = gamesName.get(prp);
		gamesName.remove(prp);

		prp = rdm.nextInt(gamesName.size());
		
		String proposal3 = gamesName.get(prp);
		gamesName.remove(prp);
		
		List<String> proposals = new ArrayList<>();
		proposals.add(proposal1);
		proposals.add(proposal2);
		proposals.add(proposal3);
		
		return proposals;
	}
	
	public static void displayProposal(Main main) {
		List<String> proposals = getProposal();
		
		//Vote 1
		Location loc = new Location(Bukkit.getWorlds().get(0), main.getConfig().getInt("lobby.vote.text1.X") + 0.5, main.getConfig().getInt("lobby.vote.text1.Y"), main.getConfig().getInt("lobby.vote.text1.Z") + 0.5);
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setInvisible(true);
		stand.setInvulnerable(true);
		stand.setCustomNameVisible(true);
		stand.setGravity(false);
		stand.setCollidable(false);
		stand.setCustomName("§6Vote for " + games.get(proposals.get(0)).get(0)); //Get Nom du proposal 1
		ids.add(stand.getUniqueId());
		
		//Vote 2
		Location loc2 = new Location(Bukkit.getWorlds().get(0), main.getConfig().getInt("lobby.vote.text2.X") + 0.5, main.getConfig().getInt("lobby.vote.text2.Y"), main.getConfig().getInt("lobby.vote.text2.Z") + 0.5);
		ArmorStand stand2 = (ArmorStand) loc2.getWorld().spawnEntity(loc2, EntityType.ARMOR_STAND);
		stand2.setInvisible(true);
		stand2.setInvulnerable(true);
		stand2.setCustomNameVisible(true);
		stand2.setGravity(false);
		stand2.setCollidable(false);
		stand2.setCustomName("§6Vote for " + games.get(proposals.get(1)).get(0)); //Get Nom du proposal 2
		ids.add(stand2.getUniqueId());
		
		//Vote 3
		Location loc3 = new Location(Bukkit.getWorlds().get(0), main.getConfig().getInt("lobby.vote.text3.X") + 0.5, main.getConfig().getInt("lobby.vote.text3.Y"), main.getConfig().getInt("lobby.vote.text3.Z") + 0.5);
		ArmorStand stand3 = (ArmorStand) loc3.getWorld().spawnEntity(loc3, EntityType.ARMOR_STAND);
		stand3.setInvisible(true);
		stand3.setInvulnerable(true);
		stand3.setCustomNameVisible(true);
		stand3.setGravity(false);
		stand3.setCollidable(false);
		stand3.setCustomName("§6Vote for " + games.get(proposals.get(2)).get(0)); //Get Nom (games.get(x).get(0)) du proposal 3 --> proposal.get(3)
		ids.add(stand3.getUniqueId());
	}
	
	public static List<UUID> getArmorStandIDs() {
		return ids;
	}
	
	public static void clearDisplay() {
		List<UUID> removed = new ArrayList<>();
		for (UUID id : ids) {
			ArmorStand stand = (ArmorStand) Bukkit.getEntity(id);
			stand.remove();
			
			removed.add(id);
		}
		for (UUID uuid : removed) {
			if(ids.contains(uuid)) {
				ids.remove(uuid);
			}
		}
	}
	
	public static HashMap<String, Boolean> hasVoted() {
		return hasVoted;
	}
	
	public static void setHasVoted(String playername, Boolean voted) {
		hasVoted.put(playername, voted);
	}
	
	@EventHandler
	public static void interact(PlayerInteractEvent e) {
		if(!open) return;
		Player p = e.getPlayer();
		String name = p.getName();
		
		if(!hasVoted.containsKey(name)) return;
		
		int X = e.getClickedBlock().getX();
		int Z = e.getClickedBlock().getZ();
		
		if((X == X1 || X == X2 || X == X3) && (Z == Z1 || Z == Z2 || Z == Z3)) { //si interaction ds zone de vote
			if(p.getInventory().getItemInMainHand().getType() == Material.SAND) {
				if(hasVoted.get(name) == true) {
					p.spigot().sendMessage(ChatMessageType.SYSTEM, new TextComponent("§cVous avez déja voté"));
				} else {
					
				}
			}
		}
	}
}
