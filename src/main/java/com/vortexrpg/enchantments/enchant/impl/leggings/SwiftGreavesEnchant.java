package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * SwiftGreaves: Grants passive Speed while wearing.
 */
public class SwiftGreavesEnchant extends VortexEnchant {
    public SwiftGreavesEnchant() { super("swift_greaves", "Swift Greaves", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int amp = cfgi("speed_amplifier", level - 1);
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, amp, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants permanent §aSpeed " + level + "§7.";
    }
}
