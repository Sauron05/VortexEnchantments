package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mindforge: XP gain is multiplied by 1.X while wearing.
 */
public class MindforgeEnchant extends VortexEnchant {
    public MindforgeEnchant() { super("mindforge", "Mindforge", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Indicated by passive particle; actual XP multiplier works via VortexEnchant XP handling
        com.vortexrpg.enchantments.util.ParticleUtil.spawn(
                player.getLocation().add(0, 2.3, 0), org.bukkit.Particle.ENCHANT, 3, 0.2);
    }

    @Override public String getDescription(int level) {
        int pct = 15 + level * 10;
        return "§7XP gained multiplied by §a" + pct + "%§7.";
    }
}
