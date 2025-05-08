package mctdl.game.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;

public class Spectate implements Listener{
	
	static Main main;
	public Spectate(Main main) {
		Spectate.main = main;
		isSpectatedValid();
	}
	
	static HashMap<UUID, Boolean> isSpectating = new HashMap<UUID, Boolean>();
	static HashMap<Player, List<String>> spectateTargets = new HashMap<Player, List<String>>();
	static HashMap<String, List<Player>> spectated = new HashMap<String, List<Player>>();
	static HashMap<UUID, UUID> targetting = new HashMap<UUID, UUID>();
	
	public static void unSpectatePlayer(Player p) {
		isSpectating.put(p.getUniqueId(), false);
		spectateTargets.remove(p);
		spectated.remove(p.getSpectatorTarget().getUniqueId().toString());
		targetting.remove(p.getUniqueId());
	}
	
	public static void setSpectatingGroup(Player p, List<String> members) {
		if(p.getGameMode() != GameMode.SPECTATOR) p.setGameMode(GameMode.SPECTATOR);
		if(members.contains(p.getUniqueId().toString())) members.remove(p.getUniqueId().toString());
		
		List<String> targets = getValidTargets(members);
		if(targets.isEmpty()) {
			System.out.println("[MCTdL] SpectateMode > Error, spectating targets are null (no teammates)");
			return;
		}
		
		Player target = Bukkit.getPlayer(targets.get(0));
		if(target == null) target = NPCManager.getNpcPlayerIfItIs(targets.get(0));
		
		p.setSpectatorTarget(target);
		targetting.put(p.getUniqueId(), target.getUniqueId());
		
		isSpectating.put(p.getUniqueId(), true);
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
						if(isSpectating.containsKey(UUID.fromString(player)) && isSpectating.get(UUID.fromString(player))) { //MAIS QU'IL EST LUI MEME EN SPECTATE
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
		if(!isSpectating.containsKey(p.getUniqueId())) return;
		
		List<String> members = spectateTargets.get(p);
		List<String> targets = getValidTargets(members);
		
		String tgUUID = targetting.get(p.getUniqueId()).toString();
		
		if(!targets.contains(tgUUID)) {
			System.out.println("TAGERTS DOESNT CONTAIN");
			p.setSpectatorTarget(Bukkit.getPlayer(targets.get(0)));
			
			targetting.put(p.getUniqueId(), UUID.fromString(targets.get(0)));
			
			isSpectating.put(p.getUniqueId(), true);
			spectateTargets.put(p, targets);
			List<Player> truc = spectated.get(targets.get(0));
			truc.add(p);
			spectated.put(targets.get(0),truc);
			e.setCancelled(true);
			return;
		}

		System.out.println("TAREGT CONTAINS");
		int current = targets.indexOf(tgUUID);
		if(current == targets.size() - 1) {
			current = 0;
			System.out.println("CURRENT=0");
		} else {
			current++;

			System.out.println("CURRENT = " + current);
		}
		UUID ntUUID = UUID.fromString(targets.get(current));
		Player newTarget = Bukkit.getPlayer(ntUUID);
		if(newTarget == null) newTarget = NPCManager.getNpcPlayerIfItIs(ntUUID.toString());
		p.setSpectatorTarget(newTarget);
		
		targetting.put(p.getUniqueId(), ntUUID);
		
		isSpectating.put(p.getUniqueId(), true);
		spectateTargets.put(p, targets);
		List<Player> truc = spectated.get(targets.get(current));
		truc.add(p);
		spectated.put(targets.get(current),truc);
		e.setCancelled(true);
		return;
		
	}
	public static List<String> getValidTargets(List<String> members) {
		List<String> targets = new ArrayList<>();
		
		for(String uuid : members) {
			Player p = Bukkit.getPlayer(UUID.fromString(uuid));
			if(p == null) p = NPCManager.getNpcPlayerIfItIs(uuid);
			if(p == null) continue;
			
			if(isSpectating.containsKey(UUID.fromString(uuid)) && isSpectating.get(UUID.fromString(uuid))) continue;
			
			targets.add(uuid);
		}
		
		return targets;
	}
}
