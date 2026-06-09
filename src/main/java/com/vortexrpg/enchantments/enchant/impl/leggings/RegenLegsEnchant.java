package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Regen Legs: Passive slow regeneration while sprinting.
 */
public class RegenLegsEnchant extends VortexEnchant {
    public RegenLegsEnchant() { super("regen_legs", "Regen Legs", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() >= maxHp) return;
        double heal = cfgd("heal_per_tick", 0.25 * level);
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        ParticleUtil.spawn(player.getLocation().add(0, 0.5, 0), Particle.HAPPY_VILLAGER, 1, 0.3);
    }

    @Override public String getDescription(int level) {
        return "§7While sprinting: regenerate §a" + String.format("%.2f", 0.25 * level) + " §7HP/s.";
    }
}
