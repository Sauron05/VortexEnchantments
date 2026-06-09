package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Miasma: Arrow impact creates a poison cloud — all entities in 3-block radius
 * get Poison I for 3/5/7 seconds.
 */
public class MiasmaEnchant extends VortexEnchant {

    public MiasmaEnchant() {
        super("miasma", "Miasma", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 3.0);
        int duration = cfgi("poison_duration", (1 + level * 2)) * 20;
        Location center = victim.getLocation();

        for (LivingEntity nearby : MathUtil.getNearbyLiving(center, radius)) {
            if (nearby.equals(shooter)) continue;
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 0, false, true));
        }

        ParticleUtil.drawCircle(center, radius, 16, Particle.ITEM_SLIME);
        ParticleUtil.spawn(center.add(0, 1, 0), Particle.ITEM_SLIME, 15, 1.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Arrow: §2poison cloud §7— Poison I §e" + dur + "s §7in 3-block radius.";
    }
}
