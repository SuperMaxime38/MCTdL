package mctdl.game.games.deathswap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mctdl.game.Main;

public class DeathSwapCommand implements CommandExecutor{

	
	static Main main;
	public DeathSwapCommand(Main main) {
		DeathSwapCommand.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if(args.length == 0) {
			s.sendMessage("/deathswap §astart §f: Start the game");
			s.sendMessage("/deathswap §astop §f: Stop the game");
			s.sendMessage("/deathswap §agetdata <player> §f: Get random data");
		} else if(args.length == 1) {
			if(args[0].equals("start")) {
				//new DeathSwap(main);
				DeathSwap.enable();
				s.sendMessage("Enabled DeathSwap");
			}
			if(args[0].equals("stop")) {
				new DeathSwap(main);
				DeathSwap.disable();
				s.sendMessage("Disabled DeathSwap");		
			}
		} else if(args.length == 2) {

			if(args[0].equals("getdata")) {
				System.out.println(DeathSwap.getDataOf(args[1]));
			}
		}
		
		return false;
	}

}
