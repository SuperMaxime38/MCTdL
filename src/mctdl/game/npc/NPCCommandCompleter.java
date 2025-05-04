package mctdl.game.npc;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class NPCCommandCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		return null;
	}

}
