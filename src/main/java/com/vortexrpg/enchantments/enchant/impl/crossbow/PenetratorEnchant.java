package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Penetrator: Bolt ignores 15/25/35% of target's armor value.
 * Direct armor penetration — deals true-ish damage.
 */
public class PenetratorEnchant extends VortexEnchant {

    public PenetratorEnchant() {
        super("penetrator", "Penetrator", EnchantRarity.COMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double armorBypass = cfgd("bypass_pct", 0.10 + level * 0.05);
        double absorbed = event.getDamage() - event.getFinalDamage();
        double reclaim = absorbed * armorBypass;
        event.setDamage(event.getDamage() + reclaim);

        if (reclaim > 0.5) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 6, 0.2);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7Ignore §e" + pct + "% §7of target armor.";
    }
}
