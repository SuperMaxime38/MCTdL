package mctdl.game.games.meltdown.npc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;

public class MeltdownNPC {
	
	PlayerAI npc;
	Main main;
	
	String team;
	
	public MeltdownNPC(Main main, Player parent, String team) {
		this.npc = PlayerAI.createNPC(main, parent, "Poutre", parent.getWorld(), parent.getLocation());
		this.main = main;
		this.team = team;
		
		//Show NPC
		for(Player p : Bukkit.getOnlinePlayers()) {
			NPCManager.showNPCFor(npc, p, null);
		}
		
		// Register NPC as Player
		fakeJoin();
		registerIntoTeam();
	}
	
	private void fakeJoin() {
		
		Player p = npc.getBukkitEntity();
		
		//p.setFoodLevel(20);
		System.out.println("main config stuff " + main.getConfig().getString("game"));
		String game = main.getConfig().getString("game");
		
		HashMap<String, Integer> balances = MoneyManager.getRegsPlayer();
		HashMap<String, Integer> lifetime = MoneyManager.getLifeTimeCoins();
		HashMap<String, Integer> poutres = MoneyManager.getWhoHasPoutres();
		
		HashMap<String, Boolean> hasVoted = GameVoting.hasVoted();
		
		if(!balances.containsKey(p.getUniqueId().toString())) {
			MoneyManager.setPlayerMoney(p.getUniqueId().toString(), 0);
		}
		if(!lifetime.containsKey(p.getUniqueId().toString())) {
			MoneyManager.setPlayerLifeTimeCoins(p.getUniqueId().toString(), 0);
		}
		if(!poutres.containsKey(p.getUniqueId().toString())) {
			MoneyManager.setPlayerPoutres(p.getUniqueId().toString(), 0);
		}
		if(game.equals("lobby")) {
			p.teleport(new Location(p.getWorld(), 8, 6, 8));
			PlayerData.registerPlayer(p);
		}
		
		if(!hasVoted.containsKey(p.getUniqueId().toString())) {
			GameVoting.setHasVoted(p.getUniqueId().toString(), false);
		}
		
		TeamsManager.updatePseudo(p.getUniqueId().toString(), p.getName());
	}
	
	private void registerIntoTeam() {

		Player p = npc.getBukkitEntity();
		TeamsManager.setPlayerTeam(p.getUniqueId().toString(), team);
		
		Bukkit.broadcastMessage(Main.header() + "AI Player " + TeamsManager.getTeamColorByTeam(team) + p.getName() + "§r has joined team " + TeamsManager.getTeamNameByTeam(team));
	}
}
