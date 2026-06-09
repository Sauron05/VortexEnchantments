package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * VigorStride: Regenerate HP while sprinting at full food.
 */
public class VigorStrideEnchant extends VortexEnchant {
    public VigorStrideEnchant() { super("vigor_stride", "Vigor Stride", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isSprinting()) return;
        if (player.getFoodLevel() < cfgi("food_threshold", 18)) return;
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() >= maxHp) return;
        double heal = cfgd("heal_per_tick", 0.2 * level);
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        ParticleUtil.spawn(player.getLocation().add(0, 0.5, 0), Particle.HAPPY_VILLAGER, 1, 0.3);
    }

    @Override public String getDescription(int level) {
        return "§7Sprint at full food: heal §a" + String.format("%.1f", 0.2 * level) + " §7HP/s.";
    }
}
