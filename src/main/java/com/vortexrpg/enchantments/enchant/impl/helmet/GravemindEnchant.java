package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Gravemind: On death, curse the killer — they take X damage per second for 5s.
 */
public class GravemindEnchant extends VortexEnchant {
    public GravemindEnchant() { super("gravemind", "Gravemind", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth > 0) return;
        if (!(attacker instanceof LivingEntity killer)) return;

        double dps = cfgd("dps", 1.0 + level);
        int ticks = cfgi("duration_ticks", 100);
        ParticleUtil.spawn(victim.getLocation(), Particle.SOUL, 20, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 1.5f);

        new org.bukkit.scheduler.BukkitRunnable() {
            int elapsed = 0;
            @Override
            public void run() {
                if (killer.isDead() || !killer.isValid() || elapsed >= ticks) { cancel(); return; }
                killer.damage(dps);
                ParticleUtil.spawn(killer.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 5, 0.3);
                elapsed += 20;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @Override public String getDescription(int level) {
        return "§7On death: curse your killer for §c" + (1.0 + level) + " §7damage/s for 5s.";
    }
}
