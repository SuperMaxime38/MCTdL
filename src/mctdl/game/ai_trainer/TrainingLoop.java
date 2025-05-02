package mctdl.game.ai_trainer;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.ejml.simple.SimpleMatrix;

import mctdl.game.Main;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import ntdjl.RL;
import ntdjl.utils.ActivationFunction;

public class TrainingLoop {
	
	Main main;
	
	RL rl;
	
	int batches;
	
	boolean isEnabled;
	
	public TrainingLoop(Main main, MCTdLGamemode gamemode, int batches) {
		this.main = main;
		this.batches = batches;
		this.isEnabled = true;
		
		switch(gamemode) {
		case MELTDOWN:
			
			int[] structure = {149, 1600, 1600, 2400, 3200, 2400, 1600, 1600, 14};
			
			this.rl = new RL(structure, ActivationFunction.GELU, Meltdown.getNPCs().size());
			this.rl.setTopClones(20, 8, 4);
			
			meltdownLoop(1);
			
			break;
		}
	}
	
	public void meltdownLoop(int currentBatch) {
		
		if(currentBatch >= this.batches || !isEnabled) {
			rl.saveModel("C:/Users/maxime/Documents/rl");
			System.out.println("Stopped training at batch " + currentBatch);
			
			return;
		}
		
		if(currentBatch%10 == 0) {
			rl.saveModel("C:/Users/maxime/Documents/rl");
		}
		
		for(MeltdownNPC npc : Meltdown.getNPCs()) {
			npc.setModel(this.rl.nextAgent());
			//System.out.println("NPC " + npc.getNPC().getName() + " got a model !");
		}
		
		Meltdown.enable();
		System.out.println("Batch " + currentBatch + " started");
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!Meltdown.isEnabled() || !isEnabled) {
					System.out.println("Meltdown game ended");
					cancel();
				}
				
				for(MeltdownNPC npc : Meltdown.getNPCs()) {
					List<Integer> pDatas = Meltdown.getRawPlayerDatas(npc.getNPC().getUniqueIDString());
					
					if(pDatas.get(0) == 0 || pDatas.get(1) == 1) continue; // S'il est mort ou gelé on s'en fout
					
					npc.getEnvironnement().update();
					SimpleMatrix input = new SimpleMatrix(npc.getEnvironnement().inputs);
					List<Float> actions = npc.getModel().predictList(input);
					
					int selectedAction = 0;
					float maxVal = actions.get(0);
					
					for(int i = 1; i < actions.size(); i++) {
						if(actions.get(i) > maxVal) {
							maxVal = actions.get(i);
							selectedAction = i;
						}
					}
					
					switch(selectedAction) {
					case 0:
						npc.moveForward();
						break;
					case 1:
						npc.moveBackward();
						break;
					case 2:
						npc.moveLeft();
						break;
					case 3:
						npc.moveRight();
						break;
					case 4:
						npc.jump();
						break;
					case 5:
						npc.sneak();
						break;
					case 6:
						npc.placeHeater();;
						break;
					case 7:
						npc.claimPickaxe();
						break;
					case 8:
						npc.breakBlock();
						break;
					case 9:
						npc.shoot();
						break;
					case 10:
						npc.addYaw();
						break;
					case 11:
						npc.removeYaw();
						break;
					case 12:
						npc.addPitch();
						break;
					case 13:
						npc.removePitch();
						break;
					}
				}
				
			}
			
		}.runTaskTimer(main, 201, 10);
		
		for(MeltdownNPC npc : Meltdown.getNPCs()) {
			int score = npc.getNPC().getScore();
			rl.setScore(npc.getModel(), score);
		}

		rl.nextGeneration();
		currentBatch++;
		
		meltdownLoop(currentBatch);
	}
	
	public void forceDisable() {
		this.isEnabled = false;
	}
	
}
