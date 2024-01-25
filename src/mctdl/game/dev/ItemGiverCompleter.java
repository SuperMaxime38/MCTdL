package mctdl.game.dev;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import mctdl.game.dev.ItemGiver.ItemList;

public class ItemGiverCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		
		List<String> arg = new ArrayList<>();
		
		if(args.length == 1) {
			arg.add("list");
			arg.add("add");
			return arg;
		}
		if(args.length == 2) {
			for(ItemList item : ItemList.values()) {
				arg.add(item.toString());
			}
			return arg;
		}
		
		return null;
	}

}
