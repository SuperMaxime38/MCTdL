package mctdl.game.games.meltdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.utils.Cuboid;

public class MDMap {
	
	static List<Location> banned = new ArrayList<>();
	static List<Cuboid> melting = new ArrayList<>();
	static List<Location> alarms = new ArrayList<>();
	
	public static void generateMap(Main main) {
		
		String map = main.getConfig().getString("games.meltdown.map");
		FileConfiguration datas = MeltdownFiles.checkMap(main);
		int X = datas.getInt("schemX");
		int Y = datas.getInt("schemY");
		int Z = datas.getInt("schemZ");

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world world");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/schem load " + map);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				System.out.println("[Meltdown] > Schemati loaded");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + X + "," + Y + "," + Z);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + X + "," + Y + "," + Z);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/paste");
				System.out.println("[Meltdown] > Schematic pasted");
			}
		}.runTaskLater(main, 40);
		
	}
	
	public static void alarmTrigger(List<Integer> rooms) { //Nombre doit etre pair (multiple 4)
		for(int i = 0; i < rooms.size(); i += 4) {
			int X1 = rooms.get(i);
			int Y1 = rooms.get(i + 1);
			int X2 = rooms.get(i + 2);
			int Y2 = rooms.get(i + 3);
			
			Location pos1 = new Location(Bukkit.getWorlds().get(0), X1, 11, Y1);
			Location pos2 = new Location(Bukkit.getWorlds().get(0), X2, 26, Y2);
			Cuboid cube = new Cuboid(pos1, pos2);
			melting.add(cube);
			
			for (Block block : cube) {
				if(block.getType() == Material.CRIMSON_TRAPDOOR) {
					Openable o = (Openable) block.getState().getBlockData();
					o.setOpen(true);
					block.setBlockData(o);
					block.getState().update();
					
					alarms.add(block.getLocation());
				}
			}
		}
	}
	
	
	public static void doorTrigger(List<Integer> doors) {
		for(int i = 0; i < doors.size(); i += 6) {
			int X1 = doors.get(i);
			int Y1 = doors.get(i + 1);
			int Z1 = doors.get(i + 2);
			int X2 = doors.get(i + 3);
			int Y2 = doors.get(i + 4);
			int Z2 = doors.get(i + 5);
			
			Location pos1 = new Location(Bukkit.getWorlds().get(0), X1, Y1, Z1);
			Location pos2 = new Location(Bukkit.getWorlds().get(0), X2, Y2, Z2);
			Cuboid cube = new Cuboid(pos1, pos2);
			
			for (Block block : cube) {
				block.setType(Material.AIR);
			}
		}
	}
	
	public static void roomTrigger(List<Integer> rooms, Main main) {
		banned.add(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
		
		for(int i = 0; i < rooms.size(); i += 4) {
			int X1 = rooms.get(i);
			int Z1 = rooms.get(i + 1);
			int X2 = rooms.get(i + 2);
			int Z2 = rooms.get(i + 3);
			
			//Ys
			//11
			//26
			
			
			// Liste de tous les X
			
			List<Integer> lsX = new ArrayList<>();
			
			int spanX = X2 - X1;
			if(spanX > 0) { // Si pos1 < pos2
				for(int blockX = X1; blockX < X2 + 1; blockX++) {
					lsX.add(blockX);
				}
			} else { // si pos1 > pos2
				for(int blockX_ = X1; blockX_ > X2 -1; blockX_--) {
					lsX.add(blockX_);
				}
			}
			
			// Liste de tous les Z
			
			List<Integer> lsZ = new ArrayList<>();
			
			int spanZ = Z2 - Z1;
			if(spanZ > 0) { // Si pos1 < pos2
				for(int blockZ = Z1; blockZ < Z2 + 1; blockZ++) {
					lsZ.add(blockZ);
				}
			} else { // si pos1 > pos2
				for(int blockZ_ = Z1; blockZ_ > Z2 -1; blockZ_--) {
					lsZ.add(blockZ_);
				}
			}
			
			//Liste de toutes les coordonnées X Z
			
			List<List<Integer>> blocks = new ArrayList<>();
			
			for(int i2 = 0; i2 < lsX.size(); i2++) {
				for(int i3 = 0; i3 < lsZ.size(); i3++) {
					List<Integer> list = new ArrayList<>();
					list.add(lsX.get(i2));
					list.add(lsZ.get(i3));
					
					blocks.add(list);
				}
			}
			
			Random rand = new Random();
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(!main.getConfig().getString("game").equals("meltdown")) {
						cancel();
						return;
					}
					if(blocks.isEmpty()) {
						cancel();
						return;
					}
					
					List<Integer> coords = blocks.get(rand.nextInt(blocks.size()));
					int X = coords.get(0);
					int Z = coords.get(1);
					
					for (int i4 = 11; i4 < 27; i4++) {
						Location loc = new Location(Bukkit.getWorlds().get(0), X,i4, Z);
						loc.getBlock().setType(Material.AIR);
						
						//Ajoute cette coord aux blocks bannis
						banned.add(loc);
						
						//Test si c'est un heater --> Le rend au joueur et lui applique le cooldown
						Meltdown.whatHeater(loc);
					}
					blocks.remove(coords);
				}
			}.runTaskTimer(main, 0, 4);
		}
	}
	
	public static List<Location> getBannedLocs() {
		banned.add(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
		return banned;
	}
	
	public static List<Cuboid> getMeltingRooms() {
		return melting;
	}
	
	public static List<Location> getAlarmsLocs() {
		return alarms;
	}
 }
