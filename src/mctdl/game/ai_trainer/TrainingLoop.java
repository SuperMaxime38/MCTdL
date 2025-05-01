package mctdl.game.ai_trainer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import mctdl.game.Main;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.games.meltdown.npc.MeltdownNPC;
import ntdjl.RL;
import ntdjl.utils.ActivationFunction;
import ntdjl.utils.DataHolder;

public class TrainingLoop {
	
	Main main;
	
	RL rl;
	
	int batches;
	
	public TrainingLoop(Main main, MCTdLGamemode gamemode, int batches) {
		this.main = main;
		this.batches = batches;
		
		switch(gamemode) {
		case MELTDOWN:
			
			int[] structure = {149, 1600, 1600, 2400, 3200, 2400, 1600, 1600, 14};
			
			this.rl = new RL(structure, ActivationFunction.GELU, 32);
			this.rl.setTopClones(20, 8, 4);
			
			break;
		}
	}
	
	public void meltdownLoop(int currentBatch) {
		
		if(currentBatch >= this.batches) {
			rl.saveModel("C:/Users/maxime/Documents/rl");
			
			return;
		}
		
		if(currentBatch%10 == 0) {
			rl.saveModel("C:/Users/maxime/Documents/rl");
		}
		
		for(MeltdownNPC npc : Meltdown.getNPCs()) {
			npc.setModel(this.rl.nextAgent());
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!Meltdown.isEnabled()) {
					cancel();
				}
				
				for(MeltdownNPC npc : Meltdown.getNPCs()) {
					DataHolder datas = new DataHolder();
					ArrayList<Double> inputs = new ArrayList<Double>();
					for(float data : npc.getEnvironnement().datas) {
						String dt = String.valueOf(data);
						inputs.add(Double.parseDouble(dt));
					}
					
					datas.addData(inputs);
					List<Float> actions = npc.getModel().predictList(datas.convertToMatrix());
					
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
			
		}.runTaskTimer(main, 0, 4);
		

		rl.nextGeneration();
		currentBatch++;
		
		meltdownLoop(currentBatch);
	}
	
}
