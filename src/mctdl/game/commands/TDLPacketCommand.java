package mctdl.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mctdl.game.npc.NPCManager;
import mctdl.game.teams.TeamsManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class TDLPacketCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if(args.length == 0) {
			s.sendMessage("Command help will come soon");
			return true;
		}
		
		if(args.length == 1) {
			if(args[0].equals("tp")) {
				s.sendMessage("/tdlpacket tp <npc> <x> <y> <z>");
				return true;
			}
		}
		
		if(args.length == 5) {
			if(args[0].equals("tp")) {
				
				EntityPlayer npc = NPCManager.getNpcByUUID(TeamsManager.getUUIDByPseudo(args[1]).toString());
				if(npc == null) {
					s.sendMessage("NPC not found");
					return true;
				}
				
				NPCManager.teleportNPC(npc, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
				s.sendMessage("Teleported");
				
				return true;
			}
		}
		
		return false;
	}

}
