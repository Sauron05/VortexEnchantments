package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Slagstrike: Penetrate 50/65/80% of shield blocking + set target on fire for 2 seconds.
 * Hammer melts through defenses.
 */
public class SlagstrikeEnchant extends VortexEnchant {

    public SlagstrikeEnchant() {
        super("slagstrike", "Slagstrike", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Check if victim is blocking (Player only)
        if (victim instanceof Player target && target.isBlocking()) {
            double penetration = cfgd("penetration", 0.35 + level * 0.15);
            double blocked = event.getDamage();
            // Re-apply a fraction of blocked damage
            event.setDamage(blocked * penetration);
        }

        int fireTicks = cfgi("fire_ticks", 40);
        victim.setFireTicks(fireTicks);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.LAVA, 6, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.6f, 1.2f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.35 + level * 0.15) * 100);
        return "§7Penetrate §c" + pct + "% §7of shields + §6fire 2s§7.";
    }
}
