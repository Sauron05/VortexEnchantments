package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Conduit Plate: Grants permanent Haste while wearing.
 */
public class ConduitPlateEnchant extends VortexEnchant {
    public ConduitPlateEnchant() { super("conduit_plate", "Conduit Plate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int amp = cfgi("haste_amplifier", level - 1);
        if (!player.hasPotionEffect(PotionEffectType.HASTE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, amp, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Grants permanent §aHaste " + level + "§7 while wearing.";
    }
}
