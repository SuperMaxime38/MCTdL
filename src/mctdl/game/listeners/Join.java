package mctdl.game.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import mctdl.game.Main;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.NPCManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;

public class Join implements Listener{
	
	static Main main;
	public Join(Main main) {
		Join.main = main;
	}
	
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setFoodLevel(20);
		String game = main.getConfig().getString("game");
		
		HashMap<String, Integer> balances = MoneyManager.getRegsPlayer();
		HashMap<String, Integer> lifetime = MoneyManager.getLifeTimeCoins();
		HashMap<String, Integer> poutres = MoneyManager.getWhoHasPoutres();
		
		HashMap<String, Boolean> hasVoted = GameVoting.hasVoted();
		if(!balances.containsKey(p.getName())) {
			MoneyManager.setPlayerMoney(p.getName(), 0);
		}
		if(!lifetime.containsKey(p.getName())) {
			MoneyManager.setPlayerLifeTimeCoins(p.getName(), 0);
		}
		if(!poutres.containsKey(p.getName())) {
			MoneyManager.setPlayerPoutres(p.getName(), 0);
		}
		if(game.equals("lobby")) {
			p.teleport(new Location(p.getWorld(), 8, 6, 8));
			PlayerData.registerPlayer(p);
		}
		
		if(!hasVoted.containsKey(p.getName())) {
			GameVoting.setHasVoted(p.getName(), false);
		}
		
		//--IMPORTANT ---------> Meltown.java is handling connexion/disconnexion during md game
		
		//TEXTURE
		if(main.getConfig().getBoolean("enable-npc")) {
			HashMap<String, List<String>> textures = NPCManager.getTextures();
			if(!textures.containsKey(p.getName())) {
				NPCManager.getPlayerTexture(p.getName(), main);
				System.out.println("[MCTdL] Handler > Loaded " + p.getName() + "'s texture");
			}
			NPCManager.onPlayerJoin(p, main, 200);
		}
		
	}
}
