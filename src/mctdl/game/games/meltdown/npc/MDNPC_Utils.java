package mctdl.game.games.meltdown.npc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MDNPC_Utils {
	
	HashMap<String, List<String>> npc_names = new HashMap<String, List<String>>();
	Random rdm;
	
	public MDNPC_Utils() {

		npc_names.put("red", Arrays.asList("Star_Lord", "Lorem_Impsum", "Red_Steel_Beam"));
		npc_names.put("blue", Arrays.asList("Fat Seal", "Sea_You_Soon", "Titanic_Driver"));
		npc_names.put("green", Arrays.asList("Grass_Toucher", "Géant_Vert"));
		npc_names.put("yellow", Arrays.asList("Lorem", "ShinyMe"));
		npc_names.put("purple", Arrays.asList("NO_NAME"));
		npc_names.put("aqua", Arrays.asList("LackOfInspi"));
		npc_names.put("black", Arrays.asList("Shockdart_Vador"));
		npc_names.put("orange", Arrays.asList("Le_poisson_Steve"));
		
		rdm = new Random();
	}
	
	public String getRandomName(String team) {
		return npc_names.get(team).get(rdm.nextInt(npc_names.get(team).size()));
	}

}
