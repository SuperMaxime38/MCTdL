package mctdl.game.utils.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.type.Grindstone;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.utils.LobbyData;
import mctdl.game.utils.NBTAPI;

public class Canon implements Listener{
	
	static Main main;
	public Canon(Main main) {
		Canon.main = main;
	}
	
	private static List<ArmorStand> chairs = new ArrayList<>();
	static HashMap<String, List<String>> canons = new HashMap<String, List<String>>();
	static HashMap<String, List<Location>> defaut = new HashMap<String, List<Location>>();
	static List<Player> sat = new ArrayList<>();
	
	public static ItemStack getItem() {//Get ItemStack
		ItemStack item = new ItemStack(Material.GUNPOWDER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cCanon");
		meta.setLore(Arrays.asList("§7Un canon placable qui... bah tire des boulets"));
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(meta);
		
		NBTAPI.addNBT(item, "mctdlID", "CANON");
		
		return item;
	}
	
	public static void placeCanon(Player p, Location loc, Main main) {
		canons = LobbyData.getCanonDatas();
		loc.add(0, 1, 0);
		
		float yaw = p.getLocation().getYaw();
		p.sendMessage("yaw:" + yaw);
		
		//Remove item canon from inv if not creative
		if(p.getGameMode() != GameMode.CREATIVE) {
			for (ItemStack it : p.getInventory().getContents()) {
				if(it != null) {
					int slot = p.getInventory().first(it);
					if(it.isSimilar(getItem())) {
						it.setAmount(it.getAmount() - 1);
						p.getInventory().setItem(slot, it);
					}
				}
			}
		}
		
		//Stair placing ----->
		Block b = loc.getBlock();
		b.setType(Material.GRINDSTONE);
		BlockData bdata = b.getBlockData();
		if (bdata instanceof Grindstone) {
		Grindstone stair = (Grindstone) bdata;
		stair.setAttachedFace(AttachedFace.CEILING);
		
		loc.add(0.5, 0, 0.5);
		double x = loc.getX(), z = loc.getZ();
		
		double xA = x, zA = z;
		double xB = x, zB = z;
		double xC = x, zC = z;
		float chairyaw = 0;
		
		if(yaw < -135 || yaw > 135) { //CA VEUT DIRE NORTH
			stair.setFacing(BlockFace.SOUTH);
			zA -= 0.62;
			zB -= 1.15;
			zC += 1;
			chairyaw = 180;
			p.sendMessage("N");
		}
		if(yaw > 45 && yaw < 136) { //WEST
			stair.setFacing(BlockFace.EAST);
			xA -= 0.62;
			xB -= 1.15;
			xC += 1;
			chairyaw = 90;
			p.sendMessage("W");
		}
		if(yaw > -45 && yaw < 45) { //SOUTH
			stair.setFacing(BlockFace.NORTH);
			zA += 0.62;
			zB += 1.15;
			zC -= 1;
			p.sendMessage("S");
		}
		if(yaw > -136 && yaw < -46) { //EAST
			stair.setFacing(BlockFace.WEST);
			xA += 0.62;
			xB += 1.15;
			xC -= 1;
			chairyaw = -90;
			p.sendMessage("E");
		}
		b.setBlockData(stair);
		b.getState().update();
		//---------
		Location O = new Location(loc.getWorld(), x, loc.getY() - 0.7, z, yaw, 0);
		
		ArmorStand bouche1 = (ArmorStand) O.getWorld().spawnEntity(O, EntityType.ARMOR_STAND);
		bouche1.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
		bouche1.setGravity(false);
		bouche1.setInvisible(true);
		
		
		Location A = new Location(loc.getWorld(), xA, loc.getY() -0.7, zA, yaw, 0);
		ArmorStand bouche2 = (ArmorStand) A.getWorld().spawnEntity(A, EntityType.ARMOR_STAND);
		bouche2.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
		bouche2.setGravity(false);
		bouche2.setInvisible(true);

		Location B = new Location(loc.getWorld(), xB, loc.getY() +0.05, zB, yaw, 0);
		ArmorStand bouche3 = (ArmorStand) B.getWorld().spawnEntity(B, EntityType.ARMOR_STAND);
		bouche3.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
		bouche3.setSmall(true);
		bouche3.setGravity(false);
		bouche3.setInvisible(true);
		

		Location Chair = new Location(loc.getWorld(), xC, loc.getY() -1.3, zC, chairyaw, 0);
		ArmorStand chairsd = (ArmorStand) Chair.getWorld().spawnEntity(Chair, EntityType.ARMOR_STAND);
		chairsd.getEquipment().setHelmet(new ItemStack(Material.OAK_STAIRS));
		chairsd.setGravity(false);
		chairsd.setInvisible(true);
		
		chairs.add(chairsd);
		List<String> ids = new ArrayList<>();
		ids.add(bouche1.getUniqueId().toString());
		ids.add(bouche2.getUniqueId().toString());
		ids.add(bouche3.getUniqueId().toString());
		ids.add(chairsd.getUniqueId().toString());
		
		canons.put(String.valueOf(chairsd.getUniqueId()), ids);
		
		List<Location> dfloc = new ArrayList<>();
		dfloc.add(O);
		dfloc.add(A);
		dfloc.add(B);
		dfloc.add(Chair);
		defaut.put(String.valueOf(chairsd.getUniqueId()), dfloc);
		
		LobbyData.setCanonDatas(canons);
		
		
		}
		
	}
	
	@EventHandler
	public static void onSit(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		if(chairs == null) return;
		
		Entity ent = e.getRightClicked();
		if(sat.contains(p)) return;
		if(chairs.contains(ent)) {
			sat.add(p);
			ent.addPassenger(p);
			List<String> data = LobbyData.getCanonDatas().get(ent.getUniqueId().toString());
			rotateCanon(data, p);
		}
	}
	
	@EventHandler
	public static void dismount(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(sat.contains(p)) {
			sat.remove(p);
		}
	}
	
	public static void rotateCanon(List<String> entities, Player p) {
		
		ArmorStand bouche1 = (ArmorStand) Bukkit.getEntity(UUID.fromString(entities.get(0)));
		ArmorStand bouche2 = (ArmorStand) Bukkit.getEntity(UUID.fromString(entities.get(1)));
		ArmorStand bouche3 = (ArmorStand) Bukkit.getEntity(UUID.fromString(entities.get(2)));
		ArmorStand chairsd = (ArmorStand) Bukkit.getEntity(UUID.fromString(entities.get(3)));
		
		new BukkitRunnable() {
			
			double x = bouche1.getLocation().getX();
			double z = bouche1.getLocation().getZ();
			
			double xA = bouche2.getLocation().getX();
			double zA = bouche2.getLocation().getZ();
			
			double xB = bouche3.getLocation().getX();
			double zB = bouche3.getLocation().getZ();
			
			double xC =chairsd.getLocation().getX();
			double zC = chairsd.getLocation().getZ();
			
			float b1yaw = bouche1.getLocation().getYaw();
			
			@Override
			public void run() {
				
				if(!sat.contains(p)) {
					/*
					List<Location> locs = defaut.get(chairsd.getUniqueId().toString());
					bouche1.teleport(locs.get(0));
					bouche2.teleport(locs.get(1));
					bouche3.teleport(locs.get(2));
					chairsd.teleport(locs.get(3));
					*/
					cancel();
					return;
				}
				
				float yaw = p.getLocation().getYaw();
				float dAngle = yaw - b1yaw;
				
					double angle = Math.toRadians(dAngle);
					
					double xA2 = (xA - x)*Math.cos(angle) - (zA-z) * Math.cos(angle) + x;
					double zA2 = (xA-x)*Math.sin(angle) + (zA-z) * Math.cos(angle) + z;
					
					double xB2 = (xB - x)*Math.cos(angle) - (zB-z) * Math.cos(angle) + x;
					double zB2 = (xB-x)*Math.sin(angle) + (zB-z) * Math.cos(angle) + z;
					
					double xC2 = (xC - x)*Math.cos(angle) - (zC-z) * Math.cos(angle) + x;
					double zC2 = (xC-x)*Math.sin(angle) + (zC-z) * Math.cos(angle) + z;
					
					Location A2 = bouche2.getLocation().clone();
					A2.setX(xA2);
					A2.setZ(zA2);
					A2.setYaw(yaw);
					
					bouche2.teleport(A2);
					
					Location B2 = bouche3.getLocation().clone();
					B2.setX(xB2);
					B2.setZ(zB2);
					B2.setYaw(yaw);
					
					bouche3.teleport(B2);
					
					CraftEntity ent = (CraftEntity) chairsd;
					ent.getHandle().setPositionRotation(xC2, chairsd.getLocation().getY(), zC2, yaw, 0); 
					
				
			}
		}.runTaskTimer(main, 0, 1);
		
	}
	
	@EventHandler
	public static void onShoot(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!sat.contains(p)) return;
		
		if(e.getAction() == Action.LEFT_CLICK_AIR) {
			List<String> data = LobbyData.getCanonDatas().get(p.getVehicle().getUniqueId().toString());
			ArmorStand bouche3 = (ArmorStand) Bukkit.getEntity(UUID.fromString(data.get(2)));
			
			ArmorStand bullet = (ArmorStand) p.getWorld().spawnEntity(bouche3.getLocation(), EntityType.ARMOR_STAND);
			bullet.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
			bullet.setSmall(true);
			bullet.setGravity(true);
			bullet.setInvisible(true);
			
			
			new BukkitRunnable() {
				
				Vector dir = p.getLocation().getDirection();
				int t = 0;
				double gravity = 0;
				double acceleration = 0.004;

				@Override
				public void run() {
					if(t==0) {
						dir = dir.multiply(0.1);
						p.sendMessage(dir.toString());
						t++;
					}
					if(!bullet.getNearbyEntities(0.5, 0.5, 0.5).isEmpty() || bullet.getLocation().getBlock().getType() != Material.AIR) { //si ya contact alors boom
						if(!bullet.getNearbyEntities(0.5, 0.5, 0.5).isEmpty()) {
							if(!bullet.getNearbyEntities(0.5, 0.5, 0.5).contains(bouche3)) {
								//boom
								
								bullet.remove();
								p.sendMessage("obstacle");
								
								cancel();
								return;
								
							} else {
								
							}
						} else {
							//boom
							
							bullet.remove();
							p.sendMessage("obstacle");
							
							cancel();
							return;
						}
					}
					gravity += acceleration;
					dir.setY(dir.getY() - gravity);
					bullet.setVelocity(dir);
				}
				
			}.runTaskTimer(main, 0, 1);
		}
	}
	
	
}
