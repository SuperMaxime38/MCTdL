package mctdl.game.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import mctdl.game.commands.BaltopCommand;
import mctdl.game.games.lobby.PouleZooka;
import mctdl.game.teams.TeamsManager;
import mctdl.game.utils.PlayerData;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.MinecraftServer;
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
	

static List<EntityPlayer> npcss = new ArrayList<>();
static List<EntityPlayer> lookPlayer = new ArrayList<>();
static HashMap<String, List<String>> data = new HashMap<String, List<String>>();

	public static boolean fileCheck(Main main){
    	
	     File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + "npc");
	     File f = new File(userdata, File.separator + "npc.yml");
	     FileConfiguration preset = YamlConfiguration.loadConfiguration(f);

	     
	     if (!f.exists()) { //CREER SI FICHIER N'EXISTE PAS
	         try {
	        	 
	        	 preset.createSection("textures");
	        	 
	        	 preset.createSection("npc");
	        	 List<Integer> data = new ArrayList<>();
	        	 data.add(-73);
	        	 data.add(14);
	        	 data.add(72);
	        	 preset.set("npc.top1", data);
	        	 
	             preset.save(f);
	             
	         } catch (IOException exception) {

	             exception.printStackTrace();
	         }
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
	    for(String role : yaml.getConfigurationSection("npc").getKeys(false)) {
	    	List<String> elements = yaml.getStringList("npc." + role);
	    	data.put(role, elements);
	    }
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
	    
	    for (String role : data.keySet()) {
	    	List<?> elements = data.get(role);
	    	
			yaml.set("npc." + role, elements);
		}
	    
	    try {
			yaml.save(f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void onPlayerJoin(Player p, Main main, int delay) {
		String name = "";
		for (String role : data.keySet()) {
			List<String> elements = data.get(role);
			
			
			List<String> classement = BaltopCommand.getClassement();
			ChatColor c = ChatColor.WHITE;
			String owner = "Dream";
			Location loc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(elements.get(0)), Double.parseDouble(elements.get(1)), Double.parseDouble(elements.get(2)));
			//isLookingPlyaer
			boolean doesLook = false;
			
			//Body+Head Rotation
			float yaw = 0;
			float pitch = 0;
			
			
			//other npc infos---
			List<ItemStack> items = new ArrayList<>();
			ItemStack righthand = new ItemStack(Material.AIR);
			ItemStack lefthand = new ItemStack(Material.AIR);
			ItemStack helmet = new ItemStack(Material.AIR);
			ItemStack chestplate = new ItemStack(Material.AIR);
			ItemStack leggings = new ItemStack(Material.AIR);
			ItemStack boots = new ItemStack(Material.AIR);
			
			switch(role) {
				case "top1":
					name = classement.get(0);
					c = TeamsManager.getTeamColor(name);
					owner = name;
					doesLook = Boolean.parseBoolean(elements.get(3));
					yaw = Float.parseFloat(elements.get(4));
					pitch = Float.parseFloat(elements.get(5));
					break;
				case "top2":
					name = classement.get(1);
					c = TeamsManager.getTeamColor(name);
					owner = name;
					break;
				case "poulezooka":
					name = "Canwardo";
					c = ChatColor.YELLOW;
					owner = "Le_canward";
					righthand = PouleZooka.getBazooka();
					doesLook = true;
					break;
				default:
					name = elements.get(3);
					owner = elements.get(4);
					
					//Equipment
					righthand = PlayerData.getItem(elements.get(5));
					lefthand = PlayerData.getItem(elements.get(6));
					helmet = PlayerData.getItem(elements.get(7));
					chestplate = PlayerData.getItem(elements.get(8));
					leggings = PlayerData.getItem(elements.get(9));
					boots = PlayerData.getItem(elements.get(10));
					
					doesLook = Boolean.valueOf(elements.get(11));
					//Rotation
					yaw = Float.parseFloat(elements.get(12));
					pitch = Float.parseFloat(elements.get(13));
					break;
			}
			
			EntityPlayer npc = npcBuilder(c + name, owner, loc, p, main);
			
			if(doesLook) lookPlayer.add(npc);
			
			 items.add(righthand);
			 items.add(lefthand);
			 items.add(helmet);
			 items.add(chestplate);
			 items.add(leggings);
			 items.add(boots);
			
			npcss.add(npc);
			showNPCFor(npc, p, items);
			rotateNPC(npc, yaw, pitch, p);
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
	
	public static EntityPlayer npcBuilder(String name, String textureowner, Location loc, Player p, Main main) {
		CraftPlayer cplayer  = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		MinecraftServer server = sp.getMinecraftServer(); //get Server
		WorldServer lvl = sp.getWorldServer(); //get World
		
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name); //NPC Profile
		
		List<String> textures = NPCManager.getPlayerTexture(textureowner, main);
		String texture = textures.get(0);
		String signature = textures.get(1);
		
		gameProfile.getProperties().put("textures", new Property("textures", texture , signature));
	      
		PlayerInteractManager pi = new PlayerInteractManager(lvl);
		
		EntityPlayer npc = new EntityPlayer(server, lvl, gameProfile, pi); //Create NPC
		npc.setPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		
		return npc;
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
			
		equipment.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(items.get(0)))); //Item Main Hand
		equipment.add(new Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(items.get(1)))); //Item Left Hand
		equipment.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(2)))); //Item Helmlet
		equipment.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(items.get(3)))); //Item Chestplate
		equipment.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(items.get(4)))); //Item Leggings
		equipment.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(items.get(5)))); //Item Boots
			
			
		ps.sendPacket(new PacketPlayOutEntityEquipment(npc.getBukkitEntity().getEntityId(), equipment));
		
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
		
		ps.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		ps.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		npcss.remove(npc);
	}
	
	public static void killAllNPCs(Player p) {
		CraftPlayer cplayer = (CraftPlayer) p;
		EntityPlayer sp = cplayer.getHandle(); // get EntityPlayer from player
		
		PlayerConnection ps = sp.playerConnection; //GET Player "connection"
		for(EntityPlayer npc : npcss) {
			ps.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
			ps.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}
		npcss.clear();
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
	
	public static HashMap<String, List<String>> getTextures() {return textures;}
	
	public static List<String> getPlayerTexture(String name, Main main) {
		
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
	        } catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
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
}
