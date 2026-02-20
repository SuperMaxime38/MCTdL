package mctdl.game.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.games.lobby.FortuneWheel;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.FileLoader;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Time;

public class TdLCommand implements CommandExecutor{

	static Main main;
	String h;

	List<String> red = new ArrayList<String>(Arrays.asList("Star_Lord", "Lorem_Impsum", "Red_Steel_Beam", "Fireplace"));
	List<String> blue = new ArrayList<String>(Arrays.asList("Fat_Seal", "Sea_You_Soon", "Titanic_Driver", "Lipton"));
	List<String> green = new ArrayList<String>(Arrays.asList("Grass_Toucher", "G§ant_Vert", "JaimeLesPommes", "Harry_Coteur"));
	List<String> yellow = new ArrayList<String>(Arrays.asList("YellowSnow", "ShinyMe", "Sparkles", "SunWeightedGuy"));
	List<String> purple = new ArrayList<String>(Arrays.asList("Jaded", "Crystalline", "PASDINSPI1", "PASDINSPI2"));
	List<String> aqua = new ArrayList<String>(Arrays.asList("RapinDolphin", "Aqualand", "SlipMan", "DrinkWater"));
	List<String> black = new ArrayList<String>(Arrays.asList("Shockdart_Vador", "IGotNPass", "UnZoizeau", "SoMuchDarkSasuke"));
	List<String> orange = new ArrayList<String>(Arrays.asList("Le_poisson_Steve", "CarSmasher", "MecanismMyLove", "BeepBeepImABot"));
	
	Random rdm = new Random();
	
	public TdLCommand(Main main) {
		TdLCommand.main = main;
		h = Main.header();
	}
	
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		String playername; //Variables pouvant etre utilis§s dans le code
		String teamname;
		int amount;
		Player p;
		
