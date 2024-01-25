package mctdl.game.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import mctdl.game.Main;

public class Permissions {
	
	static Main main;
	public Permissions(Main main) {
		Permissions.main = main;
	}
	
	static HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();
	
	public static void regPlayer(Player p) {
		PermissionAttachment attachment = p.addAttachment(main);
		perms.put(p.getUniqueId(), attachment);
	}
	
	public static void addPerm(Player p, String permission) {
		PermissionAttachment pperms = perms.get(p.getUniqueId());
		pperms.setPermission(permission, true);
		System.out.println(pperms.getPermissions());
		
	}
	
	public static void removePerm(Player p, String permission) {
		perms.get(p.getUniqueId()).unsetPermission(permission);
	}
	
}
