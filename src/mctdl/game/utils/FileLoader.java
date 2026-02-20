package mctdl.game.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.bukkit.Bukkit;

import mctdl.game.Main;

public class FileLoader {
	
	private static final String DEFAULT_PATH = "/files/";
	
	public static void loadFiles() {
		
		InputStream is = Main.class.getResourceAsStream(DEFAULT_PATH + "index.txt");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line;
		
		String fileName, filePath;
		
		try {
			while((line = reader.readLine()) != null) {
				fileName = line.split(":")[0];
				filePath = line.split(":")[1];
				
				loadFile(fileName, filePath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadFile(String fileName, String filePath) {
		File directory = new File(Bukkit.getServer().getPluginManager().getPlugin("MCTdL").getDataFolder(), File.separator + filePath);
		
		try {
			
			InputStream is = Main.class.getResourceAsStream(DEFAULT_PATH + filePath + fileName);
			copyStreamIfMissing(is, fileName, directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NullPointerException e) {
			System.out.println("Could not find resource file: " + filePath + fileName);
		}
		
	}
	
	private static void copyStreamIfMissing(InputStream is, String fileName, File targetDir) throws IOException {

	    if (!targetDir.exists()) {
	        targetDir.mkdirs();
	    }

	    File target = new File(targetDir, fileName);

	    if (!target.exists()) {
	        Files.copy(is, target.toPath());
	        
	        System.out.println("Succesfully copied " + fileName);
	    }

	    is.close();
	}
}
