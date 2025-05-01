package mctdl.game.games.meltdown;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;

public class MDCommand implements CommandExecutor{
	
	static Main main;
	public MDCommand(Main main) {
		MDCommand.main = main;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		Player p = null;
		
		if(args.length == 0) {
			s.sendMessage("§a/meltdown §fCommand List :"
					+ "\n§a/meltdown start §f: Démarre une game de meltdown"
					+ "\n§a/meltdown stop §f: Arrête une partie de meltdown"
					+ "\n§a/meltdown getdata §f: get raw players datas"
					+ "\n§a/meltdown freeze §f: Freeze someone"
					+ "\n§a/meltdown unfreeze §f: Unfreeze someone");
			return true;
			
		} else if(args.length == 1) {
			if(args[0].equals("start")) {
				Meltdown.enable();
				return true;
			}
			if(args[0].equals("stop")) {
				new Meltdown(main);
				Meltdown.disable(main);
				Bukkit.broadcastMessage("§cLa partie a été interrompue...");
				
				return true;
			}
			if(args[0].equals("alarm")) {
				List<Integer> room = new ArrayList<>();
				room.add(-37);
				room.add(-33);
				room.add(12);
				room.add(12);
				room.add(-27);
				room.add(12);
				room.add(-24);
				room.add(16);
				MDMap.alarmTrigger(room);
				return true;
			}
			if(args[0].equals("door")) {
				List<Integer> room = new ArrayList<>();
				room.add(-37);
				room.add(12);
				room.add(-33);
				room.add(17);
				room.add(-27);
				room.add(12);
				room.add(-24);
				room.add(16);
				MDMap.doorTrigger(room);
				return true;
			}
			if(args[0].equals("room")) {
				List<Integer> room = new ArrayList<>();
				room.add(-16);
				room.add(43);
				room.add(-7);
				room.add(53);

				room.add(-16);
				room.add(56);
				room.add(-12);
				room.add(60);
				MDMap.roomTrigger(room, main);
				
				return true;
			}
			if(args[0].equals("test")) {
				p = (Player) s;
				Meltdown.particleEffect(p.getLocation(), Material.AIR, p.getLocation().getBlock());
				return true;
			}
			if((args[0].equals("genmap"))) {
				MDMap.generateMap(main);
				return true;
			}
			if(args[0].equals("in_range_ai")) {
				s.sendMessage("IAs à portée de vue : " + Meltdown.inViewNPCs);
			}
		} else if(args.length == 2) {
			if(args[0].equals("getdata")) {
				if(!Meltdown.isEnabled()) {
					s.sendMessage("Aucune partie n'est en cours");
					return true;
				}
				p = Bukkit.getPlayer(args[1]);
				if(p == null) {
					System.out.println("Unknown player");
					return true;
				}
				s.sendMessage(Meltdown.getRawPlayerDatas(args[1]).toString());
			}
			if(args[0].equals("freeze")) {
				String uuid = TeamsManager.getUUIDByPseudo(args[1]).toString();
				if(TeamsManager.getPlayerTeam(uuid).equals("none")) {
					s.sendMessage("Unknown player");
					return true;
				}
				System.out.println("Freezing test : " + args[1]);
				Meltdown.iceCube(uuid);
				return true;
			}
			if(args[0].equals("unfreeze")) {
				String uuid = TeamsManager.getUUIDByPseudo(args[1]).toString();
				if(TeamsManager.getPlayerTeam(uuid).equals("none")) {
					s.sendMessage("Unknown player");
					return true;
				}
				Meltdown.unFreeze(uuid);
				return true;
			}
			if(args[0].equals("setgen")) {
				MeltdownFiles.setMapGenerated(main, Boolean.valueOf(args[1]));
				s.sendMessage("The map is set as generated (if u wrote true)");
			}
			if(args[0].equals("spectest")) {
				p = (Player) s;
				p.setSpectatorTarget(Bukkit.getPlayer(args[1]));
				p.sendMessage("set " + args[1] + " as your spectating target");
			}
		}
		return false;
	}

}
