package mctdl.game.games.meltdown.npc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MDNPC_Utils {
	
	static HashMap<String, List<String>> npc_names = new HashMap<String, List<String>>();
	
	public MDNPC_Utils() {
		
		npc_names.put("red", Arrays.asList("Star Lord"));
	}
	
	public static String getRandomName(String team) {
		return "Poutre";
	}

}
