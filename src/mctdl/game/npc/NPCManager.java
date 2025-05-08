package mctdl.game.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.http.HttpResponse;
import org.bukkit.craftbukkit.libs.org.apache.http.client.ClientProtocolException;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpGet;
import org.bukkit.craftbukkit.libs.org.apache.http.impl.client.CloseableHttpClient;
import org.bukkit.craftbukkit.libs.org.apache.http.impl.client.HttpClients;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;

import mctdl.game.Main;
import mctdl.game.money.MoneyManager;
import mctdl.game.tablist.TabManager;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class NPCManager {


//Liste des textures
static HashMap<String, List<String>> textures = new HashMap<String, List<String>>();

public static HashMap<Player, List<EntityPlayer>> inViewNPCs = new HashMap<Player, List<EntityPlayer>>();
	

static List<EntityPlayer> npcss = new ArrayList<>();
static List<EntityPlayer> lookPlayer = new ArrayList<>();
static Main main;
//static HashMap<String, List<String>> data = new HashMap<String, List<String>>();

	public static boolean fileCheck(Main main){
		NPCManager.main = main;
    	
	     File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "npc");
	     File f = new File(userdata, File.separator + "npc.yml");
	     FileConfiguration preset = YamlConfiguration.loadConfiguration(f);

	     
	     if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	         preset.createSection("textures");
			 
			 preset.createSection("npc");
//	        	 List<Integer> data = new ArrayList<>();
//	        	 data.add(-73);
//	        	 data.add(14);
//	        	 data.add(72);
//	        	 preset.set("npc.top1", data);
//	        	 
//	             preset.save(f);
	         return false;
	     } else {
	    	return true; 
	     }
     }
	
	public static void loadHashMap(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "npc");
	    File f = new File(userdata, File.separator + "npc.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	    
	    if(yaml.getConfigurationSection("textures") != null) {
	    	 for (String uuid : yaml.getConfigurationSection("textures").getKeys(false)) {
	 	    	List<String> texture = new ArrayList<>();
	 			texture.add(yaml.getStringList("textures." + uuid).get(0));
	 			texture.add(yaml.getStringList("textures." + uuid).get(1));
	 			textures.put(uuid, texture);
	 		}
	    }
//	    for(String role : yaml.getConfigurationSection("npc").getKeys(false)) {
//	    	List<String> elements = yaml.getStringList("npc." + role);
//	    	data.put(role, elements);
//	    }
	}
	
	public static void updateConfig(Main main) {
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "npc"); //A FAIRE
	    File f = new File(userdata, File.separator + "npc.yml");
	    fileCheck(main);
	    FileConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	    
	    yaml.set("textures", null);
	    for (String uuid : textures.keySet()) {
			yaml.set("textures." + uuid, textures.get(uuid));
		}
	    
