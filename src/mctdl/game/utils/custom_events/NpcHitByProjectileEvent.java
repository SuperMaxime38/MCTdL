package mctdl.game.utils.custom_events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import mctdl.game.npc.PlayerAI;

public class NpcHitByProjectileEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerAI npc;
    private final Projectile projectile;
    private final LivingEntity shooter;

    private float damage;
    private boolean cancelled;

    public NpcHitByProjectileEvent(PlayerAI npc, Projectile projectile, LivingEntity shooter, float damage) {
        this.npc = npc;
        this.projectile = projectile;
        this.shooter = shooter;
        this.damage = damage;
    }

    public PlayerAI getNpc() {
        return npc;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public LivingEntity getShooter() {
        return shooter;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}