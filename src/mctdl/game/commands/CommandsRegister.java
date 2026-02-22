package mctdl.game.commands;

import mctdl.game.Main;
import mctdl.game.dev.ItemGiver;
import mctdl.game.dev.ItemGiverCompleter;
import mctdl.game.games.deathswap.DeathSwapCommand;
import mctdl.game.games.lobby.LobbyJump;
import mctdl.game.games.meltdown.MDCommand;
import mctdl.game.games.nexus.NexusCommand;
import mctdl.game.npc.NPCCommandCompleter;

public class CommandsRegister {
	
	public static void register(Main main) {
		//Commands -->
		main.getCommand("mctdl").setExecutor(new TdLCommand(main));
		main.getCommand("baltop").setExecutor(new BaltopCommand(main));
		main.getCommand("npc").setExecutor(new NPCCommand(main));
		main.getCommand("dg").setExecutor(new ItemGiver());
		main.getCommand("poulezooka").setExecutor(new PouleZookaCMD());
		main.getCommand("dummy").setExecutor(new DummyCommand(main));
		main.getCommand("testcmd").setExecutor(new TestCommand(main));
		main.getCommand("tdlpacket").setExecutor(new TDLPacketCommand());
		
		//lobby
		main.getCommand("jump").setExecutor(new LobbyJump(main));
		
		//meltdown
		main.getCommand("meltdown").setExecutor(new MDCommand(main));
		
		//deathswap
		main.getCommand("deathswap").setExecutor(new DeathSwapCommand(main));
		
		//nexus
		main.getCommand("nexus").setExecutor(new NexusCommand(main));
		
		//TabCompleter -->
		main.getCommand("npc").setTabCompleter(new NPCCommandCompleter());
		main.getCommand("mctdl").setTabCompleter(new TdLTabCompleter());
		main.getCommand("dg").setTabCompleter(new ItemGiverCompleter());
	}

}
