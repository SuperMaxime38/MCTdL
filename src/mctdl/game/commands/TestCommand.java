package mctdl.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import mctdl.game.npc.NPCManager;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.objects.Canon;

public class TestCommand implements CommandExecutor{
	
	static Main main;
	public TestCommand(Main main) {
		TestCommand.main = main;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

		if(args.length == 0) {
			
		}
		if(args.length == 1) {
			switch(args[0]) {
			case "canon":
				if(s instanceof Player) {
					Player p = (Player) s;
					Canon.placeCanon(p, p.getLocation(), main);
				}
				return true;
			case "npc":
				if(s instanceof Player) {
					Player p = (Player) s;
					MeltdownNPC npc = new MeltdownNPC(main, p, "red");
					
				    TabManager.updateTabList();
				}
				
				return true;
			}
		}
		
		if(args.length == 2) {
			if(args[0].equals("npc")) {
				
				if(s instanceof Player) {
					Player p = (Player) s;
					MeltdownNPC npc = new MeltdownNPC(main, p, args[1]);

				    TabManager.updateTabList();
				}
			}
		}
		
		if(args.length == 3) {
			if(args[0].equals("npc")) {
				if(args[1].equals("kill")) {
					NPCManager.destroyNPC(NPCManager.getNpcByUUID(TeamsManager.getUUIDByPseudo(args[2]).toString()));
					System.out.println("Killed NPC " + args[2]);
				}
			}
		}
		
		return false;
	}

}
