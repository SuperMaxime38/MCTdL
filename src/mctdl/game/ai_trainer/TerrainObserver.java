package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;

import mctdl.game.npc.PlayerAI;

public class TerrainObserver {

	PlayerAI player;
	int cornerX, cornerZ, width;
	List<Double> blocks;
	World world;
	
	public TerrainObserver(PlayerAI player, double radius) {
		this.player = player;
		this.cornerX = (int) (player.locX() - radius);
		this.cornerZ = (int) (player.locZ() - radius);
		this.width = (int) (radius * 2);
		this.world = player.getBukkitEntity().getWorld();
		
		this.blocks = new ArrayList<Double>();
	}
	
	public List<Double> update() {
		blocks.clear();
		for(int x = this.cornerX; x < this.cornerX + this.width; x++) {
			for(int z = this.cornerZ; z < this.cornerZ + this.width; z++) {
				Block b = world.getBlockAt(x, (int) player.locY(), z);
				
				if(Environnement.getTransparentBlocks().contains(b.getType())) {
					blocks.add(0.0);
				} else {
					blocks.add(1.0);
				}
			}
		}
		
		return blocks;
	}
	
}
