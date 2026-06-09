package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Sureshot: 15/20/25% bonus damage to targets at full HP.
 * Punishes the uninjured — first shot matters most.
 */
public class SureshotEnchant extends VortexEnchant {

    public SureshotEnchant() {
        super("sureshot", "Sureshot", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double maxHp = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() < maxHp - 0.1) return;

        double bonus = cfgd("bonus", 0.10 + level * 0.05);
        event.setDamage(event.getDamage() * (1.0 + bonus));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 10, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7+" + pct + "% damage to §efull HP §7targets.";
    }
}
