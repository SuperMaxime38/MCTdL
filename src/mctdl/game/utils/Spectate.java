package mctdl.game.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.teams.TeamsManager;

public class Spectate implements Listener{
	
	static Main main;
	public Spectate(Main main) {
		Spectate.main = main;
		isSpectatedValid();
	}
	
	static HashMap<Player, Boolean> isSpectating = new HashMap<Player, Boolean>();
	static HashMap<Player, List<String>> spectateTargets = new HashMap<Player, List<String>>();
	static HashMap<String, List<Player>> spectated = new HashMap<String, List<Player>>();
	
	public static void setSpectatingTeammates(Player p) {
		
	}
	
	public static void setSpectatingGroup(Player p, List<String> members) {
		if(p.getGameMode() != GameMode.SPECTATOR) p.setGameMode(GameMode.SPECTATOR);
		if(members.contains(p.getName())) members.remove(p.getName());
		
		List<String> targets = getValidTargets(members);
		if(targets.isEmpty()) {
			System.out.println("[MCTdL] SpectateMode > Error, spectating targets are null (no teammates)");
			return;
		}
		p.setSpectatorTarget(Bukkit.getPlayer(targets.get(0)));
		isSpectating.put(p, true);
		spectateTargets.put(p, targets);
		List<Player> truc = spectated.get(targets.get(0));
		if(truc == null) {
			truc = new ArrayList<>();
		}
		truc.add(p);
		spectated.put(targets.get(0),truc);
	}
	
	public static void isSpectatedValid() {
		new BukkitRunnable() {

			HashMap<String, String> teamsmembers = new HashMap<String, String>();
			
			@Override
			public void run() {
				teamsmembers = TeamsManager.getOnlinePlayers();
				
				for (String player : teamsmembers.keySet()) {
					if(spectated.containsKey(player)) { //SI LE JOUEUR EST SPECTATE PAR QLQ
						if(Bukkit.getPlayer(player).getGameMode() == GameMode.SPECTATOR) { //MAIS QU'IL EST LUI MEME EN SPECTATE
							for (Player pl : spectated.get(player)) {
								setSpectatingGroup(pl, spectateTargets.get(pl));
							}
						}
					}
				}
			}
			
		}.runTaskTimer(main, 0, 20);
	}
	
	@EventHandler
	public static void changeView(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(!isSpectating.containsKey(p)) return;
		
		List<String> members = spectateTargets.get(p);
		List<String> targets = getValidTargets(members);
		
		String tgname = p.getSpectatorTarget().getName();
		
		if(!targets.contains(tgname)) {
			System.out.println("TAGERTS DOESNT CONTAIN");
			p.setSpectatorTarget(Bukkit.getPlayer(targets.get(0)));
			isSpectating.put(p, true);
			spectateTargets.put(p, targets);
			List<Player> truc = spectated.get(targets.get(0));
			truc.add(p);
			spectated.put(targets.get(0),truc);
			e.setCancelled(true);
			return;
		}

		System.out.println("TAREGT CONTAINS");
		int current = targets.indexOf(tgname);
		if(current == targets.size() - 1) {
			current = 0;
			System.out.println("CURRENT=0");
		} else {
			current++;

			System.out.println("CURRENT = " + current);
		}
		p.setSpectatorTarget(Bukkit.getPlayer(targets.get(current)));
		isSpectating.put(p, true);
		spectateTargets.put(p, targets);
		List<Player> truc = spectated.get(targets.get(current));
		truc.add(p);
		spectated.put(targets.get(current),truc);
		e.setCancelled(true);
		return;
		
	}
	public static List<String> getValidTargets(List<String> members) {
		List<String> targets = new ArrayList<>();
		
		for(String playername : members) {
			if(Bukkit.getPlayer(playername) != null) {
				GameMode gm = Bukkit.getPlayer(playername).getGameMode();
				if(gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE) {
					targets.add(playername);
				}
			}
		}
		
		return targets;
	}
}
