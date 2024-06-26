package mctdl.game.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import mctdl.game.Main;
import mctdl.game.commands.BaltopCommand;
import mctdl.game.money.MoneyManager;
import mctdl.game.teams.TeamsManager;

public class ScoreboardManager implements Listener{
	
	static Main main;
	public ScoreboardManager(Main main) {
		ScoreboardManager.main = main;
	}
	
	static int order;
	static Score score;
 	
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		List<String> old = new ArrayList<String>();	
		
		org.bukkit.scoreboard.ScoreboardManager mng = Bukkit.getScoreboardManager();
		
		Scoreboard board = mng.getNewScoreboard();
		Objective obj = board.registerNewObjective("title", "dummy", "�l�3Tournoi des L�gendes");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		order = BaltopCommand.getClassement().indexOf(p.getName()) +1; // 0
		score = obj.getScore("�3Personnal Rank : �6#" + order);
		score.setScore(0);
		old.add(0, "�3Personnal Rank : �6#" + order);
		
		order = BaltopCommand.getTeamClassement().indexOf(TeamsManager.getPlayerTeam(p.getName())) +1; // 1
		score = obj.getScore("�bTeam Rank : �6#" + order);
		score.setScore(1);
		old.add(1, "�bTeam Rank : �6#" + order);
		
		score = obj.getScore("�6Coins �f: " + MoneyManager.getPlayerMoney(p.getName())); // 2
		score.setScore(2);
		old.add(2, "�6Coins �f: " + MoneyManager.getPlayerMoney(p.getName()));
		
		score = obj.getScore("�aTeam : " + TeamsManager.getTeamName(p.getName())); //3
		score.setScore(3);
		old.add(3, "�aTeam : " + TeamsManager.getTeamName(p.getName()));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {

				board.resetScores(old.get(0));
				order = BaltopCommand.getClassement().indexOf(p.getName()) +1; // 0
				old.set(0, "�3Personnal Rank : �6#" + order);
				score = obj.getScore(old.get(0)); //0
				score.setScore(0);

				board.resetScores(old.get(1));
				order = BaltopCommand.getTeamClassement().indexOf(TeamsManager.getPlayerTeam(p.getName())) +1;  // 1
				old.set(1, "�bTeam Rank : �6#" + order);
				score = obj.getScore(old.get(1)); //0
				score.setScore(1);

				board.resetScores(old.get(2));
				old.set(2, "�6Coins �f: " + MoneyManager.getPlayerMoney(p.getName())); //2
				score = obj.getScore(old.get(2)); //0
				score.setScore(2);

				board.resetScores(old.get(3));
				old.set(3, "�aTeam : " + TeamsManager.getTeamName(p.getName())); //3
				score = obj.getScore(old.get(3)); //0
				score.setScore(3);
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
		
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if(i > 5) i = 0;
				
				switch(i) {
				case 0:
					obj.setDisplayName("�l�3Tournoi des L�gendes");
					break;
				case 1:
					obj.setDisplayName("�l�6Tournoi �3des L�gendes");
					break;
				case 2:
					obj.setDisplayName("�l�6Tournoi des �3L�gendes");
					break;
				case 3:
					obj.setDisplayName("�l�6Tournoi des L�gendes");
					break;
				case 4:
					obj.setDisplayName("�l�3Tournoi �6des L�gendes");
					break;
				case 5:
					obj.setDisplayName("�l�3Tournoi des �6L�gendes");
					break;
				}
				i++;
			}
		}.runTaskTimer(main, 0, 5);
		
		
		p.setScoreboard(board);
		
	}

}
