package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Soulbind: Bind your soul to the sword. On death, the sword stays in your
 * inventory instead of dropping. Your soul protects it.
 */
public class SoulbindEnchant extends VortexEnchant {

    public SoulbindEnchant() {
        super("soulbind", "Soulbind", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (level >= 2 && player.getHealth() > 0) {
            ParticleUtil.spawn(player.getLocation().add(0, 0.5, 0), Particle.SOUL, 1, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Your sword is §5soulbound§7. It stays in inventory on death.";
    }
}
