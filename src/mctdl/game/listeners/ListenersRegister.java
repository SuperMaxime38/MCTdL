package mctdl.game.listeners;

import mctdl.game.Main;
import mctdl.game.games.deathswap.DeathSwap;
import mctdl.game.games.general_items.VelocityBooster;
import mctdl.game.games.hungergames.HungerGames;
import mctdl.game.games.lobby.LobbyJump;
import mctdl.game.games.lobby.items.NuclearRollerSkates;
import mctdl.game.games.lobby.items.PortalGun;
import mctdl.game.games.lobby.items.PouleZooka;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.nexus.Nexus;
import mctdl.game.scoreboard.ScoreboardManager;
import mctdl.game.tablist.TabManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.Spectate;
import mctdl.game.utils.objects.Canon;
import mctdl.game.utils.objects.riffles.AssaultRiffle;

public class ListenersRegister {
	
	public static void register(Main main) {
		//Register Events------------------------------------------------------------
		main.getServer().getPluginManager().registerEvents(new TabManager(main), main);
		main.getServer().getPluginManager().registerEvents(new ScoreboardManager(main), main);
		main.getServer().getPluginManager().registerEvents(new GameVoting(main), main);
		
		//Register Games-------------------------------------------------------------
		
		//Meltdown
		main.getServer().getPluginManager().registerEvents(new Meltdown(main), main);
		
		//DeathSwap
		main.getServer().getPluginManager().registerEvents(new DeathSwap(main), main);
		
		//Nexus
		main.getServer().getPluginManager().registerEvents(new Nexus(main), main);
		
		//HungerGames
		main.getServer().getPluginManager().registerEvents(new HungerGames(main), main);
		
		//Register LISTENERS---------------------------------------------------------
		main.getServer().getPluginManager().registerEvents(new Damage(main), main);
		main.getServer().getPluginManager().registerEvents(new Join(main), main);
		main.getServer().getPluginManager().registerEvents(new Interact(main), main);
		main.getServer().getPluginManager().registerEvents(new Move(main), main);
		main.getServer().getPluginManager().registerEvents(new Spectate(main), main);
		main.getServer().getPluginManager().registerEvents(new ChunkUnload(), main);
		main.getServer().getPluginManager().registerEvents(new ProjectileHit(main), main);
		main.getServer().getPluginManager().registerEvents(new DropItemEvent(main), main);
		
		
		//Lobby Games----------------------------------------------------------------
			// --> Jump
		main.getServer().getPluginManager().registerEvents(new LobbyJump(main), main);
		main.getServer().getPluginManager().registerEvents(new Canon(main), main);
			// --> PouleZooka
		main.getServer().getPluginManager().registerEvents(new PouleZooka(main), main);
		    // --> Nuclear Roller Skates
		main.getServer().getPluginManager().registerEvents(new NuclearRollerSkates(), main);
			// --> Portal Gun
		main.getServer().getPluginManager().registerEvents(new PortalGun(main), main);

		// Objects
		main.getServer().getPluginManager().registerEvents(new AssaultRiffle(main), main);
		main.getServer().getPluginManager().registerEvents(new VelocityBooster(), main);
	}

}
