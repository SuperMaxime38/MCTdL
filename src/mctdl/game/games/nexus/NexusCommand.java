package mctdl.game.games.nexus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mctdl.game.Main;

public class NexusCommand implements CommandExecutor{
	
	static Main main;
	public NexusCommand(Main main) {
		NexusCommand.main = main;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if(args.length == 0) {
			s.sendMessage("Ouiii j'ai peut être eu la flm de lister tt les cmd possibles");
			return true;
		} else if(args.length == 1) {
			switch(args[0]) {
			case "start":
				Nexus.start(false);
				s.sendMessage("Nexus started");
				return true;
			case "stop":
				if(Nexus.enabled == false) {
					s.sendMessage("Faudrait d'abord penser à démarrer une game avant de l'arrêter...");
				} else {
					Nexus.stop();
					s.sendMessage("Nexus stopped");
				}
				return true;
			}
		} else {
			
		}
		
		return false;
	}

}
