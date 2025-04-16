package mctdl.game.npc;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import mctdl.game.tablist.TabManager;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumProtocolDirection;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class PlayerAI extends EntityPlayer {
	
	public PlayerAI(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
		super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
	}
	
	public static PlayerAI createNPC(Player p, String name, World world, Location location) {

	    MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
	    WorldServer nmsWorld = ((CraftWorld) world).getHandle();
	    GameProfile profile = new GameProfile(UUID.randomUUID(), name);
	    PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
	    PlayerAI entityPlayer = new PlayerAI(nmsServer, nmsWorld, profile, interactManager);
	    entityPlayer.playerConnection = new PlayerConnection(nmsServer, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);

	 
	    List<String> textures = NPCManager.getPlayerTexture("PoutreCosmique");
		String texture = textures.get(0);
		String signature = textures.get(1);
		
		profile.getProperties().put("textures", new Property("textures", texture , signature));

	    entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

	    nmsWorld.addEntity(entityPlayer);

	    PacketPlayOutPlayerInfo playerInfoAdd = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
	    PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
	    PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((location.getYaw() * 256f) / 360f));
	    PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

	    for (Player player : Bukkit.getOnlinePlayers()) {
	        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
	        connection.sendPacket(playerInfoAdd);
	        connection.sendPacket(namedEntitySpawn);
	        connection.sendPacket(headRotation);
	        connection.sendPacket(playerInfoRemove);
	    }
	    
	    NPCManager.addExternalNPC(entityPlayer);
	    
	    TabManager.updateTabList();
	    
	    return entityPlayer;
	    }
	
    @Override public void tick() {
    super.tick();
    if (noDamageTicks > 0) {
        --noDamageTicks;
    }
    }

    public void despawn() {
    this.despawn();
    }

    @Override
    public void collide(net.minecraft.server.v1_16_R3.Entity entity) {
    super.collide(entity);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
    net.minecraft.server.v1_16_R3.Entity attacker = damagesource.getEntity();

    boolean damaged = super.damageEntity(damagesource, f);
    return damaged;
    }

}
