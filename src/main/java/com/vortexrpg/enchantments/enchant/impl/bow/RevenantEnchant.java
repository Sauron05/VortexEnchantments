package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Revenant: If you die within 5s of shooting an arrow, a spectral arrow
 * seeks your killer and deals revenge damage (8/12/16).
 */
public class RevenantEnchant extends VortexEnchant {

    private static final Map<UUID, Long> LAST_SHOT_TIME = new HashMap<>();

    public RevenantEnchant() {
        super("revenant", "Revenant", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(org.bukkit.event.entity.EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        LAST_SHOT_TIME.put(shooter.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, org.bukkit.entity.Entity attacker, int level) {
        if (!isEnabled()) return;

        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth > 0) return;

        Long lastShot = LAST_SHOT_TIME.get(victim.getUniqueId());
        if (lastShot == null) return;
        long window = cfgi("window_ms", 5000);
        if (System.currentTimeMillis() - lastShot > window) return;

        if (attacker instanceof LivingEntity killer) {
            Location spawnLoc = victim.getLocation().add(0, 2, 0);
            double damage = cfgd("revenge_damage", 4.0 + level * 4.0);

            Arrow revengeArrow = victim.getWorld().spawn(spawnLoc, Arrow.class);
            revengeArrow.setShooter(victim);
            revengeArrow.setDamage(damage);
            revengeArrow.setVelocity(killer.getLocation().add(0, 1, 0).toVector()
                    .subtract(spawnLoc.toVector()).normalize().multiply(2.0));

            ParticleUtil.spawn(spawnLoc, Particle.SOUL, 15, 0.5);
            SoundUtil.play(spawnLoc, Sound.ENTITY_VEX_DEATH, 1.0f, 0.5f);
        }

        LAST_SHOT_TIME.remove(victim.getUniqueId());
    }

    @Override
    public String getDescription(int level) {
        double dmg = 4 + level * 4;
        return "§7Die after shooting: §5vengeful arrow §7seeks killer (§c" + dmg + " dmg§7).";
    }
}
