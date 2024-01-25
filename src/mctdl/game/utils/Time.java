package mctdl.game.utils;

import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;

public class Time {
	
	static long tick;
	static boolean enable = true;
	
	public Time(Main main) {
		tick = 0;
		enable = true;
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!enable) {
					cancel();
					return;
				}
				tick++;
			}
			
		}.runTaskTimer(main, 0, 1);
	}
	
	public static void stop() {
		enable = false;
	}
	
	public static long getCurrentTick() {
		return tick;
	}
	
	public static long getCurrentSecond() {
		return Math.round((tick/20)*10) / 10;
	}
	
	public static String getUptime() {
		return TimeFormater.getFormatedTime(getCurrentSecond());
	}
}
