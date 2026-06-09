package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Genesis: Arrow heals allies nearby and damages enemies.
 * A divine arrow — within 4/5/6 blocks of impact, allies heal 2/3/4, enemies take 2/3/4.
 */
public class GenesisEnchant extends VortexEnchant {

    public GenesisEnchant() {
        super("genesis", "Genesis", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        double radius = cfgd("radius", 3.0 + level);
        double healAmount = cfgd("heal", 1.0 + level);
        double damageAmount = cfgd("damage", 1.0 + level);

        for (LivingEntity entity : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (entity.equals(shooter)) continue;
            if (entity.equals(victim)) continue;

            if (entity instanceof Player p) {
                // Heal players (allies)
                double maxHp = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                p.setHealth(Math.min(p.getHealth() + healAmount, maxHp));
                ParticleUtil.spawn(p.getLocation().add(0, 1, 0), Particle.HEART, 4, 0.3);
            } else {
                // Damage non-player entities
                entity.damage(damageAmount, shooter);
                ParticleUtil.spawn(entity.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 4, 0.3);
            }
        }

        ParticleUtil.drawCircle(victim.getLocation(), radius, 24, Particle.END_ROD);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.5f);

        setCooldownFromConfig(shooter, "cooldown", 8.0);
    }

    @Override
    public String getDescription(int level) {
        double val = 1 + level;
        return "§7Arrow: §a§lheal allies §7+ §c§ldamage enemies §7in " + (3 + level) + " blocks (§e" + val + "§7). 8s CD.";
    }
}
