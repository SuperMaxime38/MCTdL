package mctdl.game.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;
import mctdl.game.teams.TeamsManager;

public class BaltopCommand implements CommandExecutor{

	static Main main;
	public BaltopCommand(Main main) {
		BaltopCommand.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		Player p;
		if(args.length == 0) {
			List<String> result = MoneyManager.getRegsPlayer().entrySet().stream()
		            .sorted(Comparator.comparingLong(Map.Entry<String, Integer>::getValue)
		                    .reversed()
		                    .thenComparing(Map.Entry::getKey)
		            )
		            .map(it -> it.getKey())
		            .collect(Collectors.toList());
			
			String output = "§6----------------------------------------------\n"
				 +"§aLes joueurs les plus riches sont :§f";
			int order = 0;
			if(result.size() >= 10) {
				for(int i = 0; i < 10; i++) {
					order = i + 1;
					output = output + "§f\n " + order + "- "+ TeamsManager.getTeamColor(result.get(i)) + TeamsManager.getPseudo(result.get(i)) + " §f: " + MoneyManager.getPlayerMoney(result.get(i)) + " §6Coins";
				}
			} else {
				for(int i = 0; i < result.size(); i++) {
					order = i + 1;
					output = output + "§f\n " + order +"- " + TeamsManager.getTeamColor(result.get(i)) + TeamsManager.getPseudo(result.get(i)) + " §f: " + MoneyManager.getPlayerMoney(result.get(i)) + " §6Coins";
				}
			}
			if(s instanceof Player) {
				p = (Player) s;
				order = getClassement().indexOf(p.getUniqueId().toString()) + 1;
				output = output + "\n \n§f " + order +"- " + TeamsManager.getTeamColor(p.getUniqueId().toString()) + p.getName() + " §f: " + MoneyManager.getPlayerMoney(p.getUniqueId().toString()) + " §6Coins";
				
				String team = TeamsManager.getPlayerTeam(p.getUniqueId().toString());
				if(!team.equals("none")) {
					order = getTeamClassement().indexOf(team) +1;
					output = output + "\n §f" + order + "- " + TeamsManager.getTeamName(p.getUniqueId().toString()) + " §f: " + getTeamBal(team) + " §6Coins";
					//getTeamClassement().indexOf(TeamsManager.getPlayerTeam(p.getName()))
					//TeamsManager.getTeamColor(p.getName()) + TeamsManager.getTeamName(p.getName())
				}
			}
			output = output + "\n§6----------------------------------------------";
			s.sendMessage(output);
		} else if(args.length == 1) {
		//PLUS TARD PR LE CLASSEMENT DES TEAM ETC...
		} else {
			
		}
		
		return false;
	}
	
	public static List<String> getClassement() {
		List<String> result = MoneyManager.getRegsPlayer().entrySet().stream()
	            .sorted(Comparator.comparingLong(Map.Entry<String, Integer>::getValue)
	                    .reversed()
	                    .thenComparing(Map.Entry::getKey)
	            )
	            .map(it -> it.getKey())
	            .collect(Collectors.toList());
		
		return result;
	}
	
	public static List<Integer> getTeamsBal() {
		HashMap<String, String> teams = TeamsManager.getTeams();
		int red = 0;
		int blue = 0;
		int green = 0;
		int yellow = 0;
		int purple = 0;
		int aqua = 0;
		int black = 0;
		int orange = 0;
		for (String player : teams.keySet()) {
			switch(teams.get(player)) {
			case "red":
				red = red + MoneyManager.getPlayerMoney(player);
				break;
			case "blue":
				blue = blue + MoneyManager.getPlayerMoney(player);
				break;
			case "green":
				green = green + MoneyManager.getPlayerMoney(player);
				break;
			case "yellow":
				yellow = yellow + MoneyManager.getPlayerMoney(player);
				break;
			case "purple":
				purple = purple + MoneyManager.getPlayerMoney(player);
				break;
			case "aqua":
				aqua = aqua + MoneyManager.getPlayerMoney(player);
				break;
			case "black":
				black = black + MoneyManager.getPlayerMoney(player);
				break;
			case "orange":
				orange = orange + MoneyManager.getPlayerMoney(player);
				break;
			}
		}
		List<Integer> balances = new ArrayList<Integer>();
		balances.add(0, red);
		balances.add(1, blue);
		balances.add(2, green);
		balances.add(3, yellow);
		balances.add(4, purple);
		balances.add(5, aqua);
		balances.add(6, black);
		balances.add(7, orange);
		
		return balances;
	}
	
	public static Integer getTeamBal(String team) {
		List<Integer> bals = getTeamsBal();
		switch(team) {
		case "red": return bals.get(0);
		case "blue": return bals.get(1);
		case "green": return bals.get(2);
		case "yellow": return bals.get(3);
		case "purple": return bals.get(4);
		case "aqua": return bals.get(5);
		case "black": return bals.get(6);
		case "orange": return bals.get(7);
		default: return 0;
		}
	}
	
	public static List<String> getTeamClassement() {
		List<Integer> teamsbal = getTeamsBal();
		HashMap<String, Integer> teams = new HashMap<String, Integer>();
		teams.put("red", teamsbal.get(0));
		teams.put("blue", teamsbal.get(1));
		teams.put("green", teamsbal.get(2));
		teams.put("yellow", teamsbal.get(3));
		teams.put("purple", teamsbal.get(4));
		teams.put("aqua", teamsbal.get(5));
		teams.put("black", teamsbal.get(6));
		teams.put("orange", teamsbal.get(7));
		
		List<String> result = teams.entrySet().stream()
	            .sorted(Comparator.comparingLong(Map.Entry<String, Integer>::getValue)
	                    .reversed()
	                    .thenComparing(Map.Entry::getKey)
	            )
	            .map(it -> it.getKey())
	            .collect(Collectors.toList());
		return result;
	}

}
