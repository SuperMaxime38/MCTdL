package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;

import mctdl.game.npc.PlayerAI;

public class TerrainObserver {

	PlayerAI player;
	int radius;
	List<Double> blocks;
	World world;
	
	public TerrainObserver(PlayerAI player, double radius) {
		this.player = player;
		this.radius = (int) radius;
		this.world = player.getBukkitEntity().getWorld();
		
		this.blocks = new ArrayList<Double>();
	}
	
	public List<Double> update() {
		blocks.clear();
		for(int x = -radius; x < radius; x++) {
			for(int z = -radius; z < radius; z++) {
				int X = x + (int) player.getX();
				int Z = z + (int) player.getZ();
				int Y = (int) player.getY() - 1;
				Block b = world.getBlockAt(X, Y, Z);
				
				//System.out.println("loc: " + X + ", " + (player.getY()-1) + ", " + Z + " type: " + b.getType());
				
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
