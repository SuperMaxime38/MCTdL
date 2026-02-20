package mctdl.game.games.lobby.items;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.utils.NBTAPI;
import mctdl.game.utils.Ray;

public class PortalGun implements Listener {
	
	static HashMap<Location, Location> portals = new HashMap<Location, Location>(); // <from, to>
	static HashMap<Player, HashMap<String, Location>> playerPortals = new HashMap<Player, HashMap<String, Location>>();
	static HashMap<Entity, Integer> tp_cooldown = new HashMap<Entity, Integer>();
	
	static Main main;
	
	public PortalGun(Main main) {
		PortalGun.main = main;
	}
	
	public static ItemStack getItem() {
		ItemStack item = new ItemStack(Material.WOODEN_HOE);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3Portal §6Gun");
		meta.setCustomModelData(2);
		meta.setLore(Arrays.asList("§7Portal Guns are used to teleport...", "§cNOWAY BRO THE NAME ISN'T EXPLICIT ENOUGH!", "", "§7...", "§7Anyway, §3LEFT CLICK §7for a blue portal", "§6RIGHT CLICK §7for an orange portal"));
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		NBTAPI.addNBT(item, "mctdlID", "PORTAL_GUN");
		
		return item;
	}
	
	@EventHandler
	public static void itemInteraction(PlayerInteractEvent e) {
		
		if(!e.getPlayer().getInventory().getItemInMainHand().isSimilar(getItem())) return;
		
		e.setCancelled(true);
		
		Player player = e.getPlayer();
		
		Ray ray = new Ray(player.getWorld(), player.getLocation().toVector().add(new Vector(0, 1.5, 0)), player.getLocation().getDirection().normalize().multiply(0.25));
		
		
		Location loc = ray.getFrontBlockTargetedLocation(64);
		loc.setDirection(player.getLocation().getDirection().multiply(-1).normalize());
		if(loc.distance(player.getLocation()) < 0.5) return;
		
		HashMap<String, Location> portals = new HashMap<String, Location>();
		if(playerPortals.containsKey(player)) {
			portals = playerPortals.get(player);
		}
		
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			
			portals.put("blue", loc);
			
		} else if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
			portals.put("orange", loc);
			
		}
		
		playerPortals.put(player, portals);
		
		System.out.println("Triggered portalgun for " + player.getName());
		
	}
	
	public static void displayPortals() {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				portals.clear();
				for(Player portalOwners : playerPortals.keySet()) {
					
					Location blue = playerPortals.get(portalOwners).get("blue");
					Location orange = playerPortals.get(portalOwners).get("orange");
					
					if(blue != null) {
						if(orange != null) {
							portals.put(blue, orange);
							portals.put(orange, blue);
							showOrangePortal(orange);
						} else {
							portals.put(blue, null);
						}

						showBluePortal(blue);
					} else {
						portals.put(orange, null);

						showOrangePortal(orange);
					}
				}
				
				for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
					
					for(Location loc : portals.keySet()) {
						if(entity.getLocation().distance(loc) < 1.75) {
							
							if(portals.get(loc) != null) {
								if(!tp_cooldown.containsKey(entity)) {
									
									Vector delta = loc.getDirection().add(entity.getVelocity());
									
									Location out = portals.get(loc).clone();
									out.setDirection(out.getDirection().add(delta).add(new Vector(0, 1, 0)));
									
									out.add(portals.get(loc).getDirection().normalize().multiply(2));
//									out.add(0, 1, 0);
									
									entity.teleport(out);
									entity.getLocation().setDirection(out.getDirection().normalize());
									
									Vector vel = entity.getVelocity().multiply(-1);
									
//									Vector vel = entity.getVelocity().multiply(-1.25);
//									vel.multiply(portals.get(loc).getDirection().normalize());
									entity.setVelocity(vel);
									
									System.out.println("Teleported " + entity.getName() + " | vel " + vel);
									tp_cooldown.put(entity, 2);
								} else {
									if(tp_cooldown.get(entity) == 0) {
										tp_cooldown.remove(entity);
									}
									else tp_cooldown.put(entity, tp_cooldown.get(entity) - 1);
								}
							}
							
						}
					}
					
				}
				
			}
			
		}.runTaskTimer(main, 0, 0);
	}
	
	private static void showBluePortal(Location loc) {
		Location current = loc.clone();
		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current, 0, 0, 0, -0.0005);
		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current.add(current.getDirection()), 0, 0, 0, -0.0005);
		
		
//		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current.add(0,1,0), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current.add(0,0,1), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current.add(0,-1,0), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.WATER_SPLASH, current.add(0,0,-1), 0, 0, 0, -0.0005);
	}
	
	private static void showOrangePortal(Location loc) {
		Location current = loc.clone();
		loc.getWorld().spawnParticle(Particle.FLAME, current, 0, 0, 0, -0.0005);
		loc.getWorld().spawnParticle(Particle.FLAME, current.add(current.getDirection()), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.FLAME, current.add(0,1,0), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.FLAME, current.add(0,0,1), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.FLAME, current.add(0,-1,0), 0, 0, 0, -0.0005);
//		loc.getWorld().spawnParticle(Particle.FLAME, current.add(0,0,-1), 0, 0, 0, -0.0005);
	}
}
