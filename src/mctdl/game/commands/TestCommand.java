package mctdl.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.ai_trainer.Environnement;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.objects.Canon;

public class TestCommand implements CommandExecutor{
	
	List<PlayerAI> tests;
	
	static Main main;
	public TestCommand(Main main) {
		TestCommand.main = main;
		tests = new ArrayList<>();
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
				MeltdownNPC npc = new MeltdownNPC(main, "red");
				tests.add(npc.getNPC());
				
			    TabManager.updateTabList();
				
				return true;
			case "env":
				MeltdownNPC npc2 = new MeltdownNPC(main, "red");
				tests.add(npc2.getNPC());
				Environnement env = new Environnement(npc2.getNPC());
				new BukkitRunnable() {

					@Override
					public void run() {
						env.update();
					}
					
				}.runTaskTimer(main,60, 60);
				return true;
			}
		}
		
		if(args.length == 2) {
			if(args[0].equals("npc")) {
				MeltdownNPC npc = new MeltdownNPC(main, args[1]);
				tests.add(npc.getNPC());

			    TabManager.updateTabList();
			}
		}
		
		if(args.length == 3) {
			if(args[0].equals("npc")) {
				if(args[1].equals("kill")) {
					NPCManager.destroyNPC(NPCManager.getNpcByUUID(TeamsManager.getUUIDByPseudo(args[2]).toString()));
					System.out.println("Killed NPC " + args[2]);
				}
			}
			if(args[0].equals("mv")) {
				PlayerAI npc = null;
				for(PlayerAI ai : tests) {
					if(ai.getName().equals(args[1])) npc = ai;
				}
				if(npc == null) {
					s.sendMessage("NPC not found");
					return true;
				}
				switch(args[2]) {
				case "f":
					npc.walk(PlayerAI.WALK_FORWARD);
					break;
				case "b":
					npc.walk(PlayerAI.WALK_BACKWARD);
					break;
				case "l":
					npc.walk(PlayerAI.WALK_LEFT);
					break;
				case "r":
					npc.walk(PlayerAI.WALK_RIGHT);
					break;
				case "j":
					npc.jump();
					break;
				}
				return true;
			}
			if(args[0].equals("rt")) {
				PlayerAI npc = null;
				for(PlayerAI ai : tests) {
					if(ai.getName().equals(args[1])) npc = ai;
				}
				if(npc == null) {
					s.sendMessage("NPC not found");
					return true;
				}
				try {
					float value = Float.parseFloat(args[2]);
					s.sendMessage("Rotated " + value + " | pitch: " + npc.getBukkitEntity().getLocation().getPitch());
					npc.rotate(value, npc.getBukkitEntity().getLocation().getPitch());
				} catch (NumberFormatException e) {
					s.sendMessage("Invalid value (this isn't a number dude)");
				}

				return true;
				
			}
		}
		
		return false;
	}

}
