package mctdl.game.games.lobby.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;

public class PouleZooka implements Listener{
	
	static Main main;
	
	static HashMap<Chicken, Player> bullets = new HashMap<Chicken, Player>();
	
	public PouleZooka(Main main) {
		PouleZooka.main = main;
	}
	
	public static void spawnNPC(Player p) {
		
	}
	
	public static ItemStack getBazooka() {
		ItemStack item = new ItemStack(Material.WOODEN_HOE);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§ePouleZooka");
		meta.setLore(Arrays.asList("§7Wé c'est un bazooka... MAIS POUR LES POULES", "§7en plus c'est le miens", "","§7- Canwardow"));
		meta.setUnbreakable(true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		
		return item;
	}
	
//	
//	//Interaction NPC
//	@EventHandler
//	public static void npcInteract(PlayerInteractAtEntityEvent e) {
//	}
	
	
	//Bazooka Intercation
	@EventHandler
	public static void bazookaShot(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		if(item != null) {
			if(item.isSimilar(getBazooka())) {
				Player p = e.getPlayer();
				
				Vector direction = p.getLocation().getDirection();
				//bullet.setVelocity(direction.multiply(1.25));
				
				Chicken poule = (Chicken) p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.CHICKEN);
				
				bullets.put(poule, p);
				
				double Ymultiplier = 1.25;
				if(direction.getY() > 0.9) {
					Ymultiplier = 3.8;
				}
				
				poule.setGravity(false);
				poule.setVelocity(direction.add(new Vector(direction.getX()*1.25, 0.11, direction.getZ()*1.25)));
				poule.setVelocity(new Vector(poule.getVelocity().getX(), poule.getVelocity().getY()* Ymultiplier, poule.getVelocity().getZ()));
				
				new BukkitRunnable() {
					double acceleration = 0;
					
					List<Location> check = new ArrayList<>();
					
					@Override
					public void run() {
						check.add(poule.getLocation().add(0, 0.5, 0));
						check.add(poule.getLocation().add(0.5, 0, 0));
						check.add(poule.getLocation().add(0, 0, 0.5));
						check.add(poule.getLocation().add(-0.5, 0, 0));
						check.add(poule.getLocation().add(0, -0.5, 0));
						check.add(poule.getLocation().add(0, 0, -0.5));
						
						for (Location lc : check) {
							if(lc.getBlock().getType() != Material.AIR) {
								poule.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 1));
								poule.setHealth(0);
								cancel();
								return;
							}
						}
						if(!poule.getNearbyEntities(0.8, 0.8, 0.8).isEmpty()) {
							for(Entity ent : poule.getNearbyEntities(0.8, 0.8, 0.8)) {
								if(ent.getType() == EntityType.CHICKEN) {
									poule.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 1));
									poule.setHealth(0);
									
									//Trigger truc du minijeu
									
									cancel();
									return;
								}
							}
						}
						
						//poule.setVelocity(poule.getVelocity().add(new Vector(direction.getX()/10*acceleration, -acceleration, direction.getZ()/10*acceleration)));
						poule.setVelocity(poule.getVelocity().add(new Vector(direction.getX()/16, (direction.getY()/16)-acceleration, direction.getZ()/16)));
						//acceleration += 0.0040;
						acceleration += 0.0150;
						
						//Particles
						poule.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, poule.getLocation(), 1, 0.25, 0.25, 0.25, -0.0005);
						poule.getWorld().spawnParticle(Particle.FLAME, poule.getLocation(), 1, 0.25, 0.25, 0.25, -0.0005);
						
						
					}
				}.runTaskTimer(main, 0, 1);
				
			}
		}
	}
	
	@EventHandler
	public static void onDeath(EntityDeathEvent e) {
		if(bullets.containsKey(e.getEntity())) {
			e.getDrops().clear();
			e.getEntity().setInvisible(true);
			e.getEntity().remove();
			
			//Particles
			Location loc = e.getEntity().getLocation();
			loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 16, 2, 2, 2, -0.0005);
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 24, 2, 2, 2, -0.0005);
			loc.getWorld().spawnParticle(Particle.FLAME, loc, 12, 1, 1, 1, -0.0005);
			loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.8f, 0.1f);
			loc.getWorld().playSound(loc, Sound.ENTITY_CHICKEN_DEATH, 2.5f, 1.5f);
			
			//Do stuff
			List<Entity> ents = e.getEntity().getNearbyEntities(2, 2, 2);
			if(ents.isEmpty()) return;
			for(Entity ent : ents) {
				if(ent instanceof Player) {
					Player p = (Player) ent;
					Vector deltaDir = e.getEntity().getLocation().toVector().clone().subtract(p.getLocation().toVector());
					p.setVelocity(p.getVelocity().add(new Vector(-deltaDir.getX()*1.05, 0.9, -deltaDir.getZ()*1.05)));
				}
				if(thieves.contains(ent)) {
					thieves.get(thieves.indexOf(ent)).remove();
					Player p = bullets.get(e.getEntity());

					Random rdm = new Random();
					boolean earn = rdm.nextBoolean();
					int amount = 0;
					if(earn == true) {
						amount = rdm.nextInt(5) + 1;
						MoneyManager.addPlayerPoutre(p.getName(), amount);
						p.sendMessage("§aCôôôt ! Vous avez eu " + amount + " §6poutres §aavec cette poule");
					} else {
						p.sendMessage("§cCôt >:( !! Cette poule était un clochard et n'avait pas de §6poutres");
					}
				}
			}
		}
	}
	
	static List<Chicken> thieves = new ArrayList<>();
	
	//Spawn chickens
	public static void spawnChicken(Location base, int radius) {
		Random random = new Random();
		int X, Y, Z;
		for(int i=0; i<10; i++) {
			X = base.getBlockX()+ random.nextInt(radius*2) - radius;
			Y = base.getBlockY()+ random.nextInt(radius*2) - radius;
			Z = base.getBlockZ()+ random.nextInt(radius*2) - radius;
			
			Location loc = new Location(Bukkit.getWorlds().get(0), X, Y, Z);
			Chicken poule = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
			poule.setGravity(false);
			thieves.add(poule);
		}
	}
	
	public static void pouleCore() {
		new BukkitRunnable() {

			Random random = new Random();
			int rdm;
			int X = 0, Y = 0, Z = 0;
			
			@Override
			public void run() {
				for (Chicken chicken : thieves) {
					rdm = random.nextInt(4);
					switch(rdm) {
					default: break;
					case 1:
						Y = random.nextInt(8) - 4; //Peut bouger de 4blocks max
						break;
					case 2:
						X = random.nextInt(8) - 4;
						Z = random.nextInt(8) - 4;
						break;
					case 3:

						Y = random.nextInt(8) - 4;
						X = random.nextInt(8) - 4;
						Z = random.nextInt(8) - 4;
						break;
					}
					Vector v = new Vector(X, Y, Z);
					chicken.setVelocity(v.multiply(0.5));
				}
			}
			
		}.runTaskTimer(main, 0, 20);
	}
}
