package mctdl.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import mctdl.game.npc.NPCManager;
import mctdl.game.npc.PlayerAI;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class ChunkUnload implements Listener{
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e){
		for(EntityPlayer npc : NPCManager.getNPCs()) {
			if(npc instanceof PlayerAI) {
				e.getChunk().load();
			}
		}
	}

}
