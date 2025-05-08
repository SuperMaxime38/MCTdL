package mctdl.game.dev;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mctdl.game.games.lobby.items.NuclearRollerSkates;
import mctdl.game.games.lobby.items.PouleZooka;
import mctdl.game.games.meltdown.Meltdown;
import mctdl.game.utils.PlayerData;
import mctdl.game.utils.objects.Canon;
import mctdl.game.utils.objects.riffles.AssaultRiffle;

public class ItemGiver implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if(!(s instanceof Player)) {
			s.sendMessage(ChatColor.RED + "Only players can do this command");
			return true;
		}
		Player p = (Player) s;
		
		if(args.length == 0) {
			p.sendMessage("§aCette commande sert à se give les Items Customs présent dans le MCTdL");
			p.sendMessage("§aFaites §6/dg list §apour voir la liste des items disponibles");
			p.sendMessage("§3Faites §6/dg add <item> §3pour ajouter cet item a votre inv");
		}
		if(args.length == 1) {
			if(args[0].equals("list")) {
				p.sendMessage("§aVoici la liste des items disponibles §f:");
				for(ItemList item : ItemList.values()) {
					p.sendMessage("§3" + item);
				}
			}
		}
		if(args.length == 2) {
			if(args[0].equals("add")) {
				ItemList item = ItemList.valueOf(args[1].toUpperCase());
				addItem(item, p);
			}
		}
		
		return false;
	}
	
	public static void addItem(ItemList item, Player p) {
		ItemStack it;
		ItemMeta meta;
		String type = "";
		
		switch(item) {
		
		//LOBBY
		case HELICO_HAT:
			it = PlayerData.helicoHat();
			type = "truc";
			p.getInventory().setHelmet(it);
			break;
		case SUPPORTER_RED:
			it = PlayerData.supporter("red");
			break;
		case SUPPORTER_BLUE:
			it = PlayerData.supporter("blue");
			break;
		case SUPPORTER_GREEN:
			it = PlayerData.supporter("green");
			break;
		case SUPPORTER_YELLOW:
			it = PlayerData.supporter("yellow");
			break;
		case SUPPORTER_PURPLE:
			it = PlayerData.supporter("purple");
			break;
		case SUPPORTER_AQUA:
			it = PlayerData.supporter("aqua");
			break;
		case SUPPORTER_ORANGE:
			it = PlayerData.supporter("orange");
			break;
		case SUPPORTER_BLACK:
			it = PlayerData.supporter("black");
			break;
		case POULEZOOKA:
			it = PouleZooka.getBazooka();
			break;
		case NUCLEAR_ROLLERS:
			it = NuclearRollerSkates.getItem();
			break;
		
		//MELTDOWN
		case MD_FREEZING_GUN:
			it = Meltdown.getBow();
			break;
		case MD_HEATER:
			it = Meltdown.getHeater("nope");
			break;
		case MD_COOLDOWN_PICKAXE:
			it = Meltdown.getCooldownPickaxe();
			break;
		case MD_TEAM_PICKAXE:
			it = Meltdown.getPickaxe();
			break;
			
		//OBJECTS
		case CANON:
			it = Canon.getItem();
			break;
		case ASSAULT_RIFFLE:
			it = AssaultRiffle.getAssaultRiffle();
			break;
		case ASSAULT_RIFFLE_TARGET:
			it = AssaultRiffle.getAssaultRiffle();
			meta = it.getItemMeta();
			meta.setDisplayName(meta.getDisplayName() + " Target");
			it.setItemMeta(meta);
			break;
		case SHOTGUN:
			it = AssaultRiffle.getShotGun();
			break;
		case SHOTGUN_TARGET:
			it = AssaultRiffle.getShotGun();
			meta = it.getItemMeta();
			meta.setDisplayName(meta.getDisplayName() + " Target");
			it.setItemMeta(meta);
			break;
		case SNIPER_RIFFLE:
			it = AssaultRiffle.getSniper();
			break;
		case SNIPER_RIFFLE_TARGET:
			it = AssaultRiffle.getSniper();
			meta = it.getItemMeta();
			meta.setDisplayName(meta.getDisplayName() + " Target");
			it.setItemMeta(meta);
			break;
		//DECORATION
		case COLT:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fcolt");
			it.setItemMeta(meta);
			break;
		case AMMO:
			it = new ItemStack(Material.OAK_BUTTON);
			meta = it.getItemMeta();
			meta.setDisplayName("§fammo");
			it.setItemMeta(meta);
			break;
		case BLOOD:
			it = new ItemStack(Material.REDSTONE);
			meta = it.getItemMeta();
			meta.setDisplayName("§cblood");
			it.setItemMeta(meta);
			break;
		case BULLET:
			it = new ItemStack(Material.OAK_BUTTON);
			meta = it.getItemMeta();
			meta.setDisplayName("§fbullet");
			it.setItemMeta(meta);
			break;
		case BAR:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fbar");
			it.setItemMeta(meta);
			break;
		case KNIFE:
			it = new ItemStack(Material.WOODEN_SWORD);
			meta = it.getItemMeta();
			meta.setDisplayName("§fknife");
			it.setItemMeta(meta);
			break;
		case CARABINE:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fcarabine");
			it.setItemMeta(meta);
			break;
		case M1GARAND:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fM1Garand");
			it.setItemMeta(meta);
			break;
		case M4:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fM4");
			it.setItemMeta(meta);
			break;
		case MP5K:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fmp5k");
			it.setItemMeta(meta);
			break;
		case MP40:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fmp40");
			it.setItemMeta(meta);
			break;
		case DECO_SHOTGUN:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fshotgun");
			it.setItemMeta(meta);
			break;
		case SPRINGFIELDS:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fspringfields");
			it.setItemMeta(meta);
			break;
		case THOMPSON:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fthompson");
			it.setItemMeta(meta);
			break;
		case SNIPER:
			it = new ItemStack(Material.WOODEN_HOE);
			meta = it.getItemMeta();
			meta.setDisplayName("§fsniper");
			it.setItemMeta(meta);
			break;
		default:
			it = new ItemStack(Material.AIR);
			break;
		}
		if(type == "") p.getInventory().addItem(it);
	}
	
	enum ItemList {
		//Lobby Items
		HELICO_HAT,
		SUPPORTER_RED,
		SUPPORTER_BLUE,
		SUPPORTER_GREEN,
		SUPPORTER_YELLOW,
		SUPPORTER_PURPLE,
		SUPPORTER_AQUA,
		SUPPORTER_ORANGE,
		SUPPORTER_BLACK,
		POULEZOOKA,
		NUCLEAR_ROLLERS,
		
		//Meltdown items
		MD_FREEZING_GUN,
		MD_COOLDOWN_PICKAXE,
		MD_TEAM_PICKAXE,
		MD_HEATER,
		
		//Objects
		CANON,
		ASSAULT_RIFFLE,
		ASSAULT_RIFFLE_TARGET,
		SHOTGUN,
		SHOTGUN_TARGET,
		SNIPER_RIFFLE,
		SNIPER_RIFFLE_TARGET,
		
		//Decoration items
		COLT,
		AMMO,
		BLOOD,
		BULLET,
		BAR,
		KNIFE,
		CARABINE,
		M1GARAND,
		M4,
		MP5K,
		MP40,
		DECO_SHOTGUN,
		SPRINGFIELDS,
		THOMPSON,
		SNIPER;
	}

}
