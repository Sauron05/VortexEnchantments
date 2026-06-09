package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Dreadnought: Kills cause nearby mobs to flee in fear.
 * Radius: 8/10/12 blocks. Duration: 3/4/5s.
 */
public class DreadnoughtEnchant extends VortexEnchant {

    public DreadnoughtEnchant() {
        super("dreadnought", "Dreadnought", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("fear_radius", 6.0 + level * 2.0);
        int durationTicks = cfgi("fear_ticks", 40 + level * 20);

        for (LivingEntity nearby : MathUtil.getNearbyLiving(killer.getLocation(), radius)) {
            if (nearby.equals(killer)) continue;

            if (nearby instanceof Mob mob) {
                mob.setTarget(null);
            }
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 1, false, false));
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, durationTicks, 1, false, false));

            if (nearby instanceof Player p) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, durationTicks / 2, 0, false, false));
                p.sendMessage("§4[Dreadnought] §7A terrible fear grips you...");
            }
        }

        ParticleUtil.spawn(killer.getLocation(), Particle.LARGE_SMOKE, 30, radius * 0.5);
        SoundUtil.play(killer.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.6f, 0.5f);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (6 + level * 2);
        return "§7Kills cause enemies within §c" + r + " blocks §7to flee in fear.";
    }
}
