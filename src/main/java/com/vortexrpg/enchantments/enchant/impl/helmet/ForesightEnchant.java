package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Foresight: Passive Night Vision while wearing. */
public class ForesightEnchant extends VortexEnchant {
    public ForesightEnchant() { super("foresight", "Foresight", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, true, false, false));
        }
    }

    @Override public String getDescription() { return "Grants permanent Night Vision."; }
    @Override public String getDescription(int level) { return "§7Passive §aNight Vision§7 while worn."; }
}
