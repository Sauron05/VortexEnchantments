package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Decay: Arrow applies Wither effect that spreads to nearby enemies.
 * Each infected entity can spread to 1 more within 3 blocks.
 */
public class DecayEnchant extends VortexEnchant {

    public DecayEnchant() {
        super("decay", "Decay", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("wither_duration", (2 + level)) * 20;
        double spreadRadius = cfgd("spread_radius", 3.0);
        int spreadCount = cfgi("spread_count", level);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 0, false, true));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 10, 0.3);

        List<LivingEntity> nearby = MathUtil.getNearbyLiving(victim.getLocation(), spreadRadius);
        nearby.removeIf(e -> e.equals(shooter) || e.equals(victim));

        int spread = 0;
        for (LivingEntity target : nearby) {
            if (spread >= spreadCount) break;
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration / 2, 0, false, true));
            ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                    target.getLocation().add(0, 1, 0), Particle.SMOKE, 0.4);
            spread++;
        }
    }

    @Override
    public String getDescription(int level) {
        int dur = 2 + level;
        return "§7Arrow: §8Wither " + dur + "s §7that §8spreads §7to §e" + level + " §7nearby.";
    }
}
