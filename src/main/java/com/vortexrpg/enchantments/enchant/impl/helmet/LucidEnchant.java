package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Lucid: Passive slow falling while wearing helmet. */
public class LucidEnchant extends VortexEnchant {
    public LucidEnchant() { super("lucid", "Lucid", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 0, true, false, false));
        }
    }

    @Override public String getDescription() { return "Passively grants Slow Falling."; }
    @Override public String getDescription(int level) { return "§7Passive §aSlow Falling§7 while worn."; }
}
