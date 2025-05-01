package mctdl.game.games.meltdown.npc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.ai_trainer.Environnement;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.money.MoneyManager;
import mctdl.game.npc.PlayerAI;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.GameVoting;
import mctdl.game.utils.PlayerData;
import ntdjl.NN;

public class MeltdownNPC {
	
	PlayerAI npc;
	Main main;
	
	Environnement env;
	
	String team;
	
	NN model;
	
	public MeltdownNPC(Main main, String name, String team) {
		
		this.npc = PlayerAI.createNPC(name, Bukkit.getWorlds().get(0), new Location(Bukkit.getWorlds().get(0), 8, 6, 8));
		this.main = main;
		this.team = team;
		
		Meltdown.addNPC(this);
		
		model = new NN();
		
		fakeJoin();
		registerIntoTeam();
		
		this.env = new Environnement(npc);
		loop();
	}
	
	public void loop() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(main.getConfig().getString("game").equals("meltdown")) env.update();
			}
			
		}.runTaskTimer(main, 0, 100);
	}
	
	public PlayerAI getNPC() {
		return this.npc;
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
			this.npc.teleport(8, 6, 8);
			PlayerData.registerPlayer(p);
		}
		
		if(!hasVoted.containsKey(p.getUniqueId().toString())) {
			GameVoting.setHasVoted(p.getUniqueId().toString(), false);
		}
		
		TeamsManager.updatePseudo(p.getUniqueId().toString(), p.getName());
	}
	
	public void setModel(NN model) {
		this.model = model;
	}
	
	public NN getModel() {
		return this.model;
	}
	
	private void registerIntoTeam() {

		Player p = npc.getBukkitEntity();
		TeamsManager.setPlayerTeam(p.getUniqueId().toString(), team);
		
		Bukkit.broadcastMessage(Main.header() + "AI Player " + TeamsManager.getTeamColorByTeam(team) + p.getName() + "§r has joined team " + TeamsManager.getTeamNameByTeam(team));
	}
	
	public Environnement getEnvironnement() {
		return this.env;
	}

	public void moveForward() {
		this.npc.walk(PlayerAI.WALK_FORWARD);
	}
	public void moveBackward() {
		this.npc.walk(PlayerAI.WALK_BACKWARD);
	}
	public void moveLeft() {
		this.npc.walk(PlayerAI.WALK_LEFT);
	}
	public void moveRight() {
		this.npc.walk(PlayerAI.WALK_RIGHT);
	}
	
	public void jump() {
		this.npc.jump();
	}
	
	public void sneak() {
		this.npc.sneak();
	}
	
	public void placeHeater() {
		Meltdown.placeHeaterForNpc(this);
	}
	
	public void claimPickaxe() {
		Meltdown.getPickaxeForNPC(this);
	}
	
	public void breakBlock() {
		Block b = this.npc.getBukkitEntity().getTargetBlock(null, 5);
		this.npc.breakBlock(b.getLocation());
	}
	
	public void shoot() {
		Location lc = new Location(this.npc.getBukkitEntity().getWorld(), this.npc.getX(), this.npc.getY(), this.npc.getZ(), this.npc.getYaw(), this.npc.getPitch());
		this.npc.shoot(lc.getDirection());
	}
	
	public void addYaw() {
		this.npc.rotate(this.npc.getYaw() + 1, this.npc.getPitch());
	}
	
	public void removeYaw() {
		this.npc.rotate(this.npc.getYaw() - 1, this.npc.getPitch());
	}
	
	public void addPitch() {
		this.npc.rotate(this.npc.getYaw(), this.npc.getPitch() + 1);
	}
	
	public void removePitch() {
		this.npc.rotate(this.npc.getYaw(), this.npc.getPitch() - 1);
	}
	
}
