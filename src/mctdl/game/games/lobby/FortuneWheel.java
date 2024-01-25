package mctdl.game.games.lobby;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mctdl.game.money.MoneyManager;
import mctdl.game.teams.TeamsManager;

public class FortuneWheel {
	
	public static void gui(Player p) {
		Inventory inv = Bukkit.createInventory(null, 45, "§6Roue de la Fortune");
		ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "");
		
		//Remplie inv de vitres grises
		item.setItemMeta(meta);
		for(int i = 0; i< 45; i++) {
			inv.setItem(i, item);
		}
		
		//Défini infos
		item.setType(Material.GOLD_INGOT);
		meta.setDisplayName("§3Informations :");
		meta.setLore(Arrays.asList("§7Tournez la §6Roue de la Fortune §7pour gagner de nombreux lots, ", "§7comme par exemple des cosmétiques pour le §aLobby", " ", "§6Coût : 5 Poutres"));
		item.setItemMeta(meta);
		
		inv.setItem(4, item);
		
		
		//Balance
		item.setType(Material.GOLD_NUGGET);
		meta.setDisplayName("§6Balance :");
		meta.setLore(Arrays.asList("§7Vous avez actuellement §6" + MoneyManager.getPlayerPoutres(p.getName()) + " Poutres"));
		item.setItemMeta(meta);
		
		inv.setItem(8, item);
		
		//Arrow down
		item.setType(Material.ARROW);
		meta.setDisplayName("§7Look Down");
		meta.setLore(Arrays.asList(""));
		item.setItemMeta(meta);
		
		inv.setItem(13, item);
		
		//Arrow up
		item.setType(Material.ARROW);
		meta.setDisplayName("§7Look Up");
		meta.setLore(Arrays.asList(""));
		item.setItemMeta(meta);
		
		inv.setItem(31, item);
		
		//Poutres
		
		item.setType(Material.IRON_INGOT);
		item.setAmount(3);
		meta.setDisplayName("§fx3 Poutres");
		meta.setLore(Arrays.asList("§fVous gagnerez 3 Poutres"));
		item.setItemMeta(meta);
		
		inv.setItem(36, item);
		
		item.setAmount(5);
		meta.setDisplayName("§ax5 Poutres");
		meta.setLore(Arrays.asList("§aVous gagnerez 5 Poutres"));
		item.setItemMeta(meta);
		
		inv.setItem(37, item);
		
		item.setAmount(10);
		meta.setDisplayName("§ax10 Poutres");
		meta.setLore(Arrays.asList("§aVous gagnerez 10 Poutres"));
		item.setItemMeta(meta);
		
		inv.setItem(38, item);
		
		item.setAmount(1);
		meta.setDisplayName("§a 1 Poutre");
		meta.setLore(Arrays.asList("§aVous gagnerez 1 Poutre !", "§7C'est une vrai poutre et pas une version virtuelle !"));
		item.setItemMeta(meta);
		
		inv.setItem(39, item);
		
		//Supporter
		ChatColor c = TeamsManager.getTeamColor(p.getName());
		//Gant Supporter
		switch(c) {
		case RED:
			meta.setDisplayName(c + "Gant Supporter Red Rocket");
			break;
		case BLUE:
			meta.setDisplayName(c + "Gant Supporter Blue Whale");
			break;
		case GREEN:
			meta.setDisplayName(c + "Gant Supporter Green Turtle");
			break;
		case YELLOW:
			meta.setDisplayName(c + "Gant Supporter Yellow Stone");
			break;
		case DARK_PURPLE:
			meta.setDisplayName(c + "Gant Supporter Purple Amethyst");
			break;
		case DARK_AQUA:
			meta.setDisplayName(c + "Gant Supporter Aqua Doplhin");
			break;
		case DARK_GRAY:
			meta.setDisplayName(c + "Gant Supporter Black Raven");
			break;
		case GOLD:
			meta.setDisplayName(c + "Gant Supporter Orange Mechanic");
			break;
		default:
			meta.setDisplayName("§fGant Supporter Everyone<3");
			break;
		}
		meta.setLore(Arrays.asList("§7Montrez que vous soutnez à fond votre équipe", "§7ou récupérez un gant d'une équipe adverse pour... hmm... §cTRAITRE !"));
		item.setItemMeta(meta);
		
		inv.setItem(39, item);
		
		//Ouvre le GUI au joueur
		p.openInventory(inv);
	}
}
