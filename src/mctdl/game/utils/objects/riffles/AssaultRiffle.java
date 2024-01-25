package mctdl.game.utils.objects.riffles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mctdl.game.Main;
import mctdl.game.utils.Time;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class AssaultRiffle implements Listener{
	
	static Main main;
	
	static float cooldown;
	static double damage = 1;
	static float firerate = 5f;
	static int magazine = 24;
	static int reloadtime = 5 * 20; //Conversion tick
	static int sprayrate = 12;
	static double speed = 1;
	static float acceleration =  0.00094f;
	static int drop = 30;
	
	static double sprayX = 0;
	static double sprayY = 0;
	static double sprayZ = 0;
	
	static long lastshot = 0;
	
	static boolean isReloading = false;
	
	static List<String> names = new ArrayList<>();
	
	public AssaultRiffle(Main main) {
		AssaultRiffle.main = main;
		names.add(getAssaultRiffle().getItemMeta().getDisplayName());
		names.add(getAssaultRiffle().getItemMeta().getDisplayName() + " Target");
		names.add(getShotGun().getItemMeta().getDisplayName());
		names.add(getShotGun().getItemMeta().getDisplayName() + " Target");
		names.add(getSniper().getItemMeta().getDisplayName());
		names.add(getSniper().getItemMeta().getDisplayName() + " Target");
	}
	
	public static ChatColor getQuality(String stat, double value) {
		ChatColor c = ChatColor.GRAY;
		switch(stat) {
		case "damage":
			if(value <=3) {
				c = ChatColor.RED;
			} else if(value <= 6) {
				c = ChatColor.YELLOW;
			} else if(value <= 10) {
				c = ChatColor.GREEN;
			} else {
				c = ChatColor.DARK_GREEN;
			}
			break;
		case "magazine":
			if(value <= 5) {
				c = ChatColor.RED;
			} else if(value <= 12) {
				c = ChatColor.YELLOW;
			} else if(value <= 24) {
				c = ChatColor.GREEN;
			} else {
				c = ChatColor.DARK_GREEN;
			}
			break;
		case "spray":
			if(value >=30) {
				c = ChatColor.DARK_RED;
			} else if(value >= 25) {
				c = ChatColor.RED;
			} else if(value >= 20) {
				c = ChatColor.YELLOW;
			} else if(value >= 10) {
				c = ChatColor.GREEN;
			} else {
				c = ChatColor.DARK_GREEN;
			}
			break;
		case "reload":
			if(value >=10*20) {
				c = ChatColor.DARK_RED;
			} else if(value >= 8*20) {
				c = ChatColor.RED;
			} else if(value >= 5*20) {
				c = ChatColor.YELLOW;
			} else if(value >= 3*20) {
				c = ChatColor.GREEN;
			} else {
				c = ChatColor.DARK_GREEN;
			}
			break;
		}
		
		return c;
	}
	
	public static ItemStack getAssaultRiffle() {
		
		
		//Assault Riffle Stats
		damage = 2;
		firerate = 5f;
		magazine = 24;
		reloadtime = 5*20;
		sprayrate = 12;
		speed = 3;
		acceleration =  0.00034f;
		drop = 30;
		
		
		ItemStack item = new  ItemStack(Material.WOODEN_HOE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§8Assault Riffle");
		meta.setLore(Arrays.asList("§eStats :", 
				"§7Damage : " + getQuality("damage", damage) + damage, 
				"§7Spray Rate : " + getQuality("spray", sprayrate) +sprayrate,
				"§7Reload Time : " + getQuality("reload", reloadtime) + reloadtime/20,
				"§7Magazine : " + magazine + "/" + magazine));
		
		
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		meta.setUnbreakable(true);

		meta.setCustomModelData(1);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getShotGun() {
		
		//ShotGun Stats
		damage = 1;
		firerate = 1f;
		magazine = 8;
		reloadtime = 6*20;
		sprayrate = 24;
		speed = 3;
		acceleration =  0.015f;
		drop = 4;
		
		
		ItemStack item = new ItemStack(Material.WOODEN_HOE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§8ShotGun");
		meta.setLore(Arrays.asList("§eStats :", 
				"§7Damage : §c0§f-" + getQuality("damage", damage*8) + damage*8, 
				"§7Spray Rate : " + getQuality("spray", sprayrate) +sprayrate,
				"§7Reload Time : " + getQuality("reload", reloadtime) + reloadtime/20,
				"§7Magazine : " + magazine + "/" + magazine));
		
		
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		meta.setUnbreakable(true);

		item.setItemMeta(meta);
		
		
		return item;
	}
	
	public static ItemStack getSniper() {
		
		//Sniper Stats
		damage = 8.5;
		firerate = 0.5f;
		magazine = 4;
		reloadtime = 7*20;
		sprayrate = 50;
		speed = 4;
		acceleration =  0.0001f;
		drop = 80;
		
		
		ItemStack item = new ItemStack(Material.WOODEN_HOE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§8Sniper Riffle");
		meta.setLore(Arrays.asList("§eStats :", 
				"§7Damage : " + getQuality("damage", damage) + damage, 
				"§7Spray Rate : " + getQuality("spray", sprayrate) +sprayrate,
				"§7Reload Time : " + getQuality("reload", reloadtime) + reloadtime/20,
				"§7Magazine : " + magazine + "/" + magazine));
		
		
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		meta.setUnbreakable(true);

		item.setItemMeta(meta);
		
		
		return item;
	}
	
	static Vector direction;
	
	@EventHandler
	public static void onShoot(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasLore()) return;
		if(!names.contains(item.getItemMeta().getDisplayName())) return;
		e.setCancelled(true);
		
		String gun = names.get(names.indexOf(item.getItemMeta().getDisplayName()));
		gun = gun.replace(" Target", "");
		gun = gun.toLowerCase();
		if(gun.contains("assault")) { //SERT A UPDATE LES VARIABLES DE STATS
			getAssaultRiffle();
		}
		if(gun.contains("shotgun")) { //SERT A UPDATE LES VARIABLES DE STATS
			getShotGun();
		}
		if(gun.contains("sniper")) { //SERT A UPDATE LES VARIABLES DE STATS
			getSniper();
		}

		Player p = e.getPlayer();
		ItemMeta meta = item.getItemMeta();
		
		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) { //Viser
			String name = meta.getDisplayName();
			if(name.contains("Target")) {
				name = name.replace(" Target", "");
				if(name.contains("Sniper")) p.setWalkSpeed(0.2f); //Zoom OUT
			} else {
				name += " Target";
				if(name.contains("Sniper")) p.setWalkSpeed(-0.55f); //Zoom In
			}
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			return;
		}
		
		if(cooldown != 0) { //Peu pas tirer
			return;
		}
		cooldown = (float)(1f/firerate);
		cooldown();
		
		String mag = meta.getLore().get(4);
		mag = mag.replace("§7Magazine : ", "");
		mag = mag.split("/")[0];
		int ammos = Integer.parseInt(mag);
		
		if(ammos == 0) {
			reload(e.getPlayer(), item);
			return;
		}
		ammos--;
		List<String> lore = meta.getLore();
		lore.set(4, "§7Magazine : " + ammos + "/" + magazine);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.6f, 0);
		
		ArmorStand stand = getStand(p);
		
		direction = p.getLocation().getDirection();
		
		long shot = Time.getCurrentTick();
		
		if(gun.contains("shotgun")) {
			shotgunShot(p, direction.multiply(speed), shot, meta);
		} else {
			direction = getSpray(direction.multiply(speed), shot, meta);
			shoot(stand, p, direction);
		}

		lastshot = shot;
	}
	
	public static ArmorStand getStand(Player p) {
		ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(0, 1.55, 0), EntityType.ARMOR_STAND);
		stand.setSmall(true);
		stand.setInvulnerable(true);
		stand.setGravity(true);
		stand.setCollidable(false);
		stand.setInvisible(true);
		
		return stand;
	}
	
	public static Vector getSpray(Vector direction, long shot, ItemMeta meta) {
		String name = meta.getDisplayName().toLowerCase();
		if(name.contains("shotgun")) {
			Random rdm = new Random();
			sprayX = (-0.09 + rdm.nextDouble() * (0.09 - (-0.09))); //Valeur entre -0.01 et 0.01 --> min + random * (max - min)
			sprayY = (-0.09 + rdm.nextDouble() * (0.1 - (-0.9))); //Valeur entre -0.01 et 0.01
			sprayZ = (-0.09 + rdm.nextDouble() * (0.09 - (-0.9))); //Valeur entre -0.01 et 0.01
		}
		
		if(shot - lastshot < sprayrate) {
			Random rdm = new Random();
			if(name.contains("sniper")) {
				sprayX = (-0.09 + rdm.nextDouble() * (0.09 - (-0.09))); //Valeur entre -0.01 et 0.01 --> min + random * (max - min)
				sprayY = (-0.09 + rdm.nextDouble() * (0.1 - (-0.09))); //Valeur entre -0.01 et 0.01
				sprayZ = (-0.09 + rdm.nextDouble() * (0.09 - (-0.09))); //Valeur entre -0.01 et 0.01
			} else {
				sprayX = (-0.055 + rdm.nextDouble() * (0.055 - (-0.055))); //Valeur entre -0.01 et 0.01 --> min + random * (max - min)
				sprayY = (-0.075 + rdm.nextDouble() * (0.09 - (-0.075))); //Valeur entre -0.01 et 0.01
				sprayZ = (-0.055 + rdm.nextDouble() * (0.055 - (-0.055))); //Valeur entre -0.01 et 0.01
			}
		}
		if(meta.getDisplayName().contains("Target")) {
			sprayX /= 2;
			sprayY /= 2;
			sprayZ /= 2;
		}
		direction.add(new Vector(sprayX, sprayY, sprayZ));
		return direction;
	}
	
	public static void shoot(ArmorStand stand, Player p, Vector direction) {
		new BukkitRunnable(){
			List<Location> check = new ArrayList<>();
			Location loc = p.getLocation();
			float gravity = 0;
			
			@Override
			public void run() {
				if(!stand.getNearbyEntities(0.1, 0.1,0.1).isEmpty()) {
					if(!stand.getNearbyEntities(0.1, 0.1,0.1).contains(p)) {
						Entity ent = stand.getNearbyEntities(0.1, 0.1,0.1).get(0);
						if(!(ent instanceof ArmorStand)) {
							stand.remove();
							if(ent instanceof LivingEntity) {
								LivingEntity entity = (LivingEntity) ent;
								if(ent instanceof Player) { //Changer degats fonction de ou tire
									double dist = ent.getLocation().distance(stand.getLocation());
									if(dist <= 0.75) { //Legs
										entity.setHealth(entity.getHealth() - damage/2);
									} else if(dist <= 1.4) { //Chest
										entity.setHealth(entity.getHealth() - damage);
									} else { //Head
										entity.setHealth(entity.getHealth() - damage * 2);
									}
								} else {
									entity.setHealth(entity.getHealth() - damage);
								}
								entity.setVelocity(entity.getVelocity().add(direction.multiply(0.15)));
								entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 1));
								checkParticle(stand.getLocation(), true);
							}
							cancel();
							return;
						}
					}
				}
				check.add(stand.getLocation().add(0, 0.25, 0));
				check.add(stand.getLocation().add(0.5, 0, 0));
				check.add(stand.getLocation().add(0, 0, 0.5));
				check.add(stand.getLocation().add(-0.5, 0, 0));
				check.add(stand.getLocation().add(0, -0.25, 0));
				check.add(stand.getLocation().add(0, 0, -0.5));
				
				for (Location lc : check) {
					if(lc.getBlock().getType() != Material.AIR) {
						stand.remove();
						checkParticle(stand.getLocation(), false);
						cancel();
						return;
					}
				}
				if(stand.getLocation().distance(loc) >= drop) gravity += acceleration;
				stand.setVelocity(direction.add(new Vector(0, -gravity, 0)));//0.784000015258789 est l'intensité gravitationnelle dans minecraft
				stand.getWorld().spawnParticle(Particle.SMOKE_NORMAL, stand.getLocation(), 1, 0, 0, 0, -0.0005);
			}
		}.runTaskTimer(main, 0, 1);
	}
	
	public static void shotgunShot(Player p, Vector direction, long shot, ItemMeta meta) {
		List<ArmorStand> stands = new ArrayList<>();
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		stands.add(getStand(p));
		
		for(int i = 0; i < 8; i++) {

			ArmorStand stand = stands.get(i);
			new BukkitRunnable(){
				List<Location> check = new ArrayList<>();
				Location loc = p.getLocation();
				float gravity = 0;
				Vector dir = getSpray(direction.clone(), shot, meta);
				@Override
				public void run() {
					if(!stand.getNearbyEntities(0.1, 0.1,0.1).isEmpty()) {
						if(!stand.getNearbyEntities(0.1, 0.1,0.1).contains(p)) {
							for(Entity ent : stand.getNearbyEntities(0.1, 0.1,0.1)) {
								if(!(ent instanceof ArmorStand)) {
									stand.remove();
									if(ent instanceof LivingEntity) {
										LivingEntity entity = (LivingEntity) ent;
										if(ent instanceof Player) { //Changer degats fonction de ou tire
											double dist = ent.getLocation().distance(stand.getLocation());
											if(dist <= 0.75) { //Legs
												entity.setHealth(entity.getHealth() - damage/2);
											} else if(dist <= 1.4) { //Chest
												entity.setHealth(entity.getHealth() - damage);
											} else { //Head
												entity.setHealth(entity.getHealth() - damage * 2);
											}
										} else {
											entity.setHealth(entity.getHealth() - damage);
										}
										entity.setVelocity(entity.getVelocity().add(dir.multiply(0.15)));
										entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 1));
										checkParticle(stand.getLocation(), true);
									}
									cancel();
									return;
								}
							}
						}
					}
					check.add(stand.getLocation().add(0, 0.25, 0));
					check.add(stand.getLocation().add(0.5, 0, 0));
					check.add(stand.getLocation().add(0, 0, 0.5));
					check.add(stand.getLocation().add(-0.5, 0, 0));
					check.add(stand.getLocation().add(0, -0.25, 0));
					check.add(stand.getLocation().add(0, 0, -0.5));
					
					for (Location lc : check) {
						if(lc.getBlock().getType() != Material.AIR) {
							stand.remove();
							checkParticle(stand.getLocation(), false);
							cancel();
							return;
						}
					}
					if(stand.getLocation().distance(loc) >= drop) gravity += acceleration;
					stand.setVelocity(dir.add(new Vector(0, -gravity, 0)));//0.784000015258789 est l'intensité gravitationnelle dans minecraft
					stand.getWorld().spawnParticle(Particle.SMOKE_NORMAL, stand.getLocation(), 1, 0, 0, 0, -0.0005);
				}
			}.runTaskTimer(main, 0, 1);
		}
	}
	
	public static void checkParticle(Location loc, boolean isBlood) {


		Vector dir = loc.getDirection();
		loc.setX(loc.getX() + 0.2*dir.getX());
		loc.setY(loc.getY() + 0.2*dir.getY());
		loc.setZ(loc.getZ() + 0.2*dir.getZ());
		new BukkitRunnable() {
			
			int counter = 0;
			
			@Override
			public void run() {
				if(isBlood && counter == 0) {
					counter = 198;
				}
				if(counter == 200) {
					cancel();
					return;
				}
				if(isBlood) {
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.fromBGR(0, 0, 255), 1));
				} else {
					//loc.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, -0.0005);
					loc.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, -0.0005);
				}
				counter++;
			}
		}.runTaskTimer(main, 0, 1);
	}
	
	public static void cooldown() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(cooldown != 0) {
					cooldown =- 0.05f;
					if(cooldown < 0) {
						cooldown = 0;
						cancel();
						return;
					}
				}
			}
		}.runTaskTimer(main, 0, 1);
	}
	
	public static void reload(Player p, ItemStack item) {
		if(isReloading) return;
		isReloading = true;
		new BukkitRunnable() {
			
			int counter = 0;
			
			@Override
			public void run() {
				if(counter == reloadtime) {
					ItemMeta meta = item.getItemMeta();
					List<String> lore = meta.getLore();
					lore.set(4, "§7Magazine : " + magazine + "/" + magazine);
					meta.setLore(lore);
					item.setItemMeta(meta);

					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§6100%§a:::::§f]"));
					isReloading = false;
					
					cancel();
					return;
				}
				if(reloadtime/10 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:§f::::§410%§f:::::]"));
				}
				if(reloadtime/5 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a::§f:::§420%§f:::::]"));
				}
				if(reloadtime/3.33 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::§f::§c30%§f:::::]"));
				}
				if(reloadtime/2.5 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a::::§f:§c40%§f:::::]"));
				}
				if(reloadtime/2 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§e50%§f:::::]"));
				}
				if(reloadtime/1.66 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§e60%§a:§f::::]"));
				}
				if(reloadtime/1.43 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§a70%§a::§f:::]"));
				}
				if(reloadtime/1.25 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§a80%§a:::§f::]"));
				}
				if(reloadtime/1.11 <= counter) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f[§a:::::§290%§a::::§f:]"));
				}
				
				p.sendTitle("", "§cReloading...", 0, 2, 1);
				counter++;
			}
		}.runTaskTimer(main, 0, 1);
	}
	
}
