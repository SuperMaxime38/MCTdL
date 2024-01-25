package mctdl.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;

public class DummyCommand implements CommandExecutor{
	
	static List<LivingEntity> dummies = new ArrayList<>();
	
	static Main main;
	public DummyCommand(Main main) {
		DummyCommand.main = main;
		display();
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

		if(!(s instanceof Player)) {
			s.sendMessage("Command only for players");
			return true;
		}
		Player p = (Player) s;
		
		if(args.length == 0) {

			LivingEntity e = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.SHEEP);
			e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
			e.setHealth(1000);
			e.setCustomNameVisible(true);
			e.setGravity(false);
			e.setAI(false);
			
			dummies.add(e);
			
		} else if(args.length == 1) {
			if(args[0].equals("kill-all")) {
				dummies.clear();
			}
		}
		
		
		return false;
	}
	
	public static void display() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(LivingEntity e : dummies) {
					e.setCustomName("Health " + e.getHealth() + "❤️");
				}
			}
		}.runTaskTimer(main, 0, 5);
	}

}
