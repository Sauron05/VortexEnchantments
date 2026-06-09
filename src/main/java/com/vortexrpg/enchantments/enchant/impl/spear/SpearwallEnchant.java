package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Spearwall: While sneaking, reflect 15/25/35% of incoming melee damage
 * back at the attacker. The spear acts as a defensive barrier.
 */
public class SpearwallEnchant extends VortexEnchant {

    public SpearwallEnchant() {
        super("spearwall", "Spearwall", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!victim.isSneaking()) return;

        double reflectPct = cfgd("reflect_percent", 0.05 + level * 0.10);
        double reflected = event.getDamage() * reflectPct;

        if (attacker instanceof org.bukkit.entity.LivingEntity le) {
            le.damage(reflected, victim);
            ParticleUtil.spawn(le.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 8, 0.3);
        }

        ParticleUtil.drawCircle(victim.getLocation(), 1.2, 10, Particle.ENCHANTED_HIT);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7While sneaking, reflect §c" + pct + "% §7of melee damage.";
    }
}
