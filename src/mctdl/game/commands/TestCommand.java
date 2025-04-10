package mctdl.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import mctdl.game.utils.objects.Canon;
import net.minecraft.server.v1_16_R3.EntityPlayer;

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
					EntityPlayer npc = NPCManager.npcBuilder("Test", "PoutreCosmique", p.getLocation(), p, main);
					NPCManager.showNpcWithoutTabFor(main, npc, p, null);
				}
				
				return true;
			}
		}
		
		return false;
	}

}
