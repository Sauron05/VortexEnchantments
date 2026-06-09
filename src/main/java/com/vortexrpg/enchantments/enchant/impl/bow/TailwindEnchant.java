package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Tailwind: After shooting, gain Speed I/II for 2/3/4 seconds.
 * Hit-and-run archery — fire and reposition.
 */
public class TailwindEnchant extends VortexEnchant {

    public TailwindEnchant() {
        super("tailwind", "Tailwind", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("duration", (level + 1)) * 20;
        int amp = Math.min(level - 1, 1);
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amp, false, true));
        ParticleUtil.spawn(shooter.getLocation(), Particle.CLOUD, 6, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int dur = level + 1;
        int amp = Math.min(level, 2);
        return "§7After shooting: §eSpeed " + amp + " §7for §e" + dur + "s§7.";
    }
}