//	    for (String role : data.keySet()) {
//	    	List<?> elements = data.get(role);
//	    	
//			yaml.set("npc." + role, elements);
//		}
	    
	    try {
			yaml.save(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void onPlayerJoin(Player p, int delay) {
		
		for(EntityPlayer npc : npcss) {
			showNPCFor(npc, p, null);
		}
		
		new BukkitRunnable() {

			@Override
			public void run() {
				for(EntityPlayer npc : npcss) {
					hideTabNameFor(npc, p);
				}
				System.out.println("[Handler] > Randered NPCs for " + p.getName());
				
			}
			
		}.runTaskLater(main, delay);
	}
	
	public static List<EntityPlayer> getNPCs() {return npcss;}
	
	public static Player getNpcPlayerIfItIs(String uuid) {
		for(EntityPlayer pl : NPCManager.getNPCs()) {
			if(pl.getUniqueID().toString().equals(uuid)) {
				return pl.getBukkitEntity();
			}
		}
		return null;
	}
	
	public static boolean isAnNPC(String uuid) {
		for(EntityPlayer npc : npcss) {
			if(npc.getUniqueIDString().equals(uuid)) return true;
		}
		return false;
	}
	
	public static EntityPlayer npcBuilder(String name, String textureowner, Location loc, Player p) {
		CraftPlayer cplayer  = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		MinecraftServer server = sp.getMinecraftServer(); //get Server
		WorldServer lvl = sp.getWorldServer(); //get World
		
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name); //NPC Profile
		
		List<String> textures = NPCManager.getPlayerTexture(textureowner);
		String texture = textures.get(0);
		String signature = textures.get(1);
		
		gameProfile.getProperties().put("textures", new Property("textures", texture , signature));
	      
		PlayerInteractManager pi = new PlayerInteractManager(lvl);
		
		EntityPlayer npc = new EntityPlayer(server, lvl, gameProfile, pi); //Create NPC
		npc.setPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		npcss.add(npc);
		
		return npc;
	}
	
	public static void addExternalNPC(EntityPlayer npc) {
		npcss.add(npc);
	}
	
	public static void showNPCFor(EntityPlayer npc, Player p, List<ItemStack> items) {
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		
		//Player Info Packet
		ps.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc)); //"CONNECTE" LES INFOS DU NPC (tab, skin, etc...)
		
		//Spawn packet
		ps.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)); //Fait apparaitre le npc
		
		//NPC Equipment
		List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipment = new ArrayList<>();
		
		if(items == null) return;
		if(items.size() < 6) return;
			
		equipment.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(items.get(0)))); //Item Main Hand
		equipment.add(new Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(items.get(1)))); //Item Left Hand
		equipment.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(2)))); //Item Helmlet
		equipment.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(items.get(3)))); //Item Chestplate
		equipment.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(items.get(4)))); //Item Leggings
		equipment.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(items.get(5)))); //Item Boots
			
			
		ps.sendPacket(new PacketPlayOutEntityEquipment(npc.getBukkitEntity().getEntityId(), equipment));
		
	}
	
	public static void showNpcWithoutTabFor(EntityPlayer npc, Player p, List<ItemStack> items) {
		showNPCFor(npc, p, items);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				hideTabNameFor(npc, p);
			}
			
		}.runTaskLater(main, 10);
	}
	
	public static void destroyNPC(EntityPlayer npc) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			npcKiller(npc, p);
		}
		

		
		TeamsManager.removePlayerTeam(npc.getUniqueIDString());
		MoneyManager.deleteFromExistence(npc.getUniqueIDString());
		PlayerData.deleteFromExistence(npc.getUniqueIDString());

		TeamsManager.removeUUIDToPseudo(npc.getUniqueIDString());
		
		
		npcss.remove(npc);
		npc.getBukkitEntity().remove();
		npc.die();
		
		TabManager.updateTabList();
	}
	
	public static void destroyNPCs() {
		List<EntityPlayer> copy_of_npcs = npcss.stream().collect(Collectors.toList());
		for(EntityPlayer npc : copy_of_npcs) {
			destroyNPC(npc);
		}
	}
	
	public static void hideTabNameFor(EntityPlayer npc, Player p) {
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		
		ps.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
	}
	
	public static void npcKiller(EntityPlayer npc, Player p) {
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		
		//Packet<?> p1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc);
		Packet<?> p2 = new PacketPlayOutEntityDestroy(npc.getBukkitEntity().getEntityId());
		
		//ps.sendPacket(p1);
		ps.sendPacket(p2);
		
		
	}
	
	public static void killAllNPCs(Player p) {
		for(EntityPlayer npc : npcss) {
			npcKiller(npc, p);
		}
	}
	
<<<<<<< Updated upstream
	 public static void teleportNPC(EntityPlayer npc, double x, double y, double z) {

        npc.setLocation(x, y, z, npc.yaw, npc.pitch);
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        if(!loc.getChunk().isLoaded()) loc.getChunk().load();
        npc.getBukkitEntity().teleport(new Location(Bukkit.getWorld("world"), x, y, z));
=======
	 public static void teleportNPC(EntityPlayer npc, double x, double y, double z, float yaw, float pitch) {

        npc.setLocation(x, y, z, yaw, pitch);
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
        if(!loc.getChunk().isLoaded()) loc.getChunk().load();
        npc.getBukkitEntity().teleport(loc);
>>>>>>> Stashed changes
//      npc.enderTeleportTo(x, y, z);
        Packet<?> packet = new PacketPlayOutEntityTeleport(npc);
         
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

            connection.sendPacket(packet);
<<<<<<< Updated upstream
        }
	  }
