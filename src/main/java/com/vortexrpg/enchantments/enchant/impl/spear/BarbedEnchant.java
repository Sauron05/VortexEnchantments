package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Barbed: Hits cause a bleed DOT that deals 2/3/4 damage over 4 seconds.
 */
public class BarbedEnchant extends VortexEnchant {

    public BarbedEnchant() {
        super("barbed", "Barbed", EnchantRarity.COMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double totalBleed = cfgd("bleed_total", 1.0 + level);
        int ticks = cfgi("bleed_ticks", 4);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 6, 0.3);

        new BukkitRunnable() {
            int remaining = ticks;

            @Override
            public void run() {
                if (remaining <= 0 || victim.isDead() || !victim.isValid()) {
                    cancel();
                    return;
                }
                victim.damage(totalBleed / ticks, attacker);
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 3, 0.2);
                remaining--;
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 20, 20);
    }

    @Override
    public String getDescription(int level) {
        int dmg = 1 + level;
        return "§7Hits cause §cbleed §7for §e" + dmg + " §7damage over §e4s§7.";
    }
}
