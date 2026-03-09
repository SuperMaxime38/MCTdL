package mctdl.game.ai_trainer;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.ejml.simple.SimpleMatrix;

import mctdl.game.Main;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import ntdjl.NN;
import ntdjl.RL;
import ntdjl.utils.ActivationFunction;

public class TrainingLoop {
	
	private Main main;
	
	private RL rl;
	
	private int batches;

	private boolean isEnabled;
	
	private double mutationProportion = 0.1;
	private double mutationRate = 0.001;
	
	public TrainingLoop(Main main, MCTdLGamemode gamemode, int batches) {
		this.main = main;
		this.batches = batches;
		this.isEnabled = true;
		
		switch(gamemode) {
		case MELTDOWN:
			
<<<<<<< Updated upstream
			int[] structure = {324, 2000, 2100, 2200, 2400, 1600, 1200, 1200, 16}; // reduced size bcs too much for my computer ;-;
=======
			int[] structure = {332, 2048, 2100, 2200, 2400, 1600, 1200, 1200, 16}; // reduced size bcs too much for my computer ;-;
>>>>>>> Stashed changes
			
			aiRepartition(structure);
			this.rl.loadModel("C:/Users/maxime/Documents/rl+MD"); // Try to load model if it exists
			
			meltdownLoop(1);
			
			break;
		}
	}
	
	private void aiRepartition(int[] structure) {
		int a = 0;
		int b = 0;
		int c = 0;
		
		for(int i = 0; i < Meltdown.getNPCs().size(); i++) {
			if(i%3 == 0) a++;
			else if(i%3 == 1) b++;
			else c++;
		}

		this.rl = new RL(structure, ActivationFunction.SIGMOID, mutationProportion, mutationRate, a, b, c);
	}
	
	public void meltdownLoop(int currentBatch) {
		
		if(currentBatch >= this.batches || !isEnabled) {
			rl.saveModel("C:/Users/maxime/Documents/rl+MD");
			System.out.println("Stopped training at batch " + currentBatch);
			
			//Releasing RAM
			for(MeltdownNPC npc : Meltdown.getNPCs()) {
				npc.setModel(null);
			}
			
			this.rl = null;
			System.gc();
			
			return;
		}
		
		if(currentBatch%10 == 0) {
			rl.saveModel("C:/Users/maxime/Documents/rl+MD");
		}
		
		for(int i = 0; i < Meltdown.getNPCs().size(); i++) {
			Meltdown.getNPCs().get(i).setModel((NN) this.rl.getAgents().toArray()[i]);
		}
		
		Meltdown.enable();
		System.out.println("Batch " + currentBatch + " started");

		int curBatch = currentBatch;
		new BukkitRunnable() {
			
			//int counter = 0;
			
			@Override
			public void run() {
				if(!Meltdown.isEnabled() || !isEnabled) {
					System.out.println("Meltdown game ended");
					
					for(MeltdownNPC npc : Meltdown.getNPCs()) {
						int score = npc.getNPC().getScore();
						rl.setScore(npc.getModel(), score);
					}

					rl.nextGeneration();
					System.gc(); // Releasing RAM
					meltdownLoop(curBatch+1);
					cancel();
					return;
				}
				
				for(MeltdownNPC npc : Meltdown.getNPCs()) {
					List<Integer> pDatas = Meltdown.getRawPlayerDatas(npc.getNPC().getUniqueIDString());
					
<<<<<<< Updated upstream
					if(pDatas.get(0) == 0 || pDatas.get(1) == 1) continue; // S'il est mort ou gelé on s'en fout
=======
					if(pDatas.get(0) == 0 || pDatas.get(1) == 1) continue; // S'il est mort ou gel� on s'en fout
>>>>>>> Stashed changes
					
					npc.getEnvironnement().update();
					SimpleMatrix input = new SimpleMatrix(npc.getEnvironnement().getInputs());
					List<Float> actions = npc.getModel().predictList(input);
					
					int selectedAction = 0;
					float maxVal = actions.get(0);
					
					for(int i = 1; i < actions.size(); i++) {
						if(actions.get(i) > maxVal) {
							maxVal = actions.get(i);
							selectedAction = i;
						}
					}
					
//					if(Meltdown.getNPCs().get(0).equals(npc) && counter % 20 == 0) {
//						System.out.println("NPC: "+ npc.getNPC().getName() + " Selected action : " + selectedAction);
//						counter = 0;
//					}
					
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
						npc.placeHeater();;
						break;
					case 6:
						npc.claimPickaxe();
						npc.breakBlock();
						break;
					case 7:
						npc.shoot();
						break;
					case 8:
						npc.addYaw();
						break;
					case 9:
						npc.removeYaw();
						break;
					case 10:
						npc.addPitch();
						break;
					case 11:
						npc.removePitch();
						break;
					case 12:
						npc.addMoreYaw();
						break;
					case 13:
						npc.removeMoreYaw();
						break;
					case 14:
						npc.addMorePitch();
						break;
					case 15:
						npc.removeMorePitch();
						break;
					}
					
					//counter++;
				}
				
			}
			
		}.runTaskTimer(main, 300, 4);
	}
	
	public void forceDisable() {
		this.isEnabled = false;
	}
	
}
