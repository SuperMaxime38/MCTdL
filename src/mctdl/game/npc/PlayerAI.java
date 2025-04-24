package mctdl.game.npc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import mctdl.game.Main;
import mctdl.game.tablist.TabManager;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumMoveType;
import net.minecraft.server.v1_16_R3.EnumProtocolDirection;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.Vec3D;
import net.minecraft.server.v1_16_R3.WorldServer;

public class PlayerAI extends EntityPlayer {

	public static final int WALK_FORWARD = 0;
	public static final int WALK_BACKWARD = 1;
	public static final int WALK_LEFT = 2;
	public static final int WALK_RIGHT = 3;
	public static final int JUMP = 4;
	private final double speed = 0.21585850519;
	//private final double jumpSpeed = 0.42;
	private final double runMult = 1.3;
	private final double sneakMult = 0.3;
	
	private boolean isSneaking = false;
	private boolean isSprinting = false;
	
	private float yaw = 0.0F;
	
	public PlayerAI(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
		super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
		this.yaw = this.getBukkitEntity().getLocation().getYaw();
	}
	
	public static PlayerAI createNPC(String name, World world, Location location) {

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
	        
	        new BukkitRunnable() {
	        	
	        	List<Packet<?>> packets = Arrays.asList(playerInfoAdd, namedEntitySpawn, headRotation, playerInfoRemove);
	        	int counter = 0;

				@Override
				public void run() {
					if(counter >= packets.size()) {
						cancel();
						
					} else {
						connection.sendPacket(packets.get(counter));
					}
					counter++;
					
				}
		        
	        }.runTaskTimer(Main.getPlugin(Main.class), 0, 5);
	        
//	        connection.sendPacket(playerInfoAdd);
//	        connection.sendPacket(namedEntitySpawn);
//	        connection.sendPacket(headRotation);
//	        connection.sendPacket(playerInfoRemove);
	    }
	    
	    NPCManager.addExternalNPC(entityPlayer);
	    
	    TabManager.updateTabList();
	    
	    entityPlayer.setNoGravity(false);
	    entityPlayer.noclip = false;
	    entityPlayer.collides = true;
	    entityPlayer.setInvisible(false);
	    entityPlayer.setInvulnerable(false);
	    
	    return entityPlayer;
	    }

	public EntityPlayer getEntity() {
		return this;
	}
	
    @Override public void tick() {
    super.tick();

 // Gravité
    if (!this.onGround) {
        this.setMot(this.getMot().add(0, -0.08, 0));
    }

    // Appliquer le mouvement
    this.move(EnumMoveType.SELF, this.getMot());

    Vec3D mot = this.getMot();

    // Friction dépendant du sol
    if (this.onGround) {
        // Récupère le bloc sous les pieds
        int x = MathHelper.floor(this.locX());
        int y = MathHelper.floor(this.locY() - 1.0);
        int z = MathHelper.floor(this.locZ());

        float friction = 0.91f;

        if (this.world.getType(new BlockPosition(x, y, z)).getBlock() != null) {
            friction = this.world.getType(new BlockPosition(x, y, z)).getBlock().getFrictionFactor() * 0.91f;
        }

        double frictionX = mot.x * friction;
        double frictionZ = mot.z * friction;

        this.setMot(frictionX, mot.y, frictionZ);
    } else {
        // En l'air : friction moindre
        this.setMot(mot.x * 0.91, mot.y * 0.98, mot.z * 0.91);
    }

    // Collision amortie
    if (this.positionChanged && this.onGround) {
        this.setMot(this.getMot().a(0.6));
    }
	
	
	    if (this.noDamageTicks > 0) {
	        --this.noDamageTicks;
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
	    //net.minecraft.server.v1_16_R3.Entity attacker = damagesource.getEntity();
	
    	// Appliquer les dégâts
        float newHealth = Math.max(0, this.getHealth() - f);
        this.setHealth(newHealth);

        // Knockback si l'attaquant est une entité
        if (damagesource.getEntity() != null) {
            net.minecraft.server.v1_16_R3.Entity attacker = damagesource.getEntity();
            if(!(attacker instanceof net.minecraft.server.v1_16_R3.EntityArrow)) {
            	double dx = this.locX() - attacker.locX();
	            double dz = this.locZ() - attacker.locZ();
	            double magnitude = Math.sqrt(dx * dx + dz * dz);
	
	            if (magnitude > 0) {
	                double strength = 0.4; // force du knockback
	                dx /= magnitude;
	                dz /= magnitude;
	                this.setMot(this.getMot().add(dx * strength, 0.3, dz * strength)); // ajoute aussi un petit rebond vertical
	            }
            }
            
        }

        if (newHealth == 0) {
            this.respawn();
        }
        return true;
    }
    
    public void respawn() {
    	
    	this.setHealth(this.getMaxHealth());
    	
    	Packet<?> del = new PacketPlayOutEntityDestroy(this.getBukkitEntity().getEntityId());
    	Packet<?> load = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this);
	    PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation(this, (byte) ((this.getBukkitEntity().getLocation().getYaw() * 256f) / 360f));
    	Packet<?> join = new PacketPlayOutNamedEntitySpawn(this);
    	
    	for(Player p : Bukkit.getOnlinePlayers()) {
    		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
    		
			connection.sendPacket(del);
    		
	        connection.sendPacket(load);
	        connection.sendPacket(join);
	        connection.sendPacket(headRotation);
    	}
    	
    	this.setNoGravity(false);
	    this.noclip = false;
	    this.collides = true;
	    this.setInvisible(false);
	    this.setInvulnerable(false);
    }

    public boolean isHitBy(net.minecraft.server.v1_16_R3.Entity other) {
        double dx = this.locX() - other.locX();
        double dy = this.locY() - other.locY();
        double dz = this.locZ() - other.locZ();
        
        System.out.println("distance squarred: " + (dx * dx + dy * dy + dz * dz));
        
        return dx * dx + dy * dy + dz * dz < 1.6; // For some reason distances seems to be weird so 1.6 bcs it works
    }
    
    public void walk(int direction) {
    	switch(direction) {
    	case WALK_FORWARD:
    		computeVelocity(this.yaw);
    		break;
    	case WALK_BACKWARD:
			computeVelocity(this.yaw + 180);
			break;
    	case WALK_LEFT:
			computeVelocity(this.yaw - 90);
			break;
		case WALK_RIGHT:
			computeVelocity(this.yaw + 90);
			break;
		case JUMP:
			this.jump();
			break;
    	}
    }
    
    /**
     * This method rotates the player
     * @param yaw: The yaw of the player in degrees (Y axis)
     * @param pitch: The pitch of the player in degrees (X axis)
     */
    public void rotate(float yaw, float pitch) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			NPCManager.rotateNPC(this, yaw, pitch, p);
		}
		this.yaw = yaw;
    }
    
    private void computeVelocity(double angle) {
    	double rad = Math.toRadians(angle);
    	double Zmult = Math.cos(rad);
    	double Xmult = -Math.sin(rad);
    	
    	Vector vect = new Vector(Xmult * this.speed, 0, Zmult * this.speed);
    	if(isSprinting && !isSneaking) vect = vect.multiply(this.runMult);
    	if(isSneaking) vect = vect.multiply(this.sneakMult);
    	
    	double yVel = this.getBukkitEntity().getVelocity().getY();
		vect.setY(yVel);
		
		this.getBukkitEntity().setVelocity(vect);
    }
    
    public void stopWalking() {
    	this.getBukkitEntity().setVelocity(new Vector(0, 0, 0));
    }
    

}
