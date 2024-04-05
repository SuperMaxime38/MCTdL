package mctdl.game.tablist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.commands.BaltopCommand;
import mctdl.game.teams.TeamsManager;

public class TabManager implements Listener{
	
	static Main main;
	public TabManager(Main main) {
		TabManager.main = main;
	}
	
	@EventHandler
	public static void baseTab(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		tabClock(p);
		
	}
	
	public static void tabClock(Player p) {
		new BukkitRunnable() {
			
			String edition = main.getConfig().getString("edition");
			
			@Override
			public void run() {
				List<Integer> teamsbal = BaltopCommand.getTeamsBal();
				String format = "\n§f---------- Equipes ----------";
				
				String red = "";
				for (String members : getMembers("red")) {
					red = red + members + "\n";
				}
				String blue = "";
				for (String members : getMembers("blue")) {
					blue = blue + members + "\n";
				}
				String green = "";
				for (String members : getMembers("green")) {
					green = green + members + "\n";
				}
				String yellow = "";
				for (String members : getMembers("yellow")) {
					yellow = yellow + members + "\n";
				}
				String purple = "";
				for (String members : getMembers("purple")) {
					purple = purple + members + "\n";
				}
				String aqua = "";
				for (String members : getMembers("aqua")) {
					aqua = aqua + members + "\n";
				}
				String black = "";
				for (String members : getMembers("black")) {
					black = black + members + "\n";
				}
				String orange = "";
				for (String members : getMembers("orange")) {
					orange = orange + members + "\n";
				}
				List<String> members = new ArrayList<String>();
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
				p.setPlayerListHeader("§6Tournoi des Légendes  §f-  §3Edition " + edition + "\n"
						+ format 
						+ "\n§aAll Logged players :"
						);
			}
		}.runTaskTimer(main, 0, 20);
		
		
		p.setPlayerListFooter("");
	}
	
	
	public static List<String> getMembers(String team) {
		List<String> members = new ArrayList<String>();
		HashMap<String, String> teams = TeamsManager.getTeams();
		ChatColor c;
		
		for (String players : teams.keySet()) {
			Player p = Bukkit.getPlayerExact(players);
			switch(team) {
			case "red":
				switch(teams.get(players)) {
				case "red":
					c = ChatColor.RED;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "blue":
				switch(teams.get(players)) {
				case "blue":
					c = ChatColor.BLUE;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "green":
				switch(teams.get(players)) {
				case "green":
					c = ChatColor.GREEN;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "yellow":
				switch(teams.get(players)) {
				case "yellow":
					c = ChatColor.YELLOW;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "purple":
				switch(teams.get(players)) {
				case "purple":
					c = ChatColor.LIGHT_PURPLE;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "aqua":
				switch(teams.get(players)) {
				case "aqua":
					c = ChatColor.AQUA;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "black":
				switch(teams.get(players)) {
				case "black":
					c = ChatColor.DARK_GRAY;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			case "orange":
				switch(teams.get(players)) {
				case "orange":
					c = ChatColor.GOLD;
					if(p == null) {
						c = ChatColor.GRAY;
					}
					players = c + players;
					members.add(players);
					break;
				}
			break;
			}
			
		}
		
		return members;
	}
	
}
