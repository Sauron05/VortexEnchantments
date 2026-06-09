package com.vortexrpg.enchantments.enchant.impl.spear;

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
 * Skewer: Ignores 15/25/35% of target's armor, piercing through protection.
 * The spear finds gaps in the defenses.
 */
public class SkewerEnchant extends VortexEnchant {

    public SkewerEnchant() {
        super("skewer", "Skewer", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double bypassPct = cfgd("bypass_percent", 0.05 + level * 0.10);
        double absorbed = event.getDamage() - event.getFinalDamage();
        double pierced = absorbed * bypassPct;

        event.setDamage(event.getDamage() + pierced);

        if (pierced > 0.5) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 6, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7Pierces through §e" + pct + "% §7of target's armor.";
    }
}