		if(args.length == 0) {
			s.sendMessage("Liste des commandes pour §a/mctdl §f:");
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.setInvisible(false);
			}
		} else if(args.length == 1) {
			if(args[0].equals("updateconfigs")) {
				TeamsManager.updateConfig(main);
				MoneyManager.updateConfig(main);
				NPCManager.updateConfig(main);
				s.sendMessage(h + "Les configs ont été mises à jour");
				System.out.println("[MCTdL] > Les configs ont été mises à jour");
				return true;
			}
			if(args[0].equals("loadhashmaps")) {
				TeamsManager.loadHashMap(main);
				MoneyManager.loadHashMap(main);
				NPCManager.loadHashMap(main);
				s.sendMessage(h + "Les hashmaps ont §t§ charg§s selon les fichiers yml");
				System.out.println("[MCTdL] > Hashmaps loaded from .yml");
			}
			if(args[0].equals("loadteam")) {
				TeamsManager.loadHashMap(main);
				System.out.println("La hashmap a chargé");
			}
			if(args[0].equals("teams")) {
				s.sendMessage(TeamsManager.getTeams().toString());
				return true;
			}
			if(args[0].equals("balances")) {
				s.sendMessage(MoneyManager.getRegsPlayer().toString());
				return true;
			}
			if(args[0].equals("poutres")) { //POUTREEE
				if(s instanceof Player) {
					p = (Player) s;
					s.sendMessage("Vous avez " + MoneyManager.getPlayerPoutres(TeamsManager.getUUIDByPseudo(p.getName()).toString()) + " poutres");
					return true;
				}
			}
			if(args[0].equals("fwheel")) {
				if(s instanceof Player) {
					p = (Player) s;
					FortuneWheel.gui(p);
				}
			}
			if(args[0].equals("pdata-test")) {
				FileLoader.loadFile("lobby.yml", "playerdata");
				// Future maxime if you don't use this delete it
				// I updated it bcs I forgot what it is used for
			}
			if(args[0].equals("pdata-load")) {
				PlayerData.loadHashMap(main);
			}
			if(args[0].equals("pdata-reg")) {
				p = (Player) s;
				PlayerData.registerPlayer(p);  
			}
			if(args[0].equals("pdata-save")) {
				p = (Player) s;
				PlayerData.saveItems(p);  
			}
			if(args[0].equals("pdata-up")) {
				PlayerData.updateConfig(main);
			}
			if(args[0].equals("vote")) {
				GameVoting.displayProposal(main);
				System.out.println(GameVoting.getArmorStandIDs());
			}
			if(args[0].equals("votecl")) {
				GameVoting.clearDisplay();
			}
			if(args[0].equals("ressource")) {
				p = (Player) s;
				p.setResourcePack("file:\\\\\\C:\\Users\\miche\\AppData\\Roaming\\.minecraft-1.16.5\\resourcepacks\\MDTextures");
			}
			if(args[0].equals("spectate-looking")) {
				p = (Player) s;
				p.setSpectatorTarget(p.getNearbyEntities(3, 3, 3).get(0));
			}
			if(args[0].equals("uptime")) {
				s.sendMessage("Uptime : " + Time.getUptime());
				s.sendMessage("Current Tick : " + Time.getCurrentTick());
			}
			if(args[0].equals("fillteams")) {
				Meltdown.clearNPCs();
				for(String team : TeamsManager.getNonEmptyTeams()) {
					s.sendMessage("Filling team " + team);
					for(int i = 4 - TeamsManager.getTeamMembers(team).size(); i > 0; i--) {
						
						MeltdownNPC npc = new MeltdownNPC(main, nameStuff(team), team);
						Meltdown.addNPC(npc);
						s.sendMessage("Added " + npc.getNPC().getName());
					}
				}
				s.sendMessage("Teams have been filled");
				return true;
			}
		} else if(args.length == 2) {
			if(args[0].equals("teams")) {
				if(args[1].equals("clear")) {
					TeamsManager.clearTeams(main);
				}
			}
			if(args[0].equals("money")) {
				if(args[1].equals("loadhashmap")) {
					MoneyManager.loadHashMap(main);
					s.sendMessage(h + "La hashmap a été chargé selon les donn§es enregistrées dans la config");
					return true;
				}
			}

			if(args[0].equals("fillteams")) {
				if(args[1].equals("-a")) {
					Meltdown.clearNPCs();
					for(String team : Arrays.asList("red", "blue", "green", "yellow", "purple", "aqua", "orange", "black")) {
						s.sendMessage("Filling team " + team);
						for(int i = 4 - TeamsManager.getTeamMembers(team).size(); i > 0; i--) {
							MeltdownNPC npc = new MeltdownNPC(main, nameStuff(team), team);
							s.sendMessage("Added " + npc.getNPC().getName());
						}
					}
					s.sendMessage("All teams have been filled");
					return true;
				}
				
			}
		} else if(args.length == 3) {
			if(args[0].equals("money")) {
				if(args[1].equals("get")) {
					playername = args[2];
					//if(!isLogged(s, playername)) return true;
					s.sendMessage(h + playername + " a " + MoneyManager.getPlayerMoney(TeamsManager.getUUIDByPseudo(playername).toString()) + " Coins");
					return true;
				}
			}
			if(args[0].equals("team")) {
				if(args[1].equals("get")) {
					playername = args[2];
					//if(!isLogged(s, playername)) return true;
					s.sendMessage(h + playername + " est dans l'§quipe " + TeamsManager.getPlayerTeam(TeamsManager.getUUIDByPseudo(playername).toString()));
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					//if(!isLogged(s, playername)) return true;
					TeamsManager.removePlayerTeamByName(playername);
					s.sendMessage(h + playername  + " a §t§ retir§ de son §quipe");
					return true;
				}
			}
		} else if(args.length == 4){
			if(args[0].equals("team")) {
				if(args[1].equals("set")) {
					playername = args[2];
					teamname = args[3];
					
					TeamsManager.setPlayerTeam(TeamsManager.getUUIDByPseudo(playername).toString(), teamname);
					
					s.sendMessage(h + "Le joueur §a" + playername + " §fa été transferé dans l'équipe §6" + teamname);
					return true;
				}
			}
			if(args[0].equals("money")) {
				if(args[1].equals("set")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}
					
					MoneyManager.setPlayerMoney(TeamsManager.getUUIDByPseudo(playername).toString(), amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + amount);
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}
					
					String uuid = TeamsManager.getUUIDByPseudo(playername).toString();
					
					MoneyManager.removePlayerMoney(uuid, amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + MoneyManager.getPlayerMoney(uuid));
					return true;
				}
				if(args[1].equals("add")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}

					String uuid = TeamsManager.getUUIDByPseudo(playername).toString();
					
					MoneyManager.addPlayerMoney(uuid, amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + MoneyManager.getPlayerMoney(uuid));
					return true;
				}
			}
			if(args[0].equals("poutre")) { //Poutre money managing commands
				if(args[1].equals("set")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}
					
					MoneyManager.setPlayerPoutres(TeamsManager.getUUIDByPseudo(playername).toString(), amount);
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}
					MoneyManager.removePlayerPoutre(TeamsManager.getUUIDByPseudo(playername).toString(), amount);
					return true;
				}
				if(args[1].equals("add")) {
					playername = args[2];
					
					try {
						amount = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						return false;
					}
					
					MoneyManager.addPlayerPoutre(TeamsManager.getUUIDByPseudo(playername).toString(), amount);
					return true;
				}
			}
			
		} else {
			
		}
		
		return false;
	}
	
	private String nameStuff(String team) {
		switch(team) {
		case "red":
			if(red.isEmpty()) return "Dummy";
			String name = red.get(rdm.nextInt(red.size()));
			red.remove(name);
			return name;
		case "blue":
			if(blue.isEmpty()) return "Dummy";
			name = blue.get(rdm.nextInt(blue.size()));
			blue.remove(name);
			return name;
		case "green":
			if(green.isEmpty()) return "Dummy";
			name = green.get(rdm.nextInt(green.size()));
			green.remove(name);
			return name;
		case "yellow":
			if(yellow.isEmpty()) return "Dummy";
			name = yellow.get(rdm.nextInt(yellow.size()));
			yellow.remove(name);
			return name;
		case "purple":
			if(purple.isEmpty()) return "Dummy";
			name = purple.get(rdm.nextInt(purple.size()));
			purple.remove(name);
			return name;
		case "aqua":
			if(aqua.isEmpty()) return "Dummy";
			name = aqua.get(rdm.nextInt(aqua.size()));
			aqua.remove(name);
			return name;
		case "black":
			if(black.isEmpty()) return "Dummy";
			name = black.get(rdm.nextInt(black.size()));
			black.remove(name);
			return name;
		case "orange":
			if(orange.isEmpty()) return "Dummy";
			name = orange.get(rdm.nextInt(orange.size()));
			orange.remove(name);
			return name;
		default:
			return null;
			
		}
	}
	
//	private boolean isLogged(CommandSender s,String playername) {
//		if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playername))) return true;
//
//		s.sendMessage(h + "Le joueur §a" + playername + " §fne semble pas connecté");
//		return false;
//	}

}
