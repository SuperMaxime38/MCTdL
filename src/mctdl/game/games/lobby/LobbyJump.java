package mctdl.game.games.lobby;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;

public class LobbyJump implements CommandExecutor, Listener{

	static Main main;
	public LobbyJump(Main main) {
		LobbyJump.main = main;
	}
	
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

		if(!(s instanceof Player)) {
			s.sendMessage("§cOnly players can execute this command");
			return true;
		}
		Player p = (Player) s;
		
		if(!main.getConfig().getString("game").equals("lobby")) {
			p.sendMessage("§cVous ne pouvez utiliser cette commande que dans le Lobby");
			return true;
		}
		
		if(args.length == 0) {
			p.sendMessage("§6Liste des commandes pour §a/jump §f:"
					+ "\n§a/jump on §f: Active un effet de jump boost 1"
					+ "\n§a/jump off §f: Désactive les effets de jump boost"
					+ "\n§a/jump cp §f: Mettre le checkpoint à la position actuelle"
					+ "\n§a/jump tp §f: Se téléporter au checkpoint");
			
			return true;
		}
		if(args.length == 1) {
			if(args[0].equals("on")) {
				toggleJumpBoost(p, true);
				return true;
			}
			if(args[0].equals("off")) {
				toggleJumpBoost(p, false);
				return true;
			}
			if(args[0].equals("cp")) {
				setCheckpoint(p);
				p.sendMessage("§aVous avez défini un checkpoint sur votre position");
				return true;
			}
			if(args[0].equals("tp")) {
				tpToCheckpoint(p);
				return true;
			}
		}
		
		return false;
	}
	
	static HashMap<String, Location> datas = new HashMap<String, Location>();
	static Location loc;
	
	/*public static boolean fileCheck(Main main){
    	
	     File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "lobby");
	     File f = new File(userdata, File.separator + "jump.yml");
	     FileConfiguration preset = YamlConfiguration.loadConfiguration(f);

	     datas = new HashMap<String, Location>();
	     
	     if (!f.exists()) { //CREER LES SECTIONS AVEC DES LISTES VIDES SI FICHIER N'EXISTE PAS
	         try {
	        	 
	        	 preset.createSection("checkpoint");
	        	 
	             preset.save(f);
	             ConfigurationSection sec = preset.getConfigurationSection("checkpoint");
	             if(sec == null) return false;
	             if(sec.getKeys(false).isEmpty() || sec.getKeys(false) == null) return false;
	             for (String pseudo : sec.getKeys(false)) {
	            	loc = new Location(Bukkit.getPlayer(pseudo).getWorld(), sec.getInt(pseudo + ".X"), sec.getInt(pseudo + ".Y"), sec.getInt(pseudo + ".Z"));
	            	datas.put(pseudo, loc);
				}
	             
	         } catch (IOException exception) {

	             exception.printStackTrace();
	         }
	         return false;
	     } else {
	    	 return false;
	     }
	     
    }*/ //Si jump sauvegard§s dans fichier (peu probable)
	
	public static void setCheckpoint(Player p) {
		loc = p.getLocation();
		datas.put(p.getName(), loc);
	}
	
	public static void tpToCheckpoint(Player p) {
		if(!datas.containsKey(p.getName())) {
			p.sendMessage("§cVous n'avez pas d§fini de checkpoint : §a/jump cp");
			return;
		}
		
		loc = datas.get(p.getName());
		p.teleport(loc);
	}
	
	public static void toggleJumpBoost(Player p, boolean enable) {
		//PotionEffect pot = p.getPotionEffect(PotionEffectType.JUMP);
		if(enable == false) {
			p.removePotionEffect(PotionEffectType.JUMP);
		}
		if(enable == true) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
		}
	}
	
	@EventHandler
	public static void Interact(PlayerInteractEvent e) { //QUAND CLIC SUR BOUTON DE FIN
		if(!main.getConfig().getString("game").equals("lobby")) return;
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getClickedBlock().getType() == Material.OAK_BUTTON) {
				Location loc = new Location(Bukkit.getWorlds().get(0), 72, 49, 17);
				if(e.getPlayer().getLocation().distance(loc) < 3) {
					MoneyManager.addPlayerPoutre(e.getPlayer().getName(), 1);
					loc.add(0, -2, 0);
					e.getPlayer().teleport(loc);
				}
			}
		}
	}
} 
