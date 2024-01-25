package mctdl.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TdLTabCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		
		List<String> arg = new ArrayList<>();
		if(args.length == 1) {
			arg.add("team");
			arg.add("money");
			arg.add("teams");
			arg.add("loadteam");
			arg.add("updateconfigs");
			arg.add("poutres");
	 	} else if(args.length == 2) {
	 		if(args[0].equals("team")) {
	 			arg.add("set");
				arg.add("get");
				arg.add("remove");
	 		}
	 		if(args[0].equals("money")) {
	 			arg.add("set");
				arg.add("get");
				arg.add("remove");
				arg.add("add");
	 		}
	 	}
		
		return arg;
	}

}
