package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * NightRunner: Gain Night Vision passively.
 */
public class NightRunnerEnchant extends VortexEnchant {
    public NightRunnerEnchant() { super("night_runner", "Night Runner", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 0, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Grants passive §bNight Vision§7.";
    }
}
