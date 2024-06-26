package mctdl.game.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.games.lobby.FortuneWheel;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.Time;

public class TdLCommand implements CommandExecutor{

	static Main main;
	public TdLCommand(Main main) {
		TdLCommand.main = main;
	}
	
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		String playername; //Variables pouvant etre utilis�s dans le code
		String teamname;
		int amount;
		String h = Main.header();
		Player p;
		
		if(args.length == 0) {
			s.sendMessage("Liste des commandes pour �a/mctdl �f:");
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.setInvisible(false);
			}
		} else if(args.length == 1) {
			if(args[0].equals("updateconfigs")) {
				TeamsManager.updateConfig(main);
				MoneyManager.updateConfig(main);
				NPCManager.updateConfig(main);
				s.sendMessage(h + "Les configs ont �t� mises � jour");
				System.out.println("[MCTdL] > Les configs ont �t� mises � jour");
				return true;
			}
			if(args[0].equals("loadhashmaps")) {
				TeamsManager.loadHashMap(main);
				MoneyManager.loadHashMap(main);
				NPCManager.loadHashMap(main);
				s.sendMessage(h + "Les hashmaps ont �t� charg�s selon les fichiers yml");
				System.out.println("[MCTdL] > Hashmaps loaded from .yml");
			}
			if(args[0].equals("loadteam")) {
				TeamsManager.loadHashMap(main);
				System.out.println("La hashmap a charg�");
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
					s.sendMessage("Vous avez " + MoneyManager.getPlayerPoutres(p.getName()) + " poutres");
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
				PlayerData.fileCheck(main);
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
				s.sendMessage("CurrentTick : " + Time.getCurrentTick());
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
					s.sendMessage(h + "La hashmap a �t� charg� selon les donn�es enregistr�es dans la config");
					return true;
				}
			}
		} else if(args.length == 3) {
			if(args[0].equals("money")) {
				if(args[1].equals("get")) {
					playername = args[2];
					s.sendMessage(h + playername + " a " + MoneyManager.getPlayerMoney(playername) + " Coins");
					return true;
				}
			}
			if(args[0].equals("team")) {
				if(args[1].equals("get")) {
					playername = args[2];
					s.sendMessage(h + playername + " est dans l'�quipe " + TeamsManager.getPlayerTeam(playername));
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					TeamsManager.removePlayerTeam(playername);
					s.sendMessage(h + playername  + " a �t� retir� de son �quipe");
					return true;
				}
			}
		} else if(args.length == 4){
			if(args[0].equals("team")) {
				if(args[1].equals("set")) {
					playername = args[2];
					teamname = args[3];
					
					TeamsManager.setPlayerTeam(playername, teamname);
					TeamsManager.updateConfig(main);
					
					s.sendMessage(h + "Le joueur �a" + playername + " �fa �t� transfer� dans l'�quipe �6" + teamname);
					return true;
				}
			}
			if(args[0].equals("money")) {
				if(args[1].equals("set")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.setPlayerMoney(playername, amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + amount);
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.removePlayerMoney(playername, amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + MoneyManager.getPlayerMoney(playername));
					return true;
				}
				if(args[1].equals("add")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.addPlayerMoney(playername, amount);
					s.sendMessage(h + "L'argent de " + playername  + " est de " + MoneyManager.getPlayerMoney(playername));
					return true;
				}
			}
			if(args[0].equals("poutre")) { //Poutre money managing commands
				if(args[1].equals("set")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.setPlayerPoutres(playername, amount);
					return true;
				}
				if(args[1].equals("remove")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.removePlayerPoutre(playername, amount);
					return true;
				}
				if(args[1].equals("add")) {
					playername = args[2];
					amount = Integer.parseInt(args[3]);
					
					MoneyManager.addPlayerPoutre(playername, amount);
					return true;
				}
			}
			
		} else {
			
		}
		
		return false;
	}

}
