package mctdl.game.tablist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import mctdl.game.Main;
import mctdl.game.commands.BaltopCommand;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;

public class TabManager implements Listener{
	
	static Main main;
	static String format;

	static String edition;
	public TabManager(Main main) {
		TabManager.main = main;
		edition = main.getConfig().getString("edition");
		format = "";
	}
	
	@EventHandler
	public void baseTab(PlayerJoinEvent e) {
		updateTabList();
	}
	
//	public void tabClock(Player p) {
//		
//		this.updateTabList();
//		
//		new BukkitRunnable() {
//			
//			String edition = main.getConfig().getString("edition");
//			
//			@Override
//			public void run() {
//				
//				if(!p.isOnline()) { // If player disconnect we can stop loading a tablist
//					cancel();
//					return;
//				}
//				
//				if(test(format)) updateTabList();
//				
//				p.setPlayerListHeader("§6Tournoi des Légendes  §f-  §3Edition " + edition + "\n"
//						+ format 
//						+ "\n§aAll Logged players :"
//						);
//			}
//		}.runTaskTimer(main, 0, 20);
//		
//		
//		p.setPlayerListFooter("");
//	}
	
	
	
	public static List<List<String>> getMembers(String team) {
		List<List<String>> members = new ArrayList<>();
		HashMap<String, String> teams = TeamsManager.getTeams();
		Player p;
		ChatColor c;
		
		for (String uuid : teams.keySet()) {
			p = NPCManager.getNpcPlayerIfItIs(uuid);
			if(p == null) p = Bukkit.getPlayer(UUID.fromString(uuid));
			
			switch(team) {
			case "red":
				switch(teams.get(uuid)) {
				case "red":
					c = ChatColor.RED;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "blue":
				switch(teams.get(uuid)) {
				case "blue":
					c = ChatColor.BLUE;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "green":
				switch(teams.get(uuid)) {
				case "green":
					c = ChatColor.GREEN;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "yellow":
				switch(teams.get(uuid)) {
				case "yellow":
					c = ChatColor.YELLOW;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "purple":
				switch(teams.get(uuid)) {
				case "purple":
					c = ChatColor.LIGHT_PURPLE;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "aqua":
				switch(teams.get(uuid)) {
				case "aqua":
					c = ChatColor.AQUA;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "black":
				switch(teams.get(uuid)) {
				case "black":
					c = ChatColor.DARK_GRAY;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			case "orange":
				switch(teams.get(uuid)) {
				case "orange":
					c = ChatColor.GOLD;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					members.add(Arrays.asList(c.toString(), TeamsManager.getPseudo(uuid)));
					break;
				}
			break;
			}
			
		}
		
		return members;
	}
	
	public static void updateTabList() {
		List<Integer> teamsbal = BaltopCommand.getTeamsBal();
		format = "\n§f---------- Equipes ----------";
		
		String red = "";
		for (List<String> member : getMembers("red")) {
			red = red + member.get(0) + member.get(1) + "\n";
		}
		String blue = "";
		for (List<String> member : getMembers("blue")) {
			blue = blue + member.get(0) + member.get(1) + "\n";
		}
		String green = "";
		for (List<String> member : getMembers("green")) {
			green = green + member.get(0) + member.get(1) + "\n";
		}
		String yellow = "";
		for (List<String> member : getMembers("yellow")) {
			yellow = yellow + member.get(0) + member.get(1) + "\n";
		}
		String purple = "";
		for (List<String> member : getMembers("purple")) {
			purple = purple + member.get(0) + member.get(1) + "\n";
		}
		String aqua = "";
		for (List<String> member : getMembers("aqua")) {
			aqua = aqua + member.get(0) + member.get(1) + "\n";
		}
		String black = "";
		for (List<String> member : getMembers("black")) {
			black = black + member.get(0) + member.get(1) + "\n";
		}
		String orange = "";
		for (List<String> member : getMembers("orange")) {
			orange = orange + member.get(0) + member.get(1) + "\n";
		}
		List<List<String>> members = new ArrayList<>();
		for(int i = 0; i < BaltopCommand.getTeamClassement().size(); i++) {
			switch(BaltopCommand.getTeamClassement().get(i)) {
			case "red":
				members = getMembers("red");
				if(!members.isEmpty()) {
					format = format
							+ "\n§r§4Red Rocket"  + " §f- §6" + teamsbal.get(0) + " Coins"
							+ "\n" + red;
				}
				break;
			case "blue":
				members = getMembers("blue");
				if(!members.isEmpty()) {
					format = format
							+ "\n§1Blue Whale" + " §f- §6" + teamsbal.get(1) + " Coins"
							+ "\n" + blue;
				}
				break;
			case "green":
				members = getMembers("green");
				if(!members.isEmpty()) {
					format = format
							+ "\n§2Green Turtle" + " §f- §6" + teamsbal.get(2) + " Coins"
							+ "\n" + green;
				}
				break;
			case "yellow":
				members = getMembers("yellow");
				if(!members.isEmpty()) {
					format = format
							+ "\n§l§eYellow Stone" + " §r§f- §6" + teamsbal.get(3) + " Coins"
							+ "\n" + yellow;
				}
				break;
			case "purple":
				members = getMembers("purple");
				if(!members.isEmpty()) {
					format = format
							+ "\n§5Purple Amethyst" + " §f- §6" + teamsbal.get(4) + " Coins"
							+ "\n" + purple;
				}
				break;
			case "aqua":
				members = getMembers("aqua");
				if(!members.isEmpty()) {
					format = format
							+ "\n§3Aqua Dolphin" + " §f- §6" + teamsbal.get(5) + " Coins"
							+ "\n" + aqua;
				}
				break;
			case "black":
				members = getMembers("black");
				if(!members.isEmpty()) {
					format = format
							+ "\n§0Black Raven" + " §f- §6" + teamsbal.get(6) + " Coins"
							+ "\n" + black;
				}
				break;
			case "orange":
				members = getMembers("orange");
				if(!members.isEmpty()) {
					format = format
							+ "\n§l§6Orange Mechanic" + " §r§f- §6" + teamsbal.get(7) + " Coins"
							+ "\n" + orange;
				}
				break;
			}
		}
		format = format + "§f----------------------------";
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setPlayerListHeader("§6Tournoi des Légendes  §f-  §3Edition " + edition + "\n"
					+ format 
					+ "\n§aAll Logged players :"
					);
		}
	}
	
}
