package mctdl.game.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mctdl.game.games.lobby.PouleZooka;

public class PouleZookaCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		Player p;
		
		if(args.length == 0) {
			PouleZooka.spawnChicken(new Location(Bukkit.getWorlds().get(0), -12, 21, 50), 8);
		} else if(args.length == 1) {
			p = (Player) s;
			p.getInventory().addItem(PouleZooka.getBazooka());
		}
		
		return false;
	}

}
