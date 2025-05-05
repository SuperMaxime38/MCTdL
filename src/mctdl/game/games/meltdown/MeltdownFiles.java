package mctdl.game.games.meltdown;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mctdl.game.Main;

public class MeltdownFiles {
	
	public static boolean fileCheck(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + "meltdown.yml");
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);

	     
	    if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	        try {
	        	
	        	//isMapGenerated
	        	datas.set("isMapGenerated", false);
	        	datas.set("schemX", 607);
	        	datas.set("schemY", 39);
	        	datas.set("schemZ", -84);
	        	datas.set("fall_height", 11);
	        	 
	        	datas.createSection("times");
	        	datas.createSection("times.door");
	        	datas.createSection("times.room");
	        	
	        	datas.createSection("coords");
	        	datas.createSection("coords.door.0");
	        	datas.createSection("coords.door.1");
	        	datas.createSection("coords.door.2");
	        	datas.createSection("coords.door.3");
	        	datas.createSection("coords.door.4");
	        	datas.createSection("coords.door.5");
	        	
	        	datas.createSection("coords.room");
	        	datas.createSection("coords.room.A");
	        	datas.createSection("coords.room.B");
	        	datas.createSection("coords.room.C");
	        	datas.createSection("coords.room.D");
	        	datas.createSection("coords.room.E");
	        	datas.createSection("coords.room.M");
	        	
	        	//Spawn
	        	datas.createSection("spawn");
	        	datas.createSection("spawn.red");
	        	datas.createSection("spawn.blue");
	        	datas.createSection("spawn.green");
	        	datas.createSection("spawn.yellow");
	        	datas.createSection("spawn.purple");
	        	datas.createSection("spawn.aqua");
	        	datas.createSection("spawn.orange");
	        	datas.createSection("spawn.black");
	        	
	        	List<Integer> spawn = new ArrayList<>();
	        	spawn.add(497);spawn.add(19);spawn.add(-129);spawn.add(-90); //Last is yaw
	        	datas.set("spawn.red", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(497);spawn.add(19);spawn.add(-38);spawn.add(-90); //Last is yaw
	        	datas.set("spawn.orange", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(561);spawn.add(19);spawn.add(26);spawn.add(180); //Last is yaw
	        	datas.set("spawn.yellow", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(653);spawn.add(19);spawn.add(26);spawn.add(180); //Last is yaw
	        	datas.set("spawn.green", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(717);spawn.add(19);spawn.add(-37);spawn.add(90); //Last is yaw
	        	datas.set("spawn.aqua", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(717);spawn.add(19);spawn.add(-129);spawn.add(90); //Last is yaw
	        	datas.set("spawn.blue", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(653);spawn.add(19);spawn.add(-193);spawn.add(0); //Last is yaw
	        	datas.set("spawn.purple", spawn);
	        	
	        	spawn = new ArrayList<>();
	        	spawn.add(561);spawn.add(19);spawn.add(-193);spawn.add(0); //Last is yaw
	        	datas.set("spawn.black", spawn);
	        	
	        	//Times
	        	
	        	//room
	        	datas.set("times.room.A", 90);
	        	datas.set("times.room.B", 180);
	        	datas.set("times.room.C", 290);
	        	datas.set("times.room.D", 410);
	        	datas.set("times.room.E", 500);
	        	datas.set("times.room.M", 630);
	        	
	        	//door
	        	datas.set("times.door.1", 30);
	        	datas.set("times.door.2", 90);
	        	datas.set("times.door.3", 140);
	        	datas.set("times.door.4", 240);
	        	datas.set("times.door.5", 300);
	        	
	        	
	        	//Coords
	        	
	        	List<Integer> coords = new ArrayList<>();
	        	
	        	//Room coords A
	        	
	        	//Spawn
	        	coords.add(501);coords.add(-123);
	        	coords.add(495);coords.add(-137);
	        	
	        	coords.add(501);coords.add(-45);
	        	coords.add(495);coords.add(-31);
	        	
	        	coords.add(568);coords.add(-190);
	        	coords.add(554);coords.add(-196);
	        	
	        	coords.add(646);coords.add(-190);
	        	coords.add(660);coords.add(-196);
	        	
	        	coords.add(719);coords.add(-137);
	        	coords.add(713);coords.add(-123);
	        	
	        	coords.add(719);coords.add(-45);
	        	coords.add(713);coords.add(-31);
	        	
	        	coords.add(660);coords.add(28);
	        	coords.add(646);coords.add(22);
	        	
	        	coords.add(568);coords.add(28);
	        	coords.add(554);coords.add(22);
	        	
	        	//Salles
	        	coords.add(537);coords.add(-108);
	        	coords.add(503);coords.add(-152); // 4 coords --> 1 salle : 2 1ere coords --> X1, Z1, 2dernieres coors --> X2, Z2
	        	
	        	coords.add(503);coords.add(-60);
	        	coords.add(537);coords.add(-16);
	        	
	        	coords.add(583);coords.add(-188);
	        	coords.add(539);coords.add(-154);
	        	
	        	coords.add(631);coords.add(-154);
	        	coords.add(675);coords.add(-188);
	        	
	        	coords.add(711);coords.add(-108);
	        	coords.add(677);coords.add(-152);
	        	
	        	coords.add(711);coords.add(-16);
	        	coords.add(677);coords.add(-60);
	        	
	        	coords.add(675);coords.add(-14);
	        	coords.add(631);coords.add(20);
	        	
	        	coords.add(583);coords.add(-14);
	        	coords.add(539);coords.add(20);
	        	
	        	coords.add(677);coords.add(-60);
	        	coords.add(711);coords.add(-16);
	        	
	        	//dessous de portes
	        	coords.add(502);coords.add(-133);
	        	coords.add(502);coords.add(-127);
	        	
	        	coords.add(502);coords.add(-41);
	        	coords.add(502);coords.add(-35);
	        	
	        	coords.add(564);coords.add(-189);
	        	coords.add(558);coords.add(-189);
	        	
	        	coords.add(656);coords.add(-189);
	        	coords.add(650);coords.add(-189);
	        	
	        	coords.add(712);coords.add(-127);
	        	coords.add(712);coords.add(-133);
	        	
	        	coords.add(712);coords.add(-35);
	        	coords.add(712);coords.add(-41);
	        	
	        	coords.add(650);coords.add(21);
	        	coords.add(656);coords.add(21);
	        	
	        	coords.add(558);coords.add(21);
	        	coords.add(564);coords.add(21);
	        	
	        	
	        	datas.set("coords.room.A", coords);
	        	
	        	coords = new ArrayList<>();
	        	
	        	//Room coords B
	        	
	        	coords.add(503);coords.add(-62);
	        	coords.add(537);coords.add(-106);
	        	
	        	coords.add(585);coords.add(-188);
	        	coords.add(629);coords.add(-154);
	        	
	        	coords.add(677);coords.add(-106);
	        	coords.add(711);coords.add(-62);
	        	
	        	coords.add(629);coords.add(-14);
	        	coords.add(585);coords.add(20);
	        	
	        	//Dessous portes
	        	coords.add(522);coords.add(-107);
	        	coords.add(518);coords.add(-107);
	        	
	        	coords.add(518);coords.add(-61);
	        	coords.add(522);coords.add(-61);
	        	
	        	coords.add(584);coords.add(-169);
	        	coords.add(584);coords.add(-173);
	        	
	        	coords.add(630);coords.add(-169);
	        	coords.add(630);coords.add(-173);
	        	
	        	coords.add(696);coords.add(-107);
	        	coords.add(692);coords.add(-107);
	        	
	        	coords.add(696);coords.add(-61);
	        	coords.add(692);coords.add(-61);
	        	
	        	coords.add(630);coords.add(5);
	        	coords.add(630);coords.add(1);
	        	
	        	coords.add(584);coords.add(5);
	        	coords.add(584);coords.add(1);
	        	
	        	datas.set("coords.room.B", coords);

	        	coords = new ArrayList<>();
	        	
	        	//Room coords C

	        	
	        	coords.add(677);coords.add(20);
	        	coords.add(711);coords.add(-14);
	        	
	        	coords.add(503);coords.add(-14);
	        	coords.add(537);coords.add(20);
	        	
	        	coords.add(537);coords.add(-188);
	        	coords.add(503);coords.add(-154);
	        	
	        	coords.add(711);coords.add(-154);
	        	coords.add(677);coords.add(-188);
	        	
	        	
	        	//Dessous porte
	        	
	        	coords.add(528);coords.add(-15);
	        	coords.add(527);coords.add(-15);
	        	
	        	coords.add(518);coords.add(-153);
	        	coords.add(522);coords.add(-153);
	        	
	        	coords.add(538);coords.add(-163);
	        	coords.add(538);coords.add(-164);
	        	
	        	coords.add(676);coords.add(-173);
	        	coords.add(676);coords.add(-169);
	        	
	        	coords.add(687);coords.add(-153);
	        	coords.add(686);coords.add(-153);
	        	
	        	coords.add(692);coords.add(-15);
	        	coords.add(696);coords.add(-15);
	        	
	        	coords.add(676);coords.add(-4);
	        	coords.add(676);coords.add(-5);
	        	
	        	coords.add(686);coords.add(-153);
	        	coords.add(687);coords.add(-153);
	        	
	        	coords.add(692);coords.add(-15);
	        	coords.add(696);coords.add(-15);
	        	
	        	coords.add(538);coords.add(1);
	        	coords.add(538);coords.add(5);
	        	
	        	
	        	datas.set("coords.room.C", coords);

	        	coords = new ArrayList<>();
	        	
	        	//Room coords D
	        	
	        	coords.add(631);coords.add(-16);
	        	coords.add(675);coords.add(-60);
	        	
	        	coords.add(631);coords.add(-108);
	        	coords.add(675);coords.add(-152);
	        	
	        	coords.add(583);coords.add(-108);
	        	coords.add(539);coords.add(-152);
	        	
	        	coords.add(539);coords.add(-16);
	        	coords.add(583);coords.add(-60);
	        	
	        	//Dessous de porte
	        	
	        	coords.add(676);coords.add(-39);
	        	coords.add(676);coords.add(-37);
	        	
	        	coords.add(654);coords.add(-15);
	        	coords.add(652);coords.add(-15);
	        	
	        	coords.add(652);coords.add(-153);
	        	coords.add(654);coords.add(-153);
	        	
	        	coords.add(676);coords.add(-131);
	        	coords.add(676);coords.add(-129);
	        	
	        	coords.add(560);coords.add(-153);
	        	coords.add(562);coords.add(-153);
	        	
	        	coords.add(538);coords.add(-129);
	        	coords.add(538);coords.add(-131);
	        	
	        	coords.add(562);coords.add(-15);
	        	coords.add(560);coords.add(-15);
	        	
	        	coords.add(538);coords.add(-37);
	        	coords.add(538);coords.add(-39);
	        	
	        	datas.set("coords.room.D", coords);

	        	coords = new ArrayList<>();
	        	
	        	//Room coords E
	        	
	        	coords.add(539);coords.add(-106);
	        	coords.add(583);coords.add(-62);
	        	
	        	coords.add(585);coords.add(-60);
	        	coords.add(629);coords.add(-16);
	        	
	        	coords.add(631);coords.add(-62);
	        	coords.add(675);coords.add(-106);
	        	
	        	coords.add(629);coords.add(-108);
	        	coords.add(585);coords.add(-152);
	        	
	        	//Dessous de porte
	        	
	        	coords.add(572);coords.add(-61);
	        	coords.add(570);coords.add(-61);
	        	
	        	coords.add(538);coords.add(-83);
	        	coords.add(538);coords.add(-85);
	        	
	        	coords.add(570);coords.add(-107);
	        	coords.add(572);coords.add(-107);
	        	
	        	coords.add(630);coords.add(-49);
	        	coords.add(630);coords.add(-47);
	        	
	        	coords.add(608);coords.add(-15);
	        	coords.add(606);coords.add(-15);
	        	
	        	coords.add(584);coords.add(-47);
	        	coords.add(584);coords.add(-49);
	        	
	        	coords.add(642);coords.add(-107);
	        	coords.add(644);coords.add(-107);
	        	
	        	coords.add(676);coords.add(-85);
	        	coords.add(676);coords.add(-83);
	        	
	        	coords.add(644);coords.add(-61);
	        	coords.add(642);coords.add(-61);
	        	
	        	coords.add(584);coords.add(-119);
	        	coords.add(584);coords.add(-121);
	        	
	        	coords.add(606);coords.add(-153);
	        	coords.add(608);coords.add(-153);
	        	
	        	coords.add(630);coords.add(-121);
	        	coords.add(630);coords.add(-119);
	        	
	        	datas.set("coords.room.E", coords);

	        	coords = new ArrayList<>();
	        	
	        	//Room coords M
	        	
	        	coords.add(629);coords.add(-106);
	        	coords.add(625);coords.add(-67);
	        	
	        	coords.add(629);coords.add(-62);
	        	coords.add(590);coords.add(-66);
	        	
	        	coords.add(585);coords.add(-62);
	        	coords.add(589);coords.add(-101);
	        	
	        	coords.add(585);coords.add(-106);
	        	coords.add(624);coords.add(-102);
	        	
	        	datas.set("coords.room.M", coords);
	        	
	        	//Door Coords 
	        	
	        	//Door 0
	        	coords = new ArrayList<>();
	        	coords.add(502);coords.add(22);coords.add(-127);/*Corner A */ coords.add(502);coords.add(19);coords.add(-133); //Corner B
	        	
	        	coords.add(502);coords.add(22);coords.add(-35);/*Corner A */ coords.add(502);coords.add(19);coords.add(-41); //Corner B
	        	
	        	coords.add(564);coords.add(22);coords.add(21);/*Corner A */ coords.add(558);coords.add(19);coords.add(21); //Corner B
	        	
	        	coords.add(656);coords.add(22);coords.add(21);/*Corner A */ coords.add(650);coords.add(19);coords.add(21); //Corner B
	        	
	        	coords.add(712);coords.add(22);coords.add(-41);/*Corner A */ coords.add(712);coords.add(19);coords.add(-35); //Corner B
	        	
	        	coords.add(712);coords.add(22);coords.add(-133);/*Corner A */ coords.add(712);coords.add(19);coords.add(-127); //Corner B
	        	
	        	coords.add(650);coords.add(22);coords.add(-189);/*Corner A */ coords.add(656);coords.add(19);coords.add(-189); //Corner B
	        	
	        	coords.add(558);coords.add(22);coords.add(-189);/*Corner A */ coords.add(564);coords.add(19);coords.add(-189); //Corner B

	        	datas.set("coords.door.0", coords);
	        	
	        	//Door 1
	        	coords = new ArrayList<>();
	        	coords.add(522);coords.add(22);coords.add(-107);/*Corner A */ coords.add(518);coords.add(19);coords.add(-107); //Corner B
	        	
	        	coords.add(522);coords.add(22);coords.add(-61);/*Corner A */ coords.add(518);coords.add(19);coords.add(-61); //Corner B
	        	
	        	coords.add(584);coords.add(22);coords.add(1);/*Corner A */ coords.add(584);coords.add(19);coords.add(5); //Corner B
	        	
	        	coords.add(630);coords.add(22);coords.add(1);/*Corner A */ coords.add(630);coords.add(19);coords.add(5); //Corner B
	        	
	        	coords.add(692);coords.add(22);coords.add(-61);/*Corner A */ coords.add(696);coords.add(19);coords.add(-61); //Corner B
	        	
	        	coords.add(692);coords.add(22);coords.add(-107);/*Corner A */ coords.add(696);coords.add(19);coords.add(-107); //Corner B
	        	
	        	coords.add(630);coords.add(22);coords.add(-169);/*Corner A */ coords.add(630);coords.add(19);coords.add(-173); //Corner B
	        	
	        	coords.add(584);coords.add(22);coords.add(-169);/*Corner A */ coords.add(584);coords.add(19);coords.add(-173); //Corner B

	        	datas.set("coords.door.1", coords);
	        	
	        	//Door 2
	        	coords = new ArrayList<>();
	        	coords.add(528);coords.add(21);coords.add(-15);/*Corner A */ coords.add(527);coords.add(19);coords.add(-15); //Corner B
	        	
	        	coords.add(538);coords.add(22);coords.add(1);/*Corner A */ coords.add(538);coords.add(19);coords.add(5); //Corner B
	        	
	        	coords.add(676);coords.add(21);coords.add(-5);/*Corner A */ coords.add(676);coords.add(19);coords.add(-4); //Corner B
	        	
	        	coords.add(692);coords.add(22);coords.add(-15);/*Corner A */ coords.add(696);coords.add(19);coords.add(-15); //Corner B
	        	
	        	coords.add(686);coords.add(21);coords.add(-153);/*Corner A */ coords.add(687);coords.add(19);coords.add(-153); //Corner B
	        	
	        	coords.add(676);coords.add(22);coords.add(-169);/*Corner A */ coords.add(676);coords.add(19);coords.add(-173); //Corner B
	        	
	        	coords.add(538);coords.add(21);coords.add(-163);/*Corner A */ coords.add(538);coords.add(19);coords.add(-164); //Corner B
	        	
	        	coords.add(522);coords.add(22);coords.add(-153);/*Corner A */ coords.add(518);coords.add(19);coords.add(-153); //Corner B

	        	datas.set("coords.door.2", coords);
	        	
	        	//Door 3
	        	coords = new ArrayList<>();
	        	coords.add(538);coords.add(21);coords.add(-129);/*Corner A */ coords.add(538);coords.add(19);coords.add(-131); //Corner B
	        	
	        	coords.add(560);coords.add(21);coords.add(-153);/*Corner A */ coords.add(562);coords.add(19);coords.add(-153); //Corner B
	        	
	        	coords.add(538);coords.add(21);coords.add(-37);/*Corner A */ coords.add(538);coords.add(19);coords.add(-39); //Corner B
	        	
	        	coords.add(562);coords.add(21);coords.add(-15);/*Corner A */ coords.add(560);coords.add(19);coords.add(-15); //Corner B
	        	
	        	coords.add(654);coords.add(21);coords.add(-15);/*Corner A */ coords.add(652);coords.add(19);coords.add(-15); //Corner B
	        	
	        	coords.add(676);coords.add(21);coords.add(-39);/*Corner A */ coords.add(676);coords.add(19);coords.add(-37); //Corner B
	        	
	        	coords.add(676);coords.add(21);coords.add(-131);/*Corner A */ coords.add(676);coords.add(19);coords.add(-129); //Corner B
	        	
	        	coords.add(652);coords.add(21);coords.add(-153);/*Corner A */ coords.add(654);coords.add(19);coords.add(-153); //Corner B

	        	datas.set("coords.door.3", coords);
	        	
	        	//Door 4
	        	coords = new ArrayList<>();
	        	coords.add(570);coords.add(21);coords.add(-107);/*Corner A */ coords.add(572);coords.add(19);coords.add(-107); //Corner B
	        	
	        	coords.add(538);coords.add(21);coords.add(-83);/*Corner A */ coords.add(538);coords.add(19);coords.add(-85); //Corner B
	        	
	        	coords.add(572);coords.add(21);coords.add(-61);/*Corner A */ coords.add(570);coords.add(19);coords.add(-61); //Corner B
	        	
	        	coords.add(584);coords.add(21);coords.add(-47);/*Corner A */ coords.add(584);coords.add(19);coords.add(-49); //Corner B
	        	
	        	coords.add(608);coords.add(21);coords.add(-15);/*Corner A */ coords.add(606);coords.add(19);coords.add(-15); //Corner B
	        	
	        	coords.add(630);coords.add(21);coords.add(-49);/*Corner A */ coords.add(630);coords.add(19);coords.add(-47); //Corner B
	        	
	        	coords.add(644);coords.add(21);coords.add(-61);/*Corner A */ coords.add(642);coords.add(19);coords.add(-61); //Corner B
	        	
	        	coords.add(676);coords.add(21);coords.add(-85);/*Corner A */ coords.add(676);coords.add(19);coords.add(-83); //Corner B
	        	
	        	coords.add(642);coords.add(21);coords.add(-107);/*Corner A */ coords.add(644);coords.add(19);coords.add(-107); //Corner B
	        	
	        	coords.add(630);coords.add(21);coords.add(-121);/*Corner A */ coords.add(630);coords.add(19);coords.add(-119); //Corner B
	        	
	        	coords.add(606);coords.add(21);coords.add(-153);/*Corner A */ coords.add(608);coords.add(19);coords.add(-153); //Corner B
	        	
	        	coords.add(584);coords.add(21);coords.add(-119);/*Corner A */ coords.add(584);coords.add(19);coords.add(-121); //Corner B

	        	datas.set("coords.door.4", coords);
	        	
	        	//Door 5
	        	coords = new ArrayList<>();
	        	coords.add(605);coords.add(22);coords.add(-106);/*Corner A */ coords.add(609);coords.add(19);coords.add(-106); //Corner B
	        	
	        	coords.add(585);coords.add(22);coords.add(-82);/*Corner A */ coords.add(585);coords.add(19);coords.add(-86); //Corner B
	        	
	        	coords.add(609);coords.add(22);coords.add(-62);/*Corner A */ coords.add(605);coords.add(19);coords.add(-62); //Corner B
	        	
	        	coords.add(629);coords.add(22);coords.add(-86);/*Corner A */ coords.add(629);coords.add(19);coords.add(-82); //Corner B

	        	datas.set("coords.door.5", coords);
	        	 
	            datas.save(f);
	             
	        } catch (IOException exception) {

	            exception.printStackTrace();
	        }
	        return false;
	    } else {
	    	return true;
	    }

	}
	
	public static FileConfiguration checkMap(Main main) {
		String map = main.getConfig().getString("games.meltdown.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			f = new File(userdata, File.separator + "meltdown.yml");
		}
	    FileConfiguration datas = YamlConfiguration.loadConfiguration(f);
	    return datas;
	}
	
	public static List<Integer> getRoomCoords(Main main, String which) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> coords = new ArrayList<>();
	    switch(which) {
	    case "A":
	    	coords = datas.getIntegerList("coords.room.A");
	    	return coords;
	    case "B":
	    	coords = datas.getIntegerList("coords.room.B");
	    	return coords;
	    case "C":
	    	coords = datas.getIntegerList("coords.room.C");
	    	return coords;
	    case "D":
	    	coords = datas.getIntegerList("coords.room.D");
	    	return coords;
	    case "E":
	    	coords = datas.getIntegerList("coords.room.E");
	    	return coords;
	    case "M":
	    	coords = datas.getIntegerList("coords.room.M");
	    	return coords;
	    }
	    return coords;
	}
	
	public static List<Integer> getRoomTimes(Main main) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> times = new ArrayList<>();
	    times.add(datas.getInt("times.room.A"));
	    times.add(datas.getInt("times.room.B"));
	    times.add(datas.getInt("times.room.C"));
	    times.add(datas.getInt("times.room.D"));
	    times.add(datas.getInt("times.room.E"));
	    times.add(datas.getInt("times.room.M"));
	    return times;
	}
	
	public static List<Integer> getDoorCoords(Main main, Integer which) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> coords = new ArrayList<>();
	    
	    switch(which) {
	    case 0:
	    	coords = datas.getIntegerList("coords.door.0");
	    	return coords;
	    case 1:
	    	coords = datas.getIntegerList("coords.door.1");
	    	return coords;
	    case 2:
	    	coords = datas.getIntegerList("coords.door.2");
	    	return coords;
	    case 3:
	    	coords = datas.getIntegerList("coords.door.3");
	    	return coords;
	    case 4:
	    	coords = datas.getIntegerList("coords.door.4");
	    	return coords;
	    case 5:
	    	coords = datas.getIntegerList("coords.door.5");
	    	return coords;
	    default:
	    	return null;
	    }
	}
	
	public static List<Integer> getDoorTimes(Main main) {
	    FileConfiguration datas = checkMap(main);
	    
	    List<Integer> times = new ArrayList<>();
	    times.add(datas.getInt("times.door.1"));
	    times.add(datas.getInt("times.door.2"));
	    times.add(datas.getInt("times.door.3"));
	    times.add(datas.getInt("times.door.4"));
	    times.add(datas.getInt("times.door.5"));
	    return times;
	}
	
	public static List<Integer> getSpawnCoords(Main main, String team) {
		FileConfiguration datas = checkMap(main);
		List<Integer> coords = datas.getIntegerList("spawn." + team);
		return coords;
		
	}
	
	public static Integer getFallHeight(Main main) {
		FileConfiguration datas = checkMap(main);
		return datas.getInt("fall_height");
	}
	
	
	public static boolean isMapGenerated(Main main) {
		FileConfiguration datas = checkMap(main);
		if(datas.getBoolean("isMapGenerated")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setMapGenerated(Main main, boolean isGenerated) {
		FileConfiguration datas = checkMap(main);
		datas.set("isMapGenerated", isGenerated);
		saveDatas(datas, main);
	}
	
	public static void saveDatas(FileConfiguration datas, Main main) {
		String map = main.getConfig().getString("games.meltdown.map");
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "maps");
		File f = new File(userdata, File.separator + map + ".yml");
		if(!f.exists()) {
			f = new File(userdata, File.separator + "meltdown.yml");
		}
		try {
			datas.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
