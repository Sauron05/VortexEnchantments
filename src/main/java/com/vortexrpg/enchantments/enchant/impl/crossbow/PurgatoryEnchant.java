package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Purgatory: Kill with bolt traps the soul — a ghost zombie spawns and fights
 * for you for 8/12/16 seconds, then despawns.
 */
public class PurgatoryEnchant extends VortexEnchant {

    public PurgatoryEnchant() {
        super("purgatory", "Purgatory", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth > 0) return;
        if (isOnCooldown(shooter)) return;

        Location spawnLoc = victim.getLocation();
        int lifetimeTicks = cfgi("lifetime", (4 + level * 4)) * 20;

        Zombie ghost = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class);
        ghost.customName(net.kyori.adventure.text.Component.text("\u00a77\u00a7oSoul of " + victim.getName()));
        ghost.setCustomNameVisible(true);
        ghost.setAdult();
        ghost.setCanPickupItems(false);
        ghost.setInvisible(true);

        // Target nearest hostile entity
        LivingEntity nearest = com.vortexrpg.enchantments.util.MathUtil.getNearestLiving(
                spawnLoc, 15, e -> !e.equals(shooter) && !e.equals(ghost));
        if (nearest != null) {
            ghost.setTarget(nearest);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ghost.isDead()) {
                    ParticleUtil.spawn(ghost.getLocation().add(0, 1, 0), Particle.SOUL, 5, 0.3);
                    ghost.remove();
                }
            }
        }.runTaskLater(plugin, lifetimeTicks);

        ParticleUtil.spawn(spawnLoc, Particle.SOUL, 15, 0.5);
        SoundUtil.play(spawnLoc, Sound.ENTITY_VEX_AMBIENT, 1.0f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 20.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 4 + level * 4;
        return "§7Kill: §5§lSOUL GHOST §7fights for you §e" + dur + "s§7. 20s CD.";
    }
}
