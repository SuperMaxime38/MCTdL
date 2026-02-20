package mctdl.game.games.meltdown;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import mctdl.game.Main;
import mctdl.game.utils.Cuboid;
import mctdl.game.utils.FileLoader;

public class MDMap {
	
	static List<Location> banned = new ArrayList<>();
	static List<Cuboid> melting = new ArrayList<>();
	static List<Location> alarms = new ArrayList<>();
	static World world = Bukkit.getWorlds().get(0);
	
	public static void generateMap(Main main) {
		
		String map = main.getConfig().getString("games.meltdown.map");
		FileConfiguration datas = MeltdownFiles.checkMap(main);
		int X = datas.getInt("schemX");
		int Y = datas.getInt("schemY");
		int Z = datas.getInt("schemZ");
		
		Location loc = new Location(Bukkit.getWorlds().get(0), X, Y, Z);
		
		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(loc.getWorld());
		File file = FileLoader.loadFile(map + ".schem", "schematics/");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		
		try(ClipboardReader reader = format.getReader(new FileInputStream(file));
				EditSession editSession = WorldEdit.getInstance().newEditSession(world);
				) {
			
			Clipboard clipboard = reader.read();
			Operation pasteOperation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(BlockVector3.at(X, Y, Z))
					.build();
			
			Operations.complete(pasteOperation);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void alarmTrigger(List<Integer> rooms) { //Nombre doit etre pair (multiple 4)
		for(int i = 0; i < rooms.size(); i += 4) {
			int X1 = rooms.get(i);
			int Y1 = rooms.get(i + 1);
			int X2 = rooms.get(i + 2);
			int Y2 = rooms.get(i + 3);
			
			Location pos1 = new Location(world, X1, 11, Y1);
			Location pos2 = new Location(world, X2, 26, Y2);
			Cuboid cube = new Cuboid(pos1, pos2);
			melting.add(cube);
			
			for (Block block : cube) {
				if(block.getType() == Material.CRIMSON_TRAPDOOR) {
					Openable o = (Openable) block.getState().getBlockData();
					o.setOpen(true);
					block.setBlockData(o);
					block.getState().update();
					
					alarms.add(block.getLocation());
				}
			}
		}
	}
	
	
	public static void doorTrigger(List<Integer> doors) {
		for(int i = 0; i < doors.size(); i += 6) {
			int X1 = doors.get(i);
			int Y1 = doors.get(i + 1);
			int Z1 = doors.get(i + 2);
			int X2 = doors.get(i + 3);
			int Y2 = doors.get(i + 4);
			int Z2 = doors.get(i + 5);
			
			Location pos1 = new Location(world, X1, Y1, Z1);
			Location pos2 = new Location(world, X2, Y2, Z2);
			Cuboid cube = new Cuboid(pos1, pos2);
			
			for (Block block : cube) {
				block.setType(Material.AIR);
			}
		}
	}
	
	public static void roomTrigger(List<Integer> rooms, Main main) {
		banned.add(new Location(world, 0, 0, 0));
		
		for(int i = 0; i < rooms.size(); i += 4) {
			int X1 = rooms.get(i);
			int Z1 = rooms.get(i + 1);
			int X2 = rooms.get(i + 2);
			int Z2 = rooms.get(i + 3);
			
			//Ys
			//11
			//26
			
			
			// Liste de tous les X
			
			List<Integer> lsX = new ArrayList<>();
			
			int spanX = X2 - X1;
			if(spanX > 0) { // Si pos1 < pos2
				for(int blockX = X1; blockX < X2 + 1; blockX++) {
					lsX.add(blockX);
				}
			} else { // si pos1 > pos2
				for(int blockX_ = X1; blockX_ > X2 -1; blockX_--) {
					lsX.add(blockX_);
				}
			}
			
			// Liste de tous les Z
			
			List<Integer> lsZ = new ArrayList<>();
			
			int spanZ = Z2 - Z1;
			if(spanZ > 0) { // Si pos1 < pos2
				for(int blockZ = Z1; blockZ < Z2 + 1; blockZ++) {
					lsZ.add(blockZ);
				}
			} else { // si pos1 > pos2
				for(int blockZ_ = Z1; blockZ_ > Z2 -1; blockZ_--) {
					lsZ.add(blockZ_);
				}
			}
			
			//Liste de toutes les coordonnées X Z
			
			List<List<Integer>> blocks = new ArrayList<>();
			
			for(int i2 = 0; i2 < lsX.size(); i2++) {
				for(int i3 = 0; i3 < lsZ.size(); i3++) {
					List<Integer> list = new ArrayList<>();
					list.add(lsX.get(i2));
					list.add(lsZ.get(i3));
					
					blocks.add(list);
				}
			}
			
			Random rand = new Random();
			
			new BukkitRunnable() {
				
				List<Material> types = Arrays.asList(Material.AIR, Material.GOLD_BLOCK, Material.RED_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.BLACK_TERRACOTTA);
				
				@Override
				public void run() {
					if(!main.getConfig().getString("game").equals("meltdown")) {
						cancel();
						return;
					}
					if(blocks.isEmpty()) {
						cancel();
						return;
					}
					
					List<Integer> coords = blocks.get(rand.nextInt(blocks.size()));
					int X = coords.get(0);
					int Z = coords.get(1);
					
					for (int i4 = 11; i4 < 27; i4++) {
						Location loc = new Location(world, X,i4, Z);
						loc.getBlock().setType(Material.AIR);
						for(int x = -1; x < 2; x++) {

							Location neighbor = new Location(world, X + x,i4, Z);
							if(!types.contains(world.getBlockAt(neighbor).getType())) { // les blocks voisins de se qui ont fondu se transfoment en coal block
								world.getBlockAt(neighbor).setType(Material.BLACKSTONE);
								//setBlockInNativeWorld(world, neighbor.getBlockX(), neighbor.getBlockY(), neighbor.getBlockZ(), Material.BLACKSTONE, true);
							}
						}
						for(int z = -1; z < 2; z++) {
							Location neighbor = new Location(world, X,i4, Z+z);
							if(!types.contains(world.getBlockAt(neighbor).getType())) { // les blocks voisins de se qui ont fondu se transfoment en coal block
								world.getBlockAt(neighbor).setType(Material.BLACKSTONE);
								//setBlockInNativeWorld(world, neighbor.getBlockX(), neighbor.getBlockY(), neighbor.getBlockZ(), Material.BLACKSTONE, true);
							}
						}
						
						//Ajoute cette coord aux blocks bannis
						banned.add(loc);
						
						//Test si c'est un heater --> Le rend au joueur et lui applique le cooldown
						Meltdown.whatHeater(loc);
					}
					blocks.remove(coords);
				}
			}.runTaskTimer(main, 0, 4);
		}
	}
	
	public static List<Location> getBannedLocs() {
		banned.add(new Location(world, 0, 0, 0));
		return banned;
	}
	
	public static List<Cuboid> getMeltingRooms() {
		return melting;
	}
	
	public static List<Location> getAlarmsLocs() {
		return alarms;
	}
	
//	public static void setBlockInNativeWorld(World world,
//            int x, int y, int z,
//            Material mat, boolean applyPhysics) {
//			net.minecraft.server.v1_16_R3.World nmsWorld = ((CraftWorld) world).getHandle();
//			
//			nmsWorld.setTypeAndData(new BlockPosition(x, y, z), fromMaterial(mat), applyPhysics ? 3 : 2);
//	}
//	
//	public static IBlockData fromMaterial(Material m) {
//	    net.minecraft.server.v1_16_R3.Block nmsBlock = CraftMagicNumbers.getBlock(m);
//	    return nmsBlock.getBlockData();
//	}
 }
