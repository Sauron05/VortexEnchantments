package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Leg Brace: Grants Jump Boost passively.
 */
public class LegBraceEnchant extends VortexEnchant {
    public LegBraceEnchant() { super("leg_brace", "Leg Brace", EnchantRarity.COMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int amp = cfgi("jump_amplifier", level - 1);
        if (!player.hasPotionEffect(PotionEffectType.JUMP_BOOST)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 60, amp, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants permanent §aJump Boost " + level + "§7.";
    }
}