=======
            rotateNPC(npc, yaw, pitch, p);
        }
	  }
	 public static void teleportNPC(EntityPlayer npc, Location loc) {
		 	if(!loc.getChunk().isLoaded()) loc.getChunk().load();
	 		npc.getBukkitEntity().teleport(loc);
	 		
	 		Packet<?> packet = new PacketPlayOutEntityTeleport(npc);
	         
	        for (Player p : Bukkit.getOnlinePlayers()) {
	            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

	            connection.sendPacket(packet);
	            rotateNPC(npc, loc.getYaw(), loc.getPitch(), p);
	        }
>>>>>>> Stashed changes
	 
	 public static void renderNpcForPlayer(EntityPlayer npc, Player p) {
		 Packet<?> packet = new PacketPlayOutEntityTeleport(npc);
		 Packet<?> packet2 = new PacketPlayOutEntityHeadRotation(npc, (byte) ((npc.getBukkitEntity().getLocation().getYaw() * 256f) / 360f));
		 PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		 connection.sendPacket(packet);
		 connection.sendPacket(packet2);
	 }
	
	public static void rotateNPC(EntityPlayer npc, float yaw, float pitch, Player p) {
		Location loc = npc.getBukkitEntity().getLocation();
		loc.setYaw(yaw);
		loc.setPitch(pitch);
		
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		
		npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), yaw, pitch);
		
		ps.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((yaw%360)*256/360)));
		ps.sendPacket(new PacketPlayOutEntityTeleport(npc));
	}
	
	public static void moveNPC(EntityPlayer npc, Player p) {
		Location loc = npc.getBukkitEntity().getLocation();
		loc.setDirection(p.getLocation().subtract(loc).toVector());
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		
		npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), yaw, pitch);
		
		ps.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((yaw%360)*256/360)));
		ps.sendPacket(new PacketPlayOutEntityTeleport(npc));
	}
	
	public static List<EntityPlayer> getLookingNPCs() {return lookPlayer;}
	
	public static EntityPlayer getNpcByUUID(String uuid) {
		for(EntityPlayer npc : npcss) {
			if(npc.getUniqueIDString().equals(uuid)) return npc;
		}
		return null;
	}
	
	public static HashMap<String, List<String>> getTextures() {return textures;}
	
	public static List<String> getPlayerTexture(String name) {

	    if(textures.containsKey(name)) return textures.get(name);

	    Player p = Bukkit.getPlayer(name);
	    String uuid = "";
	    if(p == null) {
	        CloseableHttpClient httpclient1 = HttpClients.createDefault();
	        HttpGet httpget1 = new HttpGet("https://api.mojang.com/users/profiles/minecraft/"+ name);
	        try {
	            HttpResponse httpresponse1 = httpclient1.execute(httpget1);

	            Scanner sc1 = new Scanner(httpresponse1.getEntity().getContent());
	            
	            //Get result
	                while(sc1.hasNext()) {
	                    String str1 = sc1.nextLine();
	                    if(str1.contains("id")) {
	                        str1 = str1.replaceAll(" ", "");
	                        str1 = str1.replaceAll("id", "");
	                        str1 = str1.replaceAll("	", "");
	                        str1 = str1.replaceAll("\"", "");
	                        str1 = str1.replaceAll(",", "");
	                        str1 = str1.replaceAll(":", "");
	                        uuid = str1;
	                    }
	                }
	                
	            sc1.close();
	        } catch (ClientProtocolException e) {
	            System.out.println("[NPC] Failed to load player texture: " + name);
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            System.out.println("[NPC] Failed to load player texture: " + name);
	            e.printStackTrace();
	        }
	    } else {
	        uuid = Bukkit.getPlayer(name).getUniqueId().toString().replaceAll("-", ""); //Get UUID et enlève les -
	    }
	    
	    
	    String texture = "";
	    String signature = "";
	    
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    HttpGet httpget = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/"+ uuid +"?unsigned=false");

	        //Executing the Get request
	        try {
	        HttpResponse httpresponse = httpclient.execute(httpget);
	        System.out.println("http response stuff: " + httpresponse.getStatusLine().getStatusCode());
	        System.out.println("url: " + httpget.getURI());

	        Scanner sc = new Scanner(httpresponse.getEntity().getContent());
	        
	        //Get result
	            while(sc.hasNext()) {
	                String str = sc.nextLine();
	                if(str.contains("value")) {
	                    str = str.replaceAll(" ", "");
	                    str = str.replaceAll("value", "");
	                    str = str.replaceAll("	", "");
	                    str = str.replaceAll("\"", "");
	                    str = str.replaceAll(",", "");
	                    str = str.replaceAll(":", "");
	                    texture = str;
	                } else if(str.contains("signature")) {
	                    str = str.replaceAll(" ", "");
	                    str = str.replaceAll("signature", "");
	                    str = str.replaceAll("	", "");
	                    str = str.replaceAll("\"", "");
	                    str = str.replaceAll(":", "");
	                    signature = str;
	                }
	            }
	            
	        sc.close();
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	        
	        List<String> data = new ArrayList<>();
	        data.add(texture);
	        data.add(signature);
	        
	        textures.put(name, data); //Changer pr un uuid
	        
	        return data;
	}

	public static HashMap<Player, List<EntityPlayer>> getInViewNPCs() {
		return inViewNPCs;
	}

	public static void setInViewNPCs(HashMap<Player, List<EntityPlayer>> inViewNPCs) {
		NPCManager.inViewNPCs = inViewNPCs;
	}
}
