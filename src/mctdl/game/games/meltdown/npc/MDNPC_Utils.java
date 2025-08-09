package mctdl.game.games.meltdown.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MDNPC_Utils {
	
	static HashMap<String, List<String>> npc_names = new HashMap<String, List<String>>();
	static Random rdm;
	
	public MDNPC_Utils() {
		
		List<String> red = new ArrayList<String>();
		List<String> blue = new ArrayList<String>();
		List<String> green = new ArrayList<String>();
		List<String> yellow = new ArrayList<String>();
		List<String> purple = new ArrayList<String>();
		List<String> aqua = new ArrayList<String>();
		List<String> black = new ArrayList<String>();
		List<String> orange = new ArrayList<String>();
		
		red.addAll(Arrays.asList("Star_Lord", "Lorem_Impsum", "Red_Steel_Beam", "Fireplace"));
		blue.addAll(Arrays.asList("Fat Seal", "Sea_You_Soon", "Titanic_Driver", "Lipton"));
		green.addAll(Arrays.asList("Grass_Toucher", "Géant_Vert", "JaimeLesPommes", "Harry_Coteur"));
		yellow.addAll(Arrays.asList("YellowSnow", "ShinyMe", "Sparkles", "SunWeightedGuy"));
		purple.addAll(Arrays.asList("Jaded", "Crystalline", "PASDINSPI1", "PASDINSPI2"));
		aqua.addAll(Arrays.asList("RapinDolphin", "Aqualand", "SlipMan", "DrinkWater"));
		black.addAll(Arrays.asList("Shockdart_Vador", "IGotNPass", "UnZoizeau", "SoMuchDarkSasuke"));
		orange.addAll(Arrays.asList("Le_poisson_Steve", "CarSmasher", "MecanismMyLove", "BeepBeepImABot"));
		
		npc_names.put("red", red);
		npc_names.put("blue", blue);
		npc_names.put("green", green);
		npc_names.put("yellow", yellow);
		npc_names.put("purple", purple);
		npc_names.put("aqua", aqua);
		npc_names.put("black", black);
		npc_names.put("orange", orange);
		
		rdm = new Random();
	}
	
	public static String getRandomName(String team) {
		List<String> names = npc_names.get(team);
		String name = names.get(rdm.nextInt(names.size()));
		names.remove(name);
		System.out.println("names for team " + team + ": " + names);
		npc_names.put(team, names);
		return name;
	}

}
