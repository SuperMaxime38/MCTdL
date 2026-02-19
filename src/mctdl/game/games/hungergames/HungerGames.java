package mctdl.game.games.hungergames;

import mctdl.game.teams.TeamsManager;

public final class HungerGames {
	
	boolean enable;
	
	public HungerGames() {
		enable = false;
	}
	
	public void enable() {
		if(!canStart()) {
			System.out.println("§f[§fcHungerGames§f] §cYou need 2 teams in order to start a session");
			return;
		}
		
		
	}
	
	
	public boolean isEnabled() {
		return enable;
	}
	
	public boolean canStart() {
		if(TeamsManager.getOnlineTeams().size() < 2) return false;
		
		return true;
	}
}
