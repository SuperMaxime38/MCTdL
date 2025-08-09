package mctdl.game.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;

public class NPCCommand implements CommandExecutor{

	static Main main;
	public NPCCommand(Main main) {
		NPCCommand.main = main;
	}
	
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if(s instanceof Player) {
			Player p = (Player) s;
			if(args.length == 0) {
			} else if(args.length == 1) {
				if(args[0].equals("killall")) {
					NPCManager.killAllNPCs(p);
				}
				if(args[0].equals("refresh")) {
					NPCManager.killAllNPCs(p);
					NPCManager.onPlayerJoin(p, 60);
				}
				if(args[0].equals("refreshall")) {
					for (Player pl : Bukkit.getOnlinePlayers()) {
						NPCManager.killAllNPCs(pl);
						NPCManager.onPlayerJoin(pl, 60);
					}
				}
				if(args[0].equals("list")) {
					s.sendMessage("npcs: " + NPCManager.getNPCs().toString());
				}
			} else if(args.length == 2) {
			} else {
				
			}
		}
		
		return false;
	}

}
